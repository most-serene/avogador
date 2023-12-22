package eu.mostserene.avogador.exerciseservice.utils;

import java.util.Calendar;
import java.util.Date;

public class AvogadorDateUtils {

    private AvogadorDateUtils() {
    }

    public static Date stripSecondsFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Date(calendar.getTimeInMillis());
    }
}
