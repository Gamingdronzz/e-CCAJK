package com.mycca.CustomObjects.CustomShowCaseCard.position;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.widget.ScrollView;

import com.mycca.CustomObjects.CustomShowCaseCard.util.ActivityUtils;
import com.mycca.CustomObjects.CustomShowCaseCard.util.NavigationBarUtils;


public class BottomRight implements ShowCasePosition {

    @Override
    public PointF getPosition(Activity activity) {
        float width = (float) activity.getWindow().getDecorView().getWidth();
        float height = (float) activity.getWindow().getDecorView().getHeight();

        switch (ActivityUtils.getOrientation(activity)) {
            case Configuration.ORIENTATION_LANDSCAPE:
                return new PointF(
                        width - NavigationBarUtils.navigationBarMarginForRightOrientation(activity),
                        height
                );
            default:
                return new PointF(
                        width,
                        height - (float) NavigationBarUtils.navigationBarHeight(activity)
                );
        }
    }

    @Nullable
    @Override
    public Point getScrollPosition(@Nullable ScrollView scrollView) {
        return null;
    }
}
