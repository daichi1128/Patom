package jp.co.techfirm.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import jp.co.techfirm.util.ImageCache;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;
import android.widget.Gallery.LayoutParams;

public class BaseSelectActivity extends Activity implements
		AdapterView.OnItemSelectedListener, ViewSwitcher.ViewFactory {
	private static final String TAG = "Patom";
	private Cursor mCursor;
	private ImageSwitcher mSwitcher;
	private Gallery mGallery;
	private BitmapFactory.Options mOptions = new BitmapFactory.Options();
	private int width;
	private int height;
	private Map imageCache = ImageCache.getInstance().getCache();
	private Uri imageUri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		Display d = getWindowManager().getDefaultDisplay();
		width = d.getWidth();
		height = d.getHeight();

		Log.d(TAG, "width:" + width + " height:" + height);

		// displaySdCard();

		setContentView(R.layout.base_select);

		mSwitcher = (ImageSwitcher) findViewById(R.id.switcher);
		mSwitcher.setFactory(this);
//		mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
//				android.R.anim.fade_in));
//		mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
//				android.R.anim.fade_out));
		mSwitcher.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClassName(getPackageName(), CreateActivity.class
						.getName());
				intent.putExtra("imageUri", imageUri.toString());
				startActivity(intent);
			}

		});

		mGallery = (Gallery) findViewById(R.id.gallery);
		loadImages();
	}

	public void onItemSelected(AdapterView parent, View v, int position, long id) {
		// mSwitcher.setImageResource(mImageIds[position]);
		long start, end;
		start = System.currentTimeMillis();
		mCursor.moveToPosition(position);
		int imageColumn = mCursor
				.getColumnIndex(MediaStore.Images.Thumbnails._ID);
		int imageId = mCursor.getInt(imageColumn);

		imageUri = Uri.withAppendedPath(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + imageId);
		if (!imageCache.containsKey(imageUri)) {
			try {
				InputStream is;
				// mOptions.inJustDecodeBounds = true;
				// mOptions.inSampleSize = 1;
				// is = getContentResolver().openInputStream(imageUri);
				// BitmapFactory.decodeStream(is,null,mOptions);
				// is.close();
				//			
				// int scaleW = mOptions.outWidth / width + 1;
				// int scaleH = mOptions.outHeight / height + 1;
				// int scale = (scaleW > scaleH) ? scaleW : scaleH;
				//			
				// Log.i(TAG, "scale: 1/" + scale);

				mOptions.inJustDecodeBounds = false;
				mOptions.inSampleSize = 5;
				is = getContentResolver().openInputStream(imageUri);
				Bitmap bitmap = BitmapFactory.decodeStream(is, null, mOptions);
				BitmapDrawable drawable = new BitmapDrawable(bitmap);

				mSwitcher.setImageDrawable(drawable);
				imageCache.put(imageUri, drawable);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			mSwitcher.setImageDrawable((BitmapDrawable) imageCache
					.get(imageUri));
		}

		end = System.currentTimeMillis();

		Log.i(TAG, "display time: " + (end - start) + "ms");

	}

	public void onNothingSelected(AdapterView parent) {
	}

	public View makeView() {
		Log.i(TAG, "make view!!!");
		ImageView i = new ImageView(this);
		i.setBackgroundColor(0xFF000000);
		i.setScaleType(ImageView.ScaleType.FIT_CENTER);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		return i;
	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private Cursor mCursor;

		public ImageAdapter(Cursor cursor, Context c) {
			mCursor = cursor;
			mContext = c;
		}

		public int getCount() {
			return mCursor.getCount();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			Log.i(TAG, "get View!!!");
			long start, end;
			start = System.currentTimeMillis();

			// mCursor.requery();
			mCursor.moveToPosition(position);

			int thumbIdColumn = mCursor
					.getColumnIndex(MediaStore.Images.Thumbnails._ID);
			int thumbId = mCursor.getInt(thumbIdColumn);

			Uri thumbUri = Uri.withAppendedPath(
					MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, ""
							+ thumbId);
			Log.i(TAG, "Thumbnail Uri = " + thumbUri.toString());

			ImageView i;
			if (!imageCache.containsKey(thumbUri)) {
				i = new ImageView(mContext);
				i.setImageURI(thumbUri);
				i.setAdjustViewBounds(true);
				i.setLayoutParams(new Gallery.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				i.setBackgroundResource(R.drawable.picture_frame);
				imageCache.put(thumbUri, i);
			} else {
				i = (ImageView)imageCache.get(thumbUri);
			}
			end = System.currentTimeMillis();
			Log.i(TAG, "get view spend time: " + (end - start) + "ms");
			return i;
		}

	}

	private void loadImages() {
		Uri uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
		String[] projection = { MediaStore.Images.Thumbnails._ID,
				MediaStore.Images.Thumbnails.IMAGE_ID };
		String selection = MediaStore.Images.Thumbnails.KIND + " = 1";

		mCursor = managedQuery(uri, projection, selection, null, null);
		Log.i(TAG, "thumbnail count:" + mCursor.getCount());
		if (mCursor != null) {
			mCursor.moveToFirst();
			mGallery.setAdapter(new ImageAdapter(mCursor, this));
			mGallery.setOnItemSelectedListener(this);
		}
	}

	/**
	 * デバッグ用メソッド
	 */
	private void displaySdCard() {
		Uri uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI; // Where
		// images
		// are
		// stored
		Cursor c = this.managedQuery(uri, null, null, null, null);
		Log.i(TAG, "DISPLAYING THUMBNAILS  = " + c.getCount());
		c.moveToFirst();
		for (int k = 0; k < c.getCount(); k++) {
			Log.i(TAG, "ID = " + c.getString(c.getColumnIndexOrThrow("_id")));
			for (String column : c.getColumnNames()) {
				Log.i(TAG, column + "="
						+ c.getString(c.getColumnIndexOrThrow(column)));
			}
			c.moveToNext();
		}

		uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI; // Where images are
		// stored
		c = this.managedQuery(uri, null, null, null, null);

		Log.i(TAG, "DISPLAYING IMAGES  = " + c.getCount());
		c.moveToFirst();
		for (int k = 0; k < c.getCount(); k++) {
			Log.i(TAG, "ID = " + c.getString(c.getColumnIndexOrThrow("_id")));
			for (String column : c.getColumnNames()) {
				Log.i(TAG, column + "="
						+ c.getString(c.getColumnIndexOrThrow(column)));
			}
			c.moveToNext();
		}
	}
}
