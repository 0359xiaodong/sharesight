package seaice.app.sharesight.http;

import android.graphics.Bitmap;

public class BitmapTaskResult {

	private int imageViewId;

	private String url;

	private Bitmap bitmap;

	public BitmapTaskResult(int imageViewId, String url, Bitmap bitmap) {
		this.imageViewId = imageViewId;
		this.url = url;
		this.bitmap = bitmap;
	}

	public int getImageViewId() {
		return imageViewId;
	}

	public String getUrl() {
		return url;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}
}
