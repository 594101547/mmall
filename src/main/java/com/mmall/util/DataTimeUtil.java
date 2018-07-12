package com.mmall.util;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormat;
/*
created by dingtao
 */
public class DataTimeUtil {
    //joda-time
    //str->Date
    //Date->str

    //时间格式
    public static  final String STANDARD_FORMAT="yyyy-MM-dd HH:mm:ss";

    //自定义转化格式
    public static Date strToDate(String dateTimeStr,String formatStr){
        DateTimeFormatter dataTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dataTime = dataTimeFormatter.parseDateTime(dateTimeStr);
        return dataTime.toDate();
    }

    //自定义转化格式
    public static String dateToStr(Date date,String formatStr){
        if(date == null ){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }

    //标准格式转化
    public static Date strToDate(String dateTimeStr){
        DateTimeFormatter dataTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dataTime = dataTimeFormatter.parseDateTime(dateTimeStr);
        return dataTime.toDate();
    }

    //标准格式转化
    public static String dateToStr(Date date){
        if(date == null ){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }
}
