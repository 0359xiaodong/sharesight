package seaice.app.sharesight.loader;

import java.util.List;

import android.graphics.Bitmap;
import seaice.app.sharesight.data.ImageMeta;

public interface LoaderCallback {
	
	public void onImageMetaLoaded(List<ImageMeta> imageMetaList);
	
	public void onImageLoaded(Bitmap bitmap);

}
