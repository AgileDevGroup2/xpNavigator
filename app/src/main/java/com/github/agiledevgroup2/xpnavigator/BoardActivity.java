package com.github.agiledevgroup2.xpnavigator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BoardActivity extends AppCompatActivity implements ApiListener{
    private String boardId = "";
    private ApiHandler handler;
    private List<TrelloList> lists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        lists = new ArrayList<>();
        Intent previousIntent = getIntent();
        boardId = previousIntent.getStringExtra(LoginActivity.BOARD_EXTRA_ID);
        handler = new ApiHandler(this);

        handler.fetchLists(boardId);





    }

    @Override
    public void boardsCallback(List<TrelloBoard> boards) {

    }

    @Override
    public void listsCallback(List<TrelloList> lists, String boardId) {
        /*lists should contain a list of TrelloLists, which has info about list and a list of its
        * cards
        * */
        this.lists = lists;
        for (TrelloList l:this.lists){
            System.out.println("List: " + l.getmName());

        }

        /*TODO POPULATE UI WITH CONTENTS*/

    }

    @Override
    public void cardsCallback(List<TrelloCard> cards, String listId) {

    }
}
