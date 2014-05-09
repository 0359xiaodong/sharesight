package seaice.app.sharesight.http;

import java.io.File;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;

public class FilePostTaskParam {

	private String actionUrl;

	private HashMap<String, String> fileMap;

	private HashMap<String, String> textMap;

	public FilePostTaskParam(String actionUrl, HashMap<String, String> fileMap,
			HashMap<String, String> textMap) {
		this.actionUrl = actionUrl;
		this.fileMap = fileMap;
		this.textMap = textMap;
	}

	/**
	 * Simpler version constructor, upload only one file and no extra arguments.
	 * 
	 * @param actionUrl
	 * @param fileTag
	 * @param filePath
	 */
	public FilePostTaskParam(String actionUrl, String fileTag, String filePath) {
		this.actionUrl = actionUrl;

		fileMap = new HashMap<String, String>();
		fileMap.put(fileTag, filePath);
		this.textMap = null;
	}

	public FilePostTaskParam(String actionUrl, HashMap<String, String> fileMap) {
		this(actionUrl, fileMap, null);
	}

	public String getActionUrl() {
		return actionUrl;
	}

	public HttpEntity getPostEntity() {
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		if (fileMap == null) {
			return null;
		}
		for (String tag : fileMap.keySet()) {
			builder.addBinaryBody(tag, new File(fileMap.get(tag)));
		}
		if (textMap == null) {
			return builder.build();
		}
		for (String tag : textMap.keySet()) {
			builder.addTextBody(tag, textMap.get(tag));
		}
		return builder.build();
	}

}
