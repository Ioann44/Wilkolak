package com.ioannscorporation.wilkolak;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GameObject {

    public Bitmap image;
    public int width, height, x, y;

    public GameObject(int imageRef, Resources res, int x, int y, int width, int height) {

        this.image = BitmapFactory.decodeResource(res, imageRef);
        image = Bitmap.createScaledBitmap(image, width, height, false);

        this.x = x;
        this.y = y;

        this.width = width;
        this.height = height;
    }
}