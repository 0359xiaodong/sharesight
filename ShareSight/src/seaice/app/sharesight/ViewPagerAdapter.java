package seaice.app.sharesight;

import java.util.ArrayList;
import java.util.List;

import seaice.app.sharesight.fragment.ImageItemFragment;
import seaice.app.sharesight.loader.ImageLoader;
import seaice.app.sharesight.loader.ImageLoaderAdapter;
import seaice.app.sharesight.loader.ImageMeta;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * The Dynamic Image Displayer, which means the image array number should be
 * dynamically increased.
 * 
 * @author zhb
 * 
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<ImageMeta> mMetaList;
    private String mCity;

    private ImageLoader mLoader;

    private static final int IMAGE_COUNT_PER_PAGE = 10;

    public ViewPagerAdapter(FragmentManager fm, ArrayList<ImageMeta> metaList,
            String city) {
        super(fm);

        this.mMetaList = metaList;
        this.mCity = city;
        mLoader = new ImageLoader(new ImageLoaderAdapter() {
            @Override
            public void onImageMetaLoaded(ArrayList<ImageMeta> imageMetaList,
                    Bundle extras) {
                if (imageMetaList != null) {
                    mMetaList.addAll(imageMetaList);
                    // Must invoke this method, or there is
                    // IllegalStateException
                    ViewPagerAdapter.this.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public Fragment getItem(int i) {
        int total = getCount();
        // if it is the last image and there may be more image
        if (i == (total - 1) && (total % IMAGE_COUNT_PER_PAGE == 0)) {
            mLoader.loadCityMetaList(mCity, total / IMAGE_COUNT_PER_PAGE,
                    IMAGE_COUNT_PER_PAGE, null);
        }
        Fragment fragment = new ImageItemFragment();
        ImageMeta meta = mMetaList.get(i);
        Bundle data = new Bundle();
        data.putParcelable(ImageItemFragment.IMAGE_META_TAG, meta);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public int getCount() {
        return mMetaList.size();
    }
}
