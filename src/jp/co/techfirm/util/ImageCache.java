package jp.co.techfirm.util;

import java.util.HashMap;
import java.util.Map;

public class ImageCache {
	private static ImageCache imageCache;
	private Map cache;
	private ImageCache() {
		cache = new HashMap();
	}
	
	public static ImageCache getInstance(){
		if (imageCache == null) {
			imageCache = new ImageCache();
		}
		return imageCache;
	}
	
	public Map getCache() {
		return cache;
	}
	
	public void put(Object key, Object value) {
		cache.put(key, value);
	}
	
	public Object get(Object key){
		return cache.get(key);
	}
}
