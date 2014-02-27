package com.newresources.funkyquest.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import com.newresources.funkyquest.util.FQException;
import com.newresources.funkyquest.util.RequestCodes;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by BigBlackBug on 2/17/14.
 */
public class PhotoComponent {

	private final Activity activity;

	public PhotoComponent(Activity activity) {
		this.activity = activity;
	}

	public File dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				throw new FQException("Не могу создать файл для фотки", ex);
			}
			// Continue only if the File was successfully created
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
			                           Uri.fromFile(photoFile));
			activity.startActivityForResult(takePictureIntent, RequestCodes.TAKE_PICTURE_REQUEST_CODE);
			return photoFile;
		} else {
			throw new FQException("Камера недоступна");
		}
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = activity.getExternalFilesDir(null);
		return File.createTempFile(imageFileName, ".jpg", storageDir);
	}


}
