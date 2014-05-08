package seaice.app.sharesight.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class HttpBitmapTask extends
		AsyncTask<BitmapTaskParam, Integer, BitmapTaskResult> {

	private HttpBitmapAsyncTaskClient mClient;

	public HttpBitmapTask(HttpBitmapAsyncTaskClient client) {
		super();

		mClient = client;
	}

	@Override
	protected BitmapTaskResult doInBackground(BitmapTaskParam... args) {
		BitmapTaskParam param = args[0];
		int imageViewId = param.getImageViewId();
		String url = param.getUrl();
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);

		try {
			HttpResponse response = httpClient.execute(httpGet);
			return new BitmapTaskResult(imageViewId, url,
					BitmapFactory.decodeStream(response.getEntity()
							.getContent()));
		} catch (IOException e) {

		}
		return null;
	}

	protected void onPostExecute(BitmapTaskResult bitmapResult) {
		mClient.onGetBitmapTaskResult(bitmapResult);
	}

	public interface HttpBitmapAsyncTaskClient {

		public void onGetBitmapTaskResult(BitmapTaskResult bitmapResult);

	}
}
