package jp.co.techfirm.util;

import android.graphics.Bitmap;
import android.graphics.Color;

public class BitmapUtil {

	public static Bitmap makeGrayscale(Bitmap bitmap) {
		Bitmap bit = bitmap.copy(Bitmap.Config.ARGB_8888, true);

		int dot = 8;
		// ピクセルデータ分ループ
		for (int i = 0; i < bit.getWidth(); i++) {
			for (int j = 0; j < bit.getHeight(); j++) {
				int color = bitmap.getPixel(i, j);
				int r = Color.red(color);
				int g = Color.green(color);
				int b = Color.blue(color);

				// ここでピクセルデータをいじくる
				int gray = (77 * r + 28 * g + 151 * b) / 256;

				bit.setPixel(i, j, Color.rgb(gray, gray, gray));
			}
		}
		return bit;
	}

	public static Bitmap makeMozaic(Bitmap bitmap) {
		int dot, width, height, square;

		dot = 8;
		square = dot * dot;
		width = bitmap.getWidth() / dot;
		height = bitmap.getHeight() / dot;

		Bitmap bit = bitmap.copy(Bitmap.Config.ARGB_8888, true);

		// ピクセルデータ分ループ
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// ドットの中の平均値を使う
				int rr = 0;
				int gg = 0;
				int bb = 0;

				int moveX = i * dot;
				int moveY = j * dot;

				for (int k = 0; k < dot; k++) {
					for (int l = 0; l < dot; l++) {
						int dotColor = bitmap.getPixel(moveX + k, moveY + l);
						rr += Color.red(dotColor);
						gg += Color.green(dotColor);
						bb += Color.blue(dotColor);
					}
				}
				rr = rr / square;
				gg = gg / square;
				bb = bb / square;

				for (int k = 0; k < dot; k++) {
					for (int l = 0; l < dot; l++) {
						bit.setPixel(moveX + k, moveY + l, Color
								.rgb(rr, gg, bb));
					}
				}
			}
		}
		return bit;
	}

	public static int[] calculateMozaic(Bitmap bitmap) {
		int dot, width, height, size, square;
		int[] colorTable;

		dot = 8;
		square = dot * dot;
		width = bitmap.getWidth() / dot;
		height = bitmap.getHeight() / dot;
		size = width * height;
		colorTable = new int[size];

		int index = 0;
		// ピクセルデータ分ループ
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {

				// ドットの中の平均値を使う
				int rr = 0;
				int gg = 0;
				int bb = 0;

				int moveX = i * dot;
				int moveY = j * dot;
				for (int k = 0; k < dot; k++) {
					for (int l = 0; l < dot; l++) {
						int dotColor = bitmap.getPixel(moveX + k, moveY + l);
						rr += Color.red(dotColor);
						gg += Color.green(dotColor);
						bb += Color.blue(dotColor);
					}
				}
				rr = rr / square;
				gg = gg / square;
				bb = bb / square;
				colorTable[index++] = Color.rgb(rr, gg, bb);

			}
		}

		return colorTable;
	}

	public static int calculateImageColor(Bitmap bitmap) {
		int width, height, square;
		int color, r, g, b;

		width = bitmap.getWidth();
		height = bitmap.getHeight();
		square = width * height;
		r = g = b = 0;

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {

				color = bitmap.getPixel(i, j);
				r += Color.red(color);
				g += Color.green(color);
				b += Color.blue(color);
			}
		}

		r = r / square;
		g = g / square;
		b = b / square;

		return Color.rgb(r, g, b);
	}
}
