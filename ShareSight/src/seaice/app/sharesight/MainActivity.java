package seaice.app.sharesight;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import seaice.app.sharesight.data.ColumnMeta;
import seaice.app.sharesight.data.ImageMeta;
import seaice.app.sharesight.data.ImageTask;
import seaice.app.sharesight.loader.FileCache;
import seaice.app.sharesight.utils.AppUtils;
import seaice.app.sharesight.views.MyScrollView;
import seaice.app.sharesight.views.MyScrollView.ScrollViewListener;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * The entry of this application, this activity will take the responsibility to
 * display images, Firstly, it will try to get the meta data of image list. Here
 * the meta data means the image width and height and also the url which points
 * to it. It is important to decide the Images layouts.. Then this activity will
 * load each image one by one.
 * 
 * @author zhb
 * 
 */
public class MainActivity extends ActionBarActivity {

	/**
	 * The container to hold all the ImageViews
	 */
	private RelativeLayout mLayout;
	/**
	 * While taking communication with server, show this dialog
	 */
	private ProgressDialog mProgressDialog;
	/**
	 * The ScrollView container..
	 */
	private MyScrollView mScrollView;

	private FileCache mFileCache;

	/** Activity status variable */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// we need the imei number to identify this mobile
		TelephonyManager tm = (TelephonyManager) this
				.getSystemService(TELEPHONY_SERVICE);
		tm.getDeviceId();

		mLayout = (RelativeLayout) findViewById(R.id.layout);
		mScrollView = (MyScrollView) findViewById(R.id.container);

		mScrollView.setScrollViewListener(new ScrollViewListener() {

			@Override
			public void onScrollChanged(MyScrollView scrollView, int x, int y,
					int oldx, int oldy) {
				View view = (View) scrollView.getChildAt(scrollView
						.getChildCount() - 1);
				int diff = (view.getBottom() - (scrollView.getHeight() + scrollView
						.getScrollY()));

				if (diff == 0) {
					// load the next page photo list
					loadImageMetaListAsync(mImageCount);
				}
			}

		});

