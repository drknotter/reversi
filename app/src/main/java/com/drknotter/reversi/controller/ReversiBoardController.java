package com.drknotter.reversi.controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.drknotter.reversi.model.ReversiBoardModel;
import com.drknotter.reversi.model.ReversiMoveModel;
import com.drknotter.reversi.model.ReversiPiece;
import com.drknotter.reversi.model.ReversiPositionModel;
import com.drknotter.reversi.view.ReversiAnimatorFactory;
import com.drknotter.reversi.view.ReversiBoardView;

import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Created by plunkett on 2/22/15.
 */
public class ReversiBoardController extends Handler implements ReversiBoardView.OnPositionTouchedListener
{
    private final String TAG = ReversiBoardController.class.getSimpleName();

    private Handler uiHandler;

    private ReversiBoardModel model;
    private ReversiBoardView view;

    private ImageView currentPlayerIcon;
    private Button undoButton;

    private ReversiPiece activePlayer = ReversiPiece.DARK;
    private Stack<ReversiMoveModel> moveHistory = new Stack<ReversiMoveModel>();

    public ReversiBoardController(Looper looper, Handler uiHandler,
                                  ReversiBoardModel model, ReversiBoardView view,
                                  ImageView currentPlayerIcon, Button undoButton)
    {
        super(looper);

        this.uiHandler = uiHandler;

        this.model = model;
        this.view = view;

        this.currentPlayerIcon = currentPlayerIcon;
        this.undoButton = undoButton;

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

            case ReversiMessages.UNDO:
                undoTurn();
                break;

            case ReversiMessages.NEXT_TURN:
                nextTurn();
                break;

            case ReversiMessages.PREV_TURN:
                prevTurn();
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
        undoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sendEmptyMessage(ReversiMessages.UNDO);
            }
        });
    }

    private void handleTouch(ReversiPositionModel touchPosition)
    {
        int x = touchPosition.getX();
        int y = touchPosition.getY();
        if( model.isValidMove(x, y, activePlayer) )
        {
            List<List<ReversiPositionModel>> allFlips = model.makeMove(x, y, activePlayer);
            final ReversiMoveModel theMove = new ReversiMoveModel(activePlayer, touchPosition, allFlips);
            moveHistory.push(theMove);
            uiHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    doFlipAnimation(theMove);
                }
            });
        }
    }

    private void doFlipAnimation(ReversiMoveModel theMove)
    {
        view.setListener(null);

        ImageView moveView = view.getPositionViewAt(
                theMove.getMovePosition().getX(),
                theMove.getMovePosition().getY());
        List<List<ReversiPositionModel>> allFlips = theMove.getFlipPositions();
        final ReversiPiece thePiece = theMove.getPlayerPiece();

        moveView.setScaleX(0f);
        moveView.setScaleY(0f);
        moveView.setImageResource(thePiece.getImageResource());
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
                        neighborView.setImageResource(thePiece.getImageResource());
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
                view.setListener(ReversiBoardController.this);
                sendEmptyMessage(ReversiMessages.NEXT_TURN);
            }
        });
        allFlipsAnimatorSet.start();
    }

    private void undoFlipAnimation(ReversiMoveModel theMove)
    {
        view.setListener(null);

        ImageView moveView = view.getPositionViewAt(
                theMove.getMovePosition().getX(),
                theMove.getMovePosition().getY());
        List<List<ReversiPositionModel>> allFlips = theMove.getFlipPositions();
        final ReversiPiece thePiece = theMove.getPlayerPiece();

        moveView.setScaleX(1f);
        moveView.setScaleY(1f);
        moveView.setImageResource(thePiece.getImageResource());
        Animator zoomOutMove = ReversiAnimatorFactory.newInstance(ReversiAnimatorFactory.AnimationType.ZOOM_OUT, moveView);

        AnimatorSet allFlipsAnimatorSet = new AnimatorSet();
        allFlipsAnimatorSet.play(zoomOutMove);
        for( List<ReversiPositionModel> flipList : allFlips )
        {
            Animator lastZoomIn = zoomOutMove;
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
                        neighborView.setImageResource(thePiece.getOpponent().getImageResource());
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
                view.setListener(ReversiBoardController.this);
                sendEmptyMessage(ReversiMessages.PREV_TURN);
            }
        });
        allFlipsAnimatorSet.start();
    }

    private void undoTurn()
    {
        if (moveHistory.size() > 0)
        {
            final ReversiMoveModel theMove = moveHistory.pop();
            model.undoMove(theMove);
            uiHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    undoFlipAnimation(theMove);
                }
            });
        }
        else
        {
            prevTurn();
        }
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

    private void prevTurn()
    {
        if( moveHistory.size() > 0 )
        {
            activePlayer = moveHistory.peek().getPlayerPiece();
        }
        else
        {
            activePlayer = ReversiPiece.DARK;
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
