package seaice.app.sharesight.http;

public interface HttpTaskClient {

	public void onTimeout();
	
	public void onRefused();

}
