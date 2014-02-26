package com.funkyquest.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by BigBlackBug on 2/17/14.
 */
public class Utils {

	public static Bitmap readRotateAndScale(File imageFile, int sampleSize, Context context) {
		try {
			final BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inJustDecodeBounds = false;
			options.inSampleSize = sampleSize;
			Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(imageFile), null, options);
			ExifInterface ei = new ExifInterface(imageFile.getAbsolutePath());
			int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
			                                     ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					bmp = rotate(bmp, 90);
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					bmp = rotate(bmp, 180);
					break;
			}
			return bmp;
		} catch (IOException e) {
			throw new FQException("WILL NEVER OCCUR");
		}
	}

	public static Bitmap rotate(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
		                           source.getHeight(), matrix, true);
	}
}
