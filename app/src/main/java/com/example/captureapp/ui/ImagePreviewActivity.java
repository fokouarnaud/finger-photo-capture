package com.example.captureapp.ui;

import static com.example.captureapp.util.Constants.ACTION_PROCESSING;
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
import com.example.captureapp.data.network.APIService;
import com.example.captureapp.data.network.ApiUtils;
import com.example.captureapp.data.network.model.CandidatResult;
import com.example.captureapp.util.Constants;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImagePreviewActivity extends AppCompatActivity {

    private FloatingActionButton sendCapture;
    private ImageView previewImageView;
    private APIService mAPIService;
    String currentSourceActivity;
    public static final String TAG = "ImagePreviewActivity";
    Integer current=0;
    String tempTaskId="";




    private final String uniqueID = UUID.randomUUID().toString();
    private final SimpleDateFormat mTimeFormat = new SimpleDateFormat(
            "HH:mm:ss",
            Locale.getDefault());


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
            String filePath = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() +
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

        Cursor cursor = getContentResolver().query(uri,
                projection,
                null,
                null,
                null);

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
        mAPIService = ApiUtils.getAPIService();
        sendImageForProcessing(currentSourceActivity,ACTION_PROCESSING, base64String);

    }

    public void sendImageForProcessing(String currentSourceActivity, String action, String img) {
        toast("send: "+action);
        mAPIService.processingFingerphoto(action,img)
                .enqueue(new Callback<CandidatResult>() {
                    @Override
                    public void onResponse(Call<CandidatResult> call,
                                           Response<CandidatResult> response) {

                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            //toast(response.body().toString());
                            Log.d(TAG, "post submitted to API." + response.body().toString());
                            // ImageSingleton.byteArrayImage= Base64.decode(response.body().getImg(),
                            //         Base64.DEFAULT);
                            // ImageSingleton.descriptors =response.body().getDescriptors();
                            //ImageSingleton.keypoints="";

                            // loop stops
                            //sendBackResult();

                        }
                    }

                    @Override
                    public void onFailure(Call<CandidatResult> call, Throwable t) {
                        Log.e(TAG, "Unable to submit post to API.");
                      //Intent intent = new Intent();
                       // setResult(RESULT_CANCELED, intent);
                      //  finish();
                    }
                });
    }




    private void sendImage(Bitmap image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        String base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        toast("call send message");
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