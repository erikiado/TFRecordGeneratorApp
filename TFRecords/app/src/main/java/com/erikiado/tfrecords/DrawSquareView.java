package com.erikiado.tfrecords;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by e on 19/11/17.
 */

class DrawSquareView extends android.support.v7.widget.AppCompatImageView {

    private float leftx;
    private float topy;
    private float rightx;
    private float bottomy;

    public DrawSquareView(Context context) {
        super(context);
        leftx = 0;
        topy = 0;
        rightx = 0;
        bottomy = 0;
    }

    DrawSquareView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    DrawSquareView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setSquareSize(float x1, float y1,float x2, float y2){
        leftx = x1;
        topy = y1;
        rightx = x2;
        bottomy = y2;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(leftx != rightx && topy != bottomy){
            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);
            canvas.drawRect(leftx, topy, rightx, bottomy, paint);
        }

    }
}
