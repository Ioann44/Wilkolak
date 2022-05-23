package com.ioannscorporation.wilkolak;

import android.app.Application;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ScaleDrawable;

import java.util.Random;

class UtilApp extends Application {
    public static Resources res;
    public static Random random = new Random();
    public static int screenX, screenY;
    public static float screenRatioX, screenRatioY;

    public static Bitmap GetSubImage(int fullImageRef, int width, int height, int col, int row) {
        Bitmap fullImage = BitmapFactory.decodeResource(UtilApp.res, fullImageRef);
        int w = fullImage.getWidth(), h = fullImage.getHeight();
        return Bitmap.createBitmap(fullImage, width * col, height * row, width - 1, height - 1);
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

    public static Bitmap RotateImage(Bitmap sourceImage, float deg) {
        Matrix matrix = new Matrix();
        matrix.postRotate(deg);
        return Bitmap.createBitmap(sourceImage, 0, 0, sourceImage.getWidth(), sourceImage.getHeight(), matrix, true);
    }

    public static boolean gameModeIsMP = false;
    public static WhoIAm whoIAm = WhoIAm.wolf;
    public static int numOfPlayers = 1;
}

enum WhoIAm {
    wolf,
    chicken
}

class Btns {
    public static final short left = 0, up = 1, right = 2;
}
