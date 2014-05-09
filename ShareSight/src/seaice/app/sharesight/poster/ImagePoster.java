package seaice.app.sharesight.poster;

import android.os.Bundle;
import seaice.app.sharesight.http.TextResult;
import seaice.app.sharesight.http.TextResultCallback;
import seaice.app.sharesight.http.post.FileTask;

public class ImagePoster implements TextResultCallback {

	private ImagePosterCallback mCallback;

	public ImagePoster(ImagePosterCallback callback) {
		mCallback = callback;
	}

	public void upload(String url, String fileTag, String filePath) {
		Bundle data = new Bundle();
		new FileTask(this).execute(data);
	}

	@Override
	public void onGetTextResult(TextResult textResult) {
		mCallback.onUploadDone(true, "OK");
	}

}
