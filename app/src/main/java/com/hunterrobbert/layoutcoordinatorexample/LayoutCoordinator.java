package com.hunterrobbert.layoutcoordinatorexample;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hunter on 5/20/15.
 */
public class LayoutCoordinator {

    private static final String TAG = LayoutCoordinator.class.getSimpleName();

    private Context mContext;

    class RecyclerViewObj {

        RecyclerView recyclerView;
        String uniqueIdentifier;
        int scrollPosition;

        public RecyclerViewObj(RecyclerView view, String uniqueId) {
            recyclerView = view;
            uniqueIdentifier = uniqueId;
            initScrollPosition();
        }

        public void initScrollPosition() {

            if (mHeaderHeight > scrollAccumulator + getToolbarOffset()) {
                scrollPosition = scrollAccumulator;
            } else {
                //the toolbar is fully compacted
                LinearLayoutManager manager = getRecyclerViewLinearLayoutManager(recyclerView);
                if (manager != null) {
                    int firstVisibleChild = manager.findFirstVisibleItemPosition();
                    if (firstVisibleChild == 0) {
                        //scroll to toolbar offset
                        scrollPosition = mHeaderHeight - getToolbarOffset();
                    }
                }
            }
        }
    }


    private ArrayList<RecyclerViewObj> mRecyclerViewObjArray;
    private int mCurrentViewPage = 0;

    private TextView mTitleText;
    private TextView mSubTitleText;
    private SlidingTabLayout mTabs;
    private View mToolbarBackground;
    private FrameLayout mHeaderImageContainer;
    private ImageView mHeaderImage;

    private ArrayList<CoordinatedView> mCoordinatedViews;

    private boolean mHeaderSet = false;
    private int mHeaderHeight;
    private int mRegularToolbarHeight;
    private int mBeginParallaxTranslations = 200;
    public int mCompleteAllTranslations;


    private int mTitleTextViewOriginalY = 0;
    private double mMinimumTitleTextScaleRatio = 1;

    private int mSubTitleTextViewOriginalY = 0;

    private int mTabsOriginalY = 0;
    private int mTabsTop;

    private int mToolbarBackgroundOriginalY = 0;

    private int scrollAccumulator = 0;

    //what the custom view needs to implement in order to function properly
    public interface CoordinatorInterface {
        LayoutCoordinator getLayoutCoordinator();
        int getHeaderHeight();
        void registerScrollableView(View view, String uniqueIdentifier);
    }

    private OnLayoutCoordinatorScrollWatcher mLayoutScrollWatcher;
    // used to watch scroll for attaching custom views to the layout and defining their custom behavior
    public interface OnLayoutCoordinatorScrollWatcher {
        void onScrolled(int scrollPosition);
    }

    public void setOnLayoutCoordinatorScrollWatcher(OnLayoutCoordinatorScrollWatcher callbacks) {
        mLayoutScrollWatcher = callbacks;
    }

    public void releaseOnLayoutCoordinatorScrollWatcher() {
        mLayoutScrollWatcher = null;
    }

    public LayoutCoordinator(Context context) {
        mContext = context;
        mRecyclerViewObjArray = new ArrayList<>();
        mRegularToolbarHeight = getDimPx(R.dimen.action_bar_height);
    }


    public void registerScrollableView(View view, String uniqueIdentifier) {
        //store view to be watched/scrolled
        if (view instanceof RecyclerView) {
            for (int i = 0; i < mRecyclerViewObjArray.size(); i++) {
                if (mRecyclerViewObjArray.get(i).uniqueIdentifier == uniqueIdentifier) {
                    // The view is being re-added. update it
                    RecyclerViewObj obj = mRecyclerViewObjArray.get(i);
                    obj.recyclerView = (RecyclerView) view;
                    obj.recyclerView.setOnScrollListener(mRecyclerViewScrollWatcher());
                    return;
                }
            }

            RecyclerViewObj recyclerViewObj = new RecyclerViewObj((RecyclerView) view, uniqueIdentifier);
            mRecyclerViewObjArray.add(recyclerViewObj);
            ((RecyclerView)view).setOnScrollListener(mRecyclerViewScrollWatcher());
            scrollRecyclerViewToPosition(recyclerViewObj, scrollAccumulator);
        }
    }

