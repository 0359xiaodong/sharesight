package seaice.app.sharesight;

import java.io.File;

import seaice.app.sharesight.bcs.BCSSvc;
import seaice.app.sharesight.data.ImageMeta;
import seaice.app.sharesight.poster.ImagePoster;
import seaice.app.sharesight.poster.ImagePosterCallback;
import seaice.app.sharesight.utils.BitmapUtils;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

public class UploadActivity extends ActionBarActivity implements
        ImagePosterCallback, BDLocationListener {
    /**
     * The contrast that the caller should provide a value with this key
     */
    public static final String IMAGE_PATH_TAG = "seaice.app.sharesight.UploadActivity.IMAGE_PATH";
    /**
     * Hold this preview Image
     */
    private ImageView mImgView;

    private String mImagePath;

    private ImageMeta mImageMeta;
    /**
     * Show the location where the user are currently.
     */
    private TextView mLocationView;

    private ProgressDialog mProgressDialog;

    /**
     * The backend to post an image to server
     */
    private ImagePoster mPoster;
    /**
     * The client proxy to user the location service
     */
    private LocationClient mLocationClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        mImgView = (ImageView) findViewById(R.id.confirmImage);
        mLocationView = (TextView) findViewById(R.id.upload_location);
        mLocationView.setTextColor(Color.BLUE);

        mImagePath = getIntent().getStringExtra(IMAGE_PATH_TAG);
        Bitmap bitmap = BitmapUtils
                .decodeFileWithoutScale(new File(mImagePath));
        mImgView.setImageBitmap(bitmap);

        mProgressDialog = new ProgressDialog(this,
                ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMessage(getResources().getText(
                R.string.action_uploading)
                + "...");

        mImageMeta = new ImageMeta();
        String fileName = mImagePath.substring(mImagePath.lastIndexOf('/') + 1);
        String url = BCSSvc.generateUrl(fileName);
        mImageMeta.setUrl(url);
        mImageMeta.setWidth(bitmap.getWidth());
        mImageMeta.setHeight(bitmap.getHeight());
        mImageMeta.setDeviceId(ShareSightApplication.getDeviceId());

        mPoster = new ImagePoster(this);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(this);
        mLocationClient.start();

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Battery_Saving);
        option.setCoorType("bd09ll");
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        option.setNeedDeviceDirect(true);
        mLocationClient.setLocOption(option);
        mLocationClient.requestLocation();

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
    public void onStop() {
        super.onStop();
        // should stop the location client
        if (mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_upload) {
            mPoster.post(mImagePath, mImageMeta);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onImagePosted(boolean status, String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void beforePostImage() {
        mProgressDialog.show();
    }

    public void afterPostImage() {
        mProgressDialog.dismiss();
        finish();
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        if (location == null) {
            return;
        }
        mLocationView.setText(location.getAddrStr());
        mImageMeta.setCity(location.getCity());
        mImageMeta.setAddr(location.getAddrStr());
        mImageMeta.setLongitude(location.getLongitude());
        mImageMeta.setLatitude(location.getLatitude());
    }

    @Override
    public void onReceivePoi(BDLocation location) {
        // I DONOT CARE HERE
    }

}