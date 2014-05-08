package seaice.app.sharesight.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageMeta implements Parcelable {

	private String url;

	private int width;

	private int height;

	public ImageMeta(String url, int width, int height) {
		this.url = url;
		this.width = width;
		this.height = height;
	}

	public String getUrl() {
		return url;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(url);
		dest.writeInt(width);
		dest.writeInt(height);
	}

	public static final Parcelable.Creator<ImageMeta> CREATOR = new Creator<ImageMeta>() {

		@Override
		public ImageMeta createFromParcel(Parcel source) {
			String url = source.readString();
			int width = source.readInt();
			int height = source.readInt();
			return new ImageMeta(url, width, height);
		}

		@Override
		public ImageMeta[] newArray(int size) {
			return new ImageMeta[size];
		}

	};
}
