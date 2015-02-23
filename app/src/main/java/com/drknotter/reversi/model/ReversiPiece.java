package com.drknotter.reversi.model;

import com.drknotter.reversi.R;

/**
 * Created by plunkett on 2/22/15.
 */
public enum ReversiPiece
{
    DARK,
    LIGHT;

    public int getImageResource()
    {
        int resId = 0;
        switch (this)
        {
            case DARK:
                resId = R.drawable.dark_piece;
                break;
            case LIGHT:
                resId = R.drawable.light_piece;
                break;
        }
        return resId;
    }
}
