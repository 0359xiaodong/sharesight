package seaice.app.sharesight.http.post;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import seaice.app.sharesight.http.TextResult;
import seaice.app.sharesight.http.TextResultClient;
import android.os.AsyncTask;
import android.os.Bundle;

public class FileTask extends AsyncTask<Bundle, Integer, TextResult> {

    private TextResultClient mClient;

    public static final String URL_TAG = "URL";

    public static final String KEY_ARRAY_TAG = "KEY_ARRAY";

    public static final String VALUE_ARRAY_TAG = "VALUE_ARRAY";

    public static final String FILE_KEY_ARRAY_TAG = "FILE_KEY_ARRAY";

    public static final String FILE_VALUE_ARRAY_TAG = "FILE_VALUE_ARRAY";

    public FileTask(TextResultClient client) {
        super();
        mClient = client;
    }

    @Override
    protected TextResult doInBackground(Bundle... params) {
        Bundle data = params[0];

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        ArrayList<String> fileKeyArray = data
                .getStringArrayList(FILE_KEY_ARRAY_TAG);
        ArrayList<String> fileValueArray = data
                .getStringArrayList(FILE_VALUE_ARRAY_TAG);
        if (fileKeyArray == null || fileValueArray == null
                || fileKeyArray.size() != fileValueArray.size()) {
            return null;
        }
        for (int i = 0; i < fileKeyArray.size(); ++i) {
            System.out.println("Add: " + fileKeyArray.get(i) + ", "
                    + fileValueArray.get(i));
            builder.addBinaryBody(fileKeyArray.get(i),
                    new File(fileValueArray.get(i)));
        }
        ArrayList<String> keyArray = data.getStringArrayList(KEY_ARRAY_TAG);
        ArrayList<String> valueArray = data.getStringArrayList(VALUE_ARRAY_TAG);
        if (keyArray != null && valueArray != null
                && keyArray.size() == valueArray.size()) {
            for (int i = 0; i < keyArray.size(); ++i) {
                builder.addTextBody(keyArray.get(i), valueArray.get(i));
            }
        }

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(data.getString(URL_TAG));
        httpPost.setEntity(builder.build());

        try {
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();
            String content = EntityUtils.toString(resEntity, "UTF-8");
            resEntity.consumeContent();
            return new TextResult(content, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(TextResult textResult) {
        mClient.onGetTextResult(textResult);
    }
}
