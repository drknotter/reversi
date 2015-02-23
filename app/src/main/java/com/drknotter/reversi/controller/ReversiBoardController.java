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
    public void onPositionTouched(int position)
    {
        ReversiPiece piece = model.getPieceAt(position);
        ReversiPiece newPiece = null;
        if (piece == null)
        {
            newPiece = ReversiPiece.DARK;
        }
        else if (piece == ReversiPiece.DARK)
        {
            newPiece = ReversiPiece.LIGHT;
        }
        else if (piece == ReversiPiece.LIGHT)
        {
            newPiece = null;
        }

        model.putPieceAt(position, newPiece);
        updateView();
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
            view.getPositionAt(i).setImageResource(resId);
        }
    }
}
