package com.hunterrobbert.layoutcoordinatorexample;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hunter on 5/2/15.
 */
public class SampleListFragment extends Fragment {

    private static final String TAG = SampleListFragment.class.getSimpleName();

    public static final String KEY_FRAGMENT_TITLE = "key_fragment_title";

    private String mFragTitle;
    private CollapsibleHeaderLayout mCollapsibleHeaderLayout;
    private ListView mListView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_sample,container,false);
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
        mListView = (ListView) view.findViewById(R.id.listview);

        //setup recyclerview
        mCollapsibleHeaderLayout.registerScrollableView(mListView, mFragTitle);
        setListViewAdapter(mListView, getRandomizedData(mFragTitle));
    }

    private void setListViewAdapter(ListView listView, List<String> list) {
        if (!isAdded()) {
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, list);
        listView.setAdapter(adapter);

        //listView.setAdapter(, android.R.layout.simple_list_item_1, mCollapsibleHeaderLayout.getHeaderHeight()));
    }

    //create a random list as a sample
    private List<String> getRandomizedData(String title) {
        List<String> arrayList = new ArrayList<>();
        int max = 7;
        int random = (int )(Math.random() * max + 6);
        for (int i = 0; i < random; i++) {
            arrayList.add(title + " number " + i);
        }

        return arrayList;
    }


}


