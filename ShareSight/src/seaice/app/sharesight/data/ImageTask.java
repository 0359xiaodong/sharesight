package seaice.app.sharesight.data;

public class ImageTask {

	private int mImageViewId;

	private String mUrl;

	public ImageTask(int imageViewId, String url) {
		mImageViewId = imageViewId;
		mUrl = url;
	}

	public int getImageViewId() {
		return mImageViewId;
	}

	public String getUrl() {
		return mUrl;
	}
}
