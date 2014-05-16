package seaice.app.sharesight.http.get;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import seaice.app.sharesight.http.TextResult;
import seaice.app.sharesight.http.TextResultClient;
import android.os.AsyncTask;
import android.os.Bundle;

public class TextTask extends AsyncTask<Bundle, Integer, TextResult> {

    private TextResultClient mClient;

    public static final String URL_TAG = "URL";

    public TextTask(TextResultClient client) {
        super();
        mClient = client;
    }

    @Override
    protected TextResult doInBackground(Bundle... params) {
        Bundle data = params[0];

        String url = data.getString(URL_TAG);

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity respEntity = response.getEntity();
            return new TextResult(EntityUtils.toString(respEntity, "UTF-8"),
                    data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void onPostExecute(TextResult result) {
        mClient.onGetTextResult(result);
    }
}
