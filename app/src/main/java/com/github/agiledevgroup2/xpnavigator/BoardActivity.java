package com.github.agiledevgroup2.xpnavigator;

import android.content.ClipData;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The Activity for displaying an individual board
 ** TODO WebHooks
 * */
public class BoardActivity extends AppCompatActivity implements ApiListener{
    private String boardId = "";
    private String boardName = "";
    private ApiHandler handler;
    private ExpandableListView expandableListView;
    private CustomExpandableListAdapter adapter;
    private List<TrelloList> expandableListTitle;
    private HashMap<String, List<TrelloCard>> expandableListOverview;
    private List<TrelloList> trelloLists;


    public final static String BOARD_MEMBERS_EXTRA_ID = "BOARD_MEMBERS_ID";
    public final static String BOARD_MEMBERS_EXTRA_NAME = "BOARD_MEMBERS_NAME";

    private static final String TAG = "BoardActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        trelloLists = new ArrayList<>();


        /*Handle Passed information from previous activity*/
        Intent previousIntent = getIntent();
        boardId = previousIntent.getStringExtra(LoginActivity.BOARD_EXTRA_ID);
        this.boardName = previousIntent.getStringExtra(LoginActivity.BOARD_EXTRA_NAME);

        this.setTitle(boardName);

        handler = new ApiHandler(this);


        /*Fetch the lists in background*/
        GenerateListsTask glt = new GenerateListsTask();
        glt.execute(boardId);


        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListOverview = new HashMap();

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




