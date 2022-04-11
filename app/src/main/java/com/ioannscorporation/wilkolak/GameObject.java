package com.ioannscorporation.wilkolak;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GameObject {

    public Bitmap image;
    public int width, height, x, y;

    public GameObject(Bitmap image, int x, int y, int width, int height) {
        this.image = Bitmap.createScaledBitmap(image, width, height, false);

        this.x = x;
        this.y = y;

        this.width = width;
        this.height = height;
    }

    public GameObject(int imageRef, int x, int y, int width, int height) {
        this(BitmapFactory.decodeResource(UtilApp.res, imageRef), x, y, width, height);
    }

    public GameObject() {
    }

    public boolean IsCollide(GameObject obj) {
        return (x + width > obj.x && x < obj.x + obj.width) &&
                (y + height > obj.y && y < obj.y + obj.height);
    }

    public boolean IsCollide(int x, int y) {
        return (this.x <= x && x <= this.x + this.width &&
                this.y <= y && y <= this.y + this.height);
    }

    //Для оптимизации вывода на экран,
    //позволяет отсечь объекты, находящиеся на значительном расстоянии,
    //отсекает НЕ ВСЕ не попадающие в поле зрения объекты.
    //Может работать неправильно если размеры объектов соизмеримы с экраном.
    //В данном случае упор сделан на наглядность и скорость.
    public boolean IsInView(GameObject obj) {
        return Math.abs(x - obj.x) < UtilApp.screenX << 1 &&
                Math.abs(y - obj.y) < UtilApp.screenY << 1;
    }
}