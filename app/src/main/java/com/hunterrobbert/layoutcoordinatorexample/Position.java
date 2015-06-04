package com.hunterrobbert.layoutcoordinatorexample;

import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by hunter on 6/4/15.
 */
public class Position {

    private static final String TAG = Position.class.getSimpleName();

    private LayoutCoordinator mLayoutCoordinator;
    private View mView;
    private String mPosDesc;

    private RelativeLayout.LayoutParams mParams;

    private boolean builtAndSet = false;


    private int[] mMargins;


    private int mMarginTop;
    private boolean marginTopCalled = false;
    private String[] mMarginTopDesc;
    private boolean marginTopSet = false;
    public static final String LOC_EXTENDED_TOOLBAR_TOP = "loc_extended_toolbar_top";
    public static final String LOC_EXTENDED_TOOLBAR_CENTERLINE = "loc_extended_toolbar_centerline";
    public static final String LOC_EXTENDED_TOOLBAR_BOTTOM = "loc_extended_toolbar_bottom";

    //align to
    public static final String DEFAULT = "default";
    public static final String VIEW_TOP = "view_top";
    public static final String VIEW_CENTERLINE_Y = "view_centerline_y";
    public static final String VIEW_BOTTOM = "view_bottom";

    public static final String VIEW_RIGHT = "view_right";
    public static final String VIEW_CENTERLINE_X = "view_centerline_x";
    public static final String VIEW_LEFT = "view_left";


    private int mMarginRight;
    private boolean marginRightCalled = false;
    private String[] mMarginRightDesc;
    private boolean marginRightSet = false;

    public static final String LOC_KEYLINE_1 = "loc_keyline_1"; //16dp
    public static final String LOC_KEYLINE_2 = "loc_keyline_2"; //24dp
    public static final String LOC_KEYLINE_3 = "loc_keyline_3"; //32dp

    private int mMarginLeft;
    private boolean marginLeftCalled = false;
    private boolean marginLeftSet = false;

    private int mMarginBotton;
    private boolean marginBottomCalled = false;
    private boolean marginBottomSet = false;

    private int[] mRules;

    public static final int RULES_ALIGN_PARENT_RIGHT = RelativeLayout.ALIGN_PARENT_RIGHT;
    public static final int RULES_ALIGN_PARENT_LEFT = RelativeLayout.ALIGN_PARENT_LEFT;


    //end point
    private Position mStartPos;
    private String mEndPosDesc;
    private boolean endBuildAndSet = false;


    private int mOriginalY;
    private int mOriginalX;

    public int mMaxXTrans;
    public int mMaxYTrans;


    public Position(LayoutCoordinator layoutCoordinator, View view) {
        mLayoutCoordinator = layoutCoordinator;
        mView = view;
    }

    public Position setMarginTop(int marginTop) {
        mMarginTop = marginTop;
        marginTopCalled = true;
        marginTopSet = true;
        return this;
    }

    public Position setMarginTop(String location, String alignTo) {
        mMarginTopDesc = new String[] {location, alignTo};
        marginTopCalled = true;
        setMarginTop();
        return this;
    }

    public Position setMarginRight(int marginRight) {
        mMarginRight = marginRight;
        marginRightCalled = true;
        marginRightSet = true;
        return this;
    }

    public Position setMarginRight(String location) {
        mMarginRightDesc = new String[] {location, DEFAULT};
        marginRightCalled = true;
        return this;
    }

    public Position setMarginRight(String location, String alignTo) {
        mMarginRightDesc = new String[] {location, alignTo};
        marginRightCalled = true;
        return this;
    }

    public Position setRules(int... rules) {
        int rulesLen = rules.length;
        mRules = new int[rulesLen];
        for (int i = 0; i < rulesLen; i++) {
            mRules[i] = rules[i];
        }
        return this;
    }

