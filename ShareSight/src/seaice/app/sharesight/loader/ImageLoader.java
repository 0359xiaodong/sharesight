package seaice.app.sharesight.loader;

import java.util.ArrayList;

import seaice.app.sharesight.data.ImageMeta;
import seaice.app.sharesight.http.BitmapTaskParam;
import seaice.app.sharesight.http.BitmapTaskResult;
import seaice.app.sharesight.http.HttpBitmapTask;
import seaice.app.sharesight.http.HttpTextTask;
import seaice.app.sharesight.http.HttpBitmapTaskClient;
import seaice.app.sharesight.http.HttpTextTaskClient;
import seaice.app.sharesight.http.TextTaskParam;
import seaice.app.sharesight.http.TextTaskResult;
import android.graphics.Bitmap;

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
public class ImageLoader implements HttpTextTaskClient, HttpBitmapTaskClient {

	private static final String IMAGE_META_SERVER = "http://www.zhouhaibing.com/app/sharesight/getimage";

	private ImageLoaderCallback mCallback;

	private FileCache mFileCache;

	public ImageLoader(ImageLoaderCallback callback) {
		mCallback = callback;
		mFileCache = new FileCache();
	}

	public void loadImageMetaList(int begin) {
		mCallback.beforeLoadImageMeta();
		String url = IMAGE_META_SERVER + "/" + begin;
		new HttpTextTask(this).execute(new TextTaskParam(url));
	}

	public void loadImage(ImageLoaderTask task) {
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
		new HttpBitmapTask(this).execute(new BitmapTaskParam(imageViewId, url));
	}

	@Override
	public void onGetTextTaskResult(TextTaskResult result) {
		String json = result.getContent();
		if (json == null) {
			return;
		}
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
	public void onGetBitmapTaskResult(BitmapTaskResult bitmapResult) {
		int imageViewId = bitmapResult.getImageViewId();
		String url = bitmapResult.getUrl();
		Bitmap bitmap = bitmapResult.getBitmap();
		if (bitmap != null) {
			// Here how to save it to cache
			mFileCache.addToCache(url, bitmap);
		}
		mCallback.onImageLoaded(imageViewId, bitmap);
	}

}
