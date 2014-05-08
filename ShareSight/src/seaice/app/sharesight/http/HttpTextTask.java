package seaice.app.sharesight.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

public class HttpTextTask extends AsyncTask<String, Integer, String> {

	private HttpTextTaskClient mClient;

	public HttpTextTask(HttpTextTaskClient client) {
		super();
		mClient = client;
	}

	@Override
	protected String doInBackground(String... args) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(args[0]);

		try {
			HttpResponse response = httpClient.execute(httpGet);
			return EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	protected void onPostExecute(String result) {
		mClient.onGetTextTaskResult(result);
	}

	public interface HttpTextTaskClient {

		public void onGetTextTaskResult(String text);

	}
}
