package com.drknotter.reversi.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.drknotter.reversi.R;
import com.drknotter.reversi.model.ReversiPiece;

import java.util.ArrayList;
import java.util.List;

public class ReversiBoardView extends LinearLayout
{
    private static final String TAG = ReversiBoardView.class.getSimpleName();

    private List<ImageView> positions;
    private int boardSize;
    private OnPositionTouchedListener listener;

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

    public void initialize(int boardSize)
    {
        this.boardSize = boardSize;

        positions = new ArrayList<ImageView>(boardSize * boardSize);
        for( int i = 0; i < boardSize * boardSize; i ++ )
        {
            final int x = i % boardSize;
            final int y = i / boardSize;

            int resId = getContext().getResources().getIdentifier("position_" + x + "_" + y, "id", getContext().getPackageName());
            final ImageView thePosition = (ImageView) findViewById(resId);

            positions.add(y * boardSize + x, thePosition);
            thePosition.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if( listener != null )
                    {
                        listener.onPositionViewTouched(x, y);
                    }
                }
            });
        }
    }

    public void setListener(OnPositionTouchedListener listener)
    {
        this.listener = listener;
    }

    public ImageView getPositionViewAt(int x, int y)
    {
        if (x >= 0 && x < boardSize && y >= 0 && y < boardSize)
        {
            return positions.get(y * boardSize + x);
        }

        return null;
    }

    public void setPieceAt(int x, int y, ReversiPiece piece)
    {
        if (x >= 0 && x < boardSize && y >= 0 && y < boardSize)
        {
            ImageView positionView = getPositionViewAt(x, y);
            if (piece != null)
            {
                positionView.setImageResource(piece.getImageResource());
            }
            else
            {
                positionView.setImageResource(R.drawable.empty_square);
            }
        }
    }

    public interface OnPositionTouchedListener
    {
        public void onPositionViewTouched(int x, int y);
    }
}
