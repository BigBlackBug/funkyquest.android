package com.newresources.funkyquest.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.newresources.funkyquest.R;

public class GameEndedActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dummy_fragment);
	    TextView tv = (TextView) findViewById(R.id.section_label);
	    tv.setText("Игра закончилась.\\n\\n Хз кто выиграл, т.к. никакой другой информации мне с сервера не приходит");
    }

}