		mFileCache = new FileCache();
	}

	public void onStart() {
		super.onStart();
		if (mCurrentIndex > 0) {
			restoreFromTaskList();
			return;
		}
		// READD IMAGE VIEW LIST
		// If it the second time to display the ImageMetaList
		mProgressDialog = new ProgressDialog(this,
				ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMessage("Loading...");
		// PREPARE LAYOUT
		prepareLayoutArguments();
		// LOAD IMAGE META LIST
		mImageCount = 0;
		loadImageMetaListAsync(mImageCount);
	}

	public void onStop() {
		super.onStop();
		// HERE WE NEED TO FREE MEMORY
		// REMOVE ALL THE IMAGE VIEWS
		for (int i = 0; i < mLayout.getChildCount(); ++i) {
			ImageView imgView = (ImageView) mLayout.getChildAt(i);
			imgView.setImageBitmap(null);
		}
	}

	/**
	 * Append images to this layout, firstly, prepare the image layouts, then
	 * load images one by one.
	 * 
	 * @param imageMetaList
	 */
	private void appendImageViewList(List<ImageMeta> imageMetaList) {
		mProgressDialog.dismiss();
		mImageCount += imageMetaList.size();
		for (ImageMeta imageMeta : imageMetaList) {
			int retId = appendImageView(imageMeta);
			// QUEUE THIS TASK
			mTaskList.add(new ImageTask(retId, imageMeta));
		}
		// LOAD IMAGE => HANDLE THE QUEUE TASK
		loadImageAsync();
	}

	/**
	 * Here is the core algorithm to decide how to place each Image.
	 * 
	 * @param imageMeta
	 */
	private int appendImageView(ImageMeta imageMeta) {
		// assign a new id for this ImageView
		int id = AppUtils.generateViewId();
		ImageView imageView = new ImageView(this);
		int realWidth = mColumnWidth;
		int realHeight = imageMeta.getHeight() * realWidth
				/ imageMeta.getWidth();
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				realWidth, realHeight);
		// sort the mColumnRecords
		Collections.sort(mColumnRecords, new Comparator<ColumnMeta>() {
			@Override
			public int compare(ColumnMeta lhs, ColumnMeta rhs) {
				if (lhs.getHeight() < rhs.getHeight()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		ColumnMeta above = mColumnRecords.get(0);
		above.addHeight(realHeight);
		// find its upper view
		if (above.getTopId() == ColumnMeta.PARENT_TOP) {
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			params.topMargin = mMarginV; // top margin
			params.bottomMargin = mMarginV; // bottom margin
		} else {
			params.addRule(RelativeLayout.BELOW, above.getTopId());
			params.topMargin = 0; // top margin
			params.bottomMargin = mMarginV; // bottom margin
		}
		above.setTopId(id);
		// find its left view
		if (above.getLeftId() == ColumnMeta.PARENT_LEFT) {
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
					RelativeLayout.TRUE);
			params.leftMargin = mMarginH;
			params.rightMargin = mMarginH;
		} else {
			params.addRule(RelativeLayout.RIGHT_OF, above.getLeftId());
			params.leftMargin = 0;
			params.rightMargin = mMarginH;
		}

		// set the leftId of the right column of current
		for (ColumnMeta record : mColumnRecords) {
			if (record.getColumn() == (above.getColumn() + 1)) {
				record.setLeftId(id);
				break;
			}
		}

		imageView.setId(id);
		imageView.setBackgroundColor(Color.GRAY);
		mLayout.addView(imageView, params);

		return id;
	}

	/**
	 * decide <b><code>mColumnCnt</code></b>, <b><code>mColumnWidth</code></b>,
	 * <b><code>mColumnRecord</code></b>
	 */
	private void prepareLayoutArguments() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		// I try to use two columns when on mobile phone
		// and three columns when on landscape view
		mColumnCnt = height > width ? 2 : 3;
		mColumnWidth = (width - (mColumnCnt + 1) * mMarginH) / mColumnCnt;
		for (int i = 1; i <= mColumnCnt; ++i) {
			ColumnMeta record = new ColumnMeta();
			record.setHeight(0);
			record.setColumn(i);
			record.setLeftId(i == 1 ? ColumnMeta.PARENT_LEFT
					: ColumnMeta.INVALID_LEFT);
			// the first row is all below parent top
			record.setTopId(ColumnMeta.PARENT_TOP);
			mColumnRecords.add(record);
		}
	}

	private void loadImageMetaListAsync(final int begin) {
		mProgressDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				loadImageMetaList(begin);
			}
		}).start();
	}

	private void restoreFromTaskList() {
		mCurrentIndex = 0;
		loadImageAsync();
	}

	/**
	 * Real networking here.. User gson library to convert a json string to an
	 * object.
	 */
	private void loadImageMetaList(int begin) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(IMAGE_META_SERVER + "/" + begin);
		ArrayList<ImageMeta> imageMetaList = new ArrayList<ImageMeta>();

		try {
			HttpResponse response = httpClient.execute(httpGet);
			String json = EntityUtils.toString(response.getEntity());
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

	private void refresh() {
		// REMOVE ALL THE IMAGE VIEWS
		mLayout.removeAllViews();
		// RESET PHOTOCOUNT
		mImageCount = 0;
		// RESET LAYOUT DATA STRUCTURE
		mColumnRecords.clear();
		prepareLayoutArguments();
		// THEN RELOAD THE IMAGE META
		loadImageMetaListAsync(mImageCount);
	}

	/**
	 * Load a single Image from url, the parameter is retrieved from the task
	 * queue. Which includes the assigned id and the target image meta data.
	 * Since it will block the UI thread, so I place the downloading code into
	 * another function and start a new thread to run it..
	 */
	private void loadImageAsync() {
		if (mCurrentIndex == mTaskList.size()) {
			// IF ALL THE TASK ARE DONE, THEN RETURN
			return;
		}
		final ImageTask task = mTaskList.get(mCurrentIndex);
		++mCurrentIndex;
		Bitmap bitmap = mFileCache.getBitmapFromCache(task.getImageMeta()
				.getUrl());
		if (bitmap != null) {
			ImageView imgView = (ImageView) findViewById(task.getImageViewId());
			if (imgView != null) {
				imgView.setImageBitmap(bitmap);
			}
			// CONTINUE LOADING
			loadImageAsync();
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				loadImage(task);
			}
		}).start();

	}

	/**
	 * Connect to the server and download the Image.
	 * 
	 * @param task
	 */
	private void loadImage(ImageTask task) {
		String url = task.getImageMeta().getUrl();
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);

		try {
			HttpResponse response = httpClient.execute(httpGet);
			Bitmap bitmap = BitmapFactory.decodeStream(response.getEntity()
					.getContent());
			Message msg = Message.obtain();
			msg.what = IMAGE_LOADED;
			Bundle data = new Bundle();
			data.putInt("id", task.getImageViewId());
			data.putString("url", task.getImageMeta().getUrl());
			data.putParcelable("bitmap", bitmap);
			msg.setData(data);
			mHandler.sendMessage(msg);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError error) {
			// when decode the url stream, there are out of memory error

		}
	}

	/**
	 * Handle to build a bridge between networking and UI thread.
	 */
	private Handler mHandler = new MyHandler(this);

	private static final int IMAGE_META_LOADED = 14122;

	private static final int IMAGE_LOADED = 22141;

	private static final int REQUEST_IMAGE_CAPTURE = 14221;

	private static final int REQUEST_IMAGE_SELECT = 12241;

	// private static final int REQUEST_IMAGE_UPLOAD = 12241;

	private static final String IMAGE_META_SERVER = "http://www.zhouhaibing.com/app/sharesight/getImage";

	private static final String IMAGE_CACHE_PATH = Environment
			.getExternalStorageDirectory() + "/ShareSight/cache/capture";

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
					// ADD A LIST OF IMAGE VIEW
					mHost.get().appendImageViewList(imageMetaList);
				}
			}
			if (msg.what == IMAGE_LOADED) {
				Bitmap bitmap = msg.getData().getParcelable("bitmap");
				String url = msg.getData().getString("url");
				int id = msg.getData().getInt("id");
				if (mHost != null) {
					mHost.get().mFileCache.addToCache(url, bitmap);
					ImageView imgView = (ImageView) mHost.get()
							.findViewById(id);
					imgView.setImageBitmap(bitmap);
					mHost.get().loadImageAsync();
				}
			}
		}
	}

	/** A helper data structure to decide image layouts */
	private ArrayList<ColumnMeta> mColumnRecords = new ArrayList<ColumnMeta>();

	/** Configuration parameter */
	private int mMarginH = 8;
	private int mMarginV = 8;
	private int mColumnWidth = 0;
	private int mColumnCnt = 0;

	/** Store the image loading task */
	private List<ImageTask> mTaskList = new ArrayList<ImageTask>();
	/** Current index which has been loaded */
	private int mCurrentIndex;

	private String mImagePath;

	private int mImageCount = 0;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_camera_capture) {
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (cameraIntent.resolveActivity(getPackageManager()) != null) {
				File imageFile = null;
				try {
					imageFile = createImageFile();
				} catch (IOException e) {
					// OMMIT THIS EXCEPTION
				}
				if (imageFile == null) {
					Toast.makeText(this, "SD card not found...",
							Toast.LENGTH_LONG).show();
					return true;
				}
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(imageFile));
				startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
			}
			return true;
		} else if (id == R.id.action_camera_select) {
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, REQUEST_IMAGE_SELECT);
		} else if (id == R.id.action_refresh) {
			refresh();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);

	}

	private File createImageFile() throws IOException {
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		String imageFileName = today.format2445();
		File imgCacheDir = new File(IMAGE_CACHE_PATH);
		// If the folder does not exist, then create it..
		if (!imgCacheDir.exists()) {
			imgCacheDir.mkdirs();
		}
		File image = File.createTempFile(imageFileName, ".jpg", imgCacheDir);
		mImagePath = image.getAbsolutePath();
		return image;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// when the camera activity returns, we need to let the user confirm
		// this image, then upload
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			// start the upload activity
			Intent uploadActivity = new Intent(this, UploadActivity.class);
			uploadActivity.putExtra("photo", mImagePath);
			startActivity(uploadActivity);
		} else if (requestCode == REQUEST_IMAGE_SELECT
				&& resultCode == RESULT_OK) {
			Uri selected = data.getData();
			Intent uploadActivity = new Intent(this, UploadActivity.class);
			uploadActivity.putExtra("photo",
					AppUtils.getRealPathFromUri(this, selected));
			startActivity(uploadActivity);
		}
	}
}
