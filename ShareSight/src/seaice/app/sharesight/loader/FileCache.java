package seaice.app.sharesight.loader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import seaice.app.sharesight.utils.AppUtils;
import android.graphics.Bitmap;
import android.os.Environment;

/**
 * The file cache, it will store the given url image to a file, and if the file
 * exists, it will returns immediately, otherwise return null;
 * 
 * And it is the user's responsibility to clear the file cache.
 * 
 * @author zhb
 * 
 */
public class FileCache {

    private static final String IMAGE_CACHE_PATH = Environment
            .getExternalStorageDirectory() + "/ShareSight/cache/downloaded";

    private int mCacheCount;

    public FileCache() {
        File mCacheDir = new File(IMAGE_CACHE_PATH);
        if (!mCacheDir.exists()) {
            mCacheDir.mkdirs();
        }
    }

    public void addToCache(String url, Bitmap bitmap) {
        File file = new File(IMAGE_CACHE_PATH + "/" + url.hashCode());
        if (file.exists()) {
            return;
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            ++mCacheCount;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmapFromCache(String url) {
        File file = new File(IMAGE_CACHE_PATH + "/" + url.hashCode());
        if (file.exists()) {
            return AppUtils.decodeFile(file);
        } else {
            return null;
        }
    }

    public int getCacheCount() {
        return mCacheCount;
    }

    /** clear cache */
    public void clear() {
        File cacheDir = new File(IMAGE_CACHE_PATH);
        File[] files = cacheDir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            file.delete();
        }
    }
}