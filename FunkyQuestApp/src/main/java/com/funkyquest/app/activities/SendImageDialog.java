package com.funkyquest.app.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.funkyquest.app.R;

public class SendImageDialog extends DialogFragment {

	private final DialogListener dialogListener;

	private final Bitmap thumbnail;

	public SendImageDialog(DialogListener dialogListener,Bitmap thumbnail) {
		this.dialogListener = dialogListener;
		this.thumbnail = thumbnail;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		LinearLayout layout= (LinearLayout) inflater.inflate(R.layout.image_dialog_view, null);
		ImageView viewById = (ImageView) layout.findViewById(R.id.iv_dialog_thumbnail);
		viewById.setImageBitmap(thumbnail);
		viewById.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		builder.setView(layout)
				.setPositiveButton(getActivity().getString(R.string.answer_dialog_confirm), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialogListener.uploadImage();
					}
				})
				.setNegativeButton(getActivity().getString(R.string.answer_dialog_cancel), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		// Create the AlertDialog object and return it
		return builder.create();
	}

	private static interface DialogListener {

		public void uploadImage();
	}
}