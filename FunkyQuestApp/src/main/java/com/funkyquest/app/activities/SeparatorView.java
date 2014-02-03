package com.funkyquest.app.activities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.qbix.funkyquest.R;

/**
 * Created by BigBlackBug on 2/3/14.
 */
public class SeparatorView extends LinearLayout {

    public SeparatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public SeparatorView(Context context, String text) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup layout= (ViewGroup) inflater.inflate(R.layout.separator_view, this, true);
        TextView separatorText = (TextView) layout.findViewById(R.id.tv_separator_text);
        separatorText.setText(text);
    }
}
