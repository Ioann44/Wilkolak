package com.ioannscorporation.wilkolak;

import android.content.res.Resources;
import android.graphics.Bitmap;

public class Hunter extends GameObject {
    float acceleration = 0.75f,
            gravity = -0.5f,
            jumpPower = 20;

    float speedX = 0, speedY = 0, maxSpeed = 15;
    public boolean goLeft = false, goRight = false, readyToJump = true;

    public Hunter(int imageRef, Resources res, int x, int y) {
        super(imageRef, res, x, y, 200, 200);
    }

    public void move() {
        speedY += gravity;

        if (goLeft && goRight) { // is jumping
            if (readyToJump) {
                speedY = jumpPower;
                readyToJump = false;
            }
        } else if (goLeft) {
            speedX = Math.max(-maxSpeed, speedX - acceleration);
        } else if (goRight) {
            speedX = Math.min(maxSpeed, speedX + acceleration);
        } else {
            if (speedX > 0) {
                speedX = Math.max(0, speedX - acceleration / 2);
            } else {
                speedX = Math.min(0, speedX + acceleration / 2);
            }
        }

        x += speedX;
        y -= speedY;


        if (y >= 880) {
            y = 880;
            readyToJump = true;
        }
    }
}
