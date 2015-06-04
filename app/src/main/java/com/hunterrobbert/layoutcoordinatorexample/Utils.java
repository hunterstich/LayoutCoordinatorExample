package com.hunterrobbert.layoutcoordinatorexample;

import android.content.Context;

/**
 * Created by hunter on 6/2/15.
 */
public class Utils {

    private Utils() {}

    public static int getDimPx(Context context, int resourceId) {
        return context.getResources().getDimensionPixelSize(resourceId);
    }
}
