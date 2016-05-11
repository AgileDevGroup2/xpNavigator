package com.github.agiledevgroup2.xpnavigator;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.List;

public class BoardListActivity extends AppCompatActivity implements ApiListener{

    private static final String TAG = "BoardListActivity";
    public final static String BOARD_EXTRA_ID = "BOARD_ID";
    public final static String BOARD_EXTRA_NAME = "BOARD_NAME";

    private ApiHandler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_list);

        handler = new ApiHandler(this);
        Timer.setContext(getApplicationContext());
        handler.fetchBoards();

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
                createTimerDialog();
                return true;

            case R.id.action_logout:
                handler.logout();
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
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

        Log.d(TAG, "add Button: " + buttonText);

        LinearLayout btnLayout = (LinearLayout) findViewById(R.id.buttonLayout);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnLayout.addView(boardButton, lp);
        boardButton.setText(boardButton.getText() + " " + buttonText);


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

    /**
     * creates a new countdown timer dialog
     */
    public void createTimerDialog() {
        final AlertDialog.Builder timeDialogBuilder = new AlertDialog.Builder(BoardListActivity.this);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.dialog_timer, null);

        //init dialog
        timeDialogBuilder.setTitle(getString(R.string.lbl_done));
        timeDialogBuilder.setView(dialogView);
        timeDialogBuilder.setPositiveButton(getString(R.string.lbl_done), null);

        NumberPicker pickers[] = {
                (NumberPicker) dialogView.findViewById(R.id.hours),
                (NumberPicker) dialogView.findViewById(R.id.minutes),
                (NumberPicker) dialogView.findViewById(R.id.seconds)};
        for (NumberPicker picker : pickers) {
            picker.setMinValue(0);
            picker.setMaxValue(59);

            picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    if (!Timer.isRunning()) {
                        NumberPicker pickers[] = {
                                (NumberPicker) dialogView.findViewById(R.id.hours),
                                (NumberPicker) dialogView.findViewById(R.id.minutes),
                                (NumberPicker) dialogView.findViewById(R.id.seconds)};
                        Timer.setTime(pickers[0].getValue(), pickers[1].getValue(), pickers[2].getValue());
                    }
                }
            });
        }
        //setup buttons
        ImageButton startB = (ImageButton) dialogView.findViewById(R.id.startButton);
        updateDialog(dialogView);
        startB.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                if (Timer.isRunning()) {
                    Timer.pause();
                } else {
                    Timer.start();
                }
                updateDialog(dialogView);
            }
        });
        ImageButton resetB = (ImageButton) dialogView.findViewById(R.id.resetButton);
        resetB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timer.reset();
                updateDialog(dialogView);
            }
        });

        //init values
        pickers[0].setValue(Timer.getHoursLeft());
        pickers[1].setValue(Timer.getMinutesLeft());
        pickers[2].setValue(Timer.getSecondsLeft());

        //register view
        Timer.registerView(dialogView);

        //create dialog
        AlertDialog timerDialog = timeDialogBuilder.create();
        timerDialog.show();
        timerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Timer.removeView();
            }
        });
    }

    /**
     * update the countdown timer dialogs elements <br>
     * <font style:color="red">Warning:</font> Use only the timer dialog!
     * @param dialogView dialog to update
     */
    @SuppressWarnings("deprecation")
    public void updateDialog(View dialogView) {
        ImageButton startB = (ImageButton) dialogView.findViewById(R.id.startButton);
        String resource = "android:drawable/ic_media_play";
        if (Timer.isRunning()) {
            resource = "android:drawable/ic_media_pause";
        }
        int identifier = getResources().getIdentifier(resource, null, null);
        startB.setImageDrawable(getResources().getDrawable(identifier));

        NumberPicker pickers[] = {
                (NumberPicker) dialogView.findViewById(R.id.hours),
                (NumberPicker) dialogView.findViewById(R.id.minutes),
                (NumberPicker) dialogView.findViewById(R.id.seconds)};
        for (NumberPicker picker : pickers) {
            if (Timer.isRunning()) picker.setEnabled(false);
            else picker.setEnabled(true);
        }
    }
}
