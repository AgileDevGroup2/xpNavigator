package com.github.agiledevgroup2.xpnavigator.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
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
import com.github.agiledevgroup2.xpnavigator.view.adapter.MemberListAdapter;

import java.util.List;

public class MembersBoardActivity extends AppCompatActivity implements ApiListener {

    private String idBoard = "";
    private String nameBoard = "";
    private String mNameBoardTeam = "Loading";
    private ApiHandler mHandler;
    private TrelloBoardMembers boardMembers;
    private MemberListAdapter adapter;
    private ListView listView;

    private boolean isOrganization = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_board);

        Timer.setContext(getApplicationContext());

        ((TextView) findViewById(R.id.headline)).setText("");

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            this.idBoard = extras.getString(BoardActivity.BOARD_MEMBERS_EXTRA_ID);
            this.nameBoard = extras.getString(BoardActivity.BOARD_MEMBERS_EXTRA_NAME);

            this.setTitle("BoardMembers");
        }

        mHandler = new ApiHandler(this);

        // enabling action bar app icon and behaving it as toggle button
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        this.getSupportActionBar().setDisplayUseLogoEnabled(true);


        GenerateNameTeamTask glt2 = new GenerateNameTeamTask();
        glt2.execute(idBoard);

        /*Fetch the lists in background*/
        GenerateMembersTask glt = new GenerateMembersTask();
        glt.execute(idBoard);



    }

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_membersboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {

            case R.id.action_logout:
                mHandler.logout();
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
                return true;

            case R.id.action_timer:
                createTimerDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




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

    @Override
    public void boardsCallback(List<TrelloBoard> boards) {

    }

    @Override
    public void listsCallback(List<TrelloList> lists, String boardId) {

    }

    @Override
    public void cardsCallback(List<TrelloCard> cards, String listId) {

    }

    @Override
    public void membersBoardCallback(TrelloBoardMembers boardMembers) {

        this.boardMembers = boardMembers;

        initView();
    }

    @Override
    public void nameBoardTeamCallback(String nameBoardTeam) {

        mNameBoardTeam = nameBoardTeam;
        ((TextView) findViewById(R.id.headline)).setText(mNameBoardTeam);
    }

    private void initView() {

        listView = (ListView) findViewById(R.id.listView);
        adapter = new MemberListAdapter(this, boardMembers);
        listView.setAdapter(adapter);
    }


    private class GenerateMembersTask extends AsyncTask<String,Void,Boolean> {
        private String[] info = new String[2];
        @Override
        protected Boolean doInBackground(String... params) {
            String boardId = params[0];
            try {
                mHandler.fetchOrganization(boardId);
            } catch (Exception e) {
                Log.d("TaskError", "GenerateMemberTaskException");
                e.printStackTrace();
            }

            return true;
        }
    }

    private class GenerateNameTeamTask extends AsyncTask<String,Void,Boolean> {
        private String[] info = new String[2];
        @Override
        protected Boolean doInBackground(String... params) {
            String boardId = params[0];
            try {
                mHandler.fetchNameBoardTeam(boardId);
            } catch (Exception e) {
                Log.d("TaskError", "GenerateNameTeamTaskException");
                e.printStackTrace();
            }

            return true;
        }
    }


    public void createTimerDialog() {
        final android.app.AlertDialog.Builder timeDialogBuilder = new android.app.AlertDialog.Builder(MembersBoardActivity.this);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.dialog_timer, null);

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

        //init dialog
        timeDialogBuilder.setTitle(getString(R.string.headline_timer));
        timeDialogBuilder.setView(dialogView);
        timeDialogBuilder.setPositiveButton(getString(R.string.lbl_done), null);

        //register view
        Timer.registerView(dialogView);

        //create dialog
        android.app.AlertDialog timerDialog = timeDialogBuilder.create();
        timerDialog.show();
        timerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Timer.removeView();
            }
        });
    }
}
