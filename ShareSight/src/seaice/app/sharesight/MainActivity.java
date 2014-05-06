package seaice.app.sharesight;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends ActionBarActivity {
	private RelativeLayout mLayout;

	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mLayout = (RelativeLayout) findViewById(R.id.layout);

		mProgressDialog = new ProgressDialog(this,
				ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMessage("Loading...");

		loadImageMetaData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

	private void addImageViewList(ArrayList<ImageMeta> imageMetaList) {
		mProgressDialog.dismiss();
		for (ImageMeta imageMeta : imageMetaList) {
			addImageView(imageMeta);
		}

		// when its done, load all the image
		loadImage();
	}

	private void addImageView(ImageMeta imageMeta) {
		Log.d("Add Image", "Real Width:" + imageMeta.width + ", Real Height:"
				+ imageMeta.height);
		// generate a new id
		int id = AppUtils.generateViewId();
		Log.d("New Id", id + "");
		ImageView imageView = new ImageView(this);
		int realWidth = mColumnWidth;
		int realHeight = imageMeta.height * realWidth / imageMeta.width;
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				realWidth, realHeight);

		// where is the current minimum record
		Collections.sort(mColumnRecords, new Comparator<ColumnRecord>() {
			@Override
			public int compare(ColumnRecord lhs, ColumnRecord rhs) {
				if (lhs.height < rhs.height) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		ColumnRecord above = mColumnRecords.get(0);
		Log.d("Top Id", above.topId + "");
		Log.d("Left Id", above.leftId + "");
		above.height += realHeight;
		// find its upper view
		if (above.topId == ColumnRecord.PARENT_TOP) {
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			params.topMargin = mMarginV; // top margin
			params.bottomMargin = mMarginV; // bottom margin
		} else {
			params.addRule(RelativeLayout.BELOW, above.topId);
			params.topMargin = 0; // top margin
			params.bottomMargin = mMarginV; // bottom margin
		}
		above.topId = id;
		// find its left view
		if (above.leftId == ColumnRecord.PARENT_LEFT) {
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
					RelativeLayout.TRUE);
			params.leftMargin = mMarginH;
			params.rightMargin = mMarginH;
		} else {
			params.addRule(RelativeLayout.RIGHT_OF, above.leftId);
			params.leftMargin = 0;
			params.rightMargin = mMarginH;
		}

		// set the leftId of the right column of current
		for (ColumnRecord record : mColumnRecords) {
			if (record.column == (above.column + 1)) {
				record.leftId = id;
				break;
			}
		}

		imageView.setId(id);
		imageView.setBackgroundColor(Color.GRAY);
		mLayout.addView(imageView, params);

		// add the pair(id and imagemeta
		taskQueue.offer(new ImageTask(id, imageMeta));
	}

	private void loadImageMetaData() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		// I try to use two columns when on mobile phone
		// and three columns when on landscape view
		mColumnCnt = height > width ? 2 : 3;
		mColumnWidth = (width - (mColumnCnt + 1) * mMarginH) / mColumnCnt;

		// here is for debug output
		Log.d("Prepare Layout", "ColumnWidth" + mColumnWidth);

		// initialize the column position records
		mColumnRecords = new ArrayList<ColumnRecord>();
		for (int i = 1; i <= mColumnCnt; ++i) {
			ColumnRecord record = new ColumnRecord();
			record.height = 0;
			record.column = i;
			record.leftId = i == 1 ? ColumnRecord.PARENT_LEFT
					: ColumnRecord.INVALID_LEFT;
			record.topId = ColumnRecord.PARENT_TOP;
			mColumnRecords.add(record);
		}

		mProgressDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				bgLoadImageMetaData();
			}
		}).start();
	}

	private void loadImage() {
		final ImageTask task = taskQueue.poll();
		if (task == null) {
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				bgLoadImage(task);
			}
		}).start();

	}

	private void bgLoadImage(ImageTask task) {
		String url = task.mImgMeta.url;
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);

		try {
			HttpResponse response = httpClient.execute(httpGet);
			Bitmap bitmap = BitmapFactory.decodeStream(response.getEntity()
					.getContent());
			Message msg = Message.obtain();
			msg.what = IMAGE_LOADED;
			Bundle data = new Bundle();
			data.putInt("id", task.mId);
			data.putParcelable("bitmap", bitmap);
			msg.setData(data);

			mHandler.sendMessage(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void bgLoadImageMetaData() {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(IMAGE_META_SERVER);
		ArrayList<ImageMeta> imageMetaList = new ArrayList<ImageMeta>();

		try {
			HttpResponse response = httpClient.execute(httpGet);
			String json = EntityUtils.toString(response.getEntity());

			Log.d("Response", json);

			JsonParser parser = new JsonParser();
			Gson gson = new Gson();
			JsonArray jsonArray = parser.parse(json).getAsJsonArray();

			for (JsonElement jsonEle : jsonArray) {
				imageMetaList.add(gson.fromJson(jsonEle, ImageMeta.class));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		Bundle data = new Bundle();
		data.putParcelableArrayList(IMAGE_META_TAG, imageMetaList);

		Message msg = Message.obtain();
		msg.what = IMAGE_META_LOADED;
		msg.setData(data);

		mHandler.sendMessage(msg);
	}

	private Handler mHandler = new MyHandler(this);

	private static final int IMAGE_META_LOADED = 14122;

	private static final int IMAGE_LOADED = 22141;

	private static final String IMAGE_META_SERVER = "http://www.zhouhaibing.com/app/sharesight/getImage";

	private static final String IMAGE_META_TAG = "IMAGEMETA";

	/**
	 * To avoid memory leak since there is a loop reference
	 * 
	 * @author zhb
	 * 
	 */
	private static class MyHandler extends Handler {

		private WeakReference<MainActivity> mHost;

		public MyHandler(MainActivity host) {
			mHost = new WeakReference<MainActivity>(host);
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == IMAGE_META_LOADED) {
				ArrayList<ImageMeta> imageMetaList = msg.getData()
						.getParcelableArrayList(IMAGE_META_TAG);
				if (mHost != null) {
					// try to add only one
					mHost.get().addImageViewList(imageMetaList);
				}
			}
			if (msg.what == IMAGE_LOADED) {
				Bitmap bitmap = msg.getData().getParcelable("bitmap");
				int id = msg.getData().getInt("id");
				ImageView imgView = (ImageView) mHost.get().findViewById(id);
				imgView.setImageBitmap(bitmap);
				mHost.get().loadImage();
			}
		}
	}

	/**
	 * Record the y position list
	 */
	private ArrayList<ColumnRecord> mColumnRecords;

	private class ColumnRecord {

		int height;
		int leftId;
		int topId;
		int column;

		public static final int PARENT_LEFT = -1;

		public static final int PARENT_TOP = -3;

		public static final int INVALID_LEFT = -3;
	}

	private int mMarginH = 8;
	private int mMarginV = 8;
	private int mColumnWidth = 0;
	private int mColumnCnt = 0;

	private Queue<ImageTask> taskQueue = new LinkedList<ImageTask>();

	private class ImageTask {
		int mId;
		ImageMeta mImgMeta;

		public ImageTask(int id, ImageMeta imgMeta) {
			mId = id;
			mImgMeta = imgMeta;
		}
	}
}
