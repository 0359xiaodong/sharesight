package seaice.app.sharesight.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import android.os.AsyncTask;

public class FilePostTask extends
		AsyncTask<FilePostTaskParam, Integer, TextTaskResult> {

	private HttpFilePostTaskClient mClient;

	public FilePostTask(HttpFilePostTaskClient client) {
		super();
		mClient = client;
	}

	@Override
	protected TextTaskResult doInBackground(FilePostTaskParam... params) {
		FilePostTaskParam param = params[0];

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(param.getActionUrl());

		httpPost.setEntity(param.getPostEntity());

		HttpResponse response;
		try {
			response = httpClient.execute(httpPost);
			HttpEntity resEntity = response.getEntity();
			String content = EntityUtils.toString(resEntity);
			resEntity.consumeContent();
			return new TextTaskResult(resEntity.getContentType(), content);
		} catch (Exception e) {
		}
		return null;
	}

	protected void onPostExecute(TextTaskResult result) {
		mClient.onGetPostTaskResult(result);
	}

	public interface HttpFilePostTaskClient {

		public void onGetPostTaskResult(TextTaskResult result);

	}

}
