package com.ioannscorporation.wilkolak;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class AdvancedAlive extends SimpleAlive {
    Bitmap[][][] images;
    int rows, cols;
    int animNum = 0;
    int animDelay = 10; //frames
    byte durIsRight = 1; //1 - true, 0 - false

    public AdvancedAlive(int imageRef, int x, int y, int width, int height, int rows, int cols) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rows = rows;
        this.cols = cols;

        Bitmap fullImage = BitmapFactory.decodeResource(UtilApp.res, imageRef);
        fullImage = Bitmap.createScaledBitmap(fullImage, width * cols, height * rows, false);
        images = new Bitmap[2][rows][cols];
        Matrix matrix = new Matrix();
        matrix.preScale(-1f, 1f);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                images[1][i][j] = Bitmap.createBitmap(fullImage, j * width, i * height, width, height);
                images[0][i][j] = Bitmap.createBitmap(images[1][i][j], 0, 0, width, height, matrix, true);
            }
        }
        image = images[0][0][0];
    }

    public void UpdateImage() {
        animNum = (animNum + 1) % ((rows + 1) * animDelay);
        int animI = animNum / animDelay;
        boolean ySpeedIsLow = Math.abs(speedY) < 5;
        if (speedX > 0)
            durIsRight = 1;
        else if (speedX < 0)
            durIsRight = 0;

        if (ySpeedIsLow) {
            if (speedX != 0) {
                image = images[durIsRight][0][animI];
            } else {
                image = images[durIsRight][1][1];
            }
        } else {
            if (speedY > 0) {
                image = images[durIsRight][1][0];
            } else {
                image = images[durIsRight][1][2];
            }
        }
    }
}
