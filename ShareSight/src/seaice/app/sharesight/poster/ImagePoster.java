package seaice.app.sharesight.poster;

import android.os.Bundle;
import seaice.app.sharesight.http.TextResult;
import seaice.app.sharesight.http.TextResultClient;
import seaice.app.sharesight.http.post.FileTask;
import seaice.app.sharesight.http.post.TextTask;

public class ImagePoster implements TextResultClient {

	private ImagePosterCallback mCallback;

	private static final String TASK_TAG = "TASK";

	private static final int POST_IMAGE = 1;

	private static final int POST_TEXT = 2;

	public ImagePoster(ImagePosterCallback callback) {
		mCallback = callback;
	}

	public void post(ImagePosterTask task) {
		Bundle data = new Bundle();
		data.putInt(TASK_TAG, POST_IMAGE);
		new FileTask(this).execute(data);
	}

	@Override
	public void onGetTextResult(TextResult textResult) {
		Bundle data = textResult.getData();
		if (data.getInt(TASK_TAG) == POST_IMAGE) {
			data.putInt(TASK_TAG, POST_TEXT);
			new TextTask(this).execute(data);
		} else if (data.getInt(TASK_TAG) == POST_TEXT) {
			mCallback.onUploadDone(true, "OK");
		}
	}

	public String getUrlTag() {
		return "url";
	}

}
