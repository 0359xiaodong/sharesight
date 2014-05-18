package seaice.app.sharesight;

import java.io.File;
import java.io.IOException;

import seaice.app.sharesight.utils.AppUtils;
import seaice.app.sharesight.utils.BitmapUtils;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

/**
 * Here is the entrance Activity for this application, there will be four tabs,
 * and so four fragments.
 * 
 * @author zhb
 * 
 */
public class MainActivity extends ActionBarActivity implements TabListener {

    /**
     * Request the camera to take a picture
     */
    private static final int REQUEST_IMAGE_CAPTURE = 14221;
    /**
     * Request to select a photo from media store
     */
    private static final int REQUEST_IMAGE_SELECT = 12241;
    /**
     * Where to save the to be uploaded picture
     */
    private static final String IMAGE_CACHE_PATH = Environment
            .getExternalStorageDirectory() + "/ShareSight/cache/capture";

    private String mImagePath;
    private ViewPager mViewPager;

    /**
     * Umeng API to push message to user
     */
    private PushAgent mPushAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        MainPagerAdapter adapter = new MainPagerAdapter(
                getSupportFragmentManager());
        mViewPager.setAdapter(adapter);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar.setSelectedNavigationItem(position);
                    }
                });

        for (int i = 0; i < adapter.getCount(); i++) {
            actionBar.addTab(actionBar.newTab()
                    .setText(adapter.getPageTitle(i)).setTabListener(this));
        }

        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable();

        PushAgent.getInstance(this).onAppStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onSaveInstanceState(Bundle instanceState) {
        instanceState.putString(UploadActivity.IMAGE_PATH_TAG, mImagePath);
    }

    @Override
    public void onRestoreInstanceState(Bundle instanceState) {
        mImagePath = instanceState.getString(UploadActivity.IMAGE_PATH_TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_camera_capture) {
            // handle the case when we need to capture an instance photo
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                File imageFile = null;
                try {
                    // where the photo should locate
                    imageFile = BitmapUtils.createTempImageFile(
                            IMAGE_CACHE_PATH, ShareSightApplication.getDeviceId());
                    mImagePath = imageFile.getAbsolutePath();
                } catch (IOException e) {
                    // OMMIT THIS EXCEPTION
                }
                if (imageFile == null) {
                    Toast.makeText(
                            this,
                            getResources().getText(
                                    R.string.action_sdcard_notfound),
                            Toast.LENGTH_LONG).show();
                    return true;
                }
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(imageFile));
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
            return true;
        } else if (id == R.id.action_camera_select) {
            // handle the case when we need to select a file from MediaStore
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, REQUEST_IMAGE_SELECT);
        } else if (id == R.id.action_exit) {
            // exit this activity, also means exit the application
            if (mPushAgent.isEnabled()) {
                mPushAgent.disable();
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Image Capture returns
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // scale it and save again
            BitmapUtils.decodeAndSave(mImagePath, mImagePath, this);
            Intent uploadActivity = new Intent(this, UploadActivity.class);
            uploadActivity.putExtra(UploadActivity.IMAGE_PATH_TAG, mImagePath);
            startActivity(uploadActivity);
        } else if (requestCode == REQUEST_IMAGE_SELECT
                && resultCode == RESULT_OK) {
            Uri selected = data.getData();
            try {
                File cacheFile = BitmapUtils.createTempImageFile(
                        IMAGE_CACHE_PATH, ShareSightApplication.getDeviceId());
                mImagePath = cacheFile.getAbsolutePath();
                // move the selected file into cache folder
                BitmapUtils.decodeAndSave(
                        AppUtils.getRealPathFromUri(this, selected),
                        mImagePath, this);
                Intent uploadActivity = new Intent(this, UploadActivity.class);
                uploadActivity.putExtra(UploadActivity.IMAGE_PATH_TAG,
                        mImagePath);
                startActivity(uploadActivity);
            } catch (IOException e) {
                Toast.makeText(
                        this,
                        getResources().getText(R.string.action_sdcard_notfound),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {
        // I do not care currently
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction) {
        // I do not care currently
    }
}
