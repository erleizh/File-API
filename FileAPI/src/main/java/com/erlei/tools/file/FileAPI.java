package com.erlei.tools.file;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;

@SuppressWarnings({"WeakerAccess", "unused"})
public class FileAPI {

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    public static final String DATA = "/data";
    public static final String MEDIA = "/media";
    public static final String OBB = "/obb";
    public static final String FILES = "/files";
    public static final String CACHE = "/cache";
    private static String BASE_DIR = "";

    public static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

    private static String INTERNAL = null;
    private static String EXTERNAL = null;
    private static String SDCARD = null;

    private FileAPI() {

    }

    public static void init(@NonNull Context context, @NonNull String baseDir) {
        BASE_DIR = baseDir;
        sContext = context.getApplicationContext();
        INTERNAL = new File(sContext.getDir("", Context.MODE_PRIVATE).getParent(), BASE_DIR).getAbsolutePath();
        EXTERNAL = new File(sContext.getExternalFilesDir(null).getParentFile(), BASE_DIR).getAbsolutePath();
        SDCARD = new File(sdcardWithoutBaseDir(), BASE_DIR).getAbsolutePath();
    }


    /**
     * /data/user/0/[applicationId]/[BASE_DIR]/
     * <p>
     * 不需要读写权限，随着app卸载而删除
     * 私有文件夹，其他app无法访问
     *
     * @param path 相对路径
     * @return FileHandle
     */
    @NonNull
    public static FileHandle internal(@NonNull String path) {
        return new FileHandle(new File(internal(), path));
    }

    /**
     * /data/user/0/[applicationId]/[BASE_DIR]/
     * <p>
     * 不需要读写权限，随着app卸载而删除
     * 私有文件夹，其他app无法访问
     *
     * @param dir  文件夹
     * @param name 文件名
     * @return FileHandle
     */
    @NonNull
    public static FileHandle internal(@NonNull String dir, @NonNull String name) {
        return new FileHandle(new File(internal(), dir + File.separator + name));
    }

    /**
     * /storage/emulated/0/Android/data/[applicationId]/[BASE_DIR]/
     * <p>
     * 不需要读写权限，随着app卸载而删除
     * 外部文件夹，其他app可以访问
     *
     * @param dir  文件夹
     * @param name 文件名
     * @return FileHandle
     */
    @NonNull
    public static FileHandle external(@NonNull String dir, @NonNull String name) {
        return new FileHandle(new File(external(), dir + File.separator + name));
    }

    /**
     * /storage/emulated/0/Android/data/[applicationId]/[BASE_DIR]/
     * <p>
     * 不需要读写权限，随着app卸载而删除
     * 外部文件夹，其他app可以访问
     *
     * @param path 相对路径
     * @return FileHandle
     */
    @NonNull
    public static FileHandle external(@NonNull String path) {
        return new FileHandle(new File(external(), path));
    }

    /**
     * /storage/emulated/0/[BASE_DIR]/
     * <p>
     * 需要读写权限，app卸载不会删除，其他app可以访问
     *
     * @param dir  文件夹
     * @param name 文件名
     * @return FileHandle
     */
    @NonNull
    public static FileHandle sdcard(@NonNull String dir, @NonNull String name) {
        return new FileHandle(new File(sdcard(), dir + File.separator + name));

    }

    /**
     * /storage/emulated/0/[BASE_DIR]/
     * <p>
     * 需要读写权限，app卸载不会删除，其他app可以访问
     *
     * @param path 相对路径
     * @return FileHandle
     */
    @NonNull
    public static FileHandle sdcard(@NonNull String path) {
        return new FileHandle(new File(sdcard(), path));
    }

    /**
     * withoutBaseDir false /storage/emulated/0/[BASE_DIR]/
     * <p>
     * withoutBaseDir true /storage/emulated/0/
     * <p>
     * 需要读写权限，app卸载不会删除，其他app可以访问
     *
     * @param path           相对路径
     * @param withoutBaseDir 是否包含baseDir
     * @return FileHandle
     */
    @NonNull
    public static FileHandle sdcard(@NonNull String path, boolean withoutBaseDir) {
        if (withoutBaseDir) {
            return new FileHandle(new File(sdcardWithoutBaseDir(), path));
        } else {
            return new FileHandle(new File(sdcard(), path));
        }
    }

    /**
     * 根路径 / 需要读写权限，app卸载不会删除，其他app可以访问
     *
     * @param path 文件绝对路径
     * @return FileHandle
     */
    @NonNull
    public static FileHandle absolute(@NonNull String path) {
        return new FileHandle(path);
    }

    /**
     * app 资产文件，只读
     *
     * @param path 文件相对路径
     * @return FileHandle
     */
    @NonNull
    public static FileHandle assets(@NonNull String path) {
        return new AssetFileHandle(sContext.getAssets(), path);
    }

    /**
     * @return /storage/emulated/0/
     */
    public static String sdcardWithoutBaseDir() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * @return /storage/emulated/0/[BASE_DIR]/
     */
    public static String sdcard() {
        return SDCARD + File.separator;
    }

    /**
     * @return /data/user/0/[applicationId]/[BASE_DIR]/
     */
    public static String internal() {
        return INTERNAL + File.separator;
    }

    /**
     * @return /storage/emulated/0/Android/data/[applicationId]/[BASE_DIR]/
     */
    public static String external() {
        return EXTERNAL + File.separator;
    }

    /**
     * @return baseDir
     * @see #init(Context, String)
     */
    public static String baseDir() {
        return BASE_DIR;
    }
}