    public void attachTitleTextView(View view) {
        mTitleText = ((TextView)view);
        mMinimumTitleTextScaleRatio = (double) getDimPx(R.dimen.action_bar_text_size) / (double) getDimPx(R.dimen.title_text_size);
    }

    public void attachSubTitleTextView(View view) {
        mSubTitleText = ((TextView)view);
    }

    public void attachTabs(View view) {
        mTabs = ((SlidingTabLayout)view);
    }

    public void attachToolbarBackground(View view) {
        mToolbarBackground = view;
    }

    public void attachHeaderImage(FrameLayout imageContainer, ImageView imageView) {
        mHeaderImageContainer = imageContainer;
        mHeaderImage = imageView;
    }



    public void attachCoordinatedView(CoordinatedView coordinatedView) {
        if (mCoordinatedViews == null) {
            mCoordinatedViews = new ArrayList<>();
        }

        mCoordinatedViews.add(coordinatedView);
    }


    public void onHeaderSizedSet(int headerHeight) {
        mHeaderHeight = headerHeight;
        mHeaderSet = true;
        for (int i = 0; i < mRecyclerViewObjArray.size(); i++) {
            RecyclerView view = mRecyclerViewObjArray.get(i).recyclerView;

            RecyclerView.Adapter adapter = view.getAdapter();
            if (adapter instanceof HeaderAutoFooterRecyclerAdapter) {
                ((HeaderAutoFooterRecyclerAdapter)adapter).updateHeaderHeight(headerHeight);
            }

        }


    }

    private void updateCoordinatedViewsLayoutConstants() {
        for (int c = 0; c < mCoordinatedViews.size(); c++) {
            mCoordinatedViews.get(c).adjustStartParams();
            mCoordinatedViews.get(c).adjustEndPoints();
        }
    }

    public void onViewPagerPageSelected(int position) {
        reassignScrollAccumulator(position);
    }

    private LinearLayoutManager getRecyclerViewLinearLayoutManager(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            return ((LinearLayoutManager)recyclerView.getLayoutManager());
        }

