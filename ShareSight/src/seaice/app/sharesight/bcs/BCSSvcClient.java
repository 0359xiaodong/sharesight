package seaice.app.sharesight.bcs;

import com.baidu.inf.iis.bcs.model.DownloadObject;
import com.baidu.inf.iis.bcs.model.ObjectMetadata;

import android.os.Bundle;

public interface BCSSvcClient {

	public void onFileUploaded(ObjectMetadata result, Bundle client);
	
	public void onFileDownloaded(DownloadObject downloadObject, Bundle clientData);
}
