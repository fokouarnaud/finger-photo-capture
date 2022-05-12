package com.example.captureapp;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ImageProcessing {

    public static final String TAG = "ImageProcessing";
    private final byte[] data;
    private final AppCompatActivity mainActivity;


    ImageProcessing(byte[] data, AppCompatActivity mainActivity) {
        this.data = data;
        this.mainActivity = mainActivity;

    }

    Bitmap getProcessedImage() {
        // convert Byte data to Bitmap
        return  bytesToBitmap(data);
    }

    private Bitmap bytesToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    private Mat bytesToMat(byte[] data) {
        // Scale down the image for performance
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        int targetWidth = 1200;
        if (bmp.getWidth() > targetWidth) {
            float scaleDownFactor = (float) targetWidth / bmp.getWidth();
            bmp = Bitmap.createScaledBitmap(bmp,
                    (int) (bmp.getWidth() * scaleDownFactor),
                    (int) (bmp.getHeight() * scaleDownFactor),
                    true);
        }
        Mat BGRImage = new Mat(bmp.getWidth(), bmp.getHeight(), CvType.CV_8UC3);
        Utils.bitmapToMat(bmp, BGRImage);

        return BGRImage;
    }

    private Bitmap mat2Bitmap(Mat src, int code) {
        Mat rgbaMat = new Mat(src.width(), src.height(), CvType.CV_8UC4);
        Imgproc.cvtColor(src, rgbaMat, code, 4);
        Bitmap bmp = Bitmap.createBitmap(rgbaMat.cols(), rgbaMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rgbaMat, bmp);
        return bmp;
    }

}
