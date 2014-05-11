package seaice.app.sharesight;

import java.io.File;

import seaice.app.sharesight.poster.ImagePoster;
import seaice.app.sharesight.poster.ImagePosterCallback;
import seaice.app.sharesight.utils.AppUtils;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class UploadActivity extends ActionBarActivity implements
        ImagePosterCallback {

    private ImageView mImgView;
    private int mWidth = 0;
    private int mHeight = 0;
    private String mImagePath;

    // The progress dialog to display percent ratio
    private ProgressDialog mProgressDialog;

    private ImagePoster mPoster;

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

        mPoster = new ImagePoster(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        setImageViewSource();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.upload, menu);
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
        Bitmap bitmap = AppUtils.decodeFileWithoutScale(new File(mImagePath));
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

}