package com.example.captureapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CameraOverlayView extends View {
    public final static float PADDING = 0.5f;
    // public final static float PADDING = 0f;


    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint innerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF overlayRect = new RectF();
    private final Rect overlaySimpleRect = new Rect();

    public CameraOverlayView(Context context) {
        super(context);
        initPaints();
    }

    public CameraOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    public CameraOverlayView(Context context, AttributeSet attrs,
                             int defStyle) {
        super(context, attrs, defStyle);
        initPaints();
    }

    @Override
    public void onDraw(Canvas canvas) {
        overlayRect.set(0.0f,
                280.0f,
                520.0f,
                520.0f);

        overlaySimpleRect.set(Math.round(0.0f),
                Math.round(280.0f),
                Math.round(520.0f),
                Math.round(520.0f));

        //canvas.drawOval(overlayRect, innerPaint);
        // canvas.drawOval(overlayRect, borderPaint);
        canvas.drawRect(overlaySimpleRect, innerPaint);
        canvas.drawRect(overlaySimpleRect, borderPaint);
    }

    private void initPaints() {
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(5);
        borderPaint.setShadowLayer(12f, 0, 0, Color.GREEN);

        innerPaint.setARGB(0, 0, 0, 0);
        innerPaint.setStyle(Paint.Style.FILL);

        setLayerType(LAYER_TYPE_SOFTWARE, borderPaint);
    }
}

