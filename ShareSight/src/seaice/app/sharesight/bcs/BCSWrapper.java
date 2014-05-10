package seaice.app.sharesight.bcs;

import java.io.File;

import com.baidu.inf.iis.bcs.BaiduBCS;
import com.baidu.inf.iis.bcs.auth.BCSCredentials;
import com.baidu.inf.iis.bcs.model.DownloadObject;
import com.baidu.inf.iis.bcs.model.ObjectMetadata;
import com.baidu.inf.iis.bcs.request.GenerateUrlRequest;
import com.baidu.inf.iis.bcs.request.GetObjectRequest;
import com.baidu.inf.iis.bcs.response.BaiduBCSResponse;

public class BCSWrapper {

	private static final String HOST = "bcs.duapp.com";

	private static final String ACCESS_KEY = "ACCESS_TOKEN";

	private static final String SECRETE_KEY = "SECRETE_KEY";

	private static final String BUCKET = "BUCKET_NAME";

	private BaiduBCS mBaiduBCS;

	public BCSWrapper() {
		BCSCredentials credentials = new BCSCredentials(ACCESS_KEY, SECRETE_KEY);
		mBaiduBCS = new BaiduBCS(credentials, HOST);
	}

	public String generateUrl(GenerateUrlRequest generateUrlRequest) {
		return mBaiduBCS.generateUrl(generateUrlRequest);
	}

	public BaiduBCSResponse<DownloadObject> getObject(String object,
			File destFile) {
		GetObjectRequest request = new GetObjectRequest(BUCKET, object);
		return mBaiduBCS.getObject(request, destFile);
	}

	public BaiduBCSResponse<ObjectMetadata> getObjectMetadata(String object) {
		return mBaiduBCS.getObjectMetadata(BUCKET, object);
	}

	public BaiduBCSResponse<ObjectMetadata> putObject(String object, File file) {
		return mBaiduBCS.putObject(BUCKET, object, file);
	}
}
