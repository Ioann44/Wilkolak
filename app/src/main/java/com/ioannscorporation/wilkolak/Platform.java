package com.ioannscorporation.wilkolak;

import android.graphics.Bitmap;

public class Platform extends GameObject {
    public Platform(int imageRef, int x, int y, int width_height) {
        super(imageRef, x, y, width_height, width_height);
    }

    public Platform(Bitmap image, int x, int y, int width_height) {
        super(image, x, y, width_height, width_height);
    }
}
