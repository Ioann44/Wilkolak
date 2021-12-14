package com.ioannscorporation.wilkolak;

import android.app.Application;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.util.Random;

public class UtilApp extends Application {
    public static Resources res;
    public static Random random = new Random();
    public static int screenX, screenY;
    public static float screenRatioX, screenRatioY;

    public static Bitmap GetSubImage(int fullImageRef, int width, int height, int col, int row) {
        Bitmap fullImage = BitmapFactory.decodeResource(UtilApp.res, fullImageRef);
        return Bitmap.createBitmap(fullImage, height * row, width * col, width, height);
    }

    public static Bitmap FlipImage(Bitmap sourceImage, boolean horizontally, boolean vertically) {
        Matrix matrix = new Matrix();
        float sx = 1, sy = 1;
        if (horizontally)
            sx *= -1;
        if (vertically)
            sy *= -1;
        matrix.preScale(sx, sy);
        return Bitmap.createBitmap(
                sourceImage, 0, 0, sourceImage.getWidth(), sourceImage.getHeight(), matrix, true);
    }
}
