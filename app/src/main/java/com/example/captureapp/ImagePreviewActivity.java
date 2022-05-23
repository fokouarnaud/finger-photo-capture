package com.example.captureapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.clans.fab.FloatingActionButton;

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
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                bitmapImage = ImageDecoder.decodeBitmap(ImageDecoder.createSource(
                        this.getContentResolver(),
                        uriPath));
                //
                bitmapImage = cropImage(bitmapImage);
            }

            previewImageView.setImageBitmap(bitmapImage);
            Log.d("uri", "URI method is successful");
        } catch (Exception e) {
            Log.d("uri", "Failure in URI method");

        }
    }

    private Bitmap cropBitmap(Bitmap bmp2) {

        Bitmap bmOverlay = Bitmap.createBitmap(bmp2.getWidth(), bmp2.getHeight(), Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        Canvas canvas = new Canvas(bmOverlay);

        canvas.drawBitmap(bmp2, 0, 0, null);
        Log.d("uri", "width_over: " + bmOverlay.getWidth() + " , height_over: " + bmOverlay.getHeight());
        canvas.drawRect(0, 280, 520, 520, paint);

        Log.d("uri", "width_crop: " + bmOverlay.getWidth() + " , height_crop: " + bmOverlay.getHeight());

        return bmOverlay;
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
        Log.d("uri", "widthFinal: " + widthFinal + " , heightFinal: " +heightFinal
        +", leftFinal: "+ leftFinal+", topFinal: "+ topFinal);

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