package com.github.agiledevgroup2.xpnavigator;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The Activity for displaying an individual board
 */
public class BoardActivity extends AppCompatActivity implements ApiListener{
    private String boardId = "";
    private ApiHandler handler;
    private ExpandableListView expandableListView;
    private CustomExpandableListAdapter adapter;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListOverview;

    private static final String TAG = "BoardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        /*Handle Passed information from previous activity*/
        Intent previousIntent = getIntent();
        boardId = previousIntent.getStringExtra(LoginActivity.BOARD_EXTRA_ID);
        handler = new ApiHandler(this);

        /*Fetch the lists in background*/
        GenerateListsTask glt = new GenerateListsTask();
        glt.execute(boardId);

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListOverview = new HashMap<String,List<String>>();

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableListOverview.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT
                ).show();
                return false;
            }
        });
    }


    private void initAdapter(List<String> listNames){
        expandableListTitle = listNames;
        adapter = new CustomExpandableListAdapter(this,expandableListTitle,expandableListOverview);
        expandableListView.setAdapter(adapter);
    }

    private class GenerateListsTask extends AsyncTask<String,Void,Boolean> {
        private String[] info = new String[2];
        @Override
        protected Boolean doInBackground(String... params) {
            String boardId = params[0];
            try {
                handler.fetchLists(boardId);
            } catch (Exception e) {
                Log.d("TaskError", "GenerateListTaskException");
                e.printStackTrace();
            }
            return true;
        }
    }


    private class GenerateCardsTask extends AsyncTask<String,Void,Boolean> {
        private String[] info = new String[2];
        @Override
        protected Boolean doInBackground(String... params) {
            String listId = params[0];
            String listName = params[1];
            try {

                handler.fetchCards(listId,listName);
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
        List<String> nameList = new ArrayList<>();
        for(TrelloList tl:lists){
            Log.d(TAG, tl.getName());
            nameList.add(tl.getName());
            new GenerateCardsTask().execute(tl.getId(),tl.getName());
        }
        /*Initiates the adapter with the titles aka Trello Lists*/
        initAdapter(nameList);
    }

    @Override
    public void cardsCallback(List<TrelloCard> cards, String listName) {
        List<String> cardNames = new ArrayList<>();
        for(TrelloCard tc: cards) {
            cardNames.add(tc.getName());

            Log.d("LISTNAME " + listName + " ",tc.getName());
        }

        /*Add cards to the HashMap, tell adapter to update
        * UI Wise, adds cards as children to the specified list
        * */

        expandableListOverview.put(listName,cardNames);
        adapter.notifyDataSetChanged();
    }
}
