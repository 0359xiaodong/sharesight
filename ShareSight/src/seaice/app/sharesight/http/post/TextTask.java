package seaice.app.sharesight.http.post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import seaice.app.sharesight.http.TextResult;
import seaice.app.sharesight.http.TextResultClient;
import android.os.AsyncTask;
import android.os.Bundle;

public class TextTask extends AsyncTask<Bundle, Integer, TextResult> {

	private TextResultClient mClient;

	public static final String URL_TAG = "URL";

	public static final String KEY_ARRAY_TAG = "KEY_ARRAY";

	public static final String VALUE_ARRAY_TAG = "VALUE_ARRAY";

	public TextTask(TextResultClient client) {
		super();
		mClient = client;
	}

	@Override
	protected TextResult doInBackground(Bundle... params) {
		Bundle data = params[0];

		List<BasicNameValuePair> postParams = new ArrayList<BasicNameValuePair>();
		ArrayList<String> keyArray = data.getStringArrayList(KEY_ARRAY_TAG);
		ArrayList<String> valueArray = data.getStringArrayList(VALUE_ARRAY_TAG);
		if (keyArray == null || valueArray == null
				|| keyArray.size() != valueArray.size()) {
			return null;
		}
		for (int i = 0; i < keyArray.size(); ++i) {
			postParams.add(new BasicNameValuePair(keyArray.get(i), valueArray
					.get(i)));
		}

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(data.getString(URL_TAG));

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(postParams));
			HttpResponse response = httpClient.execute(httpPost);
			return new TextResult(EntityUtils.toString(response.getEntity()),
					data);
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	protected void onPostExecute(TextResult textResult) {
		mClient.onGetTextResult(textResult);
	}

}
