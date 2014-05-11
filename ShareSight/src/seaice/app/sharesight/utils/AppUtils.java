package seaice.app.sharesight.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.format.Time;

public class AppUtils {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in {@link #setId(int)}. This value will
     * not collide with ID values generated at build time by aapt for R.id.
     * 
     * @return a generated ID value
     */
    public static int generateViewId() {
        while (true) {
            final int result = sNextGeneratedId.get();
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF)
                newValue = 1;
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public static Bitmap decodeFile(String filePath, Context context) {
        // On my xiaomi 2s, its value is 720, but its size is also really big.. 700k
        int desiredWidth = context.getResources().getDisplayMetrics().widthPixels;
        // int desiredWidth = 360;

        // Get the source image's dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        int srcWidth = options.outWidth;
        // Only scale if the source is big enough. This code is just trying to
        // fit a image into a certain width.
        if (desiredWidth > srcWidth)
            desiredWidth = srcWidth;

        // Calculate the correct inSampleSize/scale value. This helps reduce
        // memory use. It should be a power of 2
        // from:
        // http://stackoverflow.com/questions/477572/android-strange-out-of-memory-issue/823966#823966
        int inSampleSize = 1;
        while (srcWidth / 2 > desiredWidth) {
            srcWidth /= 2;
            inSampleSize *= 2;
        }

        float desiredScale = (float) desiredWidth / srcWidth;

        // Decode with inSampleSize
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inSampleSize = inSampleSize;
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap sampledSrcBitmap = BitmapFactory.decodeFile(filePath, options);

        // Resize
        Matrix matrix = new Matrix();
        matrix.postScale(desiredScale, desiredScale);
        Bitmap scaledBitmap = Bitmap.createBitmap(sampledSrcBitmap, 0, 0,
                sampledSrcBitmap.getWidth(), sampledSrcBitmap.getHeight(),
                matrix, true);
        sampledSrcBitmap = null;

        return scaledBitmap;
    }

    public static void saveBitmapToFile(Bitmap bitmap, String filePath) {
        try {
            FileOutputStream out = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            bitmap = null;
        } catch (FileNotFoundException e) {
            // error happened
        }
    }

    public static Bitmap decodeFileWithoutScale(File file)
            throws OutOfMemoryError {
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    public static String getRealPathFromUri(ContextWrapper contextWrapper,
            Uri uri) {
        Cursor cursor = contextWrapper.getContentResolver().query(uri, null,
                null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = contextWrapper.getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ",
                new String[] { document_id }, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor
                .getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    public static File createTempImageFile(String cacheDirPath, String deviceId)
            throws IOException {
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        String imageFileName = deviceId + "-" + today.format2445();
        File imgCacheDir = new File(cacheDirPath);
        // If the folder does not exist, then create it..
        if (!imgCacheDir.exists()) {
            imgCacheDir.mkdirs();
        }
        File image = File.createTempFile(imageFileName, ".jpg", imgCacheDir);
        return image;
    }

    public static void decodeAndSave(String bigFilePath, String scaledFilePath,
            Context context) {
        saveBitmapToFile(decodeFile(bigFilePath, context), scaledFilePath);
    }
}
