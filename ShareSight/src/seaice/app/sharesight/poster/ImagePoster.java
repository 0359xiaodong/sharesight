package seaice.app.sharesight.poster;

import java.util.ArrayList;

import seaice.app.sharesight.bcs.BCSSvc;
import seaice.app.sharesight.bcs.BCSSvcClient;
import seaice.app.sharesight.data.ImageMeta;
import seaice.app.sharesight.http.TextResult;
import seaice.app.sharesight.http.TextResultClient;
import seaice.app.sharesight.http.post.TextTask;
import android.os.Bundle;
import android.util.Log;

import com.baidu.inf.iis.bcs.model.DownloadObject;
import com.baidu.inf.iis.bcs.model.ObjectMetadata;

public class ImagePoster implements BCSSvcClient, TextResultClient {

    private ImagePosterCallback mCallback;

    private static final String IMAGE_META_TAG = "seaice.app.sharesight.poster.ImagePoster.IMAGE_META";

    private static final String IMAGE_ADD_SERVER = "http://sharesight.duapp.com/index.php/app/addrecord";

    private BCSSvc mBCS;

    private boolean mDebug = true;

    public ImagePoster(ImagePosterCallback callback) {
        mCallback = callback;
        mBCS = new BCSSvc(this);
    }

    public void post(String filePath, ImageMeta imageMeta) {
        mCallback.beforePostImage();
        Bundle data = new Bundle();
        data.putParcelable(IMAGE_META_TAG, imageMeta);
        mBCS.uploadFileAsync(filePath, data);
    }

    @Override
    public void onGetTextResult(TextResult textResult) {
        if (textResult == null) {
            // There are some error happened
            mCallback.onImagePosted(false, "FAIL");
            mCallback.afterPostImage();
            return;
        }
        if (mDebug) {
            Log.d("OnGetTextResult", textResult.getText());
        }
        mCallback.onImagePosted(true, "OK");
        mCallback.afterPostImage();
    }

    @Override
    public void onFileUploaded(ObjectMetadata result, Bundle data) {
        if (result == null) {
            // There are some error happened
            mCallback.onImagePosted(false, "FAIL");
            mCallback.afterPostImage();
            return;
        }
        ImageMeta imageMeta = data.getParcelable(IMAGE_META_TAG);
        data.putString(TextTask.URL_TAG, IMAGE_ADD_SERVER);
        // put the data needed by the TextTask object
        ArrayList<String> keyArray = new ArrayList<String>();
        ArrayList<String> valueArray = new ArrayList<String>();
        keyArray.add("deviceId");
        valueArray.add(imageMeta.getDeviceId());
        keyArray.add("url");
        valueArray.add(imageMeta.getUrl());
        keyArray.add("width");
        valueArray.add(imageMeta.getWidth() + "");
        keyArray.add("height");
        valueArray.add(imageMeta.getHeight() + "");
        keyArray.add("city");
        valueArray.add(imageMeta.getCity());
        keyArray.add("addr");
        valueArray.add(imageMeta.getAddr());
        keyArray.add("longitude");
        valueArray.add(imageMeta.getLongitude() + "");
        keyArray.add("latitude");
        valueArray.add(imageMeta.getLatitude() + "");
        keyArray.add("text");
        valueArray.add(imageMeta.getText());

        data.putStringArrayList(TextTask.KEY_ARRAY_TAG, keyArray);
        data.putStringArrayList(TextTask.VALUE_ARRAY_TAG, valueArray);

        new TextTask(this).execute(data);
    }

    @Override
    public void onFileDownloaded(DownloadObject downloadObject,
            Bundle clientData) {
        // OMMITTED
    }

}
