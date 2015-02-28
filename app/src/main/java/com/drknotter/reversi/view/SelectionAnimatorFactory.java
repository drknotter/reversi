package com.drknotter.reversi.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by plunkett on 2/27/15.
 */
public class SelectionAnimatorFactory
{
    public enum AnimationType
    {
        MAKE_SELECTION,
        END_SELECTION,
        CONFIRM_SELECTION,
    }

    private static final int ZOOM_DURATION = 300;
    private static final int LOOP_DURATION = 500;

    static AnimatorSet newInstance(AnimationType type, View view)
    {
        AnimatorSet output = null;

        switch( type )
        {
            case MAKE_SELECTION:
                output = makeSelectionAnimatorSet(view);
                break;
            case END_SELECTION:
                output = endSelection(view);
                break;
            case CONFIRM_SELECTION:
                output = confirmSelection(view);
                break;
        }

        return output;
    }

    private static AnimatorSet makeSelectionAnimatorSet(View view)
    {
        ObjectAnimator xZoomIn = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1.1f);
        xZoomIn.setDuration(ZOOM_DURATION);
        ObjectAnimator yZoomIn = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1.1f);
        yZoomIn.setDuration(ZOOM_DURATION);

        ObjectAnimator xPulse = ObjectAnimator.ofFloat(view, "scaleX", 1.1f, 0.7f);
        xPulse.setDuration(LOOP_DURATION);
        xPulse.setRepeatCount(ValueAnimator.INFINITE);
        xPulse.setRepeatMode(ValueAnimator.REVERSE);
        ObjectAnimator yPulse = ObjectAnimator.ofFloat(view, "scaleY", 1.1f, 0.7f);
        yPulse.setDuration(LOOP_DURATION);
        yPulse.setRepeatCount(ValueAnimator.INFINITE);
        yPulse.setRepeatMode(ValueAnimator.REVERSE);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(xZoomIn).with(yZoomIn);
        animatorSet.play(xZoomIn).before(xPulse);
        animatorSet.play(xPulse).with(yPulse);

        return animatorSet;
    }

    private static AnimatorSet endSelection(View view)
    {
        ObjectAnimator xZoomOut = ObjectAnimator.ofFloat(view, "scaleX", 0f);
        xZoomOut.setDuration(ZOOM_DURATION);
        xZoomOut.setInterpolator(new AccelerateInterpolator());
        ObjectAnimator yZoomOut = ObjectAnimator.ofFloat(view, "scaleY", 0f);
        yZoomOut.setDuration(ZOOM_DURATION);
        yZoomOut.setInterpolator(new AccelerateInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(xZoomOut).with(yZoomOut);

        return animatorSet;
    }

    private static AnimatorSet confirmSelection(View view)
    {
        ObjectAnimator xResetZoom = ObjectAnimator.ofFloat(view, "scaleX", 1f);
        xResetZoom.setDuration(ZOOM_DURATION);
        xResetZoom.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator yResetZoom = ObjectAnimator.ofFloat(view, "scaleY", 1f);
        yResetZoom.setDuration(ZOOM_DURATION);
        yResetZoom.setInterpolator(new DecelerateInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(xResetZoom).with(yResetZoom);

        return animatorSet;
    }
}
