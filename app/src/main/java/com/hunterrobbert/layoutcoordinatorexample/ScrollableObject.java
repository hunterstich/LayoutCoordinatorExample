package com.hunterrobbert.layoutcoordinatorexample;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by hunter on 6/4/15.
 */
public class ScrollableObject {


    private static final String TAG = ScrollableObject.class.getSimpleName();

    private static final int RECYCLER_VIEW = 0;
    private static final int LIST_VIEW = 1;


    private LayoutCoordinator mLayoutCoordinator;

    public View mScrollView;
    private int mScrollViewType;
    String uniqueIdentifier;


    int mScrollPosition;

    public ScrollableObject(LayoutCoordinator coordinator, View scrollView, String uniqueId) {
        mLayoutCoordinator = coordinator;
        defineScrollView(scrollView);
        uniqueIdentifier = uniqueId;
        initScrollPosition(coordinator);
    }

    public void updateScrollableView(View scrollableView) {
        defineScrollView(scrollableView);
    }

    private void defineScrollView(View scrollView) {
        if (scrollView instanceof RecyclerView) {
            mScrollView = scrollView;
            mScrollViewType = RECYCLER_VIEW;
            updateHeader(mLayoutCoordinator.getHeaderHeight());
        } else if (scrollView instanceof ListView) {
            mScrollView = scrollView;
            mScrollViewType = LIST_VIEW;
            updateHeader(mLayoutCoordinator.getHeaderHeight());
            updateListViewFooter((ListView)mScrollView);
        } else {
            mScrollViewType = -1;
        }
    }

    public void initScrollPosition(LayoutCoordinator coordinator) {

        switch (mScrollViewType) {
            case RECYCLER_VIEW :
                if (coordinator.getHeaderHeight() > coordinator.getScrollAccumulator() + coordinator.getToolbarOffset()) {
                    mScrollPosition = coordinator.getScrollAccumulator();
                } else {
                    //the toolbar is fully compacted
                    LinearLayoutManager manager = getRecyclerViewLinearLayoutManager((RecyclerView) mScrollView);
                    if (manager != null) {
                        int firstVisibleChild = manager.findFirstVisibleItemPosition();
                        if (firstVisibleChild == 0) {
                            //scroll to toolbar offset
                            mScrollPosition = coordinator.getHeaderHeight() - coordinator.getToolbarOffset();
                        }
                    }
                }
                break;
            case LIST_VIEW :
                //initialize list view scroll position
                break;
        }


        scrollToPosition(coordinator.getScrollAccumulator());
    }



    public void updateHeader(int headerHeight) {
        switch (mScrollViewType) {
            case RECYCLER_VIEW :
                RecyclerView.Adapter adapter = ((RecyclerView)mScrollView).getAdapter();
                if (adapter instanceof HeaderAutoFooterRecyclerAdapter) {
                    ((HeaderAutoFooterRecyclerAdapter) adapter).updateHeaderHeight(headerHeight);
                }
                break;
            case LIST_VIEW :
                //update header footer
                updateListViewHeader(((ListView)mScrollView), headerHeight);
                break;
        }
    }

