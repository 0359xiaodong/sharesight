package seaice.app.sharesight.http;

public class BitmapTaskParam {

	private int mImageViewId;
	
	private String mUrl;
	
	public BitmapTaskParam(int imageViewId, String url) {
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
