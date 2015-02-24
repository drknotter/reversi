package com.drknotter.reversi.model;

import android.util.SparseArray;

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
        putPieceAt(3,3,ReversiPiece.LIGHT);
        putPieceAt(4,3,ReversiPiece.DARK);
        putPieceAt(3,4,ReversiPiece.DARK);
        putPieceAt(4,4,ReversiPiece.LIGHT);
    }

    public void putPieceAt(int x, int y, ReversiPiece piece)
    {
        putPieceAt(size * y + x, piece);
    }

    public void putPieceAt(int index, ReversiPiece piece)
    {
        state.put(index, piece);
    }

    public ReversiPiece getPieceAt(int x, int y)
    {
        return getPieceAt(size * y + x);
    }

    public ReversiPiece getPieceAt(int index)
    {
        return state.get(index);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getX(int index)
    {
        return index % size;
    }

    public int getY(int index)
    {
        return index / size;
    }

    public int getIndex(int x, int y)
    {
        return size * y + x;
    }

    public boolean isInBounds(int index)
    {
        return index >= 0 && index < size * size;
    }

    public boolean isInBounds(int x, int y)
    {
        return isInBounds(getIndex(x,y));
    }
}
