package seaice.app.sharesight.http;

import android.graphics.Bitmap;

public class BitmapTaskResult {
	
	private int mImageViewId;

	private String mUrl;
	
	private Bitmap mBitmap;
	
	public BitmapTaskResult(int imageViewId, String url, Bitmap bitmap) {
		mImageViewId = imageViewId;
		mUrl = url;
		mBitmap = bitmap;
	}
	
	public int getImageViewId() {
		return mImageViewId;
	}
	
	public String getUrl() {
		return mUrl;
	}
	
	public Bitmap getBitmap() {
		return mBitmap;
	}
}
