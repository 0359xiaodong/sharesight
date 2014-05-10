package seaice.app.sharesight.loader;

import java.util.ArrayList;

import seaice.app.sharesight.data.ImageMeta;
import seaice.app.sharesight.http.ImageResult;
import seaice.app.sharesight.http.ImageResultClient;
import seaice.app.sharesight.http.TextResult;
import seaice.app.sharesight.http.TextResultClient;
import seaice.app.sharesight.http.get.ImageTask;
import seaice.app.sharesight.http.get.TextTask;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * The main ImageLoader to retrieve image data from network.
 * 
 * @author zhb
 * 
 */
public class ImageLoader implements TextResultClient, ImageResultClient {

	private static final String IMAGE_META_SERVER = "http://www.zhouhaibing.com/app/sharesight/getimage";

	private static final String RESOURCE_ID_TAG = "RESOURCE_ID";

	private ImageLoaderCallback mCallback;

	private FileCache mFileCache;

	private boolean mCancelled = false;

	public ImageLoader(ImageLoaderCallback callback) {
		mCallback = callback;
		mFileCache = new FileCache();
	}

	public void loadImageMetaList(int begin) {
		mCancelled = false;
		mCallback.beforeLoadImageMeta();
		String url = IMAGE_META_SERVER + "/" + begin;
		Bundle data = new Bundle();
		data.putString(TextTask.URL_TAG, url);
		new TextTask(this).execute(data);
	}

	public void loadImage(ImageLoaderTask task) {
		if (mCancelled) {
			return;
		}
		if (task == null) {
			return;
		}
		String url = task.getUrl();
		int imageViewId = task.getImageViewId();
		Bitmap bitmap = mFileCache.getBitmapFromCache(url);
		if (bitmap != null) {
			mCallback.onImageLoaded(task.getImageViewId(), bitmap);
			return;
		}

		mCallback.beforeLoadImageMeta();
		Bundle data = new Bundle();
		data.putInt(RESOURCE_ID_TAG, imageViewId);
		data.putString(ImageTask.URL_TAG, url);
		new ImageTask(this).execute(data);
	}

	public void setCancelled(boolean cancelled) {
		mCancelled = cancelled;
	}

	@Override
	public void onGetTextResult(TextResult result) {
		if (result == null) {
			mCallback.afterLoadImageMeta();
			return;
		}
		String json = result.getText();
		ArrayList<ImageMeta> imageMetaList = new ArrayList<ImageMeta>();
		JsonParser parser = new JsonParser();
		Gson gson = new Gson();
		JsonArray jsonArray = parser.parse(json).getAsJsonArray();
		for (JsonElement jsonEle : jsonArray) {
			imageMetaList.add(gson.fromJson(jsonEle, ImageMeta.class));
		}
		mCallback.afterLoadImageMeta();
		mCallback.onImageMetaLoaded(imageMetaList);
	}

	@Override
	public void onGetImageResult(ImageResult imageResult) {
		int imageViewId = imageResult.getData().getInt(RESOURCE_ID_TAG);
		String url = imageResult.getData().getString(ImageTask.URL_TAG);
		Bitmap bitmap = imageResult.getBitmap();
		if (bitmap != null) {
			// Here how to save it to cache
			mFileCache.addToCache(url, bitmap);
		}
		mCallback.onImageLoaded(imageViewId, bitmap);
	}
}
