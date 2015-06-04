package com.hunterrobbert.layoutcoordinatorexample;

import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by hunter on 6/2/15.
 */
public class CoordinatedView {

    //view starting locations
    public static final String START_FAB_STRADDLE_EXPANDED_TOOLBAR_TOP_RIGHT = "start_fab_straddle_expanded_toolbar_top_right";

    //view ending locations
    public static final String END_DEFAULT = "end_fab_offscreen";
    public static final String END_COLLAPSED_TOOLBAR_POSITION_1 = "end_collapsed_toolbar_position_1";

    //view behaviors
    public static final String CIRCULAR_HIDE_REVEAL = "circular_reveal_hide";


    //view behavior 'when reaches' options
    public static final float QUATER_POINT = .25f;
    public static final float MID_POINT = .5f;
    public static final float THREE_QUATER_POINT = .75f;




    public View mView;
    private CollapsibleHeaderLayout mLayout;

    //start layout adjustments
    private static final String SET_WITH_DESC  = "set_with_desc";
    private static final String SET_WITH_BUILDER = "set_with_builder";
    private static final String SET_WITH_PARAMS = "set_with_parmas";

    private String mStartSetMethod;
    private Position mStartPosition;
    private RelativeLayout.LayoutParams mStartParams;
    private boolean startPointSet = false;

    private String mEndSetMethod;
    public Position mEndPosition;
    private RelativeLayout.LayoutParams mEndParams;
    private boolean endPointSet = false;


    public String mBehavior;
    public float mWhenReaches;


    public boolean behaviorActive = false;


    public CoordinatedView(View view, CollapsibleHeaderLayout layout) {
        mView = view;
        mLayout = layout;
    }

    public CoordinatedView startingAt(String startLocationDesc) {
        mStartSetMethod = SET_WITH_DESC;
        mStartPosition = new Position(mLayout.getLayoutCoordinator(),mView)
                .buildFromPreset(startLocationDesc);
        return this;
    }

    public CoordinatedView startingAt(Position position) {
        mStartSetMethod = SET_WITH_BUILDER;
        mStartPosition = position;
        return this;
    }

//    public CoordinatedView startingAt(RelativeLayout.LayoutParams layoutParams) {
//        mStartSetMethod = SET_WITH_PARAMS;
//        mStartParams = layoutParams;
//        mView.setLayoutParams(layoutParams);
//        startPointSet = true;
//        return this;
//    }

    public CoordinatedView endingAt(String endLocationDesc) {
        mEndSetMethod = SET_WITH_DESC;
        mEndPosition = new Position(mLayout.getLayoutCoordinator(),mView)
                .buildEndFromPreset(endLocationDesc, mStartPosition);
        return this;
    }

    public CoordinatedView withBehavior(String behavior, float whenReaches) {
        mBehavior = behavior;
        mWhenReaches = whenReaches;
        return this;
    }

    public CoordinatedView attach() {
        mLayout.getLayoutCoordinator().attachCoordinatedView(this);
        adjustStartParams();
        adjustEndPoints();
        return this;
    }

    public void adjustStartParams() {
        if (startPointSet) {
            return;
        }

        if (mStartSetMethod.equals(SET_WITH_DESC) || mStartSetMethod.equals(SET_WITH_BUILDER)) {
            if (mStartPosition != null) {
                if (!mStartPosition.isBuiltAndSet()) {
                    mStartPosition.attemptBuild();
                } else {
                    startPointSet = true;
                }
            }
        }
    }

    public void adjustEndPoints() {
        if (endPointSet) {
            return;
        }

        if (mEndSetMethod.equals(SET_WITH_DESC) || mEndSetMethod.equals(SET_WITH_BUILDER)) {
            if (mEndPosition != null) {
                if (!mEndPosition.isEndBuildAndSet()) {
                    mEndPosition.attemptEndBuild();
                } else {
                    endPointSet = true;
                }
            }
        }
    }

}
