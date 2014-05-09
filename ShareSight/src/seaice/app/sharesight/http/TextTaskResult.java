package seaice.app.sharesight.http;

import org.apache.http.Header;

public class TextTaskResult {

	private Header contentType;

	private String content;

	public TextTaskResult(Header header, String content) {
		this.contentType = header;
		this.content = content;
	}

	public Header getContentType() {
		return contentType;
	}

	public String getContent() {
		return content;
	}
}
