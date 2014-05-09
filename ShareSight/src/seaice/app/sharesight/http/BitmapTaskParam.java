package seaice.app.sharesight.http;

public class BitmapTaskParam {

	private int imageViewId;

	private String url;

	public BitmapTaskParam(int imageViewId, String url) {
		this.imageViewId = imageViewId;
		this.url = url;
	}

	public int getImageViewId() {
		return imageViewId;
	}

	public String getUrl() {
		return url;
	}
}