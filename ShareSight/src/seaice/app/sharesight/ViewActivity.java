package seaice.app.sharesight;

import java.util.ArrayList;

import seaice.app.sharesight.data.ImageMeta;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class ViewActivity extends ActionBarActivity {

    public static final String CURRENT_ITEM_TAG = "seaice.app.sharesight.ViewActivity.CURRENT_ITEM";
    public static final String IMAGE_META_LIST_TAG = "seaice.app.sharesight.ViewActivity.IMAGE_META_LIST";

    private ArrayList<ImageMeta> mMetaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        int currentIndex = getIntent().getIntExtra(CURRENT_ITEM_TAG, 0);
        mMetaList = getIntent()
                .getParcelableArrayListExtra(IMAGE_META_LIST_TAG);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        PagerAdapter adapter = new ImageItemPagerAdapter(
                getSupportFragmentManager(), mMetaList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentIndex);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
