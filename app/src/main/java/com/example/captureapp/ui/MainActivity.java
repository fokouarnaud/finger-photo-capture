package com.example.captureapp.ui;

import static com.example.captureapp.util.Constants.FILE_NAME_TAG;
import static com.example.captureapp.util.Constants.HEIGHT_FRAME_TAG;
import static com.example.captureapp.util.Constants.HEIGHT_ORIGINAL_TAG;
import static com.example.captureapp.util.Constants.LEFT_FRAME_TAG;
import static com.example.captureapp.util.Constants.TOP_FRAME_TAG;
import static com.example.captureapp.util.Constants.WIDTH_FRAME_TAG;
import static com.example.captureapp.util.Constants.WIDTH_ORIGINAL_TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.captureapp.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity implements ImageAnalysis.Analyzer,
        View.OnClickListener {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    PreviewView previewView;
    private View cameraOverlayView;
    private ImageCapture imageCapture;
    private FloatingActionButton bCapture;
    private int heightOriginal,
            widthOriginal,
            heightFrame,
            widthFrame,
            leftFrame,
            topFrame;


    @Override
    protected void onPostResume() {
        super.onPostResume();

        //
        heightOriginal = previewView.getHeight();
        widthOriginal = previewView.getWidth();
        heightFrame = cameraOverlayView.getHeight();
        widthFrame = cameraOverlayView.getWidth();
        leftFrame = cameraOverlayView.getLeft();
        topFrame = cameraOverlayView.getTop();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check persmission
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);

        // initialize view
        previewView = findViewById(R.id.previewView);
        bCapture = findViewById(R.id.bCapture);
        cameraOverlayView = findViewById(R.id.cameraOverlayView);


        // add action on view
        bCapture.setOnClickListener(this);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, getExecutor());

    }

    Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    @SuppressLint("RestrictedApi")
    private void startCameraX(ProcessCameraProvider cameraProvider) {

        // init camera
        cameraProvider.unbindAll();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        Preview preview = new Preview.Builder()
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Image capture use case
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();


        // Image analysis use case
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(getExecutor(), this);


        //auto-focus disable use case
//        MeteringPointFactory factory =   new SurfaceOrientedMeteringPointFactory(
//                previewView.getWidth(), previewView.getHeight());
//        MeteringPoint autoFocusPoint = factory.createPoint(1, 1, 1);
//
//        FocusMeteringAction action = new FocusMeteringAction.Builder(autoFocusPoint,
//                FocusMeteringAction.FLAG_AF).disableAutoCancel().build();

        //bind to lifecycle:
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this,
                cameraSelector,
                preview,
                imageCapture);


        //camera.getCameraControl().startFocusAndMetering(action);

    }

    @Override
    public void analyze(@NonNull ImageProxy image) {
        // image processing here for the current frame
        image.close();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bCapture) {
            capturePhoto();
        }
    }

    private void enableButtons(boolean enable) {
        bCapture.setIndeterminate(!enable);
        bCapture.setEnabled(enable);
    }

    private void capturePhoto() {
        long timestamp = System.currentTimeMillis();
        enableButtons(false);
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");


        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(
                        getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                ).build(),
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults
                                                     outputFileResults) {
                        Toast.makeText(MainActivity.this,
                                "Photo has been saved successfully.",
                                Toast.LENGTH_SHORT).show();
                        enableButtons(true);


                        Intent myIntent = new Intent(MainActivity.this,
                                ImagePreviewActivity.class);
                        myIntent.putExtra(FILE_NAME_TAG, timestamp + ".jpg");
                        myIntent.putExtra(HEIGHT_ORIGINAL_TAG, heightOriginal);
                        myIntent.putExtra(WIDTH_ORIGINAL_TAG, widthOriginal);
                        myIntent.putExtra(HEIGHT_FRAME_TAG, heightFrame);
                        myIntent.putExtra(WIDTH_FRAME_TAG, widthFrame);
                        myIntent.putExtra(LEFT_FRAME_TAG, leftFrame);
                        myIntent.putExtra(TOP_FRAME_TAG, topFrame);

                        startActivity(myIntent);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(MainActivity.this,
                                "Error saving photo: " + exception.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }


}