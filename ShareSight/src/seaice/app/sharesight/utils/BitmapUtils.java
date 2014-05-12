package seaice.app.sharesight.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.format.Time;

public class BitmapUtils {

    public static Bitmap decodeFile(String filePath, Context context) {
        // On my xiaomi 2s, its value is 720, but its size is also really big..
        // 700k
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
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            bitmap = null;
        } catch (FileNotFoundException e) {
            // error happened
        }
    }
    
    public static Bitmap decodeFileWithoutScale(File file)
            throws OutOfMemoryError {
        return BitmapFactory.decodeFile(file.getAbsolutePath());
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
