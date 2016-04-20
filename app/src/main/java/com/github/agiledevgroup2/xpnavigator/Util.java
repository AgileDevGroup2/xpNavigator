package com.github.agiledevgroup2.xpnavigator;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Util class, static methods which occur more often in different classes
 */
public class Util {
    private static final String LOG_TAG = "Util";

    /**
     * Parsing Date from json string
     * @param jsonString json string to parse from
     * @return parse date;
     */
    public static Date getDateFromJson(String jsonString) {

        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        Date date = new Date();

        try {
            date = sdf.parse(jsonString.replaceAll("Z", "-0800"));
            Log.v(LOG_TAG, jsonString.toString());
        } catch (ParseException e) {
            Log.e(LOG_TAG, e.getMessage());
            date = new Date();
        }

        return date;
    }
}
