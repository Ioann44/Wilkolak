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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

@SuppressLint("ViewConstructor")
public class GameView extends SurfaceView implements Runnable {

    Thread thread;
    boolean isPlaying;
    Paint paint;
    SharedPreferences prefs;
    GameActivity activity;
    long delayTime = 22; // 1000 / fps
    long lastTime;
    boolean[] touchedIndexes = new boolean[10];

    GameObject background;
    Hunter hunter;
    ArrayList<Platform>[] platforms;
    final int plMaxCol;
    final int plWidth = 150;

    public GameView(GameActivity activity, int screenX, int screenY) {
        super(activity);

        this.activity = activity;
        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE);

        UtilApp.screenX = screenX;
        UtilApp.screenY = screenY;
        UtilApp.screenRatioX = 1920f / screenX;
        UtilApp.screenRatioY = 1080f / screenY;

        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.WHITE);

//        background = new GameObject(R.drawable.background, 0, 0, screenX, screenY);
        background = new GameObject(R.drawable.background_forest, 0, 0, screenX, screenY);
        hunter = new Hunter(150, 150);
        platforms = loadLevel(R.raw.level);
        plMaxCol = platforms.length;

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

    void update() {
        hunter.move(platforms, plMaxCol, plWidth);
    }

    void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
//            canvas.drawText(score + "", screenX / 2f, 164, paint);

            canvas.drawBitmap(background.image, background.x, background.y, paint);
            for (ArrayList<Platform> plArr : platforms) {
                for (Platform p : plArr) {
                    canvas.drawBitmap(p.image, p.x, p.y, paint);
                }
            }
            canvas.drawBitmap(hunter.image, hunter.x, hunter.y, paint);

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    ArrayList<Platform>[] loadLevel(int linkLevelFile) {
        int width = plWidth;
        ArrayList<Platform>[] res;

        InputStream in = UtilApp.res.openRawResource(linkLevelFile);
        Scanner sc = new Scanner(in);

        int n, m;
        n = sc.nextInt();
        m = sc.nextInt();
        res = new ArrayList[m];
        for (int j = 0; j < m; j++) {
            res[j] = new ArrayList<>(); //без этой инициализации не работает
        }
        //сейчас считанные строки не хранятся отдельно, лучше сначала всех их сохранить,
        //а потом на основе считанной матрицы создавать платформы с нужным изображением
        for (int i = 0; i < n; i++) {
            char[] strArr = sc.next().toCharArray();
            for (int j = 0; j < m; j++) {
                if (strArr[j] == 'o') {
                    res[j].add(new Platform(R.drawable.forest_bit, j * width, i * width, width));
                }
            }
        }
        return res;
    }

    void sleep() {
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
        if (action == MotionEvent.ACTION_UP) {
            return true;
        }
        touchedIndexes[pointerIndex] = action != MotionEvent.ACTION_POINTER_UP;
        for (int i = 0; i < pointerCount; i++) {
            Point touchLoc = new Point((int) event.getX(i), (int) event.getY(i));
            if (touchedIndexes[i]) {
                if (touchLoc.x < UtilApp.screenX / 2f) {
                    hunter.goLeft = true;
                } else {
                    hunter.goRight = true;
                }
            }
        }
        return true;
    }
}