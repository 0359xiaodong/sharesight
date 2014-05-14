package seaice.app.sharesight;

import java.io.File;

import seaice.app.sharesight.poster.ImagePoster;
import seaice.app.sharesight.poster.ImagePosterCallback;
import seaice.app.sharesight.utils.BitmapUtils;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
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

public class UploadActivity extends ActionBarActivity implements
        ImagePosterCallback, BDLocationListener {

    private ImageView mImgView;
    private int mWidth = 0;
    private int mHeight = 0;
    private String mImagePath;

    private TextView mLocationView;

    // The progress dialog to display percent ratio
    private ProgressDialog mProgressDialog;

    private ImagePoster mPoster;
    private LocationClient mLocationClient;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        mProgressDialog = new ProgressDialog(this,
                ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMessage(getResources().getText(
                R.string.action_uploading)
                + "...");

        mImagePath = getIntent().getStringExtra(MainActivity.IMAGE_PATH_TAG);
        mImgView = (ImageView) findViewById(R.id.confirmImage);
        mLocationView = (TextView) findViewById(R.id.upload_location);
        mLocationView.setTextColor(Color.BLUE);

        mPoster = new ImagePoster(this);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(this);
        mLocationClient.start();

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        option.setNeedDeviceDirect(true);
        mLocationClient.setLocOption(option);
        mLocationClient.requestLocation();
    }

    public void onStop() {
        super.onStop();
        if (mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        setImageViewSource();
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
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            mPoster.post(mImagePath, mWidth, mHeight, tm.getDeviceId());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setImageViewSource() {
        Bitmap bitmap = BitmapUtils.decodeFileWithoutScale(new File(mImagePath));
        mImgView.setImageBitmap(bitmap);
        mWidth = bitmap.getWidth();
        mHeight = bitmap.getHeight();
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
    }

    @Override
    public void onReceivePoi(BDLocation location) {
        // I DONOT CARE HERE
    }

}