        return null;
    }

    private RecyclerView.OnScrollListener mRecyclerViewScrollWatcher() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollAccumulator = scrollAccumulator + dy;

                if (mLayoutScrollWatcher != null) {
                    mLayoutScrollWatcher.onScrolled(scrollAccumulator);
                }

                updateCoordinatedViewsLayoutConstants();

                //translate attached views
                translateTitleText(scrollAccumulator);
                translateSubTitleText(scrollAccumulator);
                translateTabs(scrollAccumulator);
                translateToolbarBackground(scrollAccumulator);
                translateHeaderImage(scrollAccumulator);

                translateCoordinatedViews(scrollAccumulator);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0) {
                    //the scrolling has stopped. update all views in mRecyclerViewsArray
                    updateAllRecyclerViewScroll(scrollAccumulator);
                }
            }
        };
    }

    private void updateAllRecyclerViewScroll(int scrollPosition) {

        int toolbarOffset = getToolbarOffset();

        for (int i = 0; i < mRecyclerViewObjArray.size(); i++) {
            RecyclerViewObj recyclerViewObj = mRecyclerViewObjArray.get(i);
            //If mHeaderView isn't fully compacted:
            if (mHeaderHeight > scrollPosition + toolbarOffset) {
                setRecyclerObjectsScrollHold(recyclerViewObj, scrollPosition);
                scrollRecyclerViewToPosition(recyclerViewObj, scrollPosition);
            } else {
                //the toolbar is fully compacted
                LinearLayoutManager manager = getRecyclerViewLinearLayoutManager(recyclerViewObj.recyclerView);
                if (manager != null) {
                    int firstVisibleChild = manager.findFirstVisibleItemPosition();
                    if (firstVisibleChild == 0) {
                        //scroll to toolbar offset
                        Log.d(TAG, "mHeaderHeight: " + mHeaderHeight + ", scrollPos: " + scrollPosition + ", toolbarOff: " + toolbarOffset);
                        if (mCurrentViewPage == i) {
                            //were talking about the current fragment/recyclerview
                            setRecyclerObjectsScrollHold(recyclerViewObj, scrollPosition);
                        } else {
                            if (mHeaderHeight - recyclerViewObj.scrollPosition > toolbarOffset) {
                                setRecyclerObjectsScrollHold(recyclerViewObj, mHeaderHeight - toolbarOffset);
                                scrollRecyclerViewToPosition(recyclerViewObj, mHeaderHeight - toolbarOffset);
                            }
                        }



                    }
                }

            }
        }
    }

    private int getToolbarOffset() {
        return mRegularToolbarHeight + getDimPx(R.dimen.small_tab_size);
    }


    private void setRecyclerObjectsScrollHold(RecyclerViewObj obj, int scroll) {
        obj.scrollPosition = scroll;
    }

    private void scrollRecyclerViewToPosition(RecyclerViewObj obj, int scrollPosition) {
        if (obj.recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            ((LinearLayoutManager)obj.recyclerView.getLayoutManager()).scrollToPositionWithOffset(0, -scrollPosition);
        }
    }

    private void reassignScrollAccumulator(int position) {
        mRecyclerViewObjArray.get(mCurrentViewPage).scrollPosition = scrollAccumulator;
        updateAllRecyclerViewScroll(scrollAccumulator);
        scrollAccumulator = mRecyclerViewObjArray.get(position).scrollPosition;
        mCurrentViewPage = position;

    }

    /** Do calculations and translate the header views  */

    private void translateTitleText(int scrollPosition) {
        if (mTitleText != null) {
            //set the original position of the title textview
            if (mTitleTextViewOriginalY == 0) {
                mTitleTextViewOriginalY = (int) mTitleText.getY();
                if (mCompleteAllTranslations == 0 ) {
                    return;
                }
                return;
            }


            double titleScale = getScaleBetweenRange(scrollPosition, mBeginParallaxTranslations,-mCompleteAllTranslations,1,mMinimumTitleTextScaleRatio);
            mTitleText.setPivotX(0);
            mTitleText.setPivotY(mTitleText.getHeight() / 2);
            mTitleText.setScaleY((float) titleScale);
            mTitleText.setScaleX((float) titleScale);

            double titleParallaxTrans = getScaleBetweenRange(scrollPosition,0,-mCompleteAllTranslations,0, mCompleteAllTranslations);
            int collapseBy = (-mCompleteAllTranslations - mTitleTextViewOriginalY) + ((mRegularToolbarHeight - mTitleText.getHeight()) / 2);
            double factor = getScaleBetweenRange(scrollPosition,mBeginParallaxTranslations,-mCompleteAllTranslations,0,collapseBy);
            mTitleText.setTranslationY((float) (titleParallaxTrans + factor));
        }
    }

    private void translateSubTitleText(int scrollPosition) {
        if (mSubTitleText != null) {
            //set the original position of the subtitle textview
            if (mSubTitleTextViewOriginalY == 0) {
                mSubTitleTextViewOriginalY = (int) mSubTitleText.getY();
                return;
            }

            double subTitleParallaxTrans = getScaleBetweenRange(scrollPosition,0,-mCompleteAllTranslations,0, mCompleteAllTranslations);
            int collapseBy = (int) (((-mCompleteAllTranslations - mTitleTextViewOriginalY) + ((mRegularToolbarHeight - mTitleText.getHeight()) / 2)) - (mTitleText.getHeight() * (1- mMinimumTitleTextScaleRatio)));
            double factor = getScaleBetweenRange(scrollPosition,mBeginParallaxTranslations,-mCompleteAllTranslations,0,collapseBy);
            mSubTitleText.setTranslationY((float) (subTitleParallaxTrans + factor));
        }
    }

    private void translateTabs(int scrollPosition) {
        if (mTabs != null) {
            //set the original position of the tabs
            if (mTabsOriginalY == 0) {
                mTabsOriginalY = (int) mTabs.getY();
                mTabsTop = -mTabsOriginalY + mRegularToolbarHeight;
                mCompleteAllTranslations = mTabsTop;
                return;
            }

            mTabs.setTranslationY(Math.max(-scrollPosition, mCompleteAllTranslations));
        }
    }

    private void translateToolbarBackground(int scrollPosition) {
        if (mToolbarBackground != null) {
            if (mToolbarBackgroundOriginalY == 0) {
                mToolbarBackgroundOriginalY = (int) mToolbarBackground.getY();
                return;
            }
        }
        mToolbarBackground.setTranslationY(Math.max(-scrollPosition, mCompleteAllTranslations));
    }

    public int getToolbarBackgroundOriginalY() {
        return mToolbarBackgroundOriginalY;
    }

    public int getExtToolbarHeight() {
        if (mToolbarBackground != null) {
            return mToolbarBackground.getHeight();
        }
        return 0;
    }


    private void translateHeaderImage(int scrollPosition) {
        if (mHeaderImage == null || mHeaderImageContainer == null) {
            return;
        }
        //The image container is used to clip the imageview
        int top = Math.max(-scrollPosition,mCompleteAllTranslations);
        mHeaderImageContainer.setTranslationY(top);
        mHeaderImage.setTranslationY(top * -0.7f);
    }


    private void translateCoordinatedViews(int scrollPosition) {
        for (int i = 0; i < mCoordinatedViews.size(); i++) {
            CoordinatedView coordinatedView = mCoordinatedViews.get(i);
            int endPointX = coordinatedView.mEndPosition.mMaxXTrans;
            int endPointY = coordinatedView.mEndPosition.mMaxYTrans;

            double xParallax = getScaleBetweenRange(scrollPosition,0,-mCompleteAllTranslations,0,endPointX);
            double yParallax = getScaleBetweenRange(scrollPosition,0,-mCompleteAllTranslations,0,endPointY);
            coordinatedView.mView.setTranslationX((float) -xParallax);
            coordinatedView.mView.setTranslationY((float) -yParallax);



            // behavior instructions
            if (coordinatedView.mBehavior != null) {
                switch (coordinatedView.mBehavior) {
                    case CoordinatedView.CIRCULAR_HIDE_REVEAL :
                        if (coordinatedView.mWhenReaches != 0) {
                            int keyPoint = (int) (endPointY * coordinatedView.mWhenReaches);

                            if (keyPoint > 0 && scrollPosition > keyPoint) {
                                //circular hide
                                if (!coordinatedView.behaviorActive) {
                                    animateOut(coordinatedView);
                                    coordinatedView.behaviorActive = true;
                                }
                            } else {
                                //circular reveal
                                if (coordinatedView.behaviorActive) {
                                    animateIn(coordinatedView);
                                    coordinatedView.behaviorActive = false;
                                }
                            }
                        }
                        break;
                }
            }
        }
    }


    private void animateOut(final CoordinatedView coordinatedView) {
        coordinatedView.mView.animate().cancel();
        coordinatedView.mView.animate().scaleX(0).scaleY(0).setDuration(200).start();
    }

    private void animateIn(final CoordinatedView coordinatedView) {
        coordinatedView.mView.animate().cancel();
        coordinatedView.mView.animate().scaleX(1).scaleY(1).setDuration(200).start();
    }

    public int getDimPx(int resourceId) {
        if (mContext != null) {
            return mContext.getResources().getDimensionPixelSize(resourceId);
        }

        return 0;
    }

    public int getDisplayWidth() {
        if (mContext != null) {
            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            return size.x;
        }

        return 0;
    }

    //given a value, if that value is between a defined range, will return the linear equivalent of another range
    //for example. given the range [100-200] as the input range and [5-1] as the output range, given a value of 100, getScaleBetweenRange will
    //return 5.
    //if given a value outside of [100-200] such as 95, getScaleBetweenRange will default to the min value, which in this case would be 5.
    /**
     *
     * @param value input value to return linear output equivalent
     * @param inputMin bottom end of input range. If value is less than inputMin, value will default to inputMin
     * @param inputMax top end of input range.  If value is more than inputMax, value will default to inputMax
     * @param outputMin bottom end of output range to be returned. for value of inputMin, outputMin will be returned
     * @param outputMax top end of output range to be returned. for value of inputMax, outputMax will be returned
     * @return linear equivalent of value between outputMin and outputMax. default return for values less than inputMin is outputMin.
     *         default return for values more than inputMax is outputMax
     */
    private double getScaleBetweenRange(float value, float inputMin, float inputMax, double outputMin, double outputMax) {
        if (value < inputMin) {
            return outputMin;
        } else if (value > inputMax) {
            return outputMax;
        } else {
            return (outputMin * (1 - ((value-inputMin)/(inputMax-inputMin)))) + (outputMax * ((value-inputMin)/(inputMax-inputMin)));
        }
    }
}
