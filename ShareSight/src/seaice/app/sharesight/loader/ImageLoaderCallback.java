package seaice.app.sharesight.loader;

import java.util.List;

import android.graphics.Bitmap;
import seaice.app.sharesight.data.ImageMeta;

public interface ImageLoaderCallback {
	
	public void beforeLoadImageMeta();
	
	public void onImageMetaLoaded(List<ImageMeta> imageMetaList);
	
	public void afterLoadImageMeta();
	
	public void beforeLoadImage();
	
	public void onImageLoaded(int imageViewId, Bitmap bitmap);
	
	public void afterLoadImage();

}
