package com.example.captureapp.ui;

import static com.example.captureapp.util.Constants.HEIGHT_FRAME_TAG;
import static com.example.captureapp.util.Constants.HEIGHT_ORIGINAL_TAG;
import static com.example.captureapp.util.Constants.LEFT_FRAME_TAG;
import static com.example.captureapp.util.Constants.TOP_FRAME_TAG;
import static com.example.captureapp.util.Constants.WIDTH_FRAME_TAG;
import static com.example.captureapp.util.Constants.WIDTH_ORIGINAL_TAG;

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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.captureapp.R;
import com.example.captureapp.util.Constants;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class ImagePreviewActivity extends AppCompatActivity {

    private FloatingActionButton sendCapture;
    private ImageView previewImageView;


    // connection can be slow with long runtime server processing
    // so we have to negotiate the connection every time
    // ( long polling: not long than 40 second [or any amount of time] to respond a request)
    // because of that we constantly reestablishing a connection with the server (short polling)
    // webSocket provide a full-duplex connection to avoid the behaviour
    // of constantly reestablishing connection

    // we can use STOMP protocol for effective client-server communication with webSocket.
    // It defines a handful of frame types that are mapped onto WebSockets frames
    // e.g., CONNECT, SUBSCRIBE, UNSUBSCRIBE, ACK, or SEND

    private final String SERVER_PATH = "wss://javaweb-server.herokuapp.com/ws";
    private StompClient mStompClient;
    private final String uniqueID = UUID.randomUUID().toString();
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
        initializeView();

    }



    private void initializeView() {

        //
        sendCapture = findViewById(R.id.sendCapture);
        previewImageView = findViewById(R.id.previewImageView);

        // get activity params
        Intent intent = getIntent();
        String fileNameSource = intent.getStringExtra(Constants.FILE_NAME_TAG);

        heightOriginal = intent.getIntExtra(HEIGHT_ORIGINAL_TAG, 0);
        widthOriginal = intent.getIntExtra(WIDTH_ORIGINAL_TAG, 0);
        heightFrame = intent.getIntExtra(HEIGHT_FRAME_TAG, 0);
        widthFrame = intent.getIntExtra(WIDTH_FRAME_TAG, 0);
        leftFrame = intent.getIntExtra(LEFT_FRAME_TAG, 0);
        topFrame = intent.getIntExtra(TOP_FRAME_TAG, 0);

        //
        showPreview(fileNameSource);
    }


    private void showPreview(String nameFile) {

        try {

            // get file from gallery
            Uri uriImage = MediaStore.Images.Media.getContentUri("external");
            String[] stringsProjection = {MediaStore.Images.ImageColumns._ID};
            Cursor cursor = getContentResolver().query(uriImage,
                    stringsProjection, MediaStore.Images.ImageColumns.DISPLAY_NAME
                            + " LIKE?", new String[]{nameFile}, null);
            cursor.moveToFirst();

            //
            String filePath=  MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() +
                    "/" + cursor.getString(0);
            Uri uriPath = Uri.parse(filePath);

            Bitmap bitmapImage = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                bitmapImage = ImageDecoder.decodeBitmap(ImageDecoder.createSource(
                        this.getContentResolver(),
                        uriPath));
                //
                bitmapImage = cropImage(bitmapImage);
            }

            previewImageView.setImageBitmap(bitmapImage);

            Bitmap finalBitmapImage = bitmapImage;


            // add action on view
            sendCapture.setOnClickListener(v -> {
                assert finalBitmapImage != null;
                sendImage(finalBitmapImage);

            });

            //delete file
            File fileToDelete = new File(getFilePath(uriPath));

            if (fileToDelete.exists()) {
                if (fileToDelete.delete()) {
                    Log.d("delete", "file delete!");
                } else {
                    Log.d("delete", "file not delete...");
                }
            }
        } catch (Exception e) {
            Log.d("uri", "Failure in URI method");

        }
    }

    //getting real path from uri
    private String getFilePath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(projection[0]);
            String picturePath = cursor.getString(columnIndex); // returns null
            cursor.close();
            return picturePath;
        }
        return null;
    }


    private void sendMessage(String base64String) {
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("senderName", uniqueID);
            jsonObject.put("receiverName", uniqueID);
            jsonObject.put("status", "MESSAGE");
            jsonObject.put("message", base64String);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendImage(Bitmap image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        String base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        sendMessage(base64String);
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

        return Bitmap.createBitmap(bitmap,
                leftFinal,
                topFinal,
                widthFinal,
                heightFinal);

    }

}