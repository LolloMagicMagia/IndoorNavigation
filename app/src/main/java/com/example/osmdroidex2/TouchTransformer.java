package com.example.osmdroidex2;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.github.chrisbanes.photoview.PhotoView;

public class TouchTransformer {

    public float transformX(float touchCoordinate, PhotoView mapImage, Bitmap mapBitmap) {
        Matrix matrix = new Matrix();
        mapImage.getSuppMatrix(matrix);
        float[] matrixValues = new float[9];
        matrix.getValues(matrixValues);
        float scaleFactor1 = matrixValues[Matrix.MSCALE_X];
        float transX = matrixValues[Matrix.MTRANS_X];
        float imageX = (touchCoordinate - transX) / scaleFactor1;
        //
        float viewWidth = mapImage.getWidth();
        float bitmapWidth = mapBitmap.getWidth();
        float scaleFactor2 = Math.min(viewWidth / bitmapWidth, (float) mapImage.getHeight() / mapBitmap.getHeight());
        return (imageX - (viewWidth - bitmapWidth * scaleFactor2) / 2) / scaleFactor2;
    }

    public float transformY(float touchCoordinate, PhotoView mapImage, Bitmap mapBitmap) {
        Matrix matrix = new Matrix();
        mapImage.getSuppMatrix(matrix);
        float[] matrixValues = new float[9];
        matrix.getValues(matrixValues);
        float scaleFactor1 = matrixValues[Matrix.MSCALE_Y];
        float transY = matrixValues[Matrix.MTRANS_Y];
        float imageY = (touchCoordinate - transY) / scaleFactor1;
        //
        float viewHeight = mapImage.getHeight();
        float bitmapHeight = mapBitmap.getHeight();
        float scaleFactor2 = Math.min((float) mapImage.getWidth() / mapBitmap.getWidth(), viewHeight / bitmapHeight);
        return (imageY - (viewHeight - bitmapHeight * scaleFactor2) / 2) / scaleFactor2;
    }
}

