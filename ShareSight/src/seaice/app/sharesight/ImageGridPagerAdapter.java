package seaice.app.sharesight;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ImageGridPagerAdapter extends FragmentPagerAdapter {

    public ImageGridPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
       return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public String getPageTitle(int i) {
        return "";
    }
}
