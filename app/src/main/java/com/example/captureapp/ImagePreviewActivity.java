package com.example.captureapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.clans.fab.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class ImagePreviewActivity extends AppCompatActivity {

    private FloatingActionButton sendCapture;
    private ImageView previewImageView;

    public static final String FILE_NAME_TAG = "FILE_NAME_TAG";
    public static final String HEIGHT_ORIGINAL_TAG = "HEIGHT_ORIGINAL_TAG";
    public static final String WIDTH_ORIGINAL_TAG = "WIDTH_ORIGINAL_TAG";
    public static final String HEIGHT_FRAME_TAG = "HEIGHT_FRAME_TAG";
    public static final String WIDTH_FRAME_TAG = "WIDTH_FRAME_TAG";
    public static final String LEFT_FRAME_TAG = "LEFT_FRAME_TAG";
    public static final String TOP_FRAME_TAG = "TOP_FRAME_TAG";

    // connection can be slow with long runtime server processing
    // so we have to negotiate the connection every time
    // ( long polling: not long than 40 second [or any amount of time] to respond a request)
    // because of that we constantly reestablishing a connection with the server (short polling)
    // webSocket provide a full-duplex connection to avoid the behaviour
    // of constantly reestablishing connection

    // we can use STOMP protocol for effective client-server communication with webSocket.
    // It defines a handful of frame types that are mapped onto WebSockets frames
    // e.g., CONNECT, SUBSCRIBE, UNSUBSCRIBE, ACK, or SEND
    private WebSocket webSocket;
    private final String SERVER_PATH = "wss://javaweb-server.herokuapp.com/ws";
    private StompClient mStompClient;
    private final String uniqueID = UUID.randomUUID().toString();
    private Disposable mRestPingDisposable;
    private final SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private CompositeDisposable compositeDisposable;



    int heightOriginal,
            widthOriginal,
            heightFrame,
            widthFrame,
            leftFrame,
            topFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        String uniqueID = UUID.randomUUID().toString();
        //mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, SERVER_PATH);

        initiateSocketConnection();
    }

    public void disconnectStomp(View view) {
        mStompClient.disconnect();
    }

    @Override
    protected void onDestroy() {
        mStompClient.disconnect();
        super.onDestroy();
    }


    private void initiateSocketConnection() {
        //OkHttpClient client = new OkHttpClient();
        //Request request = new Request.Builder().url(SERVER_PATH).build();
        // webSocket= client.newWebSocket(request,new SocketListener());
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, SERVER_PATH);
        mStompClient.connect(null);

        //mStompClient.withClientHeartbeat(1000).withServerHeartbeat(1000);
        resetSubscriptions();
        Disposable dispLifecycle = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            toast("Stomp connection opened");
                            initializeView();
                            break;
                        case ERROR:
                            Log.e("stomp", "Stomp connection error", lifecycleEvent.getException());
                            toast("Stomp connection error");
                            break;
                        case CLOSED:
                            toast("Stomp connection closed");
                            resetSubscriptions();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            toast("Stomp failed server heartbeat");
                            break;
                    }
                });

        compositeDisposable.add(dispLifecycle);

        // Receive greetings
        Disposable dispTopic = mStompClient.topic("/chatroom/public")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d("stomp", "Received " + topicMessage.getPayload());
                }, throwable -> {
                    Log.e("stomp", "Error on subscribe topic", throwable);
                });

        compositeDisposable.add(dispTopic);



    }

    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    private class SocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            runOnUiThread(() -> {
                Toast.makeText(ImagePreviewActivity.this,
                        "Socket Connection Successful!",
                        Toast.LENGTH_SHORT).show();
                initializeView();
            });
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
        }
    }


    private void initializeView() {
        //
        sendCapture = findViewById(R.id.sendCapture);
        previewImageView = findViewById(R.id.previewImageView);


        //
        Intent intent = getIntent();
        String fileNameSource = intent.getStringExtra(FILE_NAME_TAG);

        heightOriginal = intent.getIntExtra(HEIGHT_ORIGINAL_TAG, 0);
        widthOriginal = intent.getIntExtra(WIDTH_ORIGINAL_TAG, 0);
        heightFrame = intent.getIntExtra(HEIGHT_FRAME_TAG, 0);
        widthFrame = intent.getIntExtra(WIDTH_FRAME_TAG, 0);
        leftFrame = intent.getIntExtra(LEFT_FRAME_TAG, 0);
        topFrame = intent.getIntExtra(TOP_FRAME_TAG, 0);

        Log.d("uri", "filename: " + fileNameSource);
        Log.d("uri", "widthOriginal: " + widthOriginal);
        Log.d("uri", "heightOriginal: " + heightOriginal);
        Log.d("uri", "heightFrame: " + heightFrame);
        Log.d("uri", "widthFrame: " + widthFrame);
        Log.d("uri", "leftFrame: " + leftFrame);
        Log.d("uri", "topFrame: " + topFrame);
        //
        showPreview(fileNameSource);
    }


    private void showPreview(String nameFile) {

        try {
            Uri uriImage = MediaStore.Images.Media.getContentUri("external");
            String[] stringsProjection = {MediaStore.Images.ImageColumns._ID};
            Cursor cursor = getContentResolver().query(uriImage,
                    stringsProjection, MediaStore.Images.ImageColumns.DISPLAY_NAME
                            + " LIKE?", new String[]{nameFile}, null);
            cursor.moveToFirst();
            Uri uriPath = Uri.parse(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() +
                            "/" + cursor.getString(0));
            Bitmap bitmapImage = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                bitmapImage = ImageDecoder.decodeBitmap(ImageDecoder.createSource(
                        this.getContentResolver(),
                        uriPath));
                //
                bitmapImage = cropImage(bitmapImage);
            }

            previewImageView.setImageBitmap(bitmapImage);
            Log.d("uri", "URI method is successful");

            Bitmap finalBitmapImage = bitmapImage;
            sendCapture.setOnClickListener(v -> {
                assert finalBitmapImage != null;
                sendImage(finalBitmapImage);

            });
        } catch (Exception e) {
            Log.d("uri", "Failure in URI method");

        }
    }

    private void sendImage(Bitmap image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        String base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("name", "ok");
            jsonObject.put("image", base64String);
            webSocket.send(jsonObject.toString());

            jsonObject.put("isSent", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Crop a image
     *
     * @param bitmap image to crop
     * @return image already cropped
     */
    private Bitmap cropImage(Bitmap bitmap) {

        int heightReal = bitmap.getHeight(),
                widthReal = bitmap.getWidth(),

                widthFinal = widthFrame * widthReal / widthOriginal,
                heightFinal = heightFrame * heightReal / heightOriginal,
                leftFinal = leftFrame * widthReal / widthOriginal,
                topFinal = topFrame * heightReal / heightOriginal;
        Log.d("uri", "widthFrame: " + widthFrame + " , heightFrame: " + heightFrame);
        Log.d("uri", "widthReal: " + widthReal + " , heightReal: " + heightReal);
        Log.d("uri", "widthFinal: " + widthFinal + " , heightFinal: " + heightFinal
                + ", leftFinal: " + leftFinal + ", topFinal: " + topFinal);

        return Bitmap.createBitmap(bitmap,
                leftFinal,
                topFinal,
                widthFinal,
                heightFinal);

//        val stream = ByteArrayOutputStream()
//        bitmapFinal.compress(
//                Bitmap.CompressFormat.JPEG,
//                100,
//                stream
//        ) //100 is the best quality possible
        //   return stream.toByteArray()

    }

}