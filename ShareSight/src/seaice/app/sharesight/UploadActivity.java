package seaice.app.sharesight;

import java.io.File;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class UploadActivity extends Activity {

	/** The preview image view */
	private ImageView mImgView;
	/** Where to read the photo */
	private String mPhotoPath;

	// The progress dialog to display percent ratio
	private ProgressDialog mProgressDialog;

	private static final int UPLOAD_SUCCESS = 1234;

	private static final int UPLOAD_FAIL = 5678;

	private Handler handler = new MyHandler(this);

	/**
	 * To avoid memory leak since there is a loop reference
	 * 
	 * @author zhb
	 * 
	 */
	private static class MyHandler extends Handler {

		private WeakReference<UploadActivity> mHost;

		public MyHandler(UploadActivity host) {
			mHost = new WeakReference<UploadActivity>(host);
		}

		@Override
		public void handleMessage(Message msg) {
			// success
			if (msg.what == 1234) {
				Toast.makeText(mHost.get(), "Upload succeeds",
						Toast.LENGTH_LONG).show();
				mHost.get().finish();
			}
			// fails
			if (msg.what == 5678) {
				Toast.makeText(mHost.get(), "Upload fails", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);

		mPhotoPath = getIntent().getStringExtra("photo");
		mImgView = (ImageView) findViewById(R.id.confirmImage);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		setImageViewSource();
	}

	public void upload(View view) {
		// begin the background upload
		Log.d("Before Upload", "Uploading " + mPhotoPath);

		// this method runs in a separate thread
		mProgressDialog = new ProgressDialog(this,
				ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setTitle("Uploading Image");
		mProgressDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				bgUpload();
			}
		}).start();
	}

	public void cancel(View view) {
		finish();
	}

	private void bgUpload() {
		boolean posted = true;

		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost httppost = new HttpPost(
				"http://www.zhouhaibing.com/app/sharesight/postImage");
		File file = new File(mPhotoPath);

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addPart("userfile", new FileBody(file));
		// MultipartEntity mpEntity = new MultipartEntity();
		// ContentBody cbFile = new FileBody(file);
		// mpEntity.addPart("userfile", cbFile);

		httppost.setEntity(builder.build());
		Log.d("Post Image File",
				"executing request " + httppost.getRequestLine());

		HttpResponse response;
		try {
			response = httpclient.execute(httppost);

			HttpEntity resEntity = response.getEntity();

			System.out.println(response.getStatusLine());
			String retVal = "";
			if (resEntity != null) {
				retVal = EntityUtils.toString(resEntity, "utf-8");
				Log.d("Response", retVal);
			}
			if (resEntity != null) {
				resEntity.consumeContent();
			}
			httpclient.getConnectionManager().shutdown();
		} catch (Exception e) {
			posted = false;
		}
		Message msg = Message.obtain();
		msg.what = posted ? UPLOAD_SUCCESS : UPLOAD_FAIL;
		handler.sendMessage(msg);
		mProgressDialog.dismiss();
	}

	private void setImageViewSource() {
		Intent intent = getIntent();
		String photoPath = intent.getStringExtra("photo");

		// Get the dimensions of the View
		int targetW = mImgView.getWidth();
		int targetH = mImgView.getHeight();

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(photoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
		// add border-radius
		mImgView.setImageBitmap(bitmap);
	}

}