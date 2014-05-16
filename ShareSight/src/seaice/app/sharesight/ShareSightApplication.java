package seaice.app.sharesight;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;

public class ShareSightApplication extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = this;
    }

    public static Context getContext() {
        return sContext;
    }

    public static String getDeviceId() {
        TelephonyManager tm = (TelephonyManager) sContext
                .getSystemService(TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }
}
