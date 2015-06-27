package com.bhat.fractals;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View;


public class MainActivity extends Activity implements View.OnTouchListener {

    MyFractalView fractalView;

    int maxIterations = 256;
    int width = 800;  //These are not correct, somehow pull them from the phone
    int height = 800;

    float xmin = -2.25f;
    float ymin = -1.5f;
    float zoomFactor = 3.0f;

    float xloc = 0f;
    float yloc = 0f;

    Bitmap fractalBitmap;

    // Our custom view which extends SurfaceView, meant to handle graphics separately than the
    // thread that handles the UI and all the other junk.
    public class MyFractalView extends SurfaceView implements Runnable {

        Thread fractalThread = null;
        SurfaceHolder fractalHolder;
        boolean isEverythingOkay = false;

        public MyFractalView(Context context) {
            super(context);
            fractalHolder = getHolder();
        }

        @Override
        public void run() {
            while (isEverythingOkay) {
                // Do some stuff here, like the drawing
                if (!fractalHolder.getSurface().isValid()){
                    continue;
                }

                Canvas fractalCanvas = fractalHolder.lockCanvas();
                updateFractal();
                fractalCanvas.drawBitmap(fractalBitmap, 0, 0, null);
                fractalHolder.unlockCanvasAndPost(fractalCanvas);
            }
        }

        public void pause() {
            isEverythingOkay = false;
            while(true){
                try {
                    fractalThread.join();
                } catch( InterruptedException e){
                    e.printStackTrace();
                }
                break;
            }
            fractalThread = null;
        }

        public void resume() {
            isEverythingOkay = true;
            fractalThread = new Thread(this);
            fractalThread.start();
        }
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

    public boolean onTouch(View v, MotionEvent m) {

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        switch(m.getAction()){
            case MotionEvent.ACTION_UP:
                xloc = (m.getX()/width)*zoomFactor + xmin;
                yloc = (m.getY()/height)*zoomFactor + ymin;
                zoomFactor*=.25;

                xmin = xloc - zoomFactor/2;
                ymin = yloc - zoomFactor/2;
                break;

            case MotionEvent.ACTION_MOVE:
                //xmin = (m.getX()/width)*zoomFactor + xmin;// - zoomFactor/2;
                //ymin = (m.getY()/height)*zoomFactor + ymin;// - zoomFactor/2;
                break;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fractalView = new MyFractalView(this);
        fractalView.setOnTouchListener(this);
        setContentView(fractalView);

        //DisplayMetrics metrics = new DisplayMetrics();
        //getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //width = metrics.widthPixels;
        //height = metrics.widthPixels;

        fractalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    public void updateFractal() {
        float xmax = xmin + zoomFactor;
        float ymax = ymin + zoomFactor; // Fix this line to be not terrible

        float dx = (xmax - xmin) / width;
        float dy = (ymax - ymin) / height; // Really think about this and make sure it's correct

        float y = ymin;
        for(int j=0; j<height; j++){

            float x=xmin;
            for(int i=0; i<width; i++){

                // Now iterate z = z^2 + C, cutoff when > 16
                float a = x;
                float b = y;

                float aa = 0;
                float bb = 0;
                float twoab = 0;
                int n = 0;

                while( (n<maxIterations) && (aa+bb < 16) ) {
                    aa = a*a;
                    bb = b*b;
                    twoab = 2*a*b;

                    a = aa - bb + x;
                    b = twoab + y;
                    n++;
                }

                fractalBitmap.setPixel(i, j, getPixel(n));
                x+=dx;
            }

            y+=dy;
        }
    }

    // Crude as shit, works flawlessly, but consider improving....
    public int getPixel(int n){
        int red, green, blue;

        if((n>64)&&(n<128)) {
            n = 64-(n-64);
        } else if((n>=128)&&(n<192)) {
            n=n-128;
        } else if(n > 192) {
            n=64-(n-192);
        }

        // Do red
        if(n>24) {
            red = 255;
        } else {
            red = (int) Math.floor(10.63*n);
        }

        // Do green
        if(n<24) {
            green = 0;
        } else if (n>48) {
            green = 255;
        } else {
            green = (int) Math.floor(-255 + 10.63 * n);
        }

        // Do blue
        if(n<48) {
            blue = 0;
        } else {
            blue = (int) Math.floor(-765 + 15.94*n);
        }

        return Color.rgb(red,green,blue);
    }

}
