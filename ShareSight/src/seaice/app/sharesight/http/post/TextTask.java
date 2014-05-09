package seaice.app.sharesight.http.post;

import seaice.app.sharesight.http.TextResult;
import seaice.app.sharesight.http.TextResultCallback;
import android.os.AsyncTask;
import android.os.Bundle;

public class TextTask extends AsyncTask<Bundle, Integer, TextResult>{
	
	private TextResultCallback mCallback;
	
	public TextTask(TextResultCallback callback) {
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
