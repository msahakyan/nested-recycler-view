package com.android.msahakyan.nestedrecycler.common;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ScrollView;

/**
 * @author msahakyan
 */
public class ViewAnimator {

    private static final int ANIMATION_TIME = 1000; // milliseconds
    private int initialHeight;

    public void expand(final ViewGroup v) {
        v.measure(LayoutParams.MATCH_PARENT, initialHeight);
        final int targetHeight = initialHeight;
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                ((ScrollView) v.getParent().getParent()).scrollTo(0, 5000);
                v.getLayoutParams().height = interpolatedTime == 1
                    ? targetHeight
                    : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration(ANIMATION_TIME);
        v.startAnimation(a);
    }

    public void collapse(final ViewGroup v) {
        initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration(ANIMATION_TIME);
        v.startAnimation(a);
    }
}
