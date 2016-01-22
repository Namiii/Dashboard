package Utils;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by nami on 1/22/16.
 */
public class DateUtils {
    public DateUtils() {
    }

    public static Date getTodaysDate(){
        Calendar cal = Calendar.getInstance();
        Log.v("cal", cal.getTime().toString() + "");
        return cal.getTime();
    }

    public static Date setDate(int year, int month, int day ){
        Date date = new Date();
        date.setYear(year);
        date.setMonth(month);
        date.setDate(day);
        return date;
    }
}
