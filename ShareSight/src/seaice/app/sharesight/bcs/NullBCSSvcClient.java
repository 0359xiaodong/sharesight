package seaice.app.sharesight.bcs;

import android.os.Bundle;

import com.baidu.inf.iis.bcs.model.DownloadObject;
import com.baidu.inf.iis.bcs.model.ObjectMetadata;

public class NullBCSSvcClient implements BCSSvcClient {

    @Override
    public void onFileUploaded(ObjectMetadata result, Bundle client) {
    }

    @Override
    public void onFileDownloaded(DownloadObject downloadObject,
            Bundle clientData) {
        // TODO Auto-generated method stub

    }

}
