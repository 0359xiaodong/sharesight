package seaice.app.sharesight;

import java.util.ArrayList;
import java.util.List;

import seaice.app.sharesight.data.ImageMeta;
import seaice.app.sharesight.fragment.ImageItemFragment;
import seaice.app.sharesight.loader.ImageLoader;
import seaice.app.sharesight.loader.ImageLoaderAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ImageItemPagerAdapter extends FragmentStatePagerAdapter {

    private List<ImageMeta> mMetaList;

    private ImageLoader mLoader;

    private static final int IMAGE_COUNT_PER_PAGE = 10;

    public ImageItemPagerAdapter(FragmentManager fm,
            ArrayList<ImageMeta> metaList) {
        super(fm);

        this.mMetaList = metaList;
        mLoader = new ImageLoader(new ImageLoaderAdapter() {
            @Override
            public void onImageMetaLoaded(ArrayList<ImageMeta> imageMetaList,
                    Bundle extras) {
                if (imageMetaList != null) {
                    mMetaList.addAll(imageMetaList);
                    ImageItemPagerAdapter.this.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public Fragment getItem(int i) {
        int total = getCount();
        // if it is the last image
        if (i == (total - 1) && (total % IMAGE_COUNT_PER_PAGE == 0)) {
            mLoader.loadImageMetaList(total / IMAGE_COUNT_PER_PAGE,
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
