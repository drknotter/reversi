package com.drknotter.reversi.controller;

import com.drknotter.reversi.model.ReversiBoardModel;
import com.drknotter.reversi.model.ReversiPiece;
import com.drknotter.reversi.view.ReversiBoardView;

/**
 * Created by plunkett on 2/22/15.
 */
public class ReversiBoardController implements ReversiBoardView.OnPositionTouchedListener
{
    private ReversiBoardModel model;
    private ReversiBoardView view;
    private ReversiPiece activePlayer = ReversiPiece.DARK;
    private int currentSelectedIndex = -1;

    public ReversiBoardController(ReversiBoardModel model, ReversiBoardView view)
    {
        this.model = model;
        this.view = view;
    }

    public ReversiBoardModel getModel()
    {
        return model;
    }

    public ReversiBoardView getView()
    {
        return view;
    }

    public int getBoardSize()
    {
        return model.getSize();
    }

    @Override
    public void onPositionViewTouched(int index)
    {
        if( isValidMove(index) )
        {

        }
        else
        {
            view.selectNone();
        }
    }

    public boolean isValidMove(int index)
    {
        boolean valid = false;
        int x = model.getX(index);
        int y = model.getY(index);

        for( int dx = -1; dx <=1; dx++ )
        {
            for( int dy = -1; dy <= 1; dy++ )
            {
                if( dx == 0 && dy == 0 )
                {
                    continue;
                }

                if( model.isInBounds(x+dx,y+dy)
                        && model.getPieceAt(x+dx,y+dy) == activePlayer.getOpponent() )
                {
                    int r = 2;
                    while( model.isInBounds(x+r*dx, y+r*dy)
                            && model.getPieceAt(x+r*dx, y+r*dy) == activePlayer.getOpponent() )
                    {
                        r++;
                    }
                }
            }
        }


        return valid;
    }

    public void updateView()
    {
        for (int i = 0; i < getBoardSize() * getBoardSize(); i++)
        {
            ReversiPiece piece = model.getPieceAt(i);
            int resId = 0;
            if( piece != null )
            {
                resId = piece.getImageResource();
            }
            view.getPositionViewAt(i).setImageResource(resId);
        }
    }
}
