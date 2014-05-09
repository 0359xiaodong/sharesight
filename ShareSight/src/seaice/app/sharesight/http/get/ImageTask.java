package seaice.app.sharesight.http.get;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import seaice.app.sharesight.http.ImageResult;
import seaice.app.sharesight.http.ImageResultClient;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

public class ImageTask extends AsyncTask<Bundle, Integer, ImageResult> {

	private ImageResultClient mClient;

	public ImageTask(ImageResultClient client) {
		super();
		mClient = client;
	}

	@Override
	protected ImageResult doInBackground(Bundle... args) {
		Bundle param = args[0];
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(mClient.getUrlTag());

		try {
			HttpResponse response = httpClient.execute(httpGet);
			return new ImageResult(BitmapFactory.decodeStream(response
					.getEntity().getContent()), param);
		} catch (IOException e) {

		}
		return null;
	}

	protected void onPostExecute(ImageResult imageResult) {
		mClient.onGetImageResult(imageResult);
	}
}
