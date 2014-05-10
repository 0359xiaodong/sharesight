package seaice.app.sharesight.bcs;

import java.io.File;

public class BCSSvc {

	private BCSWrapper mWrapper;

	private BCSSvcClient mClient;

	public BCSSvc(BCSSvcClient client) {
		mWrapper = new BCSWrapper();
		mClient = client == null ? new NullBCSSvcClient() : client;
	}

	public void uploadFileAsync(final String filePath) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				uploadFile(filePath);
			}
		}).start();
	}

	public boolean uploadFile(String filePath) {
		String fileName = filePath.substring(filePath.lastIndexOf("/"));
		String object = "/" + fileName;
		mWrapper.putObject(object, new File(filePath));
		if (mClient != null) {
		}
		return true;
	}

	public void downloadToFileAsync(final String fileName, final String destDir) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				downloadToFile(fileName, destDir);
			}
		}).start();
	}

	public void downloadToFile(String fileName, String destDir) {
		File destFile = new File(destDir + "/" + fileName);
		String object = "/" + fileName;
		mWrapper.getObject(object, destFile);
		if (mClient != null) {
			
		}
	}

}
