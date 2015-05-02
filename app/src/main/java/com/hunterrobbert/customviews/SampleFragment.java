package com.hunterrobbert.customviews;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hunter on 5/2/15.
 */
public class SampleFragment extends Fragment{

    private static final String TAG = SampleFragment.class.getSimpleName();

    public static final String KEY_FRAGMENT_TITLE = "key_fragmentt_title";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //get bundle args and set up fragment
        Bundle bundle = getArguments();
        String fragTitle = bundle.getString(KEY_FRAGMENT_TITLE);

        Log.d(TAG, "fragTitle: " + fragTitle);
    }
}
