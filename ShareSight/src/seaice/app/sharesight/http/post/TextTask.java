package seaice.app.sharesight.http.post;

import seaice.app.sharesight.http.TextResult;
import seaice.app.sharesight.http.TextResultClient;
import android.os.AsyncTask;
import android.os.Bundle;

public class TextTask extends AsyncTask<Bundle, Integer, TextResult>{
	
	private TextResultClient mCallback;
	
	public TextTask(TextResultClient callback) {
		super();
		mCallback = callback;
	}

	@Override
	protected TextResult doInBackground(Bundle... params) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected void onPostExecute(TextResult textResult) {
		mCallback.onGetTextResult(textResult);
	}

}
