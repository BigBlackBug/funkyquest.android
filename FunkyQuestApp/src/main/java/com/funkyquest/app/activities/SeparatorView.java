package com.funkyquest.app.activities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.funkyquest.app.R;

/**
 * Created by BigBlackBug on 2/3/14.
 */
public class SeparatorView extends LinearLayout {

	public SeparatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.separator_view, this, true);
	}

	public SeparatorView(Context context) {
		super(context);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.separator_view, this, true);
//		TextView separatorText = (TextView) layout.findViewById(R.id.tv_separator_text);
//		separatorText.setText(text);
	}
}
