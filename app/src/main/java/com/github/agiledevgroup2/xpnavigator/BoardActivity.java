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
    private List<TrelloList> expandableListTitle;
    private HashMap<String, List<TrelloCard>> expandableListOverview;
    private List<TrelloList> trelloLists;



    private static final String TAG = "BoardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        trelloLists = new ArrayList<>();
        cardTitle = (EditText) findViewById(R.id.cardTitle);
        cardDescription = (EditText) findViewById(R.id.cardDescription);

        /*Handle Passed information from previous activity*/
        Intent previousIntent = getIntent();
        boardId = previousIntent.getStringExtra(LoginActivity.BOARD_EXTRA_ID);
        handler = new ApiHandler(this);


        /*Fetch the lists in background*/
        GenerateListsTask glt = new GenerateListsTask();
        glt.execute(boardId);

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListOverview = new HashMap<String,List<TrelloCard>>();

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition).getName() + " List Expanded.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition).getName() + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition).getName()
                                + " -> "
                                + expandableListOverview.get(
                                expandableListTitle.get(groupPosition).getName()).get(
                                childPosition).getName(), Toast.LENGTH_SHORT
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
                    onChildLongClick(groupPosition,childPosition);
                }
                return false;
            }
        });
    }


    private void onChildLongClick(final int groupPosition, final int childPosition){

         final TrelloCard longClickedCard = expandableListOverview.get(
                expandableListTitle.get(groupPosition).getName()).get(
                childPosition);


        new AlertDialog.Builder(this,R.style.AlertDialogStyle)
            .setItems(R.array.board_activity_child_popup, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    switch(which){
                        /*0 Edit, 1 Delete, 2 Move*/


                        case 0:
                            /*Popup edit or new activity?*/
                            break;
                        case 1:
                            /*Tell handler to delete card [id]*/
                            /*handler.deleteCard(longClickedCard.getId());*/

                            break;
                        case 2:
                            /*Quickfix to dynamically display names*/
                            List<String> titles = new ArrayList<String>();
                            for(TrelloList tl:expandableListTitle){
                                titles.add(tl.getName());
                            }
                            CharSequence [] cs = titles.toArray
                                    (new CharSequence[titles.size()]);
                            new AlertDialog.Builder(BoardActivity.this,R.style.AlertDialogStyle)
                                    .setItems(cs, new DialogInterface.OnClickListener(){
                                     @Override
                                     public void onClick(DialogInterface dialog, int which) {
                                         String listId = expandableListTitle.get(which).getId();
                                         Log.d("CHILDLONGCLICK","Moved Card" + longClickedCard.getName()
                                                 + " to List " + expandableListTitle.get(which).getName()
                                                 );
                                         /*handler.moveCard(longClickedCard.getId(),listId)*/
                                     }
                                    })
                                    .show();
                    }
                }
            })
        .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    private void onGroupLongClick(final int groupPosition){
        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setItems(R.array.board_activity_popup, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            /*R.array.board_activity_popup: 0Add Card, 1Change Name, 2Save World*/
                            case 0:
                                new AlertDialog.Builder(BoardActivity.this, R.style.AlertDialogStyle)
                                        .setView(getLayoutInflater().inflate(R.layout.add_card, null))
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
                                                    expandableListTitle.get(groupPosition).getId());*/
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

    private void initAdapter(List<TrelloList> listNames){
        /*trelloLists and expandableListtitle will point to same, fix later*/
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
        trelloLists = lists;
        for(TrelloList tl:lists){
            new GenerateCardsTask().execute(tl.getId(),tl.getName());
        }
        initAdapter(trelloLists);
    }

    @Override
    public void cardsCallback(List<TrelloCard> cards, String listName) {
        for(TrelloList tl:trelloLists){
            if(tl.getName().equals(listName)){
                tl.setCards(cards);
                expandableListOverview.put(tl.getName(), tl.getCards());
            }
        }
        adapter.notifyDataSetChanged();
    }
}
