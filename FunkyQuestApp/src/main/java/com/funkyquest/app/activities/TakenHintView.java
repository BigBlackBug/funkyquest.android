package com.funkyquest.app.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.funkyquest.app.dto.HintDTO;
import com.qbix.funkyquest.R;

public class TakenHintView extends LinearLayout {

//    private View mValue;
//    private ImageView mImage;

    public TakenHintView(Context context, HintDTO hintDTO) {
        super(context);

        //TODO fill with data
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup mainView = (ViewGroup) inflater.inflate(R.layout.taken_hint_view, this, true);
        TextView hintTitleTV = (TextView) mainView.findViewById(R.id.tv_hint_title);

        hintTitleTV.setText(hintDTO.getName());
        TextView hintTextTV = (TextView) mainView.findViewById(R.id.tv_hint_text);
//        hintTextTV.setText(hintDTO.getText());

    }

}