package com.funkyquest.app.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.funkyquest.app.R;

/**
 * Created by BigBlackBug on 2/1/14.
 */
public class MapFragment extends Fragment {
    private String text;

    public MapFragment(String text) {
        this.text = text;
    }

	private static View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = inflater.inflate(R.layout.map_fragment, container, false);
		} catch (InflateException e) {
        /* map is already there, just return view as it is */
		}
		return view;
	}
}
