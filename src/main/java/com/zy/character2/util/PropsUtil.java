package com.zy.character2.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropsUtil.class);

    public static Properties loadProps(String fileName){
        Properties properties = null;
        InputStream inputStream = null;
        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if (inputStream == null){
                throw new FileNotFoundException(fileName + " file is not found");
            }
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.error("close InputStream failure, Info: {}", e);
                    e.printStackTrace();
                }
            }

        }
        return properties;
    }

    public static String getString(Properties properties, String key){
        return getString(properties, key, "");
    }

    public static String getString(Properties properties, String key, String defaultValue){
        String value = defaultValue;
        if (properties.containsKey(key)){
            value = properties.getProperty(key);
        }
        return value;
    }

    public static int getInt(Properties properties, String key, int defaultValue){

        return 0;
    }

    public static int getInt(Properties properties, String key){
        return getInt(properties, key, 0);
    }

    public static boolean getBoolean(Properties properties, String key, boolean defaultValue){
        return false;
    }

    public static  boolean getBoolean(Properties properties, String key){
        return getBoolean(properties, key, false);
    }


}
