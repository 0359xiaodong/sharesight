package seaice.app.sharesight.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

public class TextTask extends
		AsyncTask<TextTaskParam, Integer, TextTaskResult> {

	private TextTaskClient mClient;

	public TextTask(TextTaskClient client) {
		super();
		mClient = client;
	}

	@Override
	protected TextTaskResult doInBackground(TextTaskParam... params) {
		TextTaskParam param = params[0];

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(param.getUrl());

		try {
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity respEntity = response.getEntity();
			return new TextTaskResult(respEntity.getContentType(),
					EntityUtils.toString(respEntity));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	protected void onPostExecute(TextTaskResult result) {
		mClient.onGetTextTaskResult(result);
	}
}
