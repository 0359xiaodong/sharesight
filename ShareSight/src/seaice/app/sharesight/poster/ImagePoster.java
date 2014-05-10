package seaice.app.sharesight.poster;

import java.util.ArrayList;

import android.os.Bundle;
import seaice.app.sharesight.bcs.BCSSvc;
import seaice.app.sharesight.http.TextResult;
import seaice.app.sharesight.http.TextResultClient;
import seaice.app.sharesight.http.post.FileTask;
import seaice.app.sharesight.http.post.TextTask;

public class ImagePoster implements TextResultClient {

	private ImagePosterCallback mCallback;

	private static final String TASK_TAG = "seaice.app.sharesight.poster.ImagePoster.TASK";
	private static final String WIDTH_TAG = "seaice.app.sharesight.poster.ImagePoster.WIDTH";
	private static final String HEIGHT_TAG = "seaice.app.sharesight.poster.ImagePoster.HEIGHT";
	private static final String URL_TAG = "seaice.app.sharesight.poster.ImagePoster.URL";
	private static final String DEVICE_ID_TAG = "seaice.app.sharesight.poster.ImagePoster.DEVICE_ID";

	private static final String IMAGE_POST_SERVER = "http://www.zhouhaibing.com/app/sharesight/postImage";
	// private static final String IMAGE_POST_SERVER =
	// "http://sharesight.duapp.com/index.php/app/addrecord";
	// private static final String IMAGE_ADD_SERVER =
	// "http://www.zhouhaibing.com/app/sharesight/addrecord";
	private static final String IMAGE_ADD_SERVER = "http://sharesight.duapp.com/index.php/app/addrecord";
	private static final String IMAGE_PATH_SERVER = "http://www.zhouhaibing.com/static/file/app/sharesight";

	private static final int POST_IMAGE = 1;
	private static final int POST_TEXT = 2;

	private BCSSvc mBCS;

	public ImagePoster(ImagePosterCallback callback) {
		mCallback = callback;
		mBCS = new BCSSvc(null);
	}

	public void post(String filePath, int width, int height, String deviceId) {
		mCallback.beforePostImage();
		Bundle data = new Bundle();

		// Needed by self...
		data.putInt(TASK_TAG, POST_IMAGE);
		data.putInt(WIDTH_TAG, width);
		data.putInt(HEIGHT_TAG, height);
		data.putString(
				URL_TAG,
				IMAGE_PATH_SERVER
						+ filePath.substring(filePath.lastIndexOf('/')));
		data.putString(DEVICE_ID_TAG, deviceId);

		ArrayList<String> fileKeyArray = new ArrayList<String>();
		fileKeyArray.add("userfile");
		ArrayList<String> fileValueArray = new ArrayList<String>();
		fileValueArray.add(filePath);

		// Needed by the FileTask object
		data.putString(FileTask.URL_TAG, IMAGE_POST_SERVER);
		data.putStringArrayList(FileTask.FILE_KEY_ARRAY_TAG, fileKeyArray);
		data.putStringArrayList(FileTask.FILE_VALUE_ARRAY_TAG, fileValueArray);

		new FileTask(this).execute(data);

		// test baidu bcs
		mBCS.uploadFileAsync(filePath);
	}

	@Override
	public void onGetTextResult(TextResult textResult) {
		if (textResult == null) {
			// There are some error happened
			mCallback.onImagePosted(false, "FAIL");
			mCallback.afterPostImage();
			return;
		}
		Bundle data = textResult.getData();
		if (data.getInt(TASK_TAG) == POST_IMAGE) {
			data.putInt(TASK_TAG, POST_TEXT);
			data.putString(TextTask.URL_TAG, IMAGE_ADD_SERVER);

			// put the data needed by the TextTask object
			ArrayList<String> keyArray = new ArrayList<String>();
			ArrayList<String> valueArray = new ArrayList<String>();
			keyArray.add("width");
			valueArray.add(data.getInt(WIDTH_TAG) + "");
			keyArray.add("height");
			valueArray.add(data.getInt(HEIGHT_TAG) + "");
			keyArray.add("url");
			valueArray.add(data.getString(URL_TAG));
			keyArray.add("deviceId");
			valueArray.add(data.getString(DEVICE_ID_TAG));

			data.putStringArrayList(TextTask.KEY_ARRAY_TAG, keyArray);
			data.putStringArrayList(TextTask.VALUE_ARRAY_TAG, valueArray);

			new TextTask(this).execute(data);
		} else if (data.getInt(TASK_TAG) == POST_TEXT) {
			mCallback.onImagePosted(true, "OK");
			mCallback.afterPostImage();
		}
	}

}
