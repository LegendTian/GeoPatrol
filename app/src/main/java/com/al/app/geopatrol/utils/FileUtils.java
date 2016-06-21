package com.al.app.geopatrol.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.common.io.Files;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Stack;
import java.util.UUID;

/**
 * Created by Dai Jingjing on 2016/3/19.
 */
public class FileUtils {

    static final String TAG = FileUtils.class.getSimpleName();
    static final String TMP_OUTPUT_DIR = "temp"; // Directory relative to
    // External or Internal
    // (fallback) Storage

    public static void writeFile(String data, File file) throws IOException {
        writeFile(data.getBytes(Charset.forName("UTF-8")), file);
    }

    public static void writeFile(byte[] data, File file) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file, false));
        bos.write(data);
        bos.close();
    }

    public static String readFileAsString(File file) throws IOException {
        return new String(Files.toByteArray(file), Charset.forName("UTF-8"));
    }

    public static File getAppDataDirectory(Context c, String directory_name) {
        return getRootStorageDirectory(c, directory_name);
    }

    public static File getAppDataDirectory(Context context, String sub_dir, String fileName) {
        File dirPath = getAppDataDirectory(context, sub_dir);
        return new File(dirPath, fileName);
    }

    public static File getTempDirectory(Context context) {
        return getAppDataDirectory(context, TMP_OUTPUT_DIR);
    }

    /**
     * Returns a Java File initialized to a directory of given name at the root
     * storage location, with preference to external storage. If the directory
     * did not exist, it will be created at the conclusion of this call. If a
     * file with conflicting name exists, this method returns null;
     *
     * @param c
     *            the context to determine the internal storage location, if
     *            external is unavailable
     * @param directory_name
     *            the name of the directory desired at the storage location
     * @return a File pointing to the storage directory, or null if a file with
     *         conflicting name exists
     */
    public static File getRootStorageDirectory(Context c, String directory_name) {
        File result;
        if (hasExternalStorage()) {
            Log.d(TAG, "Using sdcard");
            result = new File(Environment.getExternalStorageDirectory(),
                    "/Android/data/" + c.getApplicationContext().getPackageName() + "/" + directory_name);
        } else {
            Log.d(TAG, "Using internal storage");
            result = new File(c.getApplicationContext().getFilesDir(), directory_name);
        }

        if (!result.exists()) {
            result.mkdirs();
        } else if (result.isFile()) {
            return null;
        }
        Log.d("getRootStorageDirectory", result.getAbsolutePath());

        return result;
    }

    public static Uri getCatOutImageSaveUri(final Context ctx){
        final String name = UUID.randomUUID()+".jpg";
        final File file = new File(getRootStorageDirectory(ctx, "catout"), name);
        final Uri uri = Uri.fromFile(file);
        Log.e("fileutils", "path:" + uri);
        return uri;
    }

    /**
     * 判断是否有sd卡
     *
     * @return
     */
    public static boolean hasExternalStorage() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    /**
     * 获取手机内部
     *
     * @return
     */
    static public File getDataDirectory() {
        return Environment.getDataDirectory();// Gets the Android data directory
    }

    public static void deleteFile(File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        }
    }

    // Image Cache
    public static File getImageCacheDirectory(Context c) {
        return getAppDataDirectory(c, "cache_images");
    }

    public static void clearImageCacheDirectory(Context c) {
        File dir = getImageCacheDirectory(c);
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (int i = 0; i < children.length; i++) {
                children[i].delete();
            }
        }
    }

    public static long getImageCacheSize(Context c) {
        return directorySize(getImageCacheDirectory(c));
    }

    // Net cache
    public static File getNetDataCacheDirectory(Context c) {
        return getAppDataDirectory(c, "cache_network");
    }

    public static void clearNetDataCacheDirectory(Context c) {
        File dir = getNetDataCacheDirectory(c);
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (int i = 0; i < children.length; i++) {
                children[i].delete();
            }
        }
    }

    public static long getNetDataCacheSize(Context c) {
        return directorySize(getNetDataCacheDirectory(c));
    }

    public static long directorySize(File dir) {
        long result = 0;

        Stack<File> dirlist = new Stack<File>();
        dirlist.clear();

        dirlist.push(dir);

        while (!dirlist.isEmpty()) {
            File dirCurrent = dirlist.pop();

            File[] fileList = dirCurrent.listFiles();
            for (File f : fileList) {
                if (f.isDirectory())
                    dirlist.push(f);
                else
                    result += f.length();
            }
        }

        return result;
    }

    private static final String[] file_size_units = new String[] { "B", "KB", "MB", "GB", "TB" };

    public static String perttyFileSize(long size) {
        if (size <= 0)
            return "0B";
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " "
                + file_size_units[digitGroups];
    }

    public static String getFileName(String pathAndName){
        if(pathAndName==null)return null;
        int start=pathAndName.lastIndexOf("/");
        int end=pathAndName.lastIndexOf(".");
        if (start!=-1 && end!=-1) {
            return pathAndName.substring(start+1, end);
        }
        else {
            return null;
        }
    }


    /**
     * 保存json到本地
     * @param mActivity
     * @param filename
     * @param content
     */
    public static File dir = new File(Environment.getExternalStorageDirectory() + "/GeoPatrol/json/");

    public static boolean saveToSDCard(String filename, String content) {
        String en = Environment.getExternalStorageState();
        //获取SDCard状态,如果SDCard插入了手机且为非写保护状态
        if (en.equals(Environment.MEDIA_MOUNTED)) {
            try {
                dir.mkdirs(); //create folders where write files
                File file = new File(dir, filename);

                OutputStream out = new FileOutputStream(file);
                out.write(content.getBytes());
                out.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            //提示用户SDCard不存在或者为写保护状态
            //AppUtils.showToast(mActivity, "SDCard不存在或者为写保护状态");
            return false;
        }
    }

    /**
     * 从本地读取json
     * @param filename
     */
    public static String readTextFile(String filename) {
        StringBuilder sb = new StringBuilder();
        try {
            File file = new File(dir + "/" + filename);
            InputStream in = null;
            in = new FileInputStream(file);
            int tempbyte;
            while ((tempbyte = in.read()) != -1) {
                sb.append((char) tempbyte);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


}
