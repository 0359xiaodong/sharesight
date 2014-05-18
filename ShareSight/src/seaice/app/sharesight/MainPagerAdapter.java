package seaice.app.sharesight;

import seaice.app.sharesight.fragment.ImageGridFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private String[] mTitles;

    private String mCity;

    public MainPagerAdapter(FragmentManager fm, String city) {
        super(fm);

        mTitles = ShareSightApplication.getContext().getResources()
                .getStringArray(R.array.pager_titles);
        mCity = city;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new ImageGridFragment();
        Bundle data = new Bundle();
        data.putString(ImageGridFragment.CITY_TAG, mCity);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public String getPageTitle(int i) {
        return mTitles[i];
    }
}
