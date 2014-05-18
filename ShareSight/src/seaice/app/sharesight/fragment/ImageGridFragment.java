package seaice.app.sharesight.fragment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import seaice.app.sharesight.R;
import seaice.app.sharesight.ViewActivity;
import seaice.app.sharesight.loader.ImageLoader;
import seaice.app.sharesight.loader.ImageLoaderCallback;
import seaice.app.sharesight.loader.ImageMeta;
import seaice.app.sharesight.views.ImageScrollView;
import seaice.app.sharesight.views.ImageScrollView.ImageViewClickListener;
import seaice.app.sharesight.views.ImageScrollView.ScrollViewListenner;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.umeng.analytics.MobclickAgent;

/**
 * A fragment to display image wall maybe I can call it like this, This fragment
 * will reuse most of the code from MainActivity(previous version), Based on the
 * ImageScrollView.
 * 
 * @author zhb
 * 
 */
public class ImageGridFragment extends Fragment implements ImageLoaderCallback,
        BDLocationListener {

    public static final String IMAGE_VIEW_ID_TAG = "seaice.app.sharesight.fragment.ImageGridFragment.IMAGE_VIEW_ID";
    private static final String UMENG_PAGE_TAG = "seaice.app.sharesight.fragment.ImageGridFragment.PAGE";
    protected static final String IMAGE_META_LIST_TAG = "seaice.app.sharesight.fragment.ImageGridFragment.IMAGE_META_LIST";
    protected static final String PAGE_TAG = "seaice.app.sharesight.fragment.ImageGridFragment.PAGE";

    protected ImageScrollView mScrollView;

    private Queue<ImageTask> mTaskQueue = new LinkedList<ImageTask>();
    protected ArrayList<ImageMeta> mMetaList;
    protected int mPage = 0;
    protected static final int IMAGE_COUNT_PER_PAGE = 10;

    protected ImageLoader mLoader;
    protected boolean mLoading = false;
    private ProgressDialog mProgressDialog;

    protected LocationClient mLocationClient;

    protected BDLocation mLocation;

    protected String mDefaultCity = "北京";

    public ImageGridFragment() {
        super();
        setHasOptionsMenu(true);

        mLoader = new ImageLoader(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UMENG_PAGE_TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UMENG_PAGE_TAG);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(IMAGE_META_LIST_TAG, mMetaList);
        outState.putInt(PAGE_TAG, mPage);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mProgressDialog = new ProgressDialog(activity,
                ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMessage(getResources().getText(
                R.string.action_loading)
                + "...");

        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mLoader.setCancelled(true);
            }
        });

        mLocationClient = new LocationClient(activity);
        mLocationClient.registerLocationListener(this);

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Battery_Saving);
        option.setCoorType("bd09ll");
        option.setScanSpan(0);
        option.setIsNeedAddress(true);
        option.setNeedDeviceDirect(true);
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_image_grid, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            onRefresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image_grid,
                container, false);
        mScrollView = (ImageScrollView) rootView.findViewById(R.id.container);
        onGetScrollView();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null) {
            onRefresh();
        } else {
            mMetaList = savedInstanceState
                    .getParcelableArrayList(IMAGE_META_LIST_TAG);
            mPage = savedInstanceState.getInt(PAGE_TAG);

            mTaskQueue.clear();
            addImageViewList(mMetaList);
            ImageTask task = mTaskQueue.poll();

            // If the image meta list is empty, then there is no task...
            if (task == null) {
                return;
            }
            Bundle data = new Bundle();
            data.putInt(IMAGE_VIEW_ID_TAG, task.imageViewId);
            mLoader.loadImage(task.url, data);
        }
    }

    /**
     * Subclasses can override this method to give your ScrollViewListenner and
     * ImageViewClickListener
     */
    public void onGetScrollView() {
        if (mScrollView == null) {
            throw new IllegalStateException();
        }
        mScrollView.setScrollViewListener(new DefaultScrollViewListenner());
        mScrollView
                .setImageViewClickListener(new DefaultImageViewClickListener());
    }

    private void onRefresh() {
        mPage = 0;
        if (mMetaList != null) {
            mMetaList.clear();
        }
        mScrollView.removeAllImageViews();

        mLocationClient.start();
        mLocationClient.requestLocation();
    }

    /**
     * subclasses can override this method to provide yourself strategy
     */
    public void onLoadImageMeta() {
        if (mLocation == null) {
            mLoader.loadImageMetaList(mDefaultCity, mPage,
                    IMAGE_COUNT_PER_PAGE, null);
        } else {
            mLoader.loadImageMetaList(mLocation.getCity(), mPage,
                    IMAGE_COUNT_PER_PAGE, null);
        }
    }

    protected class DefaultScrollViewListenner implements ScrollViewListenner {

        @Override
        public void onScrollChanged(ImageScrollView scrollView, int x, int y,
                int oldx, int oldy) {
            View view = (View) scrollView
                    .getChildAt(scrollView.getChildCount() - 1);
            int diff = (view.getBottom() - (scrollView.getHeight() + scrollView
                    .getScrollY()));
            /* If we reach the bottom and is not loading currently */
            if (diff == 0 && !mLoading) {
                mLoading = true;

                if ((mMetaList.size() % IMAGE_COUNT_PER_PAGE == 0)) {
                    ++mPage;
                    mLocationClient.requestLocation();
                }
            }
        }

    }

    protected class DefaultImageViewClickListener implements
            ImageViewClickListener {

        @Override
        public void onImageViewClicked(ImageView imageView, int index) {
            Activity activity = getActivity();
            Intent intent = new Intent(activity, ViewActivity.class);
            intent.putParcelableArrayListExtra(
                    ViewActivity.IMAGE_META_LIST_TAG, mMetaList);
            intent.putExtra(ViewActivity.CURRENT_ITEM_TAG, index);
            if (mLocation != null) {
                intent.putExtra(ViewActivity.CITY_TAG, mLocation.getCity());
            } else {
                intent.putExtra(ViewActivity.CITY_TAG, mDefaultCity);
            }
            startActivity(intent);
        }

    }

    @Override
    public void onImageMetaLoaded(ArrayList<ImageMeta> imageMetaList,
            Bundle extras) {
        if (mMetaList == null) {
            mMetaList = imageMetaList;
        } else {
            mMetaList.addAll(imageMetaList);
        }

        addImageViewList(imageMetaList);

        ImageTask task = mTaskQueue.poll();

        // If the image meta list is empty, then there is no task...
        if (task == null) {
            return;
        }
        Bundle data = new Bundle();
        data.putInt(IMAGE_VIEW_ID_TAG, task.imageViewId);
        mLoader.loadImage(task.url, data);
    }

    @Override
    public void onImageLoaded(Bitmap bitmap, Bundle extras) {
        ImageView imageView = (ImageView) mScrollView.findViewById(extras
                .getInt(IMAGE_VIEW_ID_TAG));
        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }

        // Needs continue?
        ImageTask task = mTaskQueue.poll();
        if (task == null) {
            return;
        }

        Bundle data = new Bundle();
        data.putInt(IMAGE_VIEW_ID_TAG, task.imageViewId);
        mLoader.loadImage(task.url, data);
    }

    @Override
    public void beforeLoadImageMeta() {
        mProgressDialog.show();
    }

    @Override
    public void beforeLoadImage() {

    }

    @Override
    public void afterLoadImageMeta() {
        mLoading = false;
        mProgressDialog.dismiss();
    }

    @Override
    public void afterLoadImage() {

    }

    private static class ImageTask {
        int imageViewId;
        String url;

        public ImageTask(int imageViewId, String url) {
            this.imageViewId = imageViewId;
            this.url = url;
        }
    }

    /**
     * Append images to this layout, firstly, prepare the image layouts, then
     * load images one by one.
     * 
     * @param imageMetaList
     */
    private void addImageViewList(List<ImageMeta> imageMetaList) {
        for (ImageMeta imageMeta : imageMetaList) {
            int retId = mScrollView.addImageView(imageMeta.getWidth(),
                    imageMeta.getHeight());
            mTaskQueue.add(new ImageTask(retId, imageMeta.getUrl()));
        }
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        mLocation = location;
        mLocationClient.stop();
        onLoadImageMeta();
    }

    @Override
    public void onReceivePoi(BDLocation location) {

    }
}
