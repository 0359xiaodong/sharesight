package seaice.app.sharesight;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import seaice.app.sharesight.data.ImageMeta;
import seaice.app.sharesight.loader.ImageLoader;
import seaice.app.sharesight.loader.ImageLoaderCallback;
import seaice.app.sharesight.loader.ImageLoaderTask;
import seaice.app.sharesight.utils.AppUtils;
import seaice.app.sharesight.views.ImageScrollView;
import seaice.app.sharesight.views.ImageScrollView.ScrollViewListenner;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
	 * While taking communication with server, show this dialog
	 */
	private ProgressDialog mProgressDialog;
	/**
	 * The ScrollView container..
	 */
	private ImageScrollView mScrollView;

	/** Image Loader Variable */
	private Queue<ImageLoaderTask> mTaskQueue = new LinkedList<ImageLoaderTask>();
	private String mImagePath;
	private int mImageCount = 0;
	private static final int IMAGE_COUNT_PER_PAGE = 10;

	private ImageLoader mLoader;

	private boolean mLoading = false;

	public static final String IMAGE_PATH_TAG = "IMAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// set up the ImageScrollView
		mScrollView = (ImageScrollView) findViewById(R.id.container);
		mScrollView.setScrollViewListener(new ScrollViewListenner() {
			@Override
			public void onScrollChanged(ImageScrollView scrollView, int x,
					int y, int oldx, int oldy) {
				View view = (View) scrollView.getChildAt(scrollView
						.getChildCount() - 1);
				int diff = (view.getBottom() - (scrollView.getHeight() + scrollView
						.getScrollY()));
				/* If we reach the bottom and is not loading currently */
				if (diff == 0 && !mLoading) {
					mLoading = true;

					if ((mImageCount % IMAGE_COUNT_PER_PAGE == 0)) {
						mLoader.loadImageMetaList(mImageCount);
					}
				}
			}
		});

		// set up the progress dialog action
		mProgressDialog = new ProgressDialog(this,
				ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMessage("Loading...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				mLoader.setCancelled(true);
			}
		});

		mLoader = new ImageLoader(this);
		mLoader.loadImageMetaList(mImageCount);

	}

	@Override
	public void onSaveInstanceState(Bundle instanceState) {
		instanceState.putString(IMAGE_PATH_TAG, mImagePath);
	}

	@Override
	public void onRestoreInstanceState(Bundle instanceState) {
		mImagePath = instanceState.getString(IMAGE_PATH_TAG);
	}

	/**
	 * Append images to this layout, firstly, prepare the image layouts, then
	 * load images one by one.
	 * 
	 * @param imageMetaList
	 */
	private void addImageViewList(List<ImageMeta> imageMetaList) {
		for (ImageMeta imageMeta : imageMetaList) {
			int retId = mScrollView.addImageView(imageMeta.getWidth(),
					imageMeta.getHeight());
			mTaskQueue.add(new ImageLoaderTask(retId, imageMeta.getUrl()));
		}
	}

	private void refresh() {
		mImageCount = 0;
		mScrollView.removeAllImageViews();

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
					imageFile = AppUtils.createTempImageFile(IMAGE_CACHE_PATH);
					mImagePath = imageFile.getAbsolutePath();
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
			uploadActivity.putExtra(IMAGE_PATH_TAG,
					AppUtils.getRealPathFromUri(this, selected));
			startActivity(uploadActivity);
		}
	}

	@Override
	public void onImageMetaLoaded(List<ImageMeta> imageMetaList) {
		addImageViewList(imageMetaList);

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

	}

	@Override
	public void afterLoadImageMeta() {
		mLoading = false;
		mProgressDialog.dismiss();
	}

	@Override
	public void afterLoadImage() {

	}
}
