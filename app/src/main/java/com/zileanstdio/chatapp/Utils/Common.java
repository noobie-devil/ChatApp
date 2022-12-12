package com.zileanstdio.chatapp.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
}
