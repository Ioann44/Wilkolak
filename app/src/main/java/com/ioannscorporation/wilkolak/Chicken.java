package com.ioannscorporation.wilkolak;

import java.util.ArrayList;

public class Chicken extends AdvancedAlive {
    int wasOnGroundI = 0; // счётчик кадров от последнего касания земли. Летающим недостаточно ySpeedIsLow

    Chicken(int x, int y) {
        super(R.drawable.chicken, x, y, 200, 200, 3, 3);
        acceleration = 1f;
        gravity = -0.3f;
        jumpPower = 0.6f;
        maxSpeedX = 10;
        maxSpeedY = 10;
    }

    @Override
    public void UpdateImage() {
        if (readyToJump)
            wasOnGroundI = 5;
        wasOnGroundI = Math.max(0, wasOnGroundI - 1);

        animNum = (animNum + 1) % (cols * animDelay);
        int animI = animNum / animDelay;
        boolean ySpeedIsLow = wasOnGroundI != 0; // поправка для летающего животного
        if (speedX > 0)
            durIsRight = 1;
        else if (speedX < 0)
            durIsRight = 0;

//        if (UtilApp.gameModeIsMP ) {
//            image = images[durIsRight][actionAnimIndex][animI];
//            return;
//        }

        if (ySpeedIsLow) {
            if (speedX != 0) {
                image = images[durIsRight][0][animI];
            } else {
                image = images[durIsRight][0][1];
            }
        } else {
            image = images[durIsRight][1][animI];
        }
    }

    @Override
    public void Move(ArrayList<Platform>[] platforms, int plMaxCol, int plWidth) {
        readyToJump = true; // пока что курица может летать вечно
        super.Move(platforms, plMaxCol, plWidth);
    }
}
