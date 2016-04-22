package com.github.agiledevgroup2.xpnavigator;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * The Activity for displaying an individual board
 */
public class BoardActivity extends AppCompatActivity implements ApiListener{
    private String boardId = "";
    private ApiHandler handler;
    private List<TrelloList> listList = new ArrayList<>();
    private List <TrelloCard> cardList = new ArrayList<>();
    private static final String TAG = "BoardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        Intent previousIntent = getIntent();
        boardId = previousIntent.getStringExtra(LoginActivity.BOARD_EXTRA_ID);
        handler = new ApiHandler(this);
        //handler.fetchLists(boardId);

        GenerateListsTask glt = new GenerateListsTask();
        glt.execute(boardId);

        //handler.fetchLists(boardId);





    }


    private class GenerateListsTask extends AsyncTask<String,Void,Boolean> {
        private String[] info = new String[2];

        @Override
        protected Boolean doInBackground(String... params) {

            String boardId = params[0];


            /* Try opening a http connection to the url above*/
            try {
                handler.fetchLists(boardId);
            } catch (Exception e) {
                Log.d("TaskError", "GenerateListTaskException");
                e.printStackTrace();
            }

            if (listList.size() != 0) return true;
            else return false;
        }

        @Override
        protected void onPostExecute(Boolean result){
            Log.d("LIST IS: " , result.toString());
            if(result){
                handler.fetchCards(listList.get(0).getId());
                handler.fetchCards(listList.get(1).getId());
                handler.fetchCards(listList.get(2).getId());
            }
        }
    }


    private class GenerateCardsTask extends AsyncTask<String,Void,Boolean> {
        private String[] info = new String[2];

        @Override
        protected Boolean doInBackground(String... params) {
            String listId = params[0];
            try {
                handler.fetchCards(listId);
            } catch (Exception e) {
                Log.d("TaskError", "GenerateListTaskException");
                e.printStackTrace();
            }
            return true;
        }
    }


    @Override
    public void boardsCallback(List<TrelloBoard> boards) {

    }

    @Override
    public void listsCallback(List<TrelloList> lists, String boardId) {
        listList = lists;
        for(TrelloList tl:listList){
            Log.d(TAG, tl.getName());
            new GenerateCardsTask().execute(tl.getId());
        }

    }

    @Override
    public void cardsCallback(List<TrelloCard> cards, String listId) {
       this.cardList = cards;
        for(TrelloCard tc: cardList) {
            Log.d("CARDS:" , tc.getName());
        }


    }


}
