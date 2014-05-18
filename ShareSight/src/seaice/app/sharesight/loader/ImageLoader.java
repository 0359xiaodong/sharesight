package seaice.app.sharesight.loader;

import java.util.ArrayList;

import seaice.app.sharesight.http.ImageResult;
import seaice.app.sharesight.http.ImageResultClient;
import seaice.app.sharesight.http.TextResult;
import seaice.app.sharesight.http.TextResultClient;
import seaice.app.sharesight.http.get.ImageTask;
import seaice.app.sharesight.http.get.TextTask;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

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

    private static final String CITY_IMAGE_META_SERVER = "http://sharesight.duapp.com/index.php/app/listimage";

    private static final String NEAR_IMAGE_META_SERVER = "http://sharesight.duapp.com/index.php/app/nearimage";

    private static final String MY_IMAGE_META_SERVER = "http://sharesight.duapp.com/index.php/app/myimage";

    private static final String TOP_IMAGE_META_SERVER = "http://sharesight.duapp.com/index.php/app/topimage";

    private ImageLoaderCallback mCallback;

    private FileCache mFileCache;

    private boolean mCancelled = false;

    private boolean mDebug = true;

    public ImageLoader(ImageLoaderCallback callback) {
        mCallback = callback;
        mFileCache = new FileCache();
    }

    public ImageLoader() {
        this(new ImageLoaderAdapter());
    }

    public void setImageLoaderCallback(ImageLoaderCallback callback) {
        mCallback = callback;
    }

    public void loadCityMetaList(String city, int page, int count, Bundle extras) {
        mCancelled = false;
        mCallback.beforeLoadImageMeta();
        String url = CITY_IMAGE_META_SERVER + "/" + city + "/" + page + "/"
                + count;
        Bundle data = new Bundle();
        data.putString(TextTask.URL_TAG, url);
        if (extras != null) {
            data.putAll(extras);
        }
        new TextTask(this).execute(data);
    }

    public void loadNearMetaList(double longitude, double latitude,
            int kilometers, int page, int count, Bundle extras) {
        mCancelled = false;
        mCallback.beforeLoadImageMeta();
        String url = NEAR_IMAGE_META_SERVER + "/" + longitude + "/" + latitude
                + "/" + kilometers + "/" + page + "/" + count;
        Bundle data = new Bundle();
        data.putString(TextTask.URL_TAG, url);
        if (extras != null) {
            data.putAll(extras);
        }
        new TextTask(this).execute(data);
    }

    public void loadTopMetaList(String city, int page, int count, Bundle extras) {
        mCancelled = false;
        mCallback.beforeLoadImageMeta();
        String url = TOP_IMAGE_META_SERVER + "/" + city + "/" + page + "/"
                + count;
        Bundle data = new Bundle();
        data.putString(TextTask.URL_TAG, url);
        if (extras != null) {
            data.putAll(extras);
        }
        new TextTask(this).execute(data);
    }

    public void loadMyMetaList(String deviceid, int page, int count,
            Bundle extras) {
        mCancelled = false;
        mCallback.beforeLoadImageMeta();
        String url = MY_IMAGE_META_SERVER + "/" + deviceid + "/" + page + "/"
                + count;
        Bundle data = new Bundle();
        data.putString(TextTask.URL_TAG, url);
        if (extras != null) {
            data.putAll(extras);
        }
        new TextTask(this).execute(data);
    }

    public void loadImage(String url, Bundle extras) {
        if (mCancelled) {
            return;
        }
        if (url == null) {
            return;
        }
        Bitmap bitmap = mFileCache.getBitmapFromCache(url);
        if (bitmap != null) {
            mCallback.onImageLoaded(bitmap, extras);
            return;
        }
        mCallback.beforeLoadImage();
        Bundle data = new Bundle();
        data.putString(ImageTask.URL_TAG, url);
        if (extras != null) {
            data.putAll(extras);
        }
        new ImageTask(this).execute(data);
    }

    public void setCancelled(boolean cancelled) {
        mCancelled = cancelled;
        if (cancelled) {
            mCallback.afterLoadImage();
        }
    }

    @Override
    public void onGetTextResult(TextResult result) {
        if (result == null) {
            mCallback.afterLoadImageMeta();
            return;
        }
        if (mDebug) {
            Log.d("OnGetTextResult", result.getText());
        }
        String json = result.getText();
        ArrayList<ImageMeta> imageMetaList = new ArrayList<ImageMeta>();
        JsonParser parser = new JsonParser();
        Gson gson = new Gson();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();
        for (JsonElement jsonEle : jsonArray) {
            imageMetaList.add(gson.fromJson(jsonEle, ImageMeta.class));
        }
        mCallback.onImageMetaLoaded(imageMetaList, result.getData());
        mCallback.afterLoadImageMeta();
    }

    @Override
    public void onGetImageResult(ImageResult imageResult) {
        Bundle data = imageResult.getData();
        String url = data.getString(ImageTask.URL_TAG);
        Bitmap bitmap = imageResult.getBitmap();
        if (bitmap != null) {
            // Here how to save it to cache
            mFileCache.addToCache(url, bitmap);
        }
        mCallback.onImageLoaded(bitmap, data);
        mCallback.afterLoadImage();
    }
}
