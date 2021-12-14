package com.ioannscorporation.wilkolak;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
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
    MediaPlayer mPlayer;
    long delayTime = 22; // 1000 / fps
    long lastTime;
    boolean[] touchedIndexes = new boolean[10];

    GameObject background;
    AdvancedAlive player;
    ArrayList<Platform>[] platforms;
    final int plMaxCol, plMaxRaw;
    final int plWidth = 150;

    public GameView(GameActivity activity, int screenX, int screenY) {
        super(activity);

        this.activity = activity;
        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE);

        UtilApp.screenX = screenX;
        UtilApp.screenY = screenY;
        UtilApp.screenRatioX = 1920f / screenX;
        UtilApp.screenRatioY = 1080f / screenY;

        mPlayer = MediaPlayer.create(activity, R.raw.sound_forest);
        mPlayer.start();

        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.WHITE);

//        background = new GameObject(R.drawable.background, 0, 0, screenX, screenY);
        background = new GameObject(R.drawable.background_forest, 0, 0, screenX, screenY);
//        player = new AdvancedAlive(R.drawable.wolf_black, 200, 200, 340, 200, 2, 3);
        platforms = loadLevel(R.raw.level1);
        plMaxCol = platforms.length;
        plMaxRaw = platforms[0].get(platforms[0].size() - 1).y / plWidth + 1;

        lastTime = System.currentTimeMillis();
    }

    void update() {
        player.Move(platforms, plMaxCol, plWidth);
        player.UpdateImage();
    }

    void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
//            canvas.drawText(score + "", screenX / 2f, 164, paint);

            canvas.drawBitmap(background.image, background.x, background.y, paint);

            int dX = player.x + player.width / 2 - UtilApp.screenX / 2;
            dX = Math.max(dX, 0);
            dX = Math.min(dX, plMaxCol * plWidth - UtilApp.screenX);
            int dY = player.y + player.height / 2 - UtilApp.screenY / 2;
            dY = Math.max(dY, 0);
            dY = Math.min(dY, plMaxRaw * plWidth - UtilApp.screenY);

            int colMin = Math.max((player.x - UtilApp.screenX) / plWidth, 0);
            int colMax = Math.min(colMin + UtilApp.screenX * 2 / plWidth, plMaxCol);
            //отображение платформ (возможна оптимизация:
            // - по горизонтали, сейчас в худшем случае выводится в 2 раза больше столбцов
            // - бинарным поиском по вертикали (на небольших значениях возможно бесполезно)
            for (int j = colMin; j < colMax; j++)
                for (Platform p : platforms[j]) {
                    int toDrawY = p.y - dY;
                    if (toDrawY < -p.height)
                        continue;
                    else if (toDrawY > UtilApp.screenY)
                        break;
                    canvas.drawBitmap(p.image, p.x - dX, p.y - dY, paint);
                }

            canvas.drawBitmap(player.image, player.x - dX, player.y - dY, paint);

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

        //"нарезка" тайлсета
        Bitmap[][] tileSet = new Bitmap[3][3];
        int refToTile = R.drawable.forest_tiles;
        int tileWidth = BitmapFactory.decodeResource(UtilApp.res, refToTile).getWidth() / 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tileSet[i][j] = UtilApp.GetSubImage(refToTile, tileWidth, tileWidth, i, j);
            }
        }

        //чтение матрицы
        char[][] levelMatrix = new char[n][m];
        for (int i = 0; i < n; i++) {
            levelMatrix[i] = sc.next().toCharArray();
        }

        //построение платформ
        byte tlX, tlY; //индексы для получения изображения из тайлсета
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (levelMatrix[i][j] == 'o') {
                    //определение изображения для платформы
                    // - по горизонтали
                    if (j != 0 && levelMatrix[i][j - 1] != 'o')
                        tlX = 0;
                    else if (j != m - 1 && levelMatrix[i][j + 1] != 'o')
                        tlX = 2;
                    else
                        tlX = 1;
                    // - по вертикали
                    if (i != 0 && levelMatrix[i - 1][j] != 'o')
                        tlY = 0;
                    else if (i != n - 1 && levelMatrix[i + 1][j] != 'o')
                        tlY = 2;
                    else
                        tlY = 1;
                    //создание платформы
                    res[j].add(new Platform(tileSet[tlY][tlX], j * width, i * width, width));
                } else if (levelMatrix[i][j] == 'p') {
                    //создание игрока
                    player = new AdvancedAlive(
                            R.drawable.wolf_black, j * width, i * width, 340, 200, 2, 3);
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

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
        mPlayer.start();
    }

    public void pause() {

        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mPlayer.pause();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        int pointerIndex = event.getActionIndex();
        int action = event.getActionMasked();
        player.goLeft = player.goRight = false;
        if (action == MotionEvent.ACTION_UP) {
            return true;
        }
        touchedIndexes[pointerIndex] = action != MotionEvent.ACTION_POINTER_UP;
        for (int i = 0; i < pointerCount; i++) {
            Point touchLoc = new Point((int) event.getX(i), (int) event.getY(i));
            if (touchedIndexes[i]) {
                if (touchLoc.x < UtilApp.screenX / 2f) {
                    player.goLeft = true;
                } else {
                    player.goRight = true;
                }
            }
        }
        return true;
    }
}
