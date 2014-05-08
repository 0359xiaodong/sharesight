package seaice.app.sharesight;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import seaice.app.sharesight.utils.AppUtils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class UploadActivity extends Activity {

	/** The preview image view */
	private ImageView mImgView;
	/** The Bitmap */
	private Bitmap mBitmap;
	/** Where to read the photo */
	private String mPhotoPath;
	/** Photo Width */
	private int mPhotoWidth = 0;
	/** Photo Height */
	private int mPhotoHeight = 0;
	/** Device Id to identify this app */
	private String mDeviceId;

	// The progress dialog to display percent ratio
	private ProgressDialog mProgressDialog;

	private static final int UPLOAD_IMAGE = 1234;

	private static final int ADD_RECORD = 9012;

	private static final String IMAGE_HOST_SERVER = "http://www.freeimagehosting.net/upl.php";

	private static final String DATA_SERVER = "http://www.zhouhaibing.com/app/sharesight/addrecord";

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
			if (msg.what == UPLOAD_IMAGE) {
				Bundle data = msg.getData();
				boolean status = data.getBoolean("status");
				if (status) {
					String url = data.getString("url");
					mHost.get().addRecordAsync(url);
				} else {
					Toast.makeText(mHost.get(), "Can not upload",
							Toast.LENGTH_LONG).show();
					mHost.get().finish();
				}
			} else if (msg.what == ADD_RECORD) {
				mHost.get().mProgressDialog.dismiss();
				Toast.makeText(mHost.get(), "Upload succeeds",
						Toast.LENGTH_LONG).show();
				mHost.get().finish();
			}
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);

		mPhotoPath = getIntent().getStringExtra("photo");
		mImgView = (ImageView) findViewById(R.id.confirmImage);

		TelephonyManager tm = (TelephonyManager) this
				.getSystemService(TELEPHONY_SERVICE);
		// how to identify this image
		mDeviceId = tm.getDeviceId();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		setImageViewSource();
	}

	public void cancel(View view) {
		finish();
	}

	public void uploadAsync(View view) {
		// this method runs in a separate thread
		mProgressDialog = new ProgressDialog(this,
				ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setTitle("Uploading Image");
		mProgressDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				upload();
			}
		}).start();
	}

	private void upload() {
		boolean status = true;
		String imageUrl = "";

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(IMAGE_HOST_SERVER);

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addBinaryBody("file",
				AppUtils.saveBitmapToFile(mBitmap, mPhotoPath));
		builder.addTextBody("format", "redirect");

		httppost.setEntity(builder.build());

		HttpResponse response;
		try {
			response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();

			String output = EntityUtils.toString(resEntity);
			Document doc = Jsoup.parse(output);
			imageUrl = doc.getElementsByAttributeValue("name", "htmlthumb")
					.attr("value");
			Log.d("Uploaded Image Url", imageUrl);
			resEntity.consumeContent();
		} catch (Exception e) {
			e.printStackTrace();
			status = false;
		}
		Message msg = Message.obtain();
		msg.what = UPLOAD_IMAGE;
		Bundle data = new Bundle();
		Log.d("Upload Status", String.valueOf(status));
		data.putBoolean("status", status);
		data.putString("url", imageUrl);
		msg.setData(data);
		handler.sendMessage(msg);
	}

	private void addRecordAsync(final String url) {

		new Thread(new Runnable() {
			public void run() {
				addRecord(url, mDeviceId, mPhotoWidth, mPhotoHeight);
			}
		}).start();
	}

	private void addRecord(String url, String deviceId, int width, int height) {
		boolean status = true;

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(DATA_SERVER);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("url", url));
		params.add(new BasicNameValuePair("deviceId", deviceId));
		params.add(new BasicNameValuePair("width", width + ""));
		params.add(new BasicNameValuePair("height", height + ""));

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse response = httpClient.execute(httpPost);
			System.out.println(EntityUtils.toString(response.getEntity()));
		} catch (Exception e) {
			status = false;
		}

		Message msg = Message.obtain();
		msg.what = ADD_RECORD;
		Bundle data = new Bundle();
		data.putBoolean("status", status);
		msg.setData(data);
		handler.sendMessage(msg);
	}

	private void setImageViewSource() {
		File imageFile = new File(mPhotoPath);
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			FileInputStream stream1 = new FileInputStream(imageFile);
			BitmapFactory.decodeStream(stream1, null, options);
			stream1.close();
			final int REQUIRED_SIZE = 70;
			mPhotoWidth = options.outWidth;
			mPhotoHeight = options.outHeight;
			int width = options.outWidth, height = options.outHeight;
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

}