    public Position buildStart() {
        mParams = (RelativeLayout.LayoutParams) mView.getLayoutParams();
        //set margin array
            //make sure dynamic margins are not 0
        if (marginLeftCalled && !marginLeftSet) {
            //set margin left
        } else if (!marginLeftCalled) {
            marginLeftSet = true;
            mMarginLeft = 0;
            marginLeftCalled = true;
        }

        if (marginTopCalled && !marginTopSet) {
            //need to set top margin
            setMarginTop();
        } else if (!marginTopCalled) {
            marginTopSet = true;
            mMarginTop = 0;
            marginTopCalled = true;
        }

        if (marginRightCalled && !marginRightSet) {
            //need to set margin right
            setMarginRight();
        } else if (!marginRightCalled) {
            marginRightSet = true;
            mMarginRight = 0;
            marginRightCalled = true;
        }

        if (marginBottomCalled && !marginBottomSet) {
            //set bottom margin
        } else if (!marginBottomCalled) {
            marginBottomSet = true;
            mMarginBotton = 0;
            marginBottomCalled = true;
        }



        if (marginLeftSet && marginTopSet && marginRightSet && marginBottomSet) {
            Log.d(TAG, "margins being set!! marginright: " + mMarginRight);
            mParams = (RelativeLayout.LayoutParams) mView.getLayoutParams();
            mParams.setMargins(mMarginLeft, mMarginTop, mMarginRight,mMarginBotton);
            for (int i = 0; i < mRules.length; i ++) {
                mParams.addRule(mRules[i]);
            }
            mView.setLayoutParams(mParams);
            builtAndSet = true;
        }

        return this;
    }

    public Position buildFromPreset(String desc) {
        mPosDesc = desc;
        switch (desc) {
            case CoordinatedView.START_FAB_STRADDLE_EXPANDED_TOOLBAR_TOP_RIGHT :
                mParams = (RelativeLayout.LayoutParams) mView.getLayoutParams();
                int toolbartop = getExtToolbarTop();
                int offset = getViewCenterOffsetY();
                if (toolbartop != 0 && offset != 0) {
                    mMarginTop = toolbartop - offset;
                }
                mMarginRight = getDimPx(R.dimen.default_keyline_1);
                if (mMarginTop != 0 && mMarginRight != 0) {
                    mParams.setMargins(0,mMarginTop, mMarginRight, 0);
                    mParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    mView.setLayoutParams(mParams);
                    builtAndSet = true;
                }
                break;
        }
        return this;
    }

    public Position buildEndFromPreset(String desc, Position start) {
        mStartPos = start;
        mEndPosDesc = desc;
        Log.d(TAG, "buildEndFromPreset being called.");
        if (start.isBuiltAndSet()) {
            //final positions for start locked.
            switch (desc) {
                case CoordinatedView.END_DEFAULT :
                    mOriginalX = (int) start.mView.getX();
                    mOriginalY = (int) start.mView.getY();

                    mMaxXTrans = getDefaultXMax();
                    mMaxYTrans = getDefaultYMax();

                    if (mOriginalX != 0 && mOriginalY != 0) {
                        Log.d(TAG, "POSITION. ends set. maxX: " + mMaxXTrans + ", maxY: " + mMaxYTrans);
                        endBuildAndSet = true;
                    }
                    break;
                case CoordinatedView.END_COLLAPSED_TOOLBAR_POSITION_1 :
                    //Get start Y
                        //calculate max Y
                    mOriginalY = (int) start.mView.getY();
                    mOriginalX = (int) start.mView.getX();


                    mMaxYTrans = mOriginalY - getTopOfCollapsedToolbarCenteredView(mView);
                    mMaxXTrans = getDisplayWidth() - mOriginalX - getDimPx(R.dimen.default_keyline_1) - getDimPx(R.dimen.default_keyline_2);

                    if (mOriginalX != 0 && mOriginalY != 0) {
                        Log.d(TAG, "POSITION. ends set. maxX: " + mMaxXTrans + ", maxY: " + mMaxYTrans);
                        endBuildAndSet = true;
                    }

            }
        }

        return this;
    }

    public void attemptBuild() {
        if (mPosDesc != null) {
            buildFromPreset(mPosDesc);
        } else {
            buildStart();
        }
    }

    public void attemptEndBuild() {
        if (mEndPosDesc != null) {
            buildEndFromPreset(mEndPosDesc, mStartPos);
        }
    }



    public boolean isBuiltAndSet() {
        return builtAndSet;
    }

