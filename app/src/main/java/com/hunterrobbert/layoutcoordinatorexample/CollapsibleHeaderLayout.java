package com.hunterrobbert.layoutcoordinatorexample;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by hunter on 4/27/15.
 */

public class CollapsibleHeaderLayout extends RelativeLayout implements LayoutCoordinator.CoordinatorInterface {

    private static final String TAG = CollapsibleHeaderLayout.class.getSimpleName();

    private static final String COLOR_ACCENT = "color_accent";
    private static final String COLOR_PRIMARY = "color_primary";
    private static final String COLOR_TITLE_TEXT = "color_title_text";
    private static final String COLOR_SUBTITLE_TEXT = "color_subtitle_text";


    private LayoutCoordinator mLayoutCoordinator;

    //attrs
    protected boolean mAutoHideHeader;
    protected String mTitleText;
    protected String mSubTitleText;
    protected int mHeaderImageId;
    protected int mToolbarColor;
    protected int mTitleTextColor;
    protected int mSubTitleTextColor;

    //views
    private FrameLayout mHeaderImageContainer;
    private ImageView mHeaderImage;
    private RelativeLayout mHeaderBox;
    private View mTitleBackgroundView;
//    private LinearLayout mTitleBox;
    private TextView mTitleTextView;
    private TextView mSubTitleTextView;
    private SlidingTabLayout mSlidingTabLayout;

    //constants
    private int mTitleBackgroundOriginalHeight;
    private int mHeaderHeight;
    private int mScrollPosition;

    private FragmentPagerAdapter mFragmentPagerAdapter;
    private ViewPager mViewPager;

    public CollapsibleHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,R.styleable.CollapsibleHeaderLayout,0,0);

        try {
            mAutoHideHeader = a.getBoolean(R.styleable.CollapsibleHeaderLayout_autoHideHeader,false);
            mTitleText = a.getString(R.styleable.CollapsibleHeaderLayout_titleText);
            mSubTitleText = a.getString(R.styleable.CollapsibleHeaderLayout_subTitleText);
            mHeaderImageId = a.getResourceId(R.styleable.CollapsibleHeaderLayout_headerImage,R.drawable.header_image_placeholder);
            mToolbarColor = a.getColor(R.styleable.CollapsibleHeaderLayout_toolbarBackgroundColor, getThemeColor(COLOR_PRIMARY));
            mTitleTextColor = a.getColor(R.styleable.CollapsibleHeaderLayout_titleTextColor, getThemeColor(COLOR_TITLE_TEXT));
            mSubTitleTextColor = a.getColor(R.styleable.CollapsibleHeaderLayout_subTitleTextColor, getThemeColor(COLOR_SUBTITLE_TEXT));
        } finally {
            a.recycle();
        }

        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.collapsible_header_layout, this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //called after onFinishInflate

        mHeaderHeight = h;
        mTitleBackgroundOriginalHeight = mTitleBackgroundView.getMeasuredHeight();

        adjustExpandedToolbarLayout(getExpandedToolbarHeight());
        getLayoutCoordinator().onHeaderSizedSet(mHeaderHeight);
    }



    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        getLayoutCoordinator();

        //find & set views
        mHeaderImageContainer = (FrameLayout) this.findViewById(R.id.header_image_container);
        mHeaderImage = (ImageView) this.findViewById(R.id.header_image);
        mHeaderBox = (RelativeLayout) this.findViewById(R.id.header_box);
        mTitleBackgroundView = this.findViewById(R.id.title_background);
