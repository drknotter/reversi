package com.drknotter.reversi.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.drknotter.reversi.R;
import com.drknotter.reversi.controller.ReversiBoardController;

import java.util.ArrayList;
import java.util.List;

public class ReversiBoardView extends LinearLayout
{
    private static final String TAG = ReversiBoardView.class.getSimpleName();

    private ReversiBoardController controller;
    private List<ImageView> positions;

    private ObjectAnimator selectionPulse;
    private ImageView selectedPositionView;

    public ReversiBoardView(Context context)
    {
        super(context);
    }

    public ReversiBoardView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ReversiBoardView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void initialize(ReversiBoardController controller)
    {
        this.controller = controller;
        Context context = getContext();

        this.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        this.setOrientation(LinearLayout.VERTICAL);
        positions = new ArrayList<ImageView>(controller.getBoardSize() * controller.getBoardSize());
        initializeRows(context);
        controller.updateView();
    }

    private void initializeRows(Context context)
    {
        addDivider(context);

        for (int i = 0; i < controller.getModel().getSize(); i++)
        {
            ReversiRow row = new ReversiRow(context, i);
            addView(row);
            addDivider(context);
        }
    }

    private void addDivider(Context context)
    {
        View divider = new View(context);
        divider.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.board_divider_size)));
        divider.setBackgroundColor(getResources().getColor(R.color.board_divider_color));
        addView(divider);
    }

    public ImageView getPositionViewAt(int index)
    {
        return positions.get(index);
    }

    public void select(int index)
    {
        if( selectionPulse != null )
        {
            selectionPulse.cancel();
            selectedPositionView.setAlpha(1f);
        }
        selectedPositionView = getPositionViewAt(index);
        selectionPulse = ObjectAnimator.ofFloat(selectedPositionView, "alpha", 1f, 0f, 1f);
        selectionPulse.setDuration(750);
        selectionPulse.setRepeatMode(ValueAnimator.RESTART);
        selectionPulse.setRepeatCount(ValueAnimator.INFINITE);
        selectionPulse.start();
    }

    public void selectNone()
    {
        if( selectionPulse != null )
        {
            selectionPulse.cancel();
            selectedPositionView.setAlpha(1f);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (width > height)
        {
            width = height;
        }
        else
        {
            height = width;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    private class ReversiRow extends LinearLayout
    {
        private int mRow;

        public ReversiRow(Context context, int row)
        {
            super(context);
            this.mRow = row;
            initialize(context);
        }

        private void initialize(Context context)
        {
            this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
            this.setOrientation(LinearLayout.HORIZONTAL);
            this.addDivider(context);

            for (int i = 0; i < controller.getBoardSize(); i++)
            {
                ImageView thePosition = new ImageView(context);
                thePosition.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));

                this.addView(thePosition);
                this.addDivider(context);

                final int position = mRow * controller.getBoardSize() + i;
                positions.add(position, thePosition);
                thePosition.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        controller.onPositionViewTouched(position);
                    }
                });
            }
        }

        private void addDivider(Context context)
        {
            View divider = new View(context);
            divider.setLayoutParams(new LayoutParams((int) getResources().getDimension(R.dimen.board_divider_size), LayoutParams.MATCH_PARENT));
            divider.setBackgroundColor(getResources().getColor(R.color.board_divider_color));
            addView(divider);
        }
    }

    public interface OnPositionTouchedListener
    {
        public void onPositionViewTouched(int position);
    }
}
