package com.al.app.geopatrol.utils;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Dai Jingjing on 2016/2/26.
 */
public class HashUtils {
    public static String md5(String inStr) {
        if (inStr == null || inStr.equals("")) {
            return "";
        }

        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            return "";
        }

        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];

        return hex(md5.digest(byteArray));
    }

    public static String hex(byte[] bytes) {
        StringBuilder hexValue = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            int val = ((int) bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }

        return hexValue.toString();
    }

    public static String getRandomId() {
        String id = UUID.randomUUID().toString();
        id = id.replaceAll("-", "");
        return id;
    }

    public static Object keyString(HashMap<Object,Object> map,Object o)
    {
        Iterator<Object> it= map.keySet().iterator();
        while(it.hasNext())
        {
            Object keyString=it.next();
            if(map.get(keyString).equals(o))
                return keyString;
        }
        return null;
    }
    public static String keyString(Map<String,String> map,String o)
    {
        Iterator<String> it= map.keySet().iterator();
        while(it.hasNext())
        {
            String keyString=it.next();
            if(map.get(keyString).equals(o))
                return keyString;
        }
        return null;
    }
}
