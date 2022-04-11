package com.ioannscorporation.wilkolak;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Button extends GameObject {
    public Bitmap[] images = new Bitmap[2];
    public boolean isPressed = false;

    public Button(int imageRef, int x, int y, int widthHeight, float rotation) {
        this.x = x;
        this.y = y;

        this.width = widthHeight;
        this.height = widthHeight;

        int bitmapHeight = BitmapFactory.decodeResource(UtilApp.res, imageRef).getHeight();
        for (int i = 0; i < 2; i++) {
            images[i] = UtilApp.GetSubImage(imageRef, bitmapHeight, bitmapHeight, i, 0);
            images[i] = UtilApp.RotateImage(images[i], rotation);
            images[i] = Bitmap.createScaledBitmap(images[i], widthHeight, widthHeight, true);
        }
        image = images[0];
    }

    public boolean IsCollide(int x, int y) {
        if (super.IsCollide(x, y)) {
            isPressed = true;
            return true;
        } else {
            return false;
        }
    }

    public void UpdateAndReset() {
        image = images[isPressed ? 1 : 0];
        isPressed = false;
    }
}
