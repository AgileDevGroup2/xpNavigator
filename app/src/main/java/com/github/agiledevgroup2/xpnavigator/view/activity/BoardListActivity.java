package com.github.agiledevgroup2.xpnavigator.view.activity;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.github.agiledevgroup2.xpnavigator.controller.ApiHandler;
import com.github.agiledevgroup2.xpnavigator.controller.ApiListener;
import com.github.agiledevgroup2.xpnavigator.R;
import com.github.agiledevgroup2.xpnavigator.model.Timer;
import com.github.agiledevgroup2.xpnavigator.model.TrelloBoard;
import com.github.agiledevgroup2.xpnavigator.model.TrelloBoardMembers;
import com.github.agiledevgroup2.xpnavigator.model.TrelloCard;
import com.github.agiledevgroup2.xpnavigator.model.TrelloList;
import com.github.agiledevgroup2.xpnavigator.view.adapter.DialogBuilder;

import java.util.List;

public class BoardListActivity extends AppCompatActivity implements ApiListener {

    private static final String TAG = "BoardListActivity";
    public final static String BOARD_EXTRA_ID = "BOARD_ID";
    public final static String BOARD_EXTRA_NAME = "BOARD_NAME";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_list);


        Timer.setContext(getApplicationContext());
        ApiHandler.setListener(this);
        ApiHandler.fetchBoards();

        /**
         *  set the logo
         */
        // enabling action bar app icon and behaving it as toggle button
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        this.getSupportActionBar().setDisplayUseLogoEnabled(true);

        ((TextView) findViewById(R.id.headline)).setText(getText(R.string.my_boards));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApiHandler.setListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Search Bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_timer:
                new DialogBuilder().createTimerDialog(this);
                return true;

            case R.id.action_logout:
                ApiHandler.logout();
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Adding buttons for each board dynamically
     *
     * @param buttonText The name of the board received from the JSON "name"
     */
    public void addBoardButton(String buttonText, String boardId) {
         /*Generate a button for each TrelloBoard*/
        final Button boardButton = new Button(getApplicationContext());
        final String id = boardId;
        final String nameBoard = buttonText;
        try {
            boardButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.blue_btn));
        } catch (Resources.NotFoundException e){
            boardButton.setBackgroundColor(0xaa3f51b5);
        }

        Log.d(TAG, "add Button: " + buttonText);

        LinearLayout btnLayout = (LinearLayout) findViewById(R.id.buttonLayout);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnLayout.addView(boardButton, lp);
        boardButton.setText(boardButton.getText() + " " + buttonText);

        TextView tv = new TextView(getApplicationContext());
        tv.setBackgroundColor(0);
        btnLayout.addView(tv, lp);


        /*Event Handler for each boardButton*/
        boardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*Launch new Activity for the clicked board*/
                Intent clickedBoard = new Intent(getApplicationContext(), BoardActivity.class);
                //Pass the BoardID to new activity
                clickedBoard.putExtra(BOARD_EXTRA_ID, id);
                clickedBoard.putExtra(BOARD_EXTRA_NAME, nameBoard);
                startActivity(clickedBoard);

            }
        });

    }

    @Override
    public void failureCallback(String failure) {

    }

    @Override
    public void boardsCallback(List<TrelloBoard> boards) {
        for(TrelloBoard b:boards){
            addBoardButton(b.getName(),b.getId());
        }

    }

    @Override
    public void listsCallback(List<TrelloList> lists, String boardId) {

    }

    @Override
    public void cardsCallback(List<TrelloCard> cards, String listId) {

    }

    @Override
    public void membersBoardCallback(TrelloBoardMembers boardMembers) {

    }

    @Override
    public void nameBoardTeamCallback(String nameBoardTeam) {

    }
}
