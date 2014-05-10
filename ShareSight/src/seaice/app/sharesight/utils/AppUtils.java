package seaice.app.sharesight.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

	public static Bitmap decodeFile(File file) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream stream1 = new FileInputStream(file);
			BitmapFactory.decodeStream(stream1, null, o);
			stream1.close();
			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 70;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			if (scale >= 2) {
				scale /= 2;
			}
			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			FileInputStream stream2 = new FileInputStream(file);
			Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
			stream2.close();
			return bitmap;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static File saveBitmapToFile(Bitmap bitmap, String name) {
		File file = new File(name);
		FileOutputStream out;
		try {
			out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	public static String getRealPathFromUri(ContextWrapper contextWrapper,
			Uri uri) {
		// String result;
		// Cursor cursor = contextWrapper.getContentResolver().query(contentURI,
		// null, null, null, null);
		// if (cursor == null) {
		// result = contentURI.getPath();
		// } else {
		// cursor.moveToFirst();
		// int idx = cursor
		// .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
		// result = cursor.getString(idx);
		// cursor.close();
		// }
		// return result;
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

	public static File createTempImageFile(String cacheDirPath)
			throws IOException {
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		String imageFileName = today.format2445();
		File imgCacheDir = new File(cacheDirPath);
		// If the folder does not exist, then create it..
		if (!imgCacheDir.exists()) {
			imgCacheDir.mkdirs();
		}
		File image = File.createTempFile(imageFileName, ".jpg", imgCacheDir);
		return image;
	}
}
