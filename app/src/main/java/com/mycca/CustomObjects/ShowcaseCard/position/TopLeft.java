package com.mycca.CustomObjects.ShowcaseCard.position;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.widget.ScrollView;

import com.mycca.CustomObjects.ShowcaseCard.util.NavigationBarUtils;


public class TopLeft implements ShowCasePosition {

    @Override
    public PointF getPosition(Activity activity) {
        return new PointF(
                NavigationBarUtils.navigationBarMarginForLeftOrientation(activity),
                0F
        );
    }

    @Nullable
    @Override
    public Point getScrollPosition(@Nullable ScrollView scrollView) {
        return null;
    }
}
