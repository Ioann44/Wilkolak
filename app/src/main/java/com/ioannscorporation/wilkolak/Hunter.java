package com.ioannscorporation.wilkolak;

import java.util.ArrayList;

public class Hunter extends GameObject {
    float acceleration = 0.75f,
            gravity = -0.5f,
            jumpPower = 20;

    float speedX = 0, speedY = 0, maxSpeed = 15;
    public boolean goLeft = false, goRight = false, readyToJump = true;

    public Hunter(int imageRef, int x, int y) {
        super(imageRef, x, y, 200, 200);
    }

    public Hunter(int x, int y) {
        super(R.drawable.turbo, x, y, 200, 200);
    }

    public void move(ArrayList<Platform>[] platforms, int plMaxCol, int plWidth) {
        //update speed
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


        //fix
        int colMin, colMax;
        colMin = (x - UtilApp.screenX / 2) / plWidth - 1;
        colMin = Math.max(0, colMin);
        colMax = colMin + UtilApp.screenX / plWidth + 2;
        colMax = Math.min(colMax, plMaxCol);

        //move & fix horizontally
        x += speedX;
        for (int j = colMin; j < colMax; j++) {
            for (Platform p : platforms[j]) {
                if (y + height > p.y && y < p.y + p.height && x + width > p.x && x < p.x + p.width) {
                    if (speedX > 0) {
                        x = p.x - width;
                        speedX = 0; //можно имитировать отскакиваение
                        j = colMax; //закончить проверку
                        break;
                    } else if (speedX < 0) {
                        x = p.x + p.width;
                        speedX = 0;
                        j = colMax; //закончить проверку
                        break;
                    }
                }
            }
        }
        //move & fix vertically
        y -= speedY;
        for (int j = colMin; j < colMax; j++) {
            for (Platform p : platforms[j]) {
                if (y + height > p.y && y < p.y + p.height && x + width > p.x && x < p.x + p.width) {
                    if (speedY < 0) {
                        y = p.y - height;
                        readyToJump = true;
                        speedY = 0;
                        j = colMax; //закончить проверку
                        break;
                    } else if (speedY > 0) {
                        y = p.y + p.height;
                        speedY = 0;
                        j = colMax; //закончить проверку
                        break;
                    }
                }
            }
        }

        //check the main borders
        if (x < 0) {
            x = 0;
            speedX = 0;
        } else if (x + width > plMaxCol * plWidth) {
            x = plMaxCol * plWidth - width;
        }
        if (y < 0) {
            y = 0;
            speedY = 0;
        } //проверки на падение вниз пока нет :)
    }
}
