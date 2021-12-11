package com.ioannscorporation.wilkolak;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GameObject {

    public Bitmap image;
    public int width, height, x, y;

    public GameObject(int imageRef, int x, int y, int width, int height) {

        this.image = BitmapFactory.decodeResource(UtilApp.res, imageRef);
        image = Bitmap.createScaledBitmap(image, width, height, false);

        this.x = x;
        this.y = y;

        this.width = width;
        this.height = height;
    }

    public boolean IsCollide(GameObject obj) {
        return (x + width > obj.x && x < obj.x + obj.width) &&
                (y + height > obj.y && y < obj.y + obj.height);
    }
}