//        mTitleBox = (LinearLayout) this.findViewById(R.id.title_box);
        mTitleTextView = (TextView) this.findViewById(R.id.title_text);
        mSubTitleTextView = (TextView) this.findViewById(R.id.sub_title_text);
        mSlidingTabLayout = (SlidingTabLayout) this.findViewById(R.id.sliding_tabs);

        mHeaderImage.setBackgroundResource(mHeaderImageId);
        mTitleBackgroundView.setBackgroundColor(mToolbarColor);

        mTitleTextView.setText(mTitleText);
        mTitleTextView.setTextColor(mTitleTextColor);

        mSubTitleTextView.setText(mSubTitleText);
        mSubTitleTextView.setTextColor(mSubTitleTextColor);

        mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        Resources res = getResources();
        mSlidingTabLayout.setSelectedIndicatorColors(res.getColor(R.color.colorAccent));
        mSlidingTabLayout.setDistributeEvenly(true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getLayoutCoordinator().attachTitleTextView(mTitleTextView);
        getLayoutCoordinator().attachSubTitleTextView(mSubTitleTextView);
        getLayoutCoordinator().attachTabs(mSlidingTabLayout);
        getLayoutCoordinator().attachToolbarBackground(mTitleBackgroundView);
        getLayoutCoordinator().attachHeaderImage(mHeaderImageContainer, mHeaderImage);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void adjustExpandedToolbarLayout(int height) {
        Log.d(TAG, "adjustExpandedToolbarLayout = " + height);
        RelativeLayout.LayoutParams params = (LayoutParams) mTitleBackgroundView.getLayoutParams();
        params.height = height;

    }

    public void setViewPager(ViewPager viewPager) {
        mSlidingTabLayout.setViewPager(viewPager);
        mSlidingTabLayout.setLayoutCoordinator(getLayoutCoordinator());
    }


    public void setDistributeTabsEvenly(boolean evenlyDistribute) {
        mSlidingTabLayout.setDistributeEvenly(evenlyDistribute);
        //TODO: need to request layout?
    }

    public void setSlidingTabLayoutContentDescriptions(int[] titleStringArray) {
        for (int i = 0; i < titleStringArray.length; i++) {
            mSlidingTabLayout.setContentDescription(i, getResources().getString(titleStringArray[i]));
        }
    }

    private int getExpandedToolbarHeight() {
        return mSlidingTabLayout.getMeasuredHeight() + mTitleBackgroundOriginalHeight;
    }

    public boolean isAutoHideHeader() {
        return mAutoHideHeader;
    }

    public void setAutoHideHeader(boolean autoHide) {
        mAutoHideHeader = autoHide;

        // don't think invalidating and redrawing will be necessary to change the autoHide functionality
        //invalidate();
        //requestLayout();
    }

    public String getTitleText() {
        return mTitleText;
    }

    public void setTitleText(String titleText) {
        mTitleText = titleText;
        invalidate();
        requestLayout();
    }

    public String getSubTitleText() {
        return mSubTitleText;
    }

    public void setSubTitleText(String subTitleText) {
        mSubTitleText = subTitleText;
        invalidate();
        requestLayout();
    }




    private int getThemeColor(String colorString) {
        TypedValue typedValue = new TypedValue();
        int colorId = R.attr.colorPrimary;
        switch (colorString) {
            case COLOR_ACCENT :
                colorId = R.attr.colorAccent;
                break;
            case COLOR_PRIMARY :
                colorId = R.attr.colorPrimary;
                break;
            case COLOR_TITLE_TEXT :
                colorId = R.attr.titleTextColor;
                break;
            case COLOR_SUBTITLE_TEXT :
                colorId = R.attr.colorAccent;
                break;
        }

        TypedArray a = getContext().obtainStyledAttributes(typedValue.data,new int[] { colorId });
        int color = a.getColor(0,0);
        a.recycle();

        return color;
    }

    /** Implement necessary methods for translations */

    @Override
    public LayoutCoordinator getLayoutCoordinator() {
        if (mLayoutCoordinator == null) {
            mLayoutCoordinator = new LayoutCoordinator(getContext());
        }

        return mLayoutCoordinator;
    }

    @Override
    public int getHeaderHeight() {
        return mHeaderHeight;
    }

    @Override
    public void registerScrollableView(View view, String uniqueIdentifier) {
        // Each view or fragment in a viewpager needs a unique id.
        // Why? - Say you have four tabs, but only 3 are held by the viewpager at any given time
        // if you page to tab 3, the viewpager is then holding tabs 2, 3 and 4 and destroys 1.
        // When you page back to tab 2, tab one's fragment
        // is then re-created and again calls registerScrollableView. To avoid
        // endlessly adding fragments, we need a way to check if they have been previously
        // been registered, and instead, update them.

        getLayoutCoordinator().registerScrollableView(view, uniqueIdentifier);
    }

    public CoordinatedView attachCoordinatedView(View view) {
        return new CoordinatedView(view, this);
    }


}
