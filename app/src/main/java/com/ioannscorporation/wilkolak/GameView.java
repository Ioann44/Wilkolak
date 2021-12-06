package com.ioannscorporation.wilkolak;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.Random;

@SuppressLint("ViewConstructor")
public class GameView extends SurfaceView implements Runnable {

    Thread thread;
    boolean isPlaying;
    public int screenX, screenY;
    public float screenRatioX, screenRatioY;
    Paint paint;
    SharedPreferences prefs;
    Random random;
    GameActivity activity;
    long delayTime = 22; // 1000 / fps
    long lastTime;
    boolean[] touchedIndexes = new boolean[10];

    GameObject background;
    Hunter hunter;

    public GameView(GameActivity activity, int screenX, int screenY) {
        super(activity);

        this.activity = activity;
        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE);

        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 1920f / screenX;
        screenRatioY = 1080f / screenY;

        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.WHITE);

        background = new GameObject(R.drawable.background, getResources(), 0, 0, screenX, screenY);
        hunter = new Hunter(R.drawable.turbo, getResources(), 100, 100);

        random = new Random();
        lastTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    private void update() {
        hunter.move();
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
//            canvas.drawText(score + "", screenX / 2f, 164, paint);

            canvas.drawBitmap(background.image, background.x, background.y, paint);
            canvas.drawBitmap(hunter.image, hunter.x, hunter.y, paint);

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void sleep() {
        long curTime = System.currentTimeMillis();
        try {
            Thread.sleep(Math.max(0, lastTime - curTime + delayTime));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lastTime = curTime;
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void pause() {

        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        int pointerIndex = event.getActionIndex();
        int action = event.getActionMasked();
        hunter.goLeft = hunter.goRight = false;
        if (action == MotionEvent.ACTION_UP){
            return true;
        }
        touchedIndexes[pointerIndex] = action != MotionEvent.ACTION_POINTER_UP;
        for (int i = 0; i < pointerCount; i++) {
            Point touchLoc = new Point((int) event.getX(i), (int) event.getY(i));
            if (touchedIndexes[i]) {
                if (touchLoc.x < screenX / 2f) {
                    hunter.goLeft = true;
                } else {
                    hunter.goRight = true;
                }
            }
        }
        return true;
    }
}
