package com.drknotter.reversi;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import com.drknotter.reversi.controller.ReversiBoardController;
import com.drknotter.reversi.model.ReversiBoardModel;
import com.drknotter.reversi.view.ReversiBoardView;


public class ReversiActivity extends Activity
{
    private static final String TAG = ReversiActivity.class.getSimpleName();

    ReversiBoardModel boardModel;
    ReversiBoardView boardView;
    ReversiBoardController boardController;

    HandlerThread handlerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reversi);

        boardModel = new ReversiBoardModel();
        boardView = (ReversiBoardView) findViewById(R.id.board_view);
        ImageView currentPlayerIcon = (ImageView) findViewById(R.id.current_player_icon);
        Button undoButton = (Button) findViewById(R.id.undo);

        handlerThread = new HandlerThread("reversi-thread");
        handlerThread.start();
        boardController = new ReversiBoardController(
                handlerThread.getLooper(),
                new Handler(Looper.getMainLooper()),
                boardModel, boardView,
                currentPlayerIcon, undoButton);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        handlerThread.quit();
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
