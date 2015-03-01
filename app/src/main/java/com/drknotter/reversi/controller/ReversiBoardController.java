package com.drknotter.reversi.controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.drknotter.reversi.model.ReversiBoardModel;
import com.drknotter.reversi.model.ReversiPiece;
import com.drknotter.reversi.model.ReversiPositionModel;
import com.drknotter.reversi.view.ReversiAnimatorFactory;
import com.drknotter.reversi.view.ReversiBoardView;

import java.util.List;
import java.util.Set;

/**
 * Created by plunkett on 2/22/15.
 */
public class ReversiBoardController extends Handler implements ReversiBoardView.OnPositionTouchedListener
{
    private final String TAG = ReversiBoardController.class.getSimpleName();

    private ReversiBoardModel model;
    private ReversiBoardView view;
    private ImageView currentPlayerIcon;
    private Handler uiHandler;

    private ReversiPiece activePlayer = ReversiPiece.DARK;

    public ReversiBoardController(Looper looper, Handler uiHandler, ReversiBoardModel model, ReversiBoardView view, ImageView currentPlayerIcon)
    {
        super(looper);

        this.uiHandler = uiHandler;
        this.model = model;
        this.view = view;
        this.currentPlayerIcon = currentPlayerIcon;
        this.sendEmptyMessage(ReversiMessages.INITIALIZE);
    }

    @Override
    public void handleMessage(Message msg)
    {
        switch ( msg.what )
        {
            case ReversiMessages.INITIALIZE:
                start();
                break;

            case ReversiMessages.TOUCH:
                if( msg.obj instanceof  ReversiPositionModel )
                {
                    handleTouch((ReversiPositionModel) msg.obj);
                }
                break;

            case ReversiMessages.NEXT_TURN:
                nextTurn();
                break;

            case ReversiMessages.GAME_OVER:
                gameOver();
                break;
        }
    }

    private void start()
    {
        view.setListener(this);
        view.initialize(model.getSize());
        updateView();
    }

    private void handleTouch(ReversiPositionModel touchPosition)
    {
        int x = touchPosition.getX();
        int y = touchPosition.getY();
        if( model.isValidMove(x, y, activePlayer) )
        {
            final List<List<ReversiPositionModel>> allFlips = model.makeMove(x, y, activePlayer);
            final ImageView moveView = view.getPositionViewAt(x,y);
            uiHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    doFlip(moveView, allFlips);
                }
            });

        }
    }

    private void doFlip(ImageView moveView, List<List<ReversiPositionModel>> allFlips)
    {
        moveView.setScaleX(0f);
        moveView.setScaleY(0f);
        moveView.setImageResource(activePlayer.getImageResource());
        Animator zoomInMove = ReversiAnimatorFactory.newInstance(ReversiAnimatorFactory.AnimationType.ZOOM_IN, moveView);

        AnimatorSet allFlipsAnimatorSet = new AnimatorSet();
        allFlipsAnimatorSet.play(zoomInMove);
        for( List<ReversiPositionModel> flipList : allFlips )
        {
            Animator lastZoomIn = zoomInMove;
            for( ReversiPositionModel flipPosition : flipList )
            {
                final ImageView neighborView = view.getPositionViewAt(flipPosition.getX(), flipPosition.getY());
                Animator thisZoomOut = ReversiAnimatorFactory.newInstance(
                        ReversiAnimatorFactory.AnimationType.ZOOM_OUT,
                        neighborView);
                allFlipsAnimatorSet.play(lastZoomIn).with(thisZoomOut);

                lastZoomIn = ReversiAnimatorFactory.newInstance(
                        ReversiAnimatorFactory.AnimationType.ZOOM_IN,
                        neighborView);
                allFlipsAnimatorSet.play(lastZoomIn).after(thisZoomOut);

                thisZoomOut.addListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        super.onAnimationEnd(animation);
                        neighborView.setImageResource(activePlayer.getImageResource());
                    }
                });
            }
        }
        allFlipsAnimatorSet.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                sendEmptyMessage(ReversiMessages.NEXT_TURN);
            }
        });
        allFlipsAnimatorSet.start();
    }

    private void nextTurn()
    {
        activePlayer = activePlayer.getOpponent();
        if( !model.canPlayerMove(activePlayer) )
        {
            activePlayer = activePlayer.getOpponent();
            if( !model.canPlayerMove(activePlayer) )
            {
                this.sendEmptyMessage(ReversiMessages.GAME_OVER);
                return;
            }
        }
        updateView();
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

    @Override
    public void onPositionViewTouched(int x, int y)
    {
        this.sendMessage(Message.obtain(this, ReversiMessages.TOUCH, new ReversiPositionModel(x, y)));
    }

    private void setCurrentPlayerIcon()
    {
        currentPlayerIcon.setImageResource(activePlayer.getImageResource());
    }

    public void updateView()
    {
        uiHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                for (int x = 0; x < model.getSize(); x++)
                {
                    for (int y = 0; y < model.getSize(); y++)
                    {
                        ReversiPiece piece = model.getPieceAt(x, y);
                        view.setPieceAt(x, y, piece);
                    }
                }

                setCurrentPlayerIcon();
            }
        });
    }
}
