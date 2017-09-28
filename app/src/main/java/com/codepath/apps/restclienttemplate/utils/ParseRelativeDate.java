package com.codepath.apps.restclienttemplate.utils;

import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tessavoon on 9/27/17.
 */

public final class ParseRelativeDate {
    public final String[] timeFrames = {"seconds", "minutes", "hours", "days", "weeks", "years"};

    public String getRelativeTimeAgo(String rawJsonDate) {
        String relativeDate = parseDate(rawJsonDate);
        return prettifyTimeStamp(relativeDate);
    }

    private String parseDate(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
            Log.d("rel", relativeDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }

    private String prettifyTimeStamp(String relativeDate) {
        String relativeDateDigits = relativeDate.replaceAll("\\D+","");
        for (int i = 0; i < timeFrames.length; i++) {
            if (isMatchTimeFrame(timeFrames[i], relativeDate)) {
                return relativeDateDigits + timeFrames[i].substring(0,1).toUpperCase();
            }
        }
        return relativeDate;
    }

    private boolean isMatchTimeFrame(String timeframe, String relativeDate) {
        Pattern pattern = Pattern.compile("\\b" + timeframe + "\\b");
        Matcher matcher = pattern.matcher(relativeDate);
        return matcher.matches();
    }

}
