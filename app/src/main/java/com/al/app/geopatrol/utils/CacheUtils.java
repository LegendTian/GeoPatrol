package com.al.app.geopatrol.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.IOException;

/**
 * Created by Dai Jingjing on 2016/3/19.
 */
public class CacheUtils {
    private static final String TAG = CacheUtils.class.getSimpleName();

    public static void put(Context c, String key, String value) {
        DiskLruCache.Editor editor = null;
        try {
            editor = getCache(c).edit(key);
            editor.set(0, value);
            editor.commit();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static void put(Context c, String key, byte[] value) {
        DiskLruCache.Editor editor = null;
        try {
            editor = getCache(c).edit(key);
            editor.newOutputStream(0).write(value);
            editor.commit();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static String get(Context c, String key) {
        String cache = null;
        try {
            DiskLruCache.Snapshot v = getCache(c).get(key);
            if (v != null) {
                cache = v.getString(0);
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return cache;
    }

    public static byte[] getBytes(Context c, String key) {
        byte[] cache = null;
        try {
            DiskLruCache.Snapshot v = getCache(c).get(key);
            if (v != null) {
               cache = new byte[(int)v.getLength(0)];
                v.getInputStream(0).read(cache);
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return cache;
    }

    public static void clear(Context c, String key) {
        try {
            getCache(c).remove(key);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static String makeKey(String str) {
        return HashUtils.md5(str);
    }

    private static final Object cache_lock = new Object();
    private static final int cache_max_size = 250000000; // 250M

    private static DiskLruCache cache = null;
    private static DiskLruCache getCache(Context c) {
        synchronized (cache_lock) {
            if (cache == null) {
                try {
                    cache = DiskLruCache.open(FileUtils.getRootStorageDirectory(c, "app_cache"),
                            c.getPackageManager().getPackageInfo(c.getPackageName(), 0).versionCode, 1, cache_max_size);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, e.getMessage());
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return cache;
    }
}
