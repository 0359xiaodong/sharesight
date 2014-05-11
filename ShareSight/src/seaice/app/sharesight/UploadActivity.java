package seaice.app.sharesight;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import seaice.app.sharesight.poster.ImagePoster;
import seaice.app.sharesight.poster.ImagePosterCallback;
import seaice.app.sharesight.utils.AppUtils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class UploadActivity extends Activity implements ImagePosterCallback {

    private ImageView mImgView;
    private Bitmap mBitmap;
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

        mImagePath = getIntent().getStringExtra(MainActivity.IMAGE_PATH_TAG);
        mImgView = (ImageView) findViewById(R.id.confirmImage);

        mPoster = new ImagePoster(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        setImageViewSource();
    }

    /**
     * listener function when the user clicked the cancel button
     * 
     * @param view
     */
    public void cancel(View view) {
        finish();
    }

    /**
     * listener function when the user clicked the ok button
     * 
     * @param view
     */
    public void upload(View view) {
        AppUtils.saveBitmapToFile(mBitmap, mImagePath);
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mPoster.post(mImagePath, mWidth, mHeight, tm.getDeviceId());
    }

    private void setImageViewSource() {
        File imageFile = new File(mImagePath);
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(imageFile);
            BitmapFactory.decodeStream(stream1, null, options);
            stream1.close();
            final int REQUIRED_SIZE = 70;
            int width = mWidth = options.outWidth;
            int height = mHeight = options.outHeight;
            int scale = 1;
            while (true) {
                if (width / 2 < REQUIRED_SIZE || height / 2 < REQUIRED_SIZE)
                    break;
                width /= 2;
                height /= 2;
                scale *= 2;
            }
            if (scale >= 2) {
                scale /= 2;
            }
            // decode with inSampleSize
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(imageFile);
            mBitmap = BitmapFactory.decodeStream(stream2, null, options2);
            mImgView.setImageBitmap(mBitmap);
            stream2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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