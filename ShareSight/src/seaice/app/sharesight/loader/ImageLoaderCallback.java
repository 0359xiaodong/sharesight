package seaice.app.sharesight.loader;

import java.util.ArrayList;

import seaice.app.sharesight.data.ImageMeta;
import android.graphics.Bitmap;
import android.os.Bundle;

public interface ImageLoaderCallback {

    public void beforeLoadImageMeta();

    public void onImageMetaLoaded(ArrayList<ImageMeta> imageMetaList,
            Bundle extras);

    public void afterLoadImageMeta();

    public void beforeLoadImage();

    public void onImageLoaded(Bitmap bitmap, Bundle extras);

    public void afterLoadImage();

}
