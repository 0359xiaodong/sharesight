package seaice.app.sharesight.poster;

public class ImagePosterTask {

	private String url;

	private String fileTag;

	private String filePath;

	public ImagePosterTask(String url, String fileTag, String filePath) {
		this.url = url;
		this.fileTag = fileTag;
		this.filePath = filePath;
	}

	public String getUrl() {
		return url;
	}

	public String getFileTag() {
		return fileTag;
	}

	public String getFilePath() {
		return filePath;
	}
}
