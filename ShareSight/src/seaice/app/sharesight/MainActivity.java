package seaice.app.sharesight;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import seaice.app.sharesight.data.ColumnMeta;
import seaice.app.sharesight.data.ImageMeta;
import seaice.app.sharesight.loader.ImageLoader;
import seaice.app.sharesight.loader.ImageLoaderCallback;
import seaice.app.sharesight.loader.ImageLoaderTask;
import seaice.app.sharesight.utils.AppUtils;
import seaice.app.sharesight.views.MyScrollView;
import seaice.app.sharesight.views.MyScrollView.ScrollViewListener;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
public class MainActivity extends ActionBarActivity implements
		ImageLoaderCallback {

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

	/** Layout Variable */
	private ArrayList<ColumnMeta> mColumnMetaList = new ArrayList<ColumnMeta>();
	private int mMarginH = 8;
	private int mMarginV = 8;
	private int mColumnWidth = 0;
	private int mColumnCnt = 0;

	/** Image Loader Variable */
	private Queue<ImageLoaderTask> mTaskQueue = new LinkedList<ImageLoaderTask>();
	private String mImagePath;
	private int mImageCount = 0;
	private static final int IMAGE_COUNT_PER_PAGE = 10;

	private ImageLoader mLoader;

	private boolean mLoading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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

				if (diff == 0 && !mLoading) {
					mLoading = true;

					if ((mImageCount % IMAGE_COUNT_PER_PAGE == 0)) {
						mLoader.loadImageMetaList(mImageCount);
					}
				}
			}

		});

		mProgressDialog = new ProgressDialog(this,
				ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMessage("Loading...");
		prepareLayoutArguments();

		mLoader = new ImageLoader(this);
		mLoader.loadImageMetaList(mImageCount);
	}

	public void onStop() {
		super.onStop();

		mProgressDialog.dismiss();
	}

	/**
	 * Append images to this layout, firstly, prepare the image layouts, then
	 * load images one by one.
	 * 
	 * @param imageMetaList
	 */
	private void appendImageViewList(List<ImageMeta> imageMetaList) {
		for (ImageMeta imageMeta : imageMetaList) {
			int retId = appendImageView(imageMeta);
			mTaskQueue.add(new ImageLoaderTask(retId, imageMeta.getUrl()));
		}
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
		Collections.sort(mColumnMetaList, new Comparator<ColumnMeta>() {
			@Override
			public int compare(ColumnMeta lhs, ColumnMeta rhs) {
				if (lhs.getHeight() < rhs.getHeight()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		ColumnMeta above = mColumnMetaList.get(0);
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
		for (ColumnMeta record : mColumnMetaList) {
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
			ColumnMeta columnMeta = new ColumnMeta();
			columnMeta.setHeight(0);
			columnMeta.setColumn(i);
			columnMeta.setLeftId(i == 1 ? ColumnMeta.PARENT_LEFT
					: ColumnMeta.INVALID_LEFT);
			// the first row is all below parent top
			columnMeta.setTopId(ColumnMeta.PARENT_TOP);
			mColumnMetaList.add(columnMeta);
		}
	}

	private void refresh() {
		mLayout.removeAllViews();
		mImageCount = 0;
		mColumnMetaList.clear();
		prepareLayoutArguments();

		mLoader.loadImageMetaList(mImageCount);
	}

	private static final int REQUEST_IMAGE_CAPTURE = 14221;

	private static final int REQUEST_IMAGE_SELECT = 12241;

	private static final String IMAGE_CACHE_PATH = Environment
			.getExternalStorageDirectory() + "/ShareSight/cache/capture";

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
			uploadActivity.putExtra(IMAGE_PATH_TAG, mImagePath);
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

	public static final String IMAGE_PATH_TAG = "IMAGE";

	@Override
	public void onImageMetaLoaded(List<ImageMeta> imageMetaList) {
		appendImageViewList(imageMetaList);

		mImageCount += mTaskQueue.size();
		ImageLoaderTask task = mTaskQueue.poll();
		mLoader.loadImage(task);
	}

	@Override
	public void onImageLoaded(int imageViewId, Bitmap bitmap) {
		ImageView imageView = (ImageView) findViewById(imageViewId);
		if (imageView != null) {
			imageView.setImageBitmap(bitmap);
		}

		// Needs continue?
		ImageLoaderTask task = mTaskQueue.poll();
		mLoader.loadImage(task);
	}

	@Override
	public void beforeLoadImageMeta() {
		mProgressDialog.show();
	}

	@Override
	public void beforeLoadImage() {
		// LEAVE IT AS A STUB
	}

	@Override
	public void afterLoadImageMeta() {
		mLoading = false;
		mProgressDialog.dismiss();
	}

	@Override
	public void afterLoadImage() {
		// LEAVE IT AS A STUB
	}
}
