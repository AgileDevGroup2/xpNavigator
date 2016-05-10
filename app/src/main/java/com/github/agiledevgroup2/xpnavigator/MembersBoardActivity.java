package com.github.agiledevgroup2.xpnavigator;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MembersBoardActivity extends AppCompatActivity implements ApiListener{

    private String idBoard = "";
    private String nameBoard = "";
    private ApiHandler handler;
    private TrelloBoardMembers boardMembers;
    private MemberListAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_board);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            this.idBoard = extras.getString(BoardActivity.BOARD_MEMBERS_EXTRA_ID);
            this.nameBoard = extras.getString(BoardActivity.BOARD_MEMBERS_EXTRA_NAME);

            this.setTitle(this.nameBoard);
        }

        handler = new ApiHandler(this);

        /*Fetch the lists in background*/
        GenerateMembersTask glt = new GenerateMembersTask();
        glt.execute(idBoard);

        Log.d("test1", "OK");
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

        Log.d("test", "OK");

        initView();
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
                Log.d("test23", boardId);
                handler.fetchOrganization(boardId);
            } catch (Exception e) {
                Log.d("TaskError", "GenerateMemberTaskException");
                e.printStackTrace();
            }

            return true;
        }
    }
}
