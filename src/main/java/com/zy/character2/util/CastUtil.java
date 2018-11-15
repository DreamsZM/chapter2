package com.zy.character2.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 转型操作工具类
 */
public class CastUtil {

    public static String castString(Object o, String defaultValue){
        return o != null ? String.valueOf(o) : defaultValue;
    }

    public static String castString(Object o){
        return castString(o, "");
    }

    public static double castDouble(Object o, double defaultValue){
        double doubleValue = defaultValue;
        if (o != null){
            String stringValue = castString(o);
            if (StringUtils.isNotEmpty(stringValue)){
                //可能会出现不能转换成功

                try {
                    doubleValue = Double.parseDouble(stringValue);
                } catch (NumberFormatException e) {
                    doubleValue = defaultValue;
                }
            }
        }

        return doubleValue;
    }

    public static double castDouble(Object o){
        return castDouble(o, 0);
    }

    public static long castLong(Object o, long defaultValue){
        long longValue = defaultValue;
        if (o != null){
            String stringValue = castString(o);
            if (StringUtils.isNotEmpty(stringValue)){
                try {
                    longValue = Long.parseLong(stringValue);
                } catch (NumberFormatException e) {
                    longValue =defaultValue;
                }
            }
        }
        return longValue;
    }

    public static long castLong(Object o){
        return castLong(o, 0);
    }

    public static int castInt(Object o, int defaultValue){
        int intValue = defaultValue;
        if (o != null){
            String stringValue = castString(o);
            if (StringUtils.isNotEmpty(stringValue)){
                try {
                    intValue = Integer.parseInt(stringValue);
                } catch (NumberFormatException e) {
                    intValue = defaultValue;
                }
            }
        }
        return intValue;
    }

    public static int castInt(Object o){
        return castInt(o, 0);
    }

    public static boolean castBoolean(Object o, boolean defaultValue){
        boolean booleanValue = defaultValue;
        if (o != null){
            String stringValue = castString(o);
            if (StringUtils.isNotEmpty(stringValue)){
                try {
                    booleanValue = Boolean.parseBoolean(stringValue);
                } catch (Exception e) {
                    booleanValue = defaultValue;
                }
            }
        }
        return booleanValue;
    }

    public static boolean castBoolean(Object o){
        return castBoolean(o, false);
    }

}
