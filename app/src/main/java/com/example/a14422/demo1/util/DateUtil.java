package com.example.a14422.demo1.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateUtil {

    public static String formatDate(String str) {
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        SimpleDateFormat sf2 = new SimpleDateFormat("MM-dd", Locale.CHINA);
        String formatStr = "";
        try {
            formatStr = sf1.format(sf1.parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatStr;
    }
}
