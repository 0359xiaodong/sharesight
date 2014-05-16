package seaice.app.sharesight.bcs;

import java.io.File;

import com.baidu.inf.iis.bcs.model.DownloadObject;
import com.baidu.inf.iis.bcs.model.ObjectMetadata;
import com.baidu.inf.iis.bcs.response.BaiduBCSResponse;

import android.os.Bundle;

public class BCSSvc {

    private BCSWrapper mWrapper;

    private BCSSvcClient mClient;

    public BCSSvc(BCSSvcClient client) {
        mWrapper = new BCSWrapper();
        mClient = client == null ? new NullBCSSvcClient() : client;
    }

    public void uploadFileAsync(final String filePath, final Bundle outData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                uploadFile(filePath, outData);
            }
        }).start();
    }

    public ObjectMetadata uploadFile(String filePath, Bundle clientData) {
        String fileName = filePath.substring(filePath.lastIndexOf("/"));
        String object = "/" + fileName;
        BaiduBCSResponse<ObjectMetadata> response = mWrapper.putObject(object,
                new File(filePath));
        ObjectMetadata result = response.getResult();
        if (mClient != null) {
            mClient.onFileUploaded(result, clientData);
        }
        return result;
    }

    public void downloadToFileAsync(final String fileName,
            final String destDir, final Bundle clientData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                downloadToFile(fileName, destDir, clientData);
            }
        }).start();
    }

    public DownloadObject downloadToFile(String fileName, String destDir,
            Bundle clientData) {
        File destFile = new File(destDir + "/" + fileName);
        String object = "/" + fileName;
        BaiduBCSResponse<DownloadObject> response = mWrapper.getObject(object,
                destFile);
        DownloadObject result = response.getResult();
        if (mClient != null) {
            mClient.onFileDownloaded(result, clientData);
        }
        return result;
    }
    
    public static String generateUrl(String fileName) {
        return BCSWrapper.generateUrl(fileName);
    }
}
