package com.drknotter.reversi.controller;

import android.nfc.Tag;
import android.util.Log;

import com.drknotter.reversi.model.ReversiBoardModel;
import com.drknotter.reversi.model.ReversiPiece;
import com.drknotter.reversi.view.ReversiBoardView;

import java.util.Set;

/**
 * Created by plunkett on 2/22/15.
 */
public class ReversiBoardController implements ReversiBoardView.OnPositionTouchedListener
{
    private final String TAG = ReversiBoardController.class.getSimpleName();

    private ReversiBoardModel model;
    private ReversiBoardView view;
    private ReversiPiece activePlayer = ReversiPiece.DARK;

    public ReversiBoardController(ReversiBoardModel model, ReversiBoardView view)
    {
        this.model = model;
        this.view = view;
    }

    @Override
    public void onPositionViewTouched(int x, int y)
    {

        if( model.isValidMove(x, y, activePlayer) )
        {
            // The player finalized their move.
            if( view.isSelected(x, y) )
            {
                model.makeMove(x, y, activePlayer);
                view.confirmSelection(x, y);
                nextTurn();
            }
            else // The player is making an initial move.
            {
                view.select(x, y, activePlayer);
            }
        }
        else
        {
            view.selectNone();
        }

        updateView();
    }

    private void nextTurn()
    {
        activePlayer = activePlayer.getOpponent();
        if( !model.canPlayerMove(activePlayer) )
        {
            activePlayer = activePlayer.getOpponent();
            if( !model.canPlayerMove(activePlayer) )
            {
                gameOver();
            }
        }
    }

    private void gameOver()
    {
        Set<ReversiPiece> winners = model.currentLeaders();
        Log.v(TAG, "And the winners are...");
        for( ReversiPiece piece : winners )
        {
            Log.v(TAG, piece.name() + "!");
        }
    }

    public void updateView()
    {
        for (int x = 0; x < model.getSize(); x++)
        {
            for (int y = 0; y < model.getSize(); y++)
            {
                ReversiPiece piece = model.getPieceAt(x, y);
                view.setPieceAt(x, y, piece);
            }
        }
    }


}
