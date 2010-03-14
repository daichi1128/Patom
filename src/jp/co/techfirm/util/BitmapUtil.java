package jp.co.techfirm.util;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

public class BitmapUtil {

	private static final String TAG = "BitmapUtil";

	// �J���[�p���b�g
	private static int[] rt;
	private static int[] gt;
	private static int[] bt;

	/**
	 * �����摜�ɂ���
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap makeGrayscale(Bitmap bitmap) {
		return makeGrayscale(bitmap, true);
	}

	/**
	 * �����摜�ɂ���
	 * 
	 * @param bitmap
	 * @param isPostalication
	 *            true���ƊK����������
	 * @return
	 */
	public static Bitmap makeGrayscale(Bitmap bitmap, boolean isPostalication) {
		if (isPostalication)
			makePostarizationTable(8, 8, 8);

		int width, height;
		int[] inputPixels, outputPixels;

		width = bitmap.getWidth();
		height = bitmap.getHeight();

		inputPixels = new int[width * height];
		outputPixels = new int[width * height];
		bitmap.getPixels(inputPixels, 0, width, 0, 0, width, height);

		Bitmap bit = bitmap.copy(Bitmap.Config.ARGB_8888, true);

		// �s�N�Z���f�[�^�����[�v
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int color = inputPixels[i + j * width];
				int r = Color.red(color);
				int g = Color.green(color);
				int b = Color.blue(color);

				int gray;
				if (isPostalication) {
					gray = rt[(77 * r + 28 * g + 151 * b) / 256];
				} else {
					gray = (77 * r + 28 * g + 151 * b) / 256;
				}

				// bit.setPixel(i, j, Color.rgb(gray, gray, gray));
				outputPixels[i + j * width] = Color.rgb(gray, gray, gray);
			}
		}
		bit.setPixels(outputPixels, 0, width, 0, 0, width, height);
		return bit;
	}

	/**
	 * ���U�C�N���ɂ���
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap makeMozaic(Bitmap bitmap) {
		return makeMozaic(bitmap, true);
	}

	/**
	 * ���U�C�N���ɂ���
	 * 
	 * getPixels, setPixels���g����getPixel,setPixel���g�������R�{�����Ȃ����� �J���[�p���b�g�ŊK���������Ă�
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap makeMozaic(Bitmap bitmap, boolean isPostarization) {
		if (isPostarization)
			makePostarizationTable(8, 8, 8);

		// ���[�v�O�ɕK�v�ȕϐ��̓��[�J���ϐ��ɃL���b�V������
		int dot, width, height, square, originalWidth, originalHeight;
		int[] inputPixels, outputPixels;

		dot = 8;
		square = dot * dot;
		width = bitmap.getWidth() / dot;
		height = bitmap.getHeight() / dot;
		originalWidth = bitmap.getWidth();
		originalHeight = bitmap.getHeight();

		inputPixels = new int[bitmap.getWidth() * bitmap.getHeight()];
		outputPixels = new int[bitmap.getWidth() * bitmap.getHeight()];
		bitmap.getPixels(inputPixels, 0, originalWidth, 0, 0, originalWidth,
				originalHeight);

		Bitmap bit = bitmap.copy(Bitmap.Config.ARGB_8888, true);

		// �s�N�Z���f�[�^�����[�v
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// �h�b�g�̒��̕��ϒl���g��
				int rr = 0;
				int gg = 0;
				int bb = 0;

				int moveX = i * dot;
				int moveY = j * dot;

				for (int k = 0; k < dot; k++) {
					for (int l = 0; l < dot; l++) {
						int dotColor = inputPixels[moveX + k + (moveY + l)
								* originalWidth];
						rr += Color.red(dotColor);
						gg += Color.green(dotColor);
						bb += Color.blue(dotColor);
					}
				}
				if (isPostarization) {
					rr = rt[rr / square];
					gg = gt[gg / square];
					bb = bt[bb / square];
				} else {
					rr = rr / square;
					gg = gg / square;
					bb = bb / square;
				}

				for (int k = 0; k < dot; k++) {
					for (int l = 0; l < dot; l++) {
						outputPixels[moveX + k + (moveY + l) * originalWidth] = Color
								.rgb(rr, gg, bb);
					}
				}
			}
		}
		bit.setPixels(outputPixels, 0, originalWidth, 0, 0, originalWidth,
				originalHeight);
		return bit;
	}
	
	/**
	 * �����Ȑ����`�ɉ摜�𕪊����A�e�����`�̕��ϐF���������z���Ԃ�
	 * @param bitmap
	 * @return
	 */
	public static int[] calculateMozaic(Bitmap bitmap) {
		return calculateMozaic(bitmap, true);
	}

	/**
	 * �����Ȑ����`�ɉ摜�𕪊����A�e�����`�̕��ϐF���������z���Ԃ�
	 * 
	 * @param bitmap
	 * @return
	 */
	public static int[] calculateMozaic(Bitmap bitmap, boolean isPostarization) {
		if (isPostarization)
			makePostarizationTable(8, 8, 8);

		int dot, width, height, size, square, originalWidth, originalHeight;
		int[] colorTable;

		dot = 8;
		square = dot * dot;
		originalWidth = bitmap.getWidth();
		originalHeight = bitmap.getHeight();
		width = originalWidth / dot;
		height = originalHeight / dot;
		size = width * height;
		colorTable = new int[size];

		Log.i(TAG, "width: " + width + " height: " + height + " length: "
				+ size);

		int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
		bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(),
				bitmap.getHeight());

		// �s�N�Z���f�[�^�����[�v
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {

				// �h�b�g�̒��̕��ϒl���g��
				int rr = 0;
				int gg = 0;
				int bb = 0;

				int moveX = i * dot;
				int moveY = j * dot;
				for (int k = 0; k < dot; k++) {
					for (int l = 0; l < dot; l++) {
						int dotColor = pixels[moveX + k + (moveY + l)
								* originalWidth];
						rr += Color.red(dotColor);
						gg += Color.green(dotColor);
						bb += Color.blue(dotColor);
					}
				}
				if (isPostarization) {
					rr = rt[rr / square];
					gg = gt[gg / square];
					bb = bt[bb / square];
				} else {
					rr = rr / square;
					gg = gg / square;
					bb = bb / square;
				}
				colorTable[i + j] = Color.rgb(rr, gg, bb);

			}
		}

		return colorTable;
	}

	/**
	 * Bitmap�̕��ϐF�����߂�
	 * 
	 * @param bitmap
	 * @return
	 */
	public static int calculateImageColor(Bitmap bitmap, boolean isPostarization) {
		if (isPostarization) makePostarizationTable(8, 8, 8);
		
		int width, height, square;
		int color, r, g, b;
		int pixels[];

		width = bitmap.getWidth();
		height = bitmap.getHeight();
		square = width * height;
		pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		r = g = b = 0;

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {

				color = pixels[i + j * width];
				r += Color.red(color);
				g += Color.green(color);
				b += Color.blue(color);
			}
		}

		if (isPostarization) {
			r = rt[r / square];
			g = gt[g / square];
			b = bt[b / square];
		} else {
			r = r / square;
			g = g / square;
			b = b / square;	
		}

		return Color.rgb(r, g, b);
	}

	/**
	 * ���T�C�Y����
	 * 
	 * @param bitmap
	 * @param afterWidth
	 * @param afterHeight
	 * @return
	 */
	public static Bitmap resize(Bitmap bitmap, int afterWidth, int afterHeight) {
		return clip(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
				afterWidth, afterHeight);
	}

	/**
	 * �؂蔲���n����̃{�X
	 * 
	 * @param bitmap
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param afterWidth
	 * @param afterHeight
	 * @return
	 */
	public static Bitmap clip(Bitmap bitmap, int x, int y, int width,
			int height, int afterWidth, int afterHeight) {
		float scaleWidth, scaleHeight;
		scaleWidth = (float) afterWidth / (float) width;
		scaleHeight = (float) afterHeight / (float) height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		return Bitmap.createBitmap(bitmap, x, y, width, height, matrix, true);
	}

	/**
	 * �c��������āA���S�����ӂ�size(px)�̐����`��Bitmap��؂蔲��
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap clipSquare(Bitmap bitmap, int size) {
		int x, y, width, height;
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		if (width > height) {
			x = (width - height) / 2;
			y = 0;
			width = height;
		} else {
			x = 0;
			y = (height - width) / 2;
			height = width;
		}

		if (size == 0)
			size = width;
		return BitmapUtil.clip(bitmap, x, y, width, height, size, size);
	}

	/**
	 * �c��������āA���S���琳���`��Bitmap��؂蔲��
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap clipSquare(Bitmap bitmap) {
		return clipSquare(bitmap, 0);
	}

	/**
	 * �K����������
	 * 
	 * @param rs
	 *            �Ԃ����F�ɂ��邩
	 * @param gs
	 *            �΂����F�ɂ��邩
	 * @param bs
	 *            �����F�ɂ��邩
	 * 
	 *            makePostarizationTable(8,8,8)�Ƃ����
	 *            �ԁA�΁A���ꂼ��256/8=32���ƂтƂт̒l�ŐU�蕪�����A8�p�^�[������8*8*8=512�F��
	 *            �J���[�p���b�g�ɂȂ�
	 * 
	 */
	public static void makePostarizationTable(int rs, int gs, int bs) {
		if (rt != null)
			return;
		rt = new int[256];
		bt = new int[256];
		gt = new int[256];

		int k, l;
		double n, d;
		// red�̃J���[�e�[�u��
		n = (256.0 / (double) rs); /* �P�K���Ɋ܂܂�錳�̊K���� */
		d = 255.0 / (double) ((double) rs - 1.0);

		for (k = 0; k < rs; k++) {
			for (l = (int) (k * n); l < (int) ((k + 1) * n); l++) {
				rt[l] = (int) (d * k);
			}
		}

		n = (256.0 / (double) gs); /* �P�K���Ɋ܂܂�錳�̊K���� */
		d = 255.0 / (double) ((double) gs - 1.0);

		for (k = 0; k < gs; k++) {
			for (l = (int) (k * n); l < (int) ((k + 1) * n); l++) {
				gt[l] = (int) (d * k);
			}
		}

		n = (256.0 / (double) bs); /* �P�K���Ɋ܂܂�錳�̊K���� */
		d = 255.0 / (double) ((double) bs - 1.0);

		for (k = 0; k < bs; k++) {
			for (l = (int) (k * n); l < (int) ((k + 1) * n); l++) {
				bt[l] = (int) (d * k);
			}
		}
	}
}