    public boolean isEndBuildAndSet() {
        return endBuildAndSet;
    }

    private int setMarginRight() {

        boolean rightSet = false;
        boolean alignSet = false;

        int right = 0;
        int align = 0;

        switch (mMarginRightDesc[0]) {
            case LOC_KEYLINE_1 :
                right = getDimPx(R.dimen.default_keyline_1);
                if (right != 0) {
                    rightSet = true;
                }
                break;
            case LOC_KEYLINE_2 :
                right = getDimPx(R.dimen.default_keyline_2);
                if (right != 0) {
                    rightSet = true;
                }
                break;
            case LOC_KEYLINE_3 :
                right = getDimPx(R.dimen.default_keyline_3);
                if (right != 0) {
                    rightSet = true;
                }
                break;
        }

        switch (mMarginRightDesc[1]) {
            case DEFAULT :
                align = 0;
                alignSet = true;
                break;
            case VIEW_RIGHT :
                align = 0;
                alignSet = true;
                break;
            case VIEW_CENTERLINE_X :
                align = getViewCenterOffsetX();
                if (align != 0) {
                    alignSet = true;
                }
                break;
        }

        if (rightSet && alignSet) {
            mMarginRight = right - align;
            marginRightSet = true;
        }

        return right - align;
    }

    private int setMarginTop() {

        boolean topSet = false;
        boolean alignSet = false;
        int top = 0;
        switch (mMarginTopDesc[0]) {
            case LOC_EXTENDED_TOOLBAR_TOP :
                top = getExtToolbarTop();
                if (top != 0) {
                    topSet = true;
                }
                break;
            case LOC_EXTENDED_TOOLBAR_CENTERLINE :
                int toolTop = getExtToolbarTop();
                int height = getToolbarHeight();
                int tabs = getDimPx(R.dimen.small_tab_size);
                if (height != 0 && tabs != 0 && toolTop != 0) {
                    top = toolTop + ((height - tabs) / 2);
                    topSet = true;
                }
        }

        int alignOffset = 0;
        switch (mMarginTopDesc[1]) {
            case VIEW_TOP :
                alignSet = true;
                alignOffset = 0;
                break;
            case VIEW_CENTERLINE_Y :
                alignOffset = getViewCenterOffsetY();
                if (alignOffset != 0) {
                    alignSet = true;
                }
                break;
        }


        if (topSet && alignSet) {
            marginTopSet = true;
            mMarginTop = top + alignOffset;
        }

        return top + alignOffset;
    }

    private int getDimPx(int res) {
        if (mLayoutCoordinator == null) {
            return 0;
        }
        return mLayoutCoordinator.getDimPx(res);
    }

    private int getDisplayWidth() {
        if (mLayoutCoordinator == null) {
            return 0;
        }

        return mLayoutCoordinator.getDisplayWidth();
    }

    private int getDefaultYMax() {
        if (mLayoutCoordinator == null) {
            return mOriginalY;
        }

        return -mLayoutCoordinator.mCompleteAllTranslations;
    }

    private int getDefaultXMax() {
       return 0;
    }


    public int getExtToolbarTop() {
        if (mLayoutCoordinator != null) {
            return mLayoutCoordinator.getToolbarBackgroundOriginalY();
        }
        return 0;
    }

    public int getToolbarHeight() {
        if (mLayoutCoordinator != null) {
            int tabsHeight = getDimPx(R.dimen.small_tab_size);
            int toolbarHeight = mLayoutCoordinator.getExtToolbarHeight();
            if (tabsHeight != 0 && toolbarHeight != 0) {
                return toolbarHeight - tabsHeight;
            }
        }

        return 0;
    }

    public int getTopOfCollapsedToolbarCenteredView(View view) {
        int viewheight = view.getHeight();
        int actionbarheight = getDimPx(R.dimen.action_bar_height);
        if (actionbarheight != 0 && viewheight != 0) {
            return (actionbarheight - viewheight) / 2;
        }

        return 0;
    }

    private int getViewCenterOffsetX() {
        return mView.getWidth() / 2;
    }

    private int getViewCenterOffsetY() {
        return mView.getHeight() / 2;
    }



}
