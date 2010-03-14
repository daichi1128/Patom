package jp.co.techfirm.global;

import jp.co.techfirm.util.ImageCache;
import android.app.Application;
import android.util.Log;

public class PatomApplication extends Application {
	private static final String TAG = "PatomApplication";
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "application start!!!");
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.d(TAG, "application terminate!!!");
	}
	
	/**
	 * メモリが少なくなったらキャッシュをクリア
	 */
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		ImageCache.getInstance().getCache().clear();
		Log.d(TAG, "application memory warning!!!");
	}
}
