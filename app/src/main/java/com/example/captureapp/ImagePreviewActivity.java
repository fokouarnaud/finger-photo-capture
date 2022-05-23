package com.example.captureapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.clans.fab.FloatingActionButton;

public class ImagePreviewActivity extends AppCompatActivity {

    private FloatingActionButton sendCapture;
    private ImageView previewImageView;
    private LinearLayout previewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        sendCapture = findViewById(R.id.sendCapture);
        previewImageView = findViewById(R.id.previewImageView);
        previewLayout= findViewById(R.id.previewLayout);
    }

    private void showPreview(String nameFile) {

        try{
            Uri uriImage = MediaStore.Images.Media.getContentUri("external");
            String[] stringsProjection = {MediaStore.Images.ImageColumns._ID};
            Cursor cursor = getContentResolver().query(uriImage,
                    stringsProjection, MediaStore.Images.ImageColumns.DISPLAY_NAME
                            + " LIKE?", new  String[]{nameFile}, null);
            cursor.moveToFirst();
            Uri uriPath = Uri.parse(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString()+
                            "/"+cursor.getString(0));
            Bitmap bitmapImage = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                bitmapImage = ImageDecoder.decodeBitmap(ImageDecoder.createSource(
                        this.getContentResolver(),
                        uriPath));
            }
            previewLayout.setVisibility(View.VISIBLE);
            previewImageView.setImageBitmap(bitmapImage);
            Log.d("uri", "URI method is successful");
        }
        catch (Exception e){
            Log.d("uri", "Failure in URI method" );

        }
    }

}