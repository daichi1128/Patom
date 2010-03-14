package jp.co.techfirm.activity;

import jp.co.techfirm.util.BitmapUtil;
import jp.co.techfirm.util.ImageCache;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class CreateActivity extends Activity {
	private static final String TAG = "CREATE";
	private ImageView image;
	private Bitmap bitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		image = (ImageView)findViewById(R.id.image);
		
		Intent intent = getIntent();
		String uri = intent.getStringExtra("imageUri");
		Uri imageUri = Uri.parse(uri);
		BitmapDrawable drawable = (BitmapDrawable)ImageCache.getInstance().getCache().get(imageUri);
		image.setImageDrawable(drawable);
		bitmap = drawable.getBitmap();
		
		createImage();
	}
	
	private void createImage(){
		ImageProcessTask task = new ImageProcessTask(image);
		task.execute(null);
	}
	
	class ImageProcessTask extends AsyncTask<Uri, Void, Bitmap> {
		private ImageView image;
		private int[] colorTable;
		
		public ImageProcessTask(ImageView imageView) {
			this.image = imageView;
		}
		
		@Override
		protected Bitmap doInBackground(Uri... params) {
			long start , end;
			start  = System.currentTimeMillis();
			colorTable = BitmapUtil.calculateMozaic(bitmap);
			Log.i(TAG, "color table count: " + colorTable.length);
			end = System.currentTimeMillis();
			
			Log.i(TAG, "calculate time: " + (end - start) + "ms.");
			
			start  = System.currentTimeMillis();
			Bitmap result = BitmapUtil.makeMozaic(bitmap);
			end = System.currentTimeMillis();
			
			Log.i(TAG, "process bitmap time: " + (end - start) + "ms.");
			
//			Bitmap resize = BitmapUtil.resize(bitmap, 8, 8);
			
//			return BitmapUtil.clipSquare(bitmap, 200);
			return result;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			BitmapDrawable drawable = new BitmapDrawable(result);
			image.setImageDrawable(drawable);
		}
		
	}
}
