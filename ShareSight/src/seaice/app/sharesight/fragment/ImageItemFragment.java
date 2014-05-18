package seaice.app.sharesight.fragment;

import seaice.app.sharesight.R;
import seaice.app.sharesight.loader.ImageLoader;
import seaice.app.sharesight.loader.ImageLoaderAdapter;
import seaice.app.sharesight.loader.ImageMeta;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

public class ImageItemFragment extends Fragment {

    public static final String IMAGE_PATH_TAG = "seaice.app.sharesight.fragment.ImageItemFragment.URL";

    public static final String IMAGE_META_TAG = "seaice.app.sharesight.fragment.ImageItemFragment.IMAGE_META";

    private static final String PAGE_TAG = "seaice.app.sharesight.fragment.ImageItemFragment.PAGE";

    private ImageLoader mLoader;

    private ImageView mImageView;

    public ImageItemFragment() {
        super();

        mLoader = new ImageLoader();
        mLoader.setImageLoaderCallback(new ImageLoaderAdapter() {
            @Override
            public void onImageLoaded(Bitmap bitmap, Bundle extras) {
                if (mImageView != null) {
                    mImageView.setImageBitmap(bitmap);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(PAGE_TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(PAGE_TAG);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image_item,
                container, false);
      
        Bundle data = getArguments();
        ImageMeta imageMeta = data.getParcelable(IMAGE_META_TAG);

        mImageView = (ImageView) rootView.findViewById(R.id.image_item_image);
        TextView locationView = (TextView) rootView
                .findViewById(R.id.image_item_location);
        locationView.setTextColor(Color.BLUE);
        locationView.setText(imageMeta.getAddr());
        TextView textView = (TextView) rootView
                .findViewById(R.id.image_item_text);
        textView.setText(imageMeta.getText());

        mLoader.loadImage(imageMeta.getUrl(), null);
        return rootView;
    }
}
