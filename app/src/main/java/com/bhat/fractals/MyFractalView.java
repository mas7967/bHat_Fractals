package com.bhat.fractals;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

// Our custom view which extends SurfaceView, meant to handle graphics separately than the
// thread that handles the UI and all the other junk.
public class MyFractalView extends SurfaceView implements Runnable {

    Thread fractalThread = null;
    SurfaceHolder fractalHolder;
    boolean isEverythingOkay = false;

    Bitmap fractalBitmap;
    Bitmap scaledFractalBitmap;
    Fractal myFrac;

    int width;
    int height;
    float scale;

    public MyFractalView(Context context, int w, int h, float s) {
        super(context);
        fractalHolder = getHolder();
        width = w;
        height = h;
        scale = s;
        myFrac = new Fractal(width, height);
    }

    @Override
    public void run() {
        while (isEverythingOkay) {
            // Do some stuff here, like the drawing
            if (!fractalHolder.getSurface().isValid()){
                continue;
            }

            Canvas fractalCanvas = fractalHolder.lockCanvas();
            fractalBitmap = myFrac.updateFractal();
            scaledFractalBitmap = Bitmap.createScaledBitmap(fractalBitmap,
                    Math.round(width * scale), Math.round(height * scale), true);
            fractalCanvas.drawBitmap(scaledFractalBitmap, 0, 0, null);
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

    public Fractal getFractal(){
        return myFrac;
    }
}