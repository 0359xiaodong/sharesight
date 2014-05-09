package seaice.app.sharesight.loader;

import java.util.List;

import seaice.app.sharesight.data.ImageMeta;
import android.graphics.Bitmap;

public class ImageLoaderAdapter implements ImageLoaderCallback {

	@Override
	public void beforeLoadImageMeta() {}

	@Override
	public void onImageMetaLoaded(List<ImageMeta> imageMetaList) {}

	@Override
	public void afterLoadImageMeta() {}

	@Override
	public void beforeLoadImage() {}

	@Override
	public void onImageLoaded(int imageViewId, Bitmap bitmap) {}

	@Override
	public void afterLoadImage() {}

}
