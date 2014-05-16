package seaice.app.sharesight;

import seaice.app.sharesight.fragment.ImageGridFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private String[] mTitles;

    private Fragment[] mFragments;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);

        mTitles = ShareSightApplication.getContext().getResources()
                .getStringArray(R.array.pager_titles);
        mFragments = new Fragment[] { new ImageGridFragment(),
                new ImageGridFragment(), new ImageGridFragment(),
                new ImageGridFragment() };
    }

    @Override
    public Fragment getItem(int i) {
        return mFragments[i];
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