    private void updateListViewHeader(ListView listView, int headerHeight) {
        //header
        //LayoutInflater layoutInflator = LayoutInflater.from(mContext);
        View header = new View(mLayoutCoordinator.getContext());
        header.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerHeight));
        listView.addHeaderView(header, null, false);
    }


    private void updateListViewFooter(final ListView listView) {
        listView.post(new Runnable() {
            @Override
            public void run() {
            int totalListHeight = listView.getHeight();
            Log.d(TAG, "List view footer being made. totalListHeight: " + totalListHeight);

            View footer = new View(mLayoutCoordinator.getContext());
            if (totalListHeight < mLayoutCoordinator.getDisplayHeight()) {
                int footerHeight = mLayoutCoordinator.getDisplayHeight() - totalListHeight;
                footer.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, totalListHeight));
                listView.addFooterView(footer, null, false);
            }
            }
        });
    }

    public void scroll(int scrollPosition, boolean isCurrentPage) {
        switch (mScrollViewType) {
            case RECYCLER_VIEW :
                //scrolling on scroll
                determineAndUpdateScrollableViews(scrollPosition, isCurrentPage);
                break;
            case LIST_VIEW :
                //TODO : scroll list view
                determineAndUpdateScrollableViews(scrollPosition, isCurrentPage);
                break;
        }
    }

    private void determineAndUpdateScrollableViews(int scrollPosition, boolean isCurrentPage) {
        if (scrollPosition < mLayoutCoordinator.getHeaderHeight() - mLayoutCoordinator.getToolbarOffset()) {
            Log.d(TAG, "determination. OPTION 1");
            //the toolbar is between fully expanded and fully compacted
                //update all views and their holds
                retainScrollPosition(scrollPosition);
                if (!isCurrentPage) {
                    scrollToPosition(scrollPosition);
                }
        } else {
            if (scrollPosition > mLayoutCoordinator.getHeaderHeight() - mLayoutCoordinator.getToolbarOffset() && scrollPosition < mLayoutCoordinator.getHeaderHeight()) {
                Log.d(TAG, "determination. OPTION 2. HeaderHeight: " + mLayoutCoordinator.getHeaderHeight());
                // item 0 is still in view but
                // the first item is below the toolbar
                    //if the current view is this
                        //don't update the scroll, but do update the hold
                    if (isCurrentPage) {
                        retainScrollPosition(scrollPosition);
                    } else {
                        // the scrolling isn't happening on this.
                        if (mScrollPosition < mLayoutCoordinator.getHeaderHeight() - mLayoutCoordinator.getToolbarOffset()) {
                            //scroll the view up to the bottom of the toolbar and hold that height
                            int bellowToolbarScroll = mLayoutCoordinator.getHeaderHeight() - mLayoutCoordinator.getToolbarOffset();
                            retainScrollPosition(bellowToolbarScroll);
                            scrollToPosition(bellowToolbarScroll);
                        }
                    }
            } else {
                Log.d(TAG, "determination. OPTION 3");
                //item 0 is completely out of view
                    //don't update scroll or hold it
                if (isCurrentPage) {
                        retainScrollPosition(scrollPosition);
                } else {
                    // the scrolling isn't happening on this.
                    if (mScrollPosition < mLayoutCoordinator.getHeaderHeight() - mLayoutCoordinator.getToolbarOffset()) {
                        //scroll the view up to the bottom of the toolbar and hold that height
                        int bellowToolbarScroll = mLayoutCoordinator.getHeaderHeight() - mLayoutCoordinator.getToolbarOffset();
                        retainScrollPosition(bellowToolbarScroll);
                        scrollToPosition(bellowToolbarScroll);
                    }
                }

            }
        }
    }

    private void updateListViewScroll(int scrollPosition, boolean isCurrentPage) {
        if (mLayoutCoordinator.getHeaderHeight() > scrollPosition + mLayoutCoordinator.getToolbarOffset()) {
            //header isn't fully compacted
            retainScrollPosition(scrollPosition);
            scrollToPosition(scrollPosition);
        } else {
            //toolbar is fully compacted
            ListView view = (ListView) mScrollView;
            int firstVisibleChild = view.getFirstVisiblePosition();
            if (firstVisibleChild == 0) {
                if (isCurrentPage) {
                    retainScrollPosition(scrollPosition);
                } else {
                    if (mLayoutCoordinator.getHeaderHeight() - scrollPosition > mLayoutCoordinator.getToolbarOffset()) {
                        retainScrollPosition(mLayoutCoordinator.getHeaderHeight() - mLayoutCoordinator.getToolbarOffset());
                        scrollToPosition(mLayoutCoordinator.getHeaderHeight() - mLayoutCoordinator.getToolbarOffset());
                    }
                }
            }
        }
    }

    private void updateRecyclerViewScroll(int scrollPosition, boolean isCurrentPage) {
            //If mHeaderView isn't fully compacted:
            if (mLayoutCoordinator.getHeaderHeight() > scrollPosition + mLayoutCoordinator.getToolbarOffset()) {
                retainScrollPosition(scrollPosition);
                scrollToPosition(scrollPosition);
            } else {
                //the toolbar is fully compacted
                LinearLayoutManager manager = getRecyclerViewLinearLayoutManager((RecyclerView)mScrollView);
                if (manager != null) {
                    int firstVisibleChild = manager.findFirstVisibleItemPosition();
                    if (firstVisibleChild == 0) {
                        //scroll to toolbar offset
                        if (isCurrentPage) {
                            //were talking about the current fragment/recyclerview
                            retainScrollPosition(scrollPosition);
                        } else {
                            if (mLayoutCoordinator.getHeaderHeight() - scrollPosition > mLayoutCoordinator.getToolbarOffset()) {
                                retainScrollPosition(mLayoutCoordinator.getHeaderHeight() - mLayoutCoordinator.getToolbarOffset());
                                scrollToPosition(mLayoutCoordinator.getHeaderHeight() - mLayoutCoordinator.getToolbarOffset());
                            }
                        }
                    }
                }
            }
    }

    private void retainScrollPosition(int position) {
        mScrollPosition = position;
    }

    private void scrollToPosition(int position) {
        switch (mScrollViewType) {
            case RECYCLER_VIEW :
                LinearLayoutManager llm = getRecyclerViewLinearLayoutManager((RecyclerView) mScrollView);
                if (llm != null) {
                    llm.scrollToPositionWithOffset(0, -position);
                }
                break;
            case LIST_VIEW :
                //scroll list view
                Log.d(TAG, "List view scroll to position: " + position);
                ((ListView)mScrollView).setSelectionFromTop(0,-position);
                break;

        }
    }

    private LinearLayoutManager getRecyclerViewLinearLayoutManager(RecyclerView recyclerView) {

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            return ((LinearLayoutManager)recyclerView.getLayoutManager());
        }

        return null;
    }

}
