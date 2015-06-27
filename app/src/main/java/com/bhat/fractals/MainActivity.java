package com.bhat.fractals;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends Activity implements View.OnTouchListener {

    MyFractalView fractalView;
    Fractal myFractal;

    int width = 360;  // These are not correct, just a placeholder for now
    int height = 640;
    float scale = 3.0f;

    float xloc = 0f;
    float yloc = 0f;

    public boolean onTouch(View v, MotionEvent m) {

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        myFractal = fractalView.getFractal();

        switch(m.getAction()){
            case MotionEvent.ACTION_DOWN:
                xloc = ((m.getX()/scale)/width)*myFractal.zoomFactor + myFractal.xmin;
                yloc = ((m.getY()/scale)/height)*myFractal.zoomFactor + myFractal.ymin;

                myFractal.setZoomFactor(myFractal.zoomFactor/4.0f);
                myFractal.setXmin(xloc - myFractal.zoomFactor/2.0f);
                myFractal.setYmin(yloc - myFractal.zoomFactor / 2.0f);
                break;

            case MotionEvent.ACTION_MOVE:
                //xloc = ((m.getX()/scale)/width)*myFractal.zoomFactor + myFractal.xmin - myFractal.zoomFactor/2;
                //yloc = ((m.getY()/scale)/height)*myFractal.zoomFactor + myFractal.ymin - myFractal.zoomFactor/2;

                myFractal.setXmin(-2.25f);
                myFractal.setYmin(-1.50f);
                myFractal.setZoomFactor(3.0f);
                break;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fractalView = new MyFractalView(this, width, height, scale);
        fractalView.setOnTouchListener(this);
        setContentView(fractalView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        fractalView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fractalView.resume();
    }

}
