package com.drknotter.reversi.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.drknotter.reversi.R;
import com.drknotter.reversi.model.ReversiPiece;

import java.util.ArrayList;
import java.util.List;

public class ReversiBoardView extends LinearLayout implements Animator.AnimatorListener
{
    private static final String TAG = ReversiBoardView.class.getSimpleName();

    private OnPositionTouchedListener listener;
    private List<ImageView> positions;
    private int boardSize;

    private int currentSelectionIndex = -1;
    private AnimatorSet selectionPulse;
    private AnimatorSet endSelection;

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

    public void initialize(OnPositionTouchedListener listener, int boardSize)
    {
        this.listener = listener;
        this.boardSize = boardSize;
        Context context = getContext();

        this.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        this.setOrientation(LinearLayout.VERTICAL);
        positions = new ArrayList<ImageView>(boardSize * boardSize);
        initializeRows(context);
    }

    private void initializeRows(Context context)
    {
        addDivider(context);

        for (int i = 0; i < boardSize; i++)
        {
            ReversiRowView rowView = new ReversiRowView(context, i);
            addView(rowView);
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

    public ImageView getPositionViewAt(int x, int y)
    {
        if( x >= 0 && x < boardSize && y >= 0 && y < boardSize )
        {
            return positions.get(y * boardSize + x);
        }

        return null;
    }

    public boolean isSelected(int x, int y)
    {
        return x >= 0 && x < boardSize && y >= 0 && y < boardSize
                && currentSelectionIndex == y * boardSize + x;
    }

    public void select(int x, int y, ReversiPiece piece)
    {
        if( x >= 0 && x < boardSize && y >= 0 && y < boardSize )
        {
            if (selectionPulse != null)
            {
                selectionPulse.cancel();
            }

            if (currentSelectionIndex >= 0 && currentSelectionIndex < positions.size())
            {
                positions.get(currentSelectionIndex).setAlpha(1f);
            }
            currentSelectionIndex = y * boardSize + x;

            setPieceAt(x, y, piece);
            selectionPulse = SelectionAnimatorFactory.newInstance(SelectionAnimatorFactory.AnimationType.MAKE_SELECTION,
                    getPositionViewAt(x, y));
            selectionPulse.start();
        }
    }

    public void confirmSelection(int x, int y)
    {
        if( x >= 0 && x < boardSize && y >= 0 && y < boardSize )
        {
            int index = y * boardSize + x;
            if( index == currentSelectionIndex )
            {
                if (selectionPulse != null)
                {
                    selectionPulse.cancel();
                }
                SelectionAnimatorFactory.newInstance(
                        SelectionAnimatorFactory.AnimationType.CONFIRM_SELECTION, positions.get(index))
                        .start();
                currentSelectionIndex = -1;
            }
        }
    }

    public void selectNone()
    {
        if( selectionPulse != null )
        {
            selectionPulse.cancel();
        }

        if( currentSelectionIndex >= 0 && currentSelectionIndex < positions.size() )
        {
            View view = positions.get(currentSelectionIndex);
            endSelection = SelectionAnimatorFactory.newInstance(SelectionAnimatorFactory.AnimationType.END_SELECTION, view);
            endSelection.addListener(this);
            endSelection.start();
        }
    }

    public void setPieceAt(int x, int y, ReversiPiece piece)
    {
        if( x >= 0 && x < boardSize && y >= 0 && y < boardSize )
        {
            if( piece != null )
            {
                getPositionViewAt(x, y).setImageResource(piece.getImageResource());
            }
            else if( currentSelectionIndex != y * boardSize + x )
            {
                getPositionViewAt(x, y).setImageResource(0);
            }
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

    @Override
    public void onAnimationStart(Animator animator)
    {
    }

    @Override
    public void onAnimationEnd(Animator animator)
    {
        if( animator == endSelection )
        {
            currentSelectionIndex = -1;
        }
    }

    @Override
    public void onAnimationCancel(Animator animator)
    {
    }

    @Override
    public void onAnimationRepeat(Animator animator)
    {
    }

    private class ReversiRowView extends LinearLayout
    {
        public ReversiRowView(Context context, int row)
        {
            super(context);
            initialize(context, row);
        }

        private void initialize(Context context, int row)
        {
            this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
            this.setOrientation(LinearLayout.HORIZONTAL);
            this.addDivider(context);

            for (int i = 0; i < boardSize; i++)
            {
                ImageView thePosition = new ImageView(context);
                thePosition.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));

                this.addView(thePosition);
                this.addDivider(context);

                final int x = i;
                final int y = row;
                positions.add(y * boardSize + x, thePosition);
                thePosition.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        listener.onPositionViewTouched(x, y);
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
        public void onPositionViewTouched(int x, int y);
    }
}
