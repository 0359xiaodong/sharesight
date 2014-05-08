package seaice.app.sharesight.data;

public class ImageTask {

	private int mImageViewId;
	
	private ImageMeta mImageMeta;
	
	public ImageTask(int imageViewId, ImageMeta imageMeta) {
		mImageViewId = imageViewId;
		mImageMeta = imageMeta;
	}
	
	public int getImageViewId() {
		return mImageViewId;
	}
	
	public ImageMeta getImageMeta() {
		return mImageMeta;
	}
}
