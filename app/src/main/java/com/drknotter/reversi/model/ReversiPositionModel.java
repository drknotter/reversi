package com.drknotter.reversi.model;

/**
 * Created by plunkett on 2/28/15.
 */
public class ReversiPositionModel
{
    private int x;
    private int y;

    public ReversiPositionModel(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }
}
