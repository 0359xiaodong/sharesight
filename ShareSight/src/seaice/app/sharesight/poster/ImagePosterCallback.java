package seaice.app.sharesight.poster;

public interface ImagePosterCallback {

    public void onImagePosted(boolean status, String message);

    public void beforePostImage();

    public void afterPostImage();
}
