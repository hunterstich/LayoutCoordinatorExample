package com.hunterrobbert.layoutcoordinatorexample;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by hunter on 5/2/15.
 */
public class SampleFragment extends Fragment {

    private static final String TAG = SampleFragment.class.getSimpleName();

    public static final String KEY_FRAGMENT_TITLE = "key_fragment_title";

    private String mFragTitle;
    private CollapsibleHeaderLayout mCollapsibleHeaderLayout;
    private RecyclerView mRecyclerView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get bundle args and set up fragment
        Bundle bundle = getArguments();
        mFragTitle = bundle.getString(KEY_FRAGMENT_TITLE);

        if (getActivity() instanceof MainActivity) {
            mCollapsibleHeaderLayout = ((MainActivity)getActivity()).getCollapsibleHeaderLayout();
        }

        //instantiate views
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        //setup recyclerview
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCollapsibleHeaderLayout.registerScrollableView(mRecyclerView, mFragTitle);
        setRecyclerViewAdapter(mRecyclerView, getRandomizedData(mFragTitle));
    }

    private void setRecyclerViewAdapter(RecyclerView recyclerView, ArrayList<String> list) {
        if (!isAdded()) {
            return;
        }
        recyclerView.setAdapter(new HeaderAutoFooterRecyclerAdapter(getActivity(),list, android.R.layout.simple_list_item_1, mCollapsibleHeaderLayout.getHeaderHeight()));
    }

    //create a random list as a sample
    private ArrayList<String> getRandomizedData(String title) {
        ArrayList<String> arrayList = new ArrayList<>();

        int random = (int )(Math.random() * 40 + 6);
        for (int i = 0; i < random; i++) {
            arrayList.add(title + " number " + i);
        }

        return arrayList;
    }


}


