package seaice.app.sharesight;

import seaice.app.sharesight.fragment.ImageGridFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private String[] mTitles;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);

        mTitles = ShareSight.getContext().getResources()
                .getStringArray(R.array.pager_titles);
    }

    @Override
    public Fragment getItem(int i) {
        return new ImageGridFragment();
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
