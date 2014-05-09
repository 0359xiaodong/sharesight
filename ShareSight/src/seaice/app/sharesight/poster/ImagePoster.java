package seaice.app.sharesight.poster;

import seaice.app.sharesight.http.HttpTextTaskClient;
import seaice.app.sharesight.http.TextTaskResult;

public class ImagePoster implements HttpTextTaskClient {

	private ImagePosterCallback mCallback;

	public ImagePoster(ImagePosterCallback callback) {
		mCallback = callback;
	}

	public void upload(String path) {

	}

	@Override
	public void onGetTextTaskResult(TextTaskResult result) {
		mCallback.onUploadDone(true, "OK");
	}

}
