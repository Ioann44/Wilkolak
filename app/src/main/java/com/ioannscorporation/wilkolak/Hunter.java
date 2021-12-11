package com.ioannscorporation.wilkolak;

import android.util.Log;

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

        //Сброс возможности прыжка, чтобы не прыгнуть после того как упал с обрыва
        //в итоге стоя на поверхности можно прыгать в половине случаев, т.е. 1 0 1 0 1 0 1 0
        //но это не имеет значения т.к. прыжок можно "зажать"
        readyToJump = false;

        //colMin и colMax определяют необходимые для проверки столбцы
        //для горизонтальной проверки особо широких объектов правильнее использовать только 2 стобца
        //с индексами colMin и colMax (для вертикальной нужны все в этом отрезке)
        //обе проверки можно оптимизировать бинарным поиском (по платформам в столбце)

        //move & fix horizontally
        x += speedX;
        int colMin = x / plWidth, colMax = (x + width) / plWidth;
        for (int j = colMin; j <= colMax; j++) {
            for (Platform p : platforms[j]) {
                if (y + height > p.y && y < p.y + p.height) {
                    if (speedX > 0) {
                        x = p.x - width;
                        speedX = 0; //можно имитировать отскакиваение
                        break;
                    } else if (speedX < 0) {
                        x = p.x + p.width;
                        speedX = 0;
                        break;
                    }
                }
            }
        }
        //move & fix vertically
        y -= speedY;
        colMin = x / plWidth; //повторное вычисление необходимо т.к. после горизонтальной проверки
        colMax = (x + width) / plWidth; // объект мог сдвинуться на значительное расстояние
        for (int j = colMin; j <= colMax; j++) {
            for (Platform p : platforms[j]) {
                if (y + height > p.y && y < p.y + p.height) {
                    if (speedY < 0) {
                        y = p.y - height;
                        readyToJump = true;
                        speedY = 0;
                        break;
                    } else if (speedY > 0) {
                        y = p.y + p.height;
                        speedY = 0;
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
