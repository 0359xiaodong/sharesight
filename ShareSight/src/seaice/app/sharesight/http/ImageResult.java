package seaice.app.sharesight.http;

import android.graphics.Bitmap;
import android.os.Bundle;

public class ImageResult {

    private Bitmap bitmap;

    private Bundle data;

    public ImageResult(Bitmap bitmap, Bundle data) {
        this.bitmap = bitmap;
        this.data = data;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Bundle getData() {
        return data;
    }
}
