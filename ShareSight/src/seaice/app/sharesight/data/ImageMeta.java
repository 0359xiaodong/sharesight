package seaice.app.sharesight.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageMeta implements Parcelable {

    private String url;

    private int width;

    private int height;

    private String city;

    private String addr;

    private double longitude;

    private double latitude;

    private String deviceid;

    public ImageMeta(String url, int width, int height, String city,
            String addr, double longitude, double latitude, String deviceid) {
        this.url = url;
        this.width = width;
        this.height = height;
        this.city = city;
        this.addr = addr;
        this.longitude = longitude;
        this.latitude = latitude;
        this.deviceid = deviceid;
    }

    public ImageMeta() {
        this("", 0, 0, "", "", 0, 0, "");
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

    public String getCity() {
        return city;
    }

    public String getAddr() {
        return addr;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getDeviceId() {
        return deviceid;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setDeviceId(String deviceid) {
        this.deviceid = deviceid;
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
        dest.writeString(city);
        dest.writeString(addr);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(deviceid);
    }

    public static final Parcelable.Creator<ImageMeta> CREATOR = new Creator<ImageMeta>() {

        @Override
        public ImageMeta createFromParcel(Parcel source) {
            String url = source.readString();
            int width = source.readInt();
            int height = source.readInt();
            String city = source.readString();
            String addr = source.readString();
            double longitude = source.readDouble();
            double latitude = source.readDouble();
            String deviceid = source.readString();
            return new ImageMeta(url, width, height, city, addr, longitude,
                    latitude, deviceid);
        }

        @Override
        public ImageMeta[] newArray(int size) {
            return new ImageMeta[size];
        }

    };
}
