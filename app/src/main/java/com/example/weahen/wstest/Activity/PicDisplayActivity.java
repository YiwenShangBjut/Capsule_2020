package com.example.weahen.wstest.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import com.example.weahen.wstest.R;

public class PicDisplayActivity extends AppCompatActivity {

    private ImageView iv;
    private float beforeScale=1.0f;
    private float nowScale;
    private String picPath;
    private Bitmap bm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_display);
        Intent intent=getIntent();
        picPath=intent.getStringExtra("picPath");
        Log.e("PicDisplayActivity","pic path is "+picPath);
        bm = BitmapFactory.decodeFile(picPath);
        iv = findViewById(R.id.imageView);
        iv.setImageBitmap(bm);
        myScale();
    }

    private void myScale(){
        final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                nowScale = scaleGestureDetector.getScaleFactor() * beforeScale;
                if (nowScale > 3 || nowScale < 1.0) {
                    beforeScale = nowScale;
                    return true;
                }
                Matrix matrix = new Matrix();
                matrix.setScale(nowScale, nowScale);
                Bitmap bm2 = bm;
                bm2 = Bitmap.createBitmap(bm2, 0, 0, bm2.getWidth(), bm2.getHeight(), matrix, true);
                iv.setImageBitmap(bm2);
                beforeScale = nowScale;
                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {

            }
        });
        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                scaleGestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });

    }
}
