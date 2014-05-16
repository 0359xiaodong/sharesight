package seaice.app.sharesight.fragment;

import com.umeng.analytics.MobclickAgent;

import seaice.app.sharesight.R;
import seaice.app.sharesight.data.ImageMeta;
import seaice.app.sharesight.loader.ImageLoader;
import seaice.app.sharesight.loader.ImageLoaderAdapter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
        RelativeLayout layout = (RelativeLayout) rootView
                .findViewById(R.id.fragment_image_item_container);
        mImageView = new ImageView(getActivity());

        Bundle data = getArguments();
        ImageMeta imageMeta = data.getParcelable(IMAGE_META_TAG);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = imageMeta.getHeight() * width / imageMeta.getWidth();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                width, height);
        // params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
        // RelativeLayout.TRUE);
        // params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
        // RelativeLayout.TRUE);
        // params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        layout.addView(mImageView, params);

        mLoader.loadImage(imageMeta.getUrl(), null);
        return rootView;
    }
}
