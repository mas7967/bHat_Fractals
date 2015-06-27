package com.bhat.fractals;

import android.graphics.Bitmap;
import android.graphics.Color;

public class Fractal {

    int maxIterations = 256;
    float xmin = -2.25f;
    float ymin = -1.50f;
    float zoomFactor = 3.0f;

    int width;
    int height;

    Bitmap fractalBitmap;

    public Fractal(int w, int h) {
        width = w;
        height = h;
        fractalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    public Bitmap updateFractal() {
        float xmax = xmin + zoomFactor;
        float ymax = ymin + zoomFactor*(height/width); // Fix this line to be not terrible

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

        return fractalBitmap;
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

        return Color.rgb(red, green, blue);
    }

    public void setXmin(float x){
        xmin = x;
    }

    public void setYmin(float y){
        ymin = y;
    }

    public void setZoomFactor(float z) {
        zoomFactor = z;
    }

}
