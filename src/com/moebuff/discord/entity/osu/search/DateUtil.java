package com.moebuff.discord.entity.osu.search;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static Date strToDate(String str){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss+00:00");
        try {
            return  simpleDateFormat.parse(str.replace("T", " "));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
