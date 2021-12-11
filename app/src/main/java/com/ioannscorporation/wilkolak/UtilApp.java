package com.ioannscorporation.wilkolak;

import android.app.Application;
import android.content.res.Resources;

import java.util.Random;

public class UtilApp extends Application {
    public static Resources res;
    public static Random random = new Random();
    public static int screenX, screenY;
    public static float screenRatioX, screenRatioY;
}
