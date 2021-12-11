package com.ioannscorporation.wilkolak;

public class Platform extends GameObject {
    public Platform(int imageRef, int x, int y, int width, int height) {
        super(imageRef, x, y, width, height);
    }

    public Platform(int imageRef, int x, int y, int width_height) {
        super(imageRef, x, y, width_height, width_height);
    }

    public Platform(int x, int y) {
        this(R.drawable.forest_bit, x, y, 150);
    }
}
