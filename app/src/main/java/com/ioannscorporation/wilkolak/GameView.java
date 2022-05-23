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
import java.util.Iterator;
import java.util.LinkedList;
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
    int gameTime;
    boolean[] touchedIndexes = new boolean[10];

    GameObject background;
    ArrayList<AdvancedAlive> animals = new ArrayList<>(UtilApp.numOfPlayers);
    AdvancedAlive player;
    ArrayList<Platform>[] platforms;
    final int plMaxCol, plMaxRaw;
    final int plWidth = 150;

    LinkedList<GameObject> cups = new LinkedList<>();
    ArrayList<GameObject> backTrees = new ArrayList<>(),
            frontTrees = new ArrayList<>();

    Button[] buttons = new Button[3];

    boolean showingStartMessage = false; // Сейчас сообщение не нужно
    Bitmap startMessageImage, foodCounterImage;

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
        paint.setTextSize(100);
        paint.setColor(Color.WHITE);

        background = new GameObject(R.drawable.background_forest, 0, 0, screenX, screenY);
        platforms = loadLevel(R.raw.level1);
        plMaxCol = platforms.length;
        plMaxRaw = platforms[0].get(platforms[0].size() - 1).y / plWidth + 1;

        for (int i = 0; i < 3; i++) {
            buttons[i] = new Button(R.drawable.arrow_left_button,
                    0, (int) (UtilApp.screenY * 0.67),
                    (int) (UtilApp.screenY * 0.23), i * 90);
        }
        buttons[Btns.left].x = UtilApp.screenX * 3 / 100;
        buttons[Btns.right].x = UtilApp.screenX * 5 / 100 + buttons[Btns.left].x + buttons[Btns.left].width;
        buttons[Btns.up].x = UtilApp.screenX * 97 / 100 - buttons[Btns.up].width;

        startMessageImage = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(UtilApp.res, R.drawable.start_message),
                UtilApp.screenX, UtilApp.screenY, false);
        foodCounterImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(UtilApp.res, R.drawable.food_counter_icon),
                UtilApp.screenX * 250 / 158 / 15, UtilApp.screenX / 15, false);

        lastTime = System.currentTimeMillis();
        gameTime = 0;
    }

    void update() {
        player.Move(platforms, plMaxCol, plWidth);
        //Сбор кубков
        {
            Iterator<GameObject> it = cups.iterator();
            while (it.hasNext()) {
                if (it.next().IsCollide(player)) {
                    it.remove();
                }
            }
        }
        player.UpdateImage();
        gameTime += delayTime;
    }

    void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();

            canvas.drawBitmap(background.image, background.x, background.y, paint);

            int dX = player.x + player.width / 2 - UtilApp.screenX / 2;
            dX = Math.max(dX, 0);
            dX = Math.min(dX, plMaxCol * plWidth - UtilApp.screenX);
            int dY = player.y + player.height / 2 - UtilApp.screenY / 2;
            dY = Math.max(dY, 0);
            dY = Math.min(dY, plMaxRaw * plWidth - UtilApp.screenY);

            //отображение фоновых деревьев
            for (GameObject t : backTrees) {
                if (player.IsInView(t)) {
                    canvas.drawBitmap(t.image, t.x - dX, t.y - dY, paint);
                }
            }

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

            //отображение целей
            for (GameObject cup : cups) {
                if (player.IsInView(cup))
                    canvas.drawBitmap(cup.image, cup.x - dX, cup.y - dY, paint);
            }

            //отображение игрока
            canvas.drawBitmap(player.image, player.x - dX, player.y - dY, paint);

            //отображение деревьев на переднем плане
            for (GameObject t : frontTrees) {
                if (player.IsInView(t)) {
                    canvas.drawBitmap(t.image, t.x - dX, t.y - dY, paint);
                }
            }

            //отображение кнопок
            for (GameObject button : buttons) {
                canvas.drawBitmap(button.image, button.x, button.y, paint);
            }

            //Вывод значения секундомера
            {
                String timeMin = String.valueOf(gameTime / 60000),
                        timeSec = String.valueOf(gameTime / 1000 % 60),
                        timeHundredthSec = String.valueOf(gameTime % 1000 / 10);
                if (timeHundredthSec.length() == 1) {
                    timeHundredthSec = '0' + timeHundredthSec;
                }
                String timeMsg = "";
                if (!timeMin.equals("0")) {
                    timeMsg = timeMin + ':';
                    if (timeSec.length() == 1) {
                        timeSec = '0' + timeSec;
                    }
                }
                timeMsg += timeSec + '.' + timeHundredthSec;
                int diffX = (timeMsg.length() - 3) * 100;
                canvas.drawText(
                        timeMsg,
                        UtilApp.screenX * 0.9f - diffX,
                        UtilApp.screenY * 0.1f,
                        paint);
            }

            //Вывод сообщения о победе, количества оставшихся целей, стартового сообщения
            if (cups.isEmpty()) {
                canvas.drawText("Вы победили!", (UtilApp.screenX >> 1) - 300, (UtilApp.screenY >> 1) - 50, paint);
                gameTime -= delayTime;
            } else {
                canvas.drawText(String.valueOf(cups.size()), UtilApp.screenX * 5 / 100, UtilApp.screenY * 13 / 100, paint);
                canvas.drawBitmap(foodCounterImage, UtilApp.screenX * 10 / 100, UtilApp.screenY * 3 / 100, paint);
                if (showingStartMessage) {
                    canvas.drawBitmap(startMessageImage, 0, 0, paint);
                    gameTime = -(int) delayTime;
                }
            }

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    ArrayList<Platform>[] loadLevel(int linkLevelFile) {
        int width = plWidth;
        int treeWidth = BitmapFactory.decodeResource(UtilApp.res, R.drawable.trees).getWidth() / 3;
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
                tileSet[i][j] = UtilApp.GetSubImage(refToTile, tileWidth, tileWidth, j, i);
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
                    if (UtilApp.whoIAm == WhoIAm.wolf)
                        player = new AdvancedAlive(R.drawable.wolf_black, j * width, i * width, 340, 200, 2, 3);
                    else if (UtilApp.whoIAm == WhoIAm.chicken)
                        player = new Chicken(j * width, i * width);
                } else if (levelMatrix[i][j] == 'c') {
                    //создание цели
                    if (UtilApp.whoIAm == WhoIAm.wolf)
                        cups.add(new GameObject(R.drawable.wolfs_food, j * width, i * width + (width >> 1), width, width >> 1));
                    else if (UtilApp.whoIAm == WhoIAm.chicken)
                        cups.add(new GameObject(R.drawable.chickens_food, j * width, i * width, width, width));
                } else if (levelMatrix[i][j] == 't') {
                    //создание деревьев на фоне
                    backTrees.add(new GameObject(
                            UtilApp.GetSubImage(R.drawable.trees, treeWidth, treeWidth << 1,
                                    UtilApp.random.nextInt(3),
                                    UtilApp.random.nextInt(2)),
                            j * width, (i - 3) * width, width << 1, width << 2));
                } else if (levelMatrix[i][j] == 'T') {
                    //создание деревьев на переднем плане
                    frontTrees.add(new GameObject(
                            UtilApp.GetSubImage(R.drawable.trees, treeWidth, treeWidth << 1,
                                    UtilApp.random.nextInt(3), 2),
                            j * width, (i - 3) * width, width << 1, width << 2));
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
        showingStartMessage = false;
        int pointerCount = event.getPointerCount();
        int pointerIndex = event.getActionIndex();
        int action = event.getActionMasked();
//        player.goLeft = player.goRight = player.goJump = false;
        touchedIndexes[pointerIndex] = action != MotionEvent.ACTION_POINTER_UP && action != MotionEvent.ACTION_UP;

        for (int i = 0; i < pointerCount; i++) {
            Point touchLoc = new Point((int) event.getX(i), (int) event.getY(i));
            if (touchedIndexes[i]) {
                for (int j = 0; j < 3; j++) {
                    buttons[j].IsCollide(touchLoc.x, touchLoc.y);
                }
//                if (touchLoc.x < UtilApp.screenX / 2f) {
//                    player.goLeft = true;
//                } else {
//                    player.goRight = true;
//                }
            }
        }
        player.goLeft = buttons[Btns.left].isPressed;
        player.goRight = buttons[Btns.right].isPressed;
        player.goJump = buttons[Btns.up].isPressed;
        for (int i = 0; i < 3; i++) {
            buttons[i].UpdateAndReset();
        }

        return true;
    }
}
