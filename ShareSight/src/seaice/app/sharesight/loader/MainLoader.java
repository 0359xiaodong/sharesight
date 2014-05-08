package seaice.app.sharesight.loader;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import seaice.app.sharesight.data.ImageMeta;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * The main ImageLoader to retrieve image data from network.
 * 
 * This class should run in a separated thread.
 * 
 * @author zhb
 * 
 */
public class MainLoader {

	private static final String IMAGE_META_SERVER = "http://www.zhouhaibing.com/app/share/sight";

	private LoaderCallback mCallback;

	private FileCache mFileCache;

	public MainLoader(LoaderCallback callback) {
		mCallback = callback;
		mFileCache = new FileCache();
	}

	public void loadImageMetaList(int begin) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(IMAGE_META_SERVER + "/" + begin);
		ArrayList<ImageMeta> imageMetaList = new ArrayList<ImageMeta>();

		try {
			HttpResponse response = httpClient.execute(httpGet);
			String json = EntityUtils.toString(response.getEntity());
			JsonParser parser = new JsonParser();
			Gson gson = new Gson();
			JsonArray jsonArray = parser.parse(json).getAsJsonArray();
			for (JsonElement jsonEle : jsonArray) {
				imageMetaList.add(gson.fromJson(jsonEle, ImageMeta.class));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		mCallback.onImageMetaLoaded(imageMetaList);
	}

	public void loadImage(String url) {
		Bitmap bitmap = mFileCache.getBitmapFromCache(url);
		if (bitmap != null) {
			mCallback.onImageLoaded(bitmap);
			return;
		}

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);

		try {
			HttpResponse response = httpClient.execute(httpGet);
			bitmap = BitmapFactory.decodeStream(response.getEntity()
					.getContent());
		} catch (IOException e) {

		}

		if (bitmap != null) {
			mFileCache.addToCache(url, bitmap);
		}
		mCallback.onImageLoaded(bitmap);
	}

}
