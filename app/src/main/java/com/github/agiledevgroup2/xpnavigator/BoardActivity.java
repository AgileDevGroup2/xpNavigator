package com.github.agiledevgroup2.xpnavigator;

import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
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
 ** TODO Refactor Class
 * */
public class BoardActivity extends AppCompatActivity implements ApiListener{
    private String boardId = "";
    private ApiHandler handler;
    private ExpandableListView expandableListView;
    private CustomExpandableListAdapter adapter;
    private EditText cardTitle, cardDescription;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListOverview;
    private HashMap<String,String> trelloListMap;

    private static final String TAG = "BoardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        cardTitle = (EditText) findViewById(R.id.cardTitle);
        cardDescription = (EditText) findViewById(R.id.cardDescription);
        /*Handle Passed information from previous activity*/
        Intent previousIntent = getIntent();
        boardId = previousIntent.getStringExtra(LoginActivity.BOARD_EXTRA_ID);
        handler = new ApiHandler(this);
        trelloListMap = new HashMap<>();

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


        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                long packedPosition = expandableListView.getExpandableListPosition(position);
                int itemType = ExpandableListView.getPackedPositionType(packedPosition);
                int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
                int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);

                if(itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP){
                    onGroupLongClick(groupPosition);
                }

                else if(itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD){
                   /*TODO Add Method & Handler. Perhaps move on longclick*/
                    //onChildLongClick(groupPosition,childPosition);
                }
                return false;
            }
        });
    }

    private void onGroupLongClick(final int groupPosition){
        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setItems(R.array.board_activity_popup, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            /*R.array.board_activity_popup: 0Add Card, 1Change Name, 2Save World*/
                            case 0:
                            new AlertDialog.Builder(BoardActivity.this, R.style.AlertDialogStyle)
                                    .setView(getLayoutInflater().inflate(R.layout.add_card,null))
                                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            /*Call handler with parameters
                                            * (String) Card Title,
                                            * (String) Card Description
                                            * (String) List ID
                                            *
                                            * Note: Leaving out due, which is required. Pass null
                                            * from handler to API
                                            * *
                                            handler.addCard(cardTitle.getText().toString(),
                                                    cardDescription.getText().toString(),
                                                    trelloListMap.get(
                                                            expandableListTitle.get(groupPosition)));*/
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            /*Do nothing for now, perhaps take one step back to prev
                                            * dialog*/
                                        }
                                    })
                                    .show();
                        }
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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

        /*Refactor*/
        List<String> nameList = new ArrayList<>();
        for(TrelloList tl:lists){
            this.trelloListMap.put(tl.getName(),tl.getId());
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
