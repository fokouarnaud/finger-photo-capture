package com.example.captureapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.clans.fab.FloatingActionButton;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener {

    // variable declaration
    public static final String TAG = "MainActivity";
    private CameraView mOpenCvCameraView;
    private CameraOverlayView mOverlayView;
    private SurfaceView mCameraProcessPreview;
    private FloatingActionButton takePictureButton;
    Mat mRGBA, mRGBAT;
    private Mat mRgba;
    private Mat mGray;

    // check openCV installation
    static {

        if (OpenCVLoader.initDebug()) {

            Log.d("Check", "OpenCv configured successfully");

        } else {

            Log.d("Check", "OpenCv doesn’t configured successfully");

        }

    }

    // define loader callback to enable cameraView
    private final BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                mOpenCvCameraView.enableView();
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    private final Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        public void onPictureTaken(final byte[] data, Camera camera) {
            AsyncTask<byte[], Integer, Bitmap> imProcessTask = new AsyncTask<byte[], Integer, Bitmap>() {


                private void enableButtons(boolean enable) {
                    takePictureButton.setIndeterminate(!enable);
                    takePictureButton.setEnabled(enable);
                }

                @Override
                protected void onPreExecute() {
                    enableButtons(false);
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                protected Bitmap doInBackground(byte[]... params) {
                    byte[] imageData = params[0];

                    ImageProcessing p = new ImageProcessing(imageData, MainActivity.this);
                    Bitmap result = p.getProcessedImage();
                    return result;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    enableButtons(true);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Capture Image");
                    builder.setMessage("image is capture , now save in galleries..");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(getApplicationContext(),"TODO: save to galleries",Toast.LENGTH_SHORT).show();
                        }
                    });
                    AlertDialog dialog = builder.show();

                    Button pbutton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setTextColor(Color.MAGENTA);
                    dialog.setCanceledOnTouchOutside(false);

                    // Must call show() prior to fetching text view
                    TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
                    messageView.setGravity(Gravity.CENTER);

                }
            };
            imProcessTask.execute(data);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            }

        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);


        mOpenCvCameraView = (CameraView) findViewById(R.id.camera_preview);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setPictureListener(pictureCallback);

        takePictureButton = (FloatingActionButton) findViewById(R.id.btn_takepicture);
        assert takePictureButton != null;
        takePictureButton.hideProgress();
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpenCvCameraView.takePicture();
            }
        });


        mOverlayView = (CameraOverlayView) findViewById(R.id.overlay);
        assert mOverlayView != null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        mRGBA = new Mat(width, height, CvType.CV_8UC4);
        mRgba=new Mat(height,width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRGBA.release();
    }

    @Override
    public Mat onCameraFrame(Mat inputFrame) {
       /* mRGBA = inputFrame;
        mRGBAT = mRGBA.t();
        Core.flip(mRGBA.t(), mRGBAT, 1);
        Imgproc.resize(mRGBAT, mRGBAT, mRGBA.size());
        return mRGBAT;
       mRgba=inputFrame;*/
        return inputFrame;
    }
}