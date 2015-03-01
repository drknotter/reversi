package com.drknotter.reversi.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by plunkett on 2/27/15.
 */
public class ReversiAnimatorFactory
{
    public enum AnimationType
    {
        ZOOM_IN,
        ZOOM_OUT,
        RESET,
    }

    private static final int ZOOM_DURATION = 200;

    public static AnimatorSet newInstance(AnimationType type, View view)
    {
        AnimatorSet output = null;

        switch( type )
        {
            case ZOOM_IN:
                output = zoomIn(view);
                break;

            case ZOOM_OUT:
                output = zoomOut(view);
                break;

            case RESET:
                output = resetView(view);
                break;
        }

        return output;
    }

    private static AnimatorSet zoomIn(View view)
    {
        ObjectAnimator xZoomIn = ObjectAnimator.ofFloat(view, "scaleX", 1f);
        xZoomIn.setDuration(ZOOM_DURATION);
        xZoomIn.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator yZoomIn = ObjectAnimator.ofFloat(view, "scaleY", 1f);
        yZoomIn.setDuration(ZOOM_DURATION);
        yZoomIn.setInterpolator(new DecelerateInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(xZoomIn).with(yZoomIn);

        return animatorSet;
    }

    private static AnimatorSet zoomOut(View view)
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

    private static AnimatorSet resetView(View view)
    {
        ObjectAnimator resetXZoom = ObjectAnimator.ofFloat(view, "scaleX", 1f);
        resetXZoom.setDuration(ZOOM_DURATION);
        resetXZoom.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator resetYZoom = ObjectAnimator.ofFloat(view, "scaleY", 1f);
        resetYZoom.setDuration(ZOOM_DURATION);
        resetYZoom.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator resetAlpha = ObjectAnimator.ofFloat(view, "alpha", 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(resetXZoom).with(resetYZoom);
        animatorSet.play(resetXZoom).with(resetAlpha);

        return animatorSet;
    }
}
