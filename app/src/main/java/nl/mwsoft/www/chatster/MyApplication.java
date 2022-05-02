package nl.mwsoft.www.chatster;


import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MyApplication extends MultiDexApplication {

    public static MyApplication myApplication = null;

    public MyApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        MultiDex.install(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/gotham-book.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    public static Context context() {
        return myApplication.getApplicationContext();
    }
}
