package jp.co.techfirm.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import jp.co.techfirm.util.BitmapUtil;
import jp.co.techfirm.util.ImageCache;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
		image = (ImageView) findViewById(R.id.image);

		Intent intent = getIntent();
		String uri = intent.getStringExtra("imageUri");
		Uri imageUri = Uri.parse(uri);
		BitmapDrawable drawable = (BitmapDrawable) ImageCache.getInstance()
				.getCache().get(imageUri);
		image.setImageDrawable(drawable);
		bitmap = drawable.getBitmap();

		createImage();
	}

	private void createImage() {
		ImageProcessTask task = new ImageProcessTask(image);
		task.execute(null);
	}

	class ImageProcessTask extends AsyncTask<Uri, Void, Bitmap> {
		private static final int SIZE = 8;
		private ImageView image;
		private int[] colorTable;
		private int[] size;
		private Bitmap[] loadedBitmap;

		public ImageProcessTask(ImageView imageView) {
			this.image = imageView;
		}

		@Override
		protected Bitmap doInBackground(Uri... params) {
			long start, end;
			start = System.currentTimeMillis();
			size = new int[2];// 0にwidth, 1にheightを入れてもらう
			colorTable = BitmapUtil.calculateMozaic(bitmap, size);
			Log.i(TAG, "color table count: " + colorTable.length);
			end = System.currentTimeMillis();

			Log.i(TAG, "calculate time: " + (end - start) + "ms.");
			
			

			Bitmap result = createBitmap();
//			Bitmap result = BitmapUtil.test(bitmap, colorTable, size[0], size[1]);
//			Bitmap result = BitmapUtil.makeMozaic(bitmap, false);
			return result;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			BitmapDrawable drawable = new BitmapDrawable(result);
			image.setImageDrawable(drawable);
		}

		private Bitmap createBitmap() {
			loadImages();
			
			Log.d(TAG, "image load!!!");

			Bitmap resultBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
			Map colorCache = new HashMap();
			int width = size[0];
			int height = size[1];
			
			Log.d(TAG, "width: " + width + " height: " + height);
			Log.d(TAG, "loaded bitmap count: " + loadedBitmap.length);

			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					int index = i + j * width;
					int[] inputPixels = new int[SIZE*SIZE];
					Integer color = new Integer(colorTable[index]);
					int distance = Integer.MAX_VALUE;
					Bitmap targetBitmap = null;
					if (!colorCache.containsKey(color)) {
						int select = 0;
						for (int k = 0; k < loadedBitmap.length; k++) {
							int bitmapColor = BitmapUtil.calculateImageColor(
									loadedBitmap[k], true);
							int calcDistance = BitmapUtil.distance(
									colorTable[index], bitmapColor);
							if (distance > calcDistance) {
								distance = calcDistance;
								targetBitmap = loadedBitmap[k];
								select = k;
							}
						}
						Log.d(TAG, "x: " +i + " y: " + j + "select: " + select);
						colorCache.put(color, targetBitmap);
					} else {
						targetBitmap = (Bitmap) colorCache.get(color);
					}
					targetBitmap.getPixels(inputPixels, 0, targetBitmap.getWidth(), 0, 0, targetBitmap.getWidth(), targetBitmap.getHeight());
					resultBitmap.setPixels(inputPixels, 0, SIZE, i*SIZE, j*SIZE, SIZE, SIZE);
				}

			}

			return resultBitmap;
		}

		/**
		 * SDカードから画像を読み込みloadedBitmapにセット
		 */
		private void loadImages() {
			Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			String[] projection = { MediaStore.Images.Media._ID };

			Cursor cursor = managedQuery(uri, projection, null, null, null);
			Log.i(TAG, "thumbnail count:" + cursor.getCount());
			if (cursor != null) {
				cursor.moveToFirst();
				loadedBitmap = new Bitmap[cursor.getCount()];

				int idColumn = cursor
						.getColumnIndex(MediaStore.Images.Media._ID);
				BitmapFactory.Options options = new BitmapFactory.Options();
				InputStream is;
				int index = 0;

				do {
					int id = cursor.getInt(idColumn);
					Uri imageUri = Uri.withAppendedPath(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ""
									+ id);
					options.inSampleSize = 10;
					try {
						is = getContentResolver().openInputStream(imageUri);
						Bitmap b = BitmapFactory
								.decodeStream(is, null, options);
						is.close();
						// 8pxの正方形に画像をリサイズ
						Bitmap scaledBitmap = BitmapUtil.clipSquare(b, SIZE);
						loadedBitmap[index] = scaledBitmap;

					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					index++;
				} while (cursor.moveToNext());
			}
		}

	}
}
