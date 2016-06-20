package com.hunterrobbert.layoutcoordinatorexample;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by hunter on 6/4/15
 */

public class ScrollAccumulator {

    private static final String TAG = ScrollAccumulator.class.getSimpleName();

    private int mScrollAccumulation = 0;

    private LayoutCoordinator mLayoutCoordinator;

    //list view constants
    float mInitialY = 0;

    private OnScrollAccumulationListener mOnScrollAccumulationListener;
    public interface OnScrollAccumulationListener {
        void onScrollAccumulation(int accumulation);
        void onScrollStateChanged(int newState, int accumulation);
    }

    public void registerOnScrollAccumulationListener(OnScrollAccumulationListener listener) {
        mOnScrollAccumulationListener = listener;
    }

    public void deregisterOnScrollAccumulationListener() {
        mOnScrollAccumulationListener = null;
    }

    //public constructor
    public ScrollAccumulator(LayoutCoordinator coordinator, OnScrollAccumulationListener listener) {
        mLayoutCoordinator = coordinator;
        registerOnScrollAccumulationListener(listener);
    }

    public void addViewToScrollAccumulator(View view) {
        if (view instanceof RecyclerView) {
            ((RecyclerView) view).setOnScrollListener(mRecyclerViewScrollWatcher());
        } else if (view instanceof ListView) {
            // TODO: set ListView scroll listener
            ((ListView)view).setOnScrollListener(mListViewScrollWatcher());
        }
    }


    public int getScrollAccumulation() {
        return mScrollAccumulation;
    }

    public void setScrollAccumulation(int newScroll) {
        mScrollAccumulation = newScroll;
    }

    private void callOnScrollAccumulation(int scrollAccumulation) {
        if (mOnScrollAccumulationListener != null) {
            mOnScrollAccumulationListener.onScrollAccumulation(scrollAccumulation);
        }
    }

    private void callOnScrollStateChanged(int newState, int scrollAccumulation) {
        if (mOnScrollAccumulationListener != null) {
            mOnScrollAccumulationListener.onScrollStateChanged(newState, scrollAccumulation);
        }
    }


    @NonNull
    private AbsListView.OnScrollListener mListViewScrollWatcher() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                callOnScrollStateChanged(scrollState, mScrollAccumulation);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    //The header is still in view
                    View c = view.getChildAt(firstVisibleItem);
                    if (c != null) {
                        int scrollY = -c.getTop() + view.getPaddingTop();
                        mScrollAccumulation = scrollY;
                        Log.d(TAG, "LV scrollAccumulation: " + mScrollAccumulation);
                        callOnScrollAccumulation(mScrollAccumulation);
                    }
                } else if (firstVisibleItem < 0) {
                    if (mLayoutCoordinator.getHeaderHeight() != 0) {
                        mScrollAccumulation = mLayoutCoordinator.getHeaderHeight();
                        callOnScrollAccumulation(mScrollAccumulation);
                    }
                }


            }
        };
    }

    private RecyclerView.OnScrollListener mRecyclerViewScrollWatcher() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mScrollAccumulation = mScrollAccumulation + dy;
                Log.d(TAG, "RV scrollAccumulation: " + mScrollAccumulation);
                callOnScrollAccumulation(mScrollAccumulation);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                callOnScrollStateChanged(newState, mScrollAccumulation);
            }
        };
    }
}
