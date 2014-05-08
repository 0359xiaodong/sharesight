package seaice.app.sharesight.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import seaice.app.sharesight.AppUtils;
import android.graphics.Bitmap;
import android.os.Environment;

public class FileCache {

	private static final String IMAGE_CACHE_PATH = Environment
			.getExternalStorageDirectory() + "/ShareSight/cache/downloaded";


	public FileCache() {
		File mCacheDir = new File(IMAGE_CACHE_PATH);
		if (!mCacheDir.exists()) {
			mCacheDir.mkdirs();
		}
	}

	public void addToCache(String url, Bitmap bitmap) {
		File file = new File(IMAGE_CACHE_PATH + "/" + url.hashCode());
		try {
			if (file.exists()) {
				return;
			}
			FileOutputStream outputStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
			outputStream.flush();
			outputStream.close();
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
}