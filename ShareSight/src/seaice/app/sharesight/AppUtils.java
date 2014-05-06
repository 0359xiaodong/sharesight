package seaice.app.sharesight;

import java.util.concurrent.atomic.AtomicInteger;

public class AppUtils {

	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	/**
	 * Generate a value suitable for use in {@link #setId(int)}. This value will
	 * not collide with ID values generated at build time by aapt for R.id.
	 * 
	 * @return a generated ID value
	 */
	public static int generateViewId() {
		for (;;) {
			final int result = sNextGeneratedId.get();
			int newValue = result + 1;
			if (newValue > 0x00FFFFFF)
				newValue = 1;
			if (sNextGeneratedId.compareAndSet(result, newValue)) {
				return result;
			}
		}
	}
}
