package seaice.app.sharesight.http.post;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import seaice.app.sharesight.http.TextResult;
import seaice.app.sharesight.http.TextResultClient;
import android.os.AsyncTask;
import android.os.Bundle;

public class FileTask extends AsyncTask<Bundle, Integer, TextResult> {

	private TextResultClient mClient;

	public FileTask(TextResultClient client) {
		super();
		mClient = client;
	}

	@Override
	protected TextResult doInBackground(Bundle... params) {
		Bundle param = params[0];

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(mClient.getUrlTag());

		HttpResponse response;
		try {
			response = httpClient.execute(httpPost);
			HttpEntity resEntity = response.getEntity();
			String content = EntityUtils.toString(resEntity);
			resEntity.consumeContent();
			return new TextResult(content, param);
		} catch (Exception e) {
		}
		return null;
	}

	protected void onPostExecute(TextResult textResult) {
		mClient.onGetTextResult(textResult);
	}
}
