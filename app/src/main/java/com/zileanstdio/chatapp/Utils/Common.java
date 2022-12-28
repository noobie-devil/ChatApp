package com.zileanstdio.chatapp.Utils;

import android.os.Handler;
import android.os.Looper;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Common {

    public static String getReadableTime(Date date) {
        long timeDiff = Math.abs(date.getTime() - new Date().getTime());
        long hoursDiff = TimeUnit.HOURS.convert(timeDiff, TimeUnit.MILLISECONDS);
        if(hoursDiff < 24) {
            return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
        }
        else {
            return new SimpleDateFormat("d MMM", Locale.getDefault()).format(date);
        }
    }

    public static String removeAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").toUpperCase(Locale.ROOT);
    }

    public static void postDelay(Runnable runnable, long delayMillis) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, delayMillis);
    }
}