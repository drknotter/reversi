package com.drknotter.reversi.model;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by plunkett on 2/22/15.
 */
public class ReversiBoardModel
{
    private int size = 8;
    private SparseArray<ReversiPiece> state;

    public ReversiBoardModel()
    {
        state = new SparseArray<ReversiPiece>(size * size);
        putPieceAt(3, 3, ReversiPiece.LIGHT);
        putPieceAt(4, 3, ReversiPiece.DARK);
        putPieceAt(3, 4, ReversiPiece.DARK);
        putPieceAt(4, 4, ReversiPiece.LIGHT);
    }

    public void putPieceAt(int x, int y, ReversiPiece piece)
    {
        if (isInBounds(x, y))
        {
            putPieceAt(size * y + x, piece);
        }
    }

    private void putPieceAt(int index, ReversiPiece piece)
    {
        state.put(index, piece);
    }

    public ReversiPiece getPieceAt(int x, int y)
    {
        if (isInBounds(x, y))
        {
            return getPieceAt(size * y + x);
        }
        else
        {
            return null;
        }
    }

    private ReversiPiece getPieceAt(int index)
    {
        return state.get(index);
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    private int getX(int index)
    {
        return index % size;
    }

    private int getY(int index)
    {
        return index / size;
    }

    private boolean isInBounds(int index)
    {
        return isInBounds(getX(index), getY(index));
    }

    public boolean isInBounds(int x, int y)
    {
        return x >= 0 && x < size && y >= 0 && y < size;
    }

    public boolean isValidMove(int x, int y, ReversiPiece playerPiece)
    {
        boolean valid = false;

        if (getPieceAt(x, y) == null)
        {
            for (int dx = -1; dx <= 1; dx++)
            {
                for (int dy = -1; dy <= 1; dy++)
                {
                    if (dx == 0 && dy == 0)
                    {
                        continue;
                    }

                    if (isInBounds(x + dx, y + dy)
                            && getPieceAt(x + dx, y + dy) == playerPiece.getOpponent())
                    {
                        int r = 2;
                        while (isInBounds(x + r * dx, y + r * dy)
                                && getPieceAt(x + r * dx, y + r * dy) == playerPiece.getOpponent())
                        {
                            r++;
                        }

                        if (isInBounds(x + r * dx, y + r * dy)
                                && getPieceAt(x + r * dx, y + r * dy) == playerPiece)
                        {
                            valid = true;
                        }
                    }
                }
            }
        }

        return valid;
    }

    public List<List<ReversiPositionModel>> makeMove(int x, int y, ReversiPiece piece)
    {
        List<List<ReversiPositionModel>> allFlips = new ArrayList<List<ReversiPositionModel>>();

        if (isInBounds(x, y))
        {
            boolean didFlip = false;
            for (int dx = -1; dx <= 1; dx++)
            {
                for (int dy = -1; dy <= 1; dy++)
                {
                    if (dx == 0 && dy == 0)
                    {
                        continue;
                    }

                    List<ReversiPositionModel> flippedPositions = new ArrayList<ReversiPositionModel>();
                    didFlip = flipInDirection(x + dx, y + dy, dx, dy, piece, flippedPositions) || didFlip;

                    if( flippedPositions.size() > 0 )
                    {
                        allFlips.add(flippedPositions);
                    }
                }
            }

            if (didFlip)
            {
                state.put(y * size + x, piece);
            }
        }

        return allFlips;
    }

    public void undoMove(ReversiMoveModel theMove)
    {
        List<List<ReversiPositionModel>> allFlips = theMove.getFlipPositions();
        ReversiPositionModel thePosition = theMove.getMovePosition();
        ReversiPiece opponent = theMove.getPlayerPiece().getOpponent();

        int index = thePosition.getY() * size + thePosition.getX();
        state.remove(index);

        for( List<ReversiPositionModel> flipList : allFlips )
        {
            for( ReversiPositionModel position : flipList )
            {
                index = position.getY() * size + position.getX();
                state.put(index, opponent);
            }
        }
    }

    private boolean flipInDirection(int x, int y, int dx, int dy, ReversiPiece piece, List<ReversiPositionModel> flippedPositions)
    {
        if (isInBounds(x, y))
        {
            if (getPieceAt(x, y) == piece.getOpponent())
            {
                if( flipInDirection(x + dx, y + dy, dx, dy, piece, flippedPositions) )
                {
                    putPieceAt(x, y, piece);
                    flippedPositions.add(0, new ReversiPositionModel(x,y));
                    return true;
                }
            }
            else if (getPieceAt(x, y) == piece)
            {
                return true;
            }
        }

        return false;
    }

    public boolean canPlayerMove(ReversiPiece player)
    {
        boolean canMove = false;

        for (int x = 0; x < getSize() && !canMove; x++)
        {
            for (int y = 0; y < getSize() && !canMove; y++)
            {
                if (isValidMove(x, y, player))
                {
                    canMove = true;
                }
            }
        }

        return canMove;
    }

    public Set<ReversiPiece> currentLeaders()
    {
        HashMap<ReversiPiece, Integer> counts = new HashMap<ReversiPiece, Integer>();
        for( ReversiPiece piece : ReversiPiece.values() )
        {
            counts.put(piece, 0);
        }

        for( int i=0; i<state.size(); i++ )
        {
            counts.put(state.valueAt(i), counts.get(state.valueAt(i)) + 1);
        }

        int max = -1;
        HashSet<ReversiPiece> leaders = new HashSet<ReversiPiece>();
        for( ReversiPiece piece : counts.keySet() )
        {
            int count = counts.get(piece);
            if( count > max )
            {
                leaders = new HashSet<ReversiPiece>();
                leaders.add(piece);
                max = count;
            }
            else if( count == max )
            {
                leaders.add(piece);
            }
        }

        return leaders;
    }
}
