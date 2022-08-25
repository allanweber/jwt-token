package com.allanweber.jwttoken.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateHelper {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static Date getUTCDatetimeAsDate() {
        return stringDateToDate(getUTCDatetimeAsString());
    }

    public static String getUTCDatetimeAsString() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    public static Date stringDateToDate(String stringDate) {
        Date dateToReturn;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        try {
            dateToReturn = dateFormat.parse(stringDate);
        } catch (ParseException e) {
            throw new DateTimeException("Invalid date", e);
        }
        return dateToReturn;
    }
}