        /**
         * Long Click Listener for the entire expandableListView.
         * Determines whether a group or child was clicked and calls
         * the appropiate method
         */
        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                long packedPosition = expandableListView.getExpandableListPosition(position);
                int itemType = ExpandableListView.getPackedPositionType(packedPosition);
                int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
                int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);

                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    onGroupLongClick(groupPosition);
                } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    //onChildLongClick(groupPosition, childPosition);
                    ClipData data  = ClipData.newPlainText("id", expandableListOverview.get(
                                    expandableListTitle.get(groupPosition).getName()).get(
                                    childPosition).getId());
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.setBackgroundColor(Color.GREEN);
                    view.startDrag(data, shadowBuilder, expandableListOverview.get(
                            expandableListTitle.get(groupPosition).getName()).get(
                            childPosition), 0);
                    view.setBackgroundColor(Color.TRANSPARENT);
                }
                return true;
            }
        });


        expandableListView.setOnDragListener(new AdapterView.OnDragListener() {
            int oldPos;
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int action = event.getAction();
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        int x_cord = (int) event.getX();
                        int y_cord = (int) event.getY();




                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();

                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();



                        break;

                    case DragEvent.ACTION_DRAG_LOCATION:
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        int pointss = expandableListView.pointToPosition(x_cord, y_cord);

                        if (expandableListView.getItemAtPosition(pointss) != null) {
                            long packedPosition = expandableListView.getExpandableListPosition(pointss);
                            int itemType = ExpandableListView.getPackedPositionType(packedPosition);
                            int ngroupPosition = expandableListView.getPackedPositionGroup(expandableListView.getExpandableListPosition(pointss));

                            int nchildPosition = expandableListView.getPackedPositionChild(expandableListView.getExpandableListPosition(pointss));
                            if(oldPos != pointss){
                                expandableListView.getChildAt(oldPos).setBackgroundColor(Color.TRANSPARENT);
                                expandableListView.getChildAt(oldPos).invalidate();
                                oldPos = pointss;

                            }

                            if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {

                                Log.d("OVER", "List: " + expandableListTitle.get(ngroupPosition).getName());
                                if(expandableListView.getFirstVisiblePosition() != 0){
                                    int fin = pointss-expandableListView.getFirstVisiblePosition();
                                    expandableListView.getChildAt(fin).setBackgroundColor(Color.argb(100, 255, 204, 0));
                                    oldPos = fin;
                                }
                                else{
                                    expandableListView.getChildAt(pointss).setBackgroundColor(Color.argb(100, 255, 204, 0));
                                    oldPos = pointss;

                                }


                            }
                            else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {

                                Log.d("OVER", "List: " + expandableListTitle.get(ngroupPosition).getName() + " Card " +
                                        expandableListOverview.get(expandableListTitle.get(ngroupPosition).getName()).get(nchildPosition).getName());
                                int count = expandableListView.getFirstVisiblePosition();
                                if(expandableListView.getFirstVisiblePosition() != 0){
                                    int fin = pointss-count;
                                    expandableListView.getChildAt(fin).setBackgroundColor(Color.argb(100, 179, 236, 255));
                                    oldPos = fin;
                                }
                                else{
                                    expandableListView.getChildAt(pointss).setBackgroundColor(Color.argb(100, 179, 236, 255));
                                    oldPos = pointss;

                                }

                                Log.d("INTS", "GPOS " + ngroupPosition + " CPOS " + nchildPosition + "POINTSS " + pointss + "COUNT " + count);



                            }



                        }
                        break;

                    case DragEvent.ACTION_DRAG_ENDED:

                        break;

                    case DragEvent.ACTION_DROP:
                        /*Get Coords of drop location and turn it into which list it was dropped in*/
                        /*TODO, Drop within list*/
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        int point = expandableListView.pointToPosition(x_cord, y_cord);

                        if (expandableListView.getItemAtPosition(point) != null) {
                            long packedPosition = expandableListView.getExpandableListPosition(point);
                            int itemType = ExpandableListView.getPackedPositionType(packedPosition);
                            int groupDroppedPos = ExpandableListView.getPackedPositionGroup(packedPosition);
                            int childDroppedPosition = ExpandableListView.getPackedPositionChild(packedPosition);



                            String group = expandableListTitle.get(groupDroppedPos).getId();
                            ClipData.Item item = event.getClipData().getItemAt(0);
                            String card = item.getText().toString();

                            if(itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP){
                                Log.d("DROP", "DROPPED " + card + " IN " + group);

                            }

                            else if(itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD){
                                String child = expandableListOverview.get(group).get(childDroppedPosition).getName();

                                Log.d("DROP", "DROPPED " + card + " ON " + child + "IN " + group);

                            }
                            handler.moveCard(card,group);


                            expandableListView.getChildAt(oldPos).setBackgroundColor(Color.TRANSPARENT);
                            expandableListView.getChildAt(oldPos).invalidate();

                        }

                        return true;

                    default:
                        break;
                }
                //return value
                return true;
            }
        });//end DragListener
    }

    /**
     *  Long Click Listener for a Child of a Group, i.e a Card in a List
     *  Generates an AlertDialog where the user can choose to
     *  (0)Edit
     *  (1)Delete
     *  (2)Move
     *  the clicked card.
     * @param groupPosition
     * @param childPosition
     */
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
                            handler.removeCard(longClickedCard.getId());
                            updateListView(expandableListTitle.get(groupPosition));

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
                                         handler.moveCard(longClickedCard.getId(),listId);
                                         updateListView(expandableListTitle.get(which));
                                         updateListView(expandableListTitle.get(groupPosition));
                                     }
                                    })
                                    .show();
                    }
                }
            })
        .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     *  Long Click Handler a longclick on a group, i.e TrelloList in the
     *  expandableListView
     *
     *  Generates an AlertDialog to show popup with alternatives for actions
     *  (0)Add Card
     *  (1)...
     *
     *
     *  Add Card generates a new AlertDialog where the user can enter
     *  cardTitle and cardDescription for a new card and is prompted to confirm or cancel the creation.
     * @param groupPosition
     */
    private void onGroupLongClick(final int groupPosition){
        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setItems(R.array.board_activity_popup, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            /*R.array.board_activity_popup: 0Add Card, 1Change Name, 2Save World*/
                            case 0:
                                final View view = getLayoutInflater().inflate(R.layout.add_card, null);
                                final EditText cardTitle = (EditText) view.findViewById(R.id.cardTitle);
                                final EditText cardDescription = (EditText) view.findViewById(R.id.cardDescription);

                                new AlertDialog.Builder(BoardActivity.this, R.style.AlertDialogStyle)
                                        .setView(view)
                                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                handler.addCard(cardTitle.getText().toString(),
                                                        cardDescription.getText().toString(),
                                                    expandableListTitle.get(groupPosition).getId());

                                                //update view (Todo: outsource this)
                                                updateListView(expandableListTitle.get(groupPosition));
                                                //expandableListView.invalidate();
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

    /**
     * update a lists view
     * @param list list to update the view of
     */
    private void updateListView(TrelloList list) {
        handler.fetchCards(list.getId(), list.getName());
    }

    /**
     * Initiates the adapter with data fields
     * @param listNames
     */
    private void initAdapter(List<TrelloList> listNames) {
        /*trelloLists and expandableListtitle will point to same, fix later*/
        expandableListTitle = listNames;
        adapter = new CustomExpandableListAdapter(this,expandableListTitle,expandableListOverview);
        expandableListView.setAdapter(adapter);
    }



    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            handler.logout();
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            //Pass the BoardID to new activity
            startActivity(login);
            return true;
        }

        if (id == R.id.button_view_members)
        {
            Intent intent = new Intent(BoardActivity.this, MembersBoardActivity.class);

            intent.putExtra(BoardActivity.BOARD_MEMBERS_EXTRA_ID, this.boardId);
            intent.putExtra(BoardActivity.BOARD_MEMBERS_EXTRA_NAME, this.boardName);

            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);


    }

    /** Asynchronous Task to fetch lists in background
     *
     */
    private class GenerateListsTask extends AsyncTask<String,Void,Boolean> {
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


    /** Async Task to fetch cards in background
     *
     */
    private class GenerateCardsTask extends AsyncTask<String,Void,Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String listId = params[0];
            String listName = params[1];
            try {

                handler.fetchCards(listId,listName);
            } catch (Exception e) {
                Log.d("TaskError", "GenerateCardTaskException");
                e.printStackTrace();
            }
            return true;
        }
    }

    @Override
    public void boardsCallback(List<TrelloBoard> boards) {

    }

    /**
     *  Callback from the ApiHandler, initiates the TrelloList's
     *  List of TrelloCards through GenerateCardsTask
     * @param lists fetched boards from trello
     * @param boardId board the lists belong to
     */
    @Override
    public void listsCallback(List<TrelloList> lists, String boardId) {
        trelloLists = lists;
        for(TrelloList tl:lists){
            new GenerateCardsTask().execute(tl.getId(),tl.getName());
        }
        initAdapter(trelloLists);
    }

    /**
     * Callback from ApiHandler, sets the listName's List<TrelloCard>
     *
     * with cards. Tells the adapter that the dataset has been updated so that the
     * expandableListView can update
     *
     * @param cards fetched cards from trello
     * @param listName name of the lists where the cards belong
     */
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


    @Override
    public void membersBoardCallback(List<TrelloMember> members) {

    }


}
