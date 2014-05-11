package seaice.app.sharesight.poster;

import java.util.ArrayList;

import seaice.app.sharesight.bcs.BCSSvc;
import seaice.app.sharesight.bcs.BCSSvcClient;
import seaice.app.sharesight.http.TextResult;
import seaice.app.sharesight.http.TextResultClient;
import seaice.app.sharesight.http.post.TextTask;
import android.os.Bundle;

import com.baidu.inf.iis.bcs.model.DownloadObject;
import com.baidu.inf.iis.bcs.model.ObjectMetadata;

public class ImagePoster implements BCSSvcClient, TextResultClient {

    private ImagePosterCallback mCallback;

    private static final String WIDTH_TAG = "seaice.app.sharesight.poster.ImagePoster.WIDTH";
    private static final String HEIGHT_TAG = "seaice.app.sharesight.poster.ImagePoster.HEIGHT";
    private static final String DEVICE_ID_TAG = "seaice.app.sharesight.poster.ImagePoster.DEVICE_ID";
    private static final String IMAGE_ADD_SERVER = "http://sharesight.duapp.com/index.php/app/addrecord";

    private BCSSvc mBCS;

    public ImagePoster(ImagePosterCallback callback) {
        mCallback = callback;
        mBCS = new BCSSvc(this);
    }

    public void post(String filePath, int width, int height, String deviceId) {
        mCallback.beforePostImage();
        Bundle data = new Bundle();
        data.putInt(WIDTH_TAG, width);
        data.putInt(HEIGHT_TAG, height);
        data.putString(DEVICE_ID_TAG, deviceId);
        mBCS.uploadFileAsync(filePath, data);
    }

    @Override
    public void onGetTextResult(TextResult textResult) {
        if (textResult == null) {
            // There are some error happened
            mCallback.onImagePosted(false, "FAIL");
        } else {
            mCallback.onImagePosted(true, "OK");
        }
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
        data.putString(TextTask.URL_TAG, IMAGE_ADD_SERVER);
        // put the data needed by the TextTask object
        ArrayList<String> keyArray = new ArrayList<String>();
        ArrayList<String> valueArray = new ArrayList<String>();
        keyArray.add("width");
        valueArray.add(data.getInt(WIDTH_TAG) + "");
        keyArray.add("height");
        valueArray.add(data.getInt(HEIGHT_TAG) + "");
        keyArray.add("url");
        valueArray.add(data.getString(BCSSvc.URL_TAG));
        keyArray.add("deviceId");
        valueArray.add(data.getString(DEVICE_ID_TAG));

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
