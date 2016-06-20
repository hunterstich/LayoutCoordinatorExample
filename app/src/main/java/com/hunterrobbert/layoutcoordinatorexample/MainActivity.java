package com.hunterrobbert.layoutcoordinatorexample;

import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;


public class MainActivity extends ActionBarActivity {

    private int[] mTabTitles = new int[] {
            R.string.title_1,
            R.string.title_2,
            R.string.title_3
    };

    public CollapsibleHeaderLayout mCollapsibleHeaderLayout;

    private ViewPager mViewPager;
    private FrameLayout mFab;
    private ImageView mOverflow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCollapsibleHeaderLayout = (CollapsibleHeaderLayout) findViewById(R.id.collapsible_header_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mFab = (FrameLayout) findViewById(R.id.fab);
        mOverflow = (ImageView) findViewById(R.id.overflow);

        mCollapsibleHeaderLayout.setSlidingTabLayoutContentDescriptions(mTabTitles);

        FragmentViewPagerAdapter fragmentViewPagerAdapter = new FragmentViewPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(fragmentViewPagerAdapter);
        mCollapsibleHeaderLayout.setViewPager(mViewPager);


        //Add a custom view to the header layout and define its scroll behavior/any extra behaviors
        mCollapsibleHeaderLayout.attachCoordinatedView(mFab)
                .startingAt(CoordinatedView.START_FAB_STRADDLE_EXPANDED_TOOLBAR_TOP_RIGHT)
                .endingAt(CoordinatedView.END_DEFAULT)
                .withBehavior(CoordinatedView.CIRCULAR_HIDE_REVEAL, CoordinatedView.QUATER_POINT)
                .attach();


        //Programmatically define where the view should be in the header when the scrollable views are scrolled all the way to the top
        //The views starting point
        Position overFlowStart = new Position(mCollapsibleHeaderLayout.getLayoutCoordinator(),mOverflow)
                .setMarginRight(Position.LOC_KEYLINE_1)
                .setMarginTop(Position.LOC_EXTENDED_TOOLBAR_CENTERLINE, Position.VIEW_CENTERLINE_Y)
                .setRules(Position.RULES_ALIGN_PARENT_RIGHT)
                .buildStart();

        mCollapsibleHeaderLayout.attachCoordinatedView(mOverflow)
                .startingAt(overFlowStart)
                .endingAt(CoordinatedView.END_COLLAPSED_TOOLBAR_POSITION_1)
                .attach();


        // If absolute control is needed over how views are translating/animating, an OnLayoutCoordinatorScrollWatcher can be set
//        mCollapsibleHeaderLayout.getLayoutCoordinator().setOnLayoutCoordinatorScrollWatcher(new LayoutCoordinator.OnLayoutCoordinatorScrollWatcher() {
//            @Override
//            public void onScrolled(int mScrollPosition) {
//                mFab.setTranslationY(-mScrollPosition);
//            }
//        });

    }

    @Override
    protected void onStop() {
//        mCollapsibleHeaderLayout.getLayoutCoordinator().releaseOnLayoutCoordinatorScrollWatcher();
        super.onStop();
    }

    //make the header layout accessible to the viewpager's fragments
    public CollapsibleHeaderLayout getCollapsibleHeaderLayout() {
        return mCollapsibleHeaderLayout;
    }


    private class FragmentViewPagerAdapter extends FragmentPagerAdapter {

        public FragmentViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.app.Fragment getItem(int i) {
            switch (i) {
                case 0 :
                    return initSampleRecyclerFrag(i);
                case 1 :
                    return initSampleRecyclerFrag(i);
                default:
                    return initSampleListFrag(i);
               }

        }

        private SampleRecyclerFragment initSampleRecyclerFrag(int i) {
            SampleRecyclerFragment sampleRecyclerFragment = new SampleRecyclerFragment();
            Bundle args = new Bundle();
            args.putString(SampleRecyclerFragment.KEY_FRAGMENT_TITLE, getString(mTabTitles[i]));
            sampleRecyclerFragment.setArguments(args);
            return sampleRecyclerFragment;
        }

        private SampleListFragment initSampleListFrag(int i) {
            SampleListFragment sampleListFragment = new SampleListFragment();
            Bundle args = new Bundle();
            args.putString(SampleListFragment.KEY_FRAGMENT_TITLE, getString(mTabTitles[i]));
            sampleListFragment.setArguments(args);
            return sampleListFragment;
        }

        @Override
        public int getCount() {
            return mTabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(mTabTitles[position]);
        }

    }
}
