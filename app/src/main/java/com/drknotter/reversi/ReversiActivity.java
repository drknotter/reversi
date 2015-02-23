package com.drknotter.reversi;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.drknotter.reversi.controller.ReversiBoardController;
import com.drknotter.reversi.model.ReversiBoardModel;
import com.drknotter.reversi.model.ReversiPiece;
import com.drknotter.reversi.view.ReversiBoardView;


public class ReversiActivity extends Activity
{
    private static final String TAG = ReversiActivity.class.getSimpleName();

    ReversiBoardModel boardModel;
    ReversiBoardView boardView;
    ReversiBoardController boardController;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reversi);

        boardModel = new ReversiBoardModel();
        boardView = (ReversiBoardView) findViewById(R.id.board_view);
        boardController = new ReversiBoardController(boardModel, boardView);

        boardView.initialize(boardController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reversi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
