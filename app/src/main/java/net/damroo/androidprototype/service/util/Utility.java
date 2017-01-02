package net.damroo.androidprototype.service.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public final class Utility {

    private Utility() {
    }

    public static int getLastPageNumber(double results, double resultsPerPage) {
        double page = results / resultsPerPage;
        return (int) Math.ceil(page);
    }


    public static String getDateInString(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        TimeZone zone = TimeZone.getTimeZone("UTC");
        df.setTimeZone(zone);
        return df.format(date);
    }


    public static String getOneYearOldTimeStamp() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) - 1;
        calendar.set(Calendar.YEAR, year);
        return getDateInString(calendar.getTime());
    }
    
}
