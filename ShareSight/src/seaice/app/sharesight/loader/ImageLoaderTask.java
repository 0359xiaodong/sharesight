package seaice.app.sharesight.loader;

public class ImageLoaderTask {

	private int imageViewId;

	private String url;

	public ImageLoaderTask(int imageViewId, String url) {
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
