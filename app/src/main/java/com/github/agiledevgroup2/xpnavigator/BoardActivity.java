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
 ** TODO WebHooks?, Refactor according to coding standards
 * TODO Add Button to Children, in CustomExpandableListAdapter, manage the button click and
 * TODO make sure [buttonName].setFocusable(false);
 *
 * display the additional options when pressing a card
 * */
public class BoardActivity extends AppCompatActivity implements ApiListener{
    private String mBoardId = "";
    private String mBoardName = "";
    private ApiHandler mHandler;
    private ExpandableListView mExpandableListView;
    private CustomExpandableListAdapter mAdapter;
    private List<TrelloList> mExpandableListTitle;
    private HashMap<String, List<TrelloCard>> mExpandableListOverview;
    private List<TrelloList> mTrelloLists;


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

        mTrelloLists = new ArrayList<>();


        /*Handle Passed information from previous activity*/
        Intent previousIntent = getIntent();
        mBoardId = previousIntent.getStringExtra(LoginActivity.BOARD_EXTRA_ID);
        this.mBoardName = previousIntent.getStringExtra(LoginActivity.BOARD_EXTRA_NAME);

        this.setTitle(mBoardName);

        mHandler = new ApiHandler(this);


        /*Fetch the lists in background*/
        GenerateListsTask glt = new GenerateListsTask();
        glt.execute(mBoardId);


        mExpandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        mExpandableListOverview = new HashMap();

        


        /*Eventlisteners for the mExpandableListView*/
        mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener
                () {
            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        mExpandableListTitle.get(groupPosition).getName() + " List Expanded.",
                        Toast.LENGTH_SHORT).show();


            }
        });

        mExpandableListView.setOnGroupCollapseListener(new ExpandableListView
                .OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        mExpandableListTitle.get(groupPosition).getName() + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {

                /*Triggers even if longclick occurs*/
                onChildLongClick(groupPosition,childPosition);
                return false;
            }
        });


        ;

        /**
         * Long Click Listener for the entire mExpandableListView.
         * Determines whether a group or child was clicked and calls
         * the appropiate method
         */
        mExpandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                long packedPosition = mExpandableListView.getExpandableListPosition(position);
                int itemType = ExpandableListView.getPackedPositionType(packedPosition);
                int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
                int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);

                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    onGroupLongClick(groupPosition);
                } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    onChildLongClick(groupPosition, childPosition);

                    /*On Child Longclick, start drag & drop*/
                    /*Pass the Card ID to be attatched to the drag-image, read android docs
                    or ask Kim
                    * for more info*/

                    ClipData data  = ClipData.newPlainText("id", mExpandableListOverview.get(
                                    mExpandableListTitle.get(groupPosition).getName()).get(
                                    childPosition).getId());

                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.setBackgroundColor(Color.argb(100,51, 204, 51));

                    view.startDrag(data, shadowBuilder, mExpandableListOverview.get(
                            mExpandableListTitle.get(groupPosition).getName()).get(
                            childPosition), 0);
                    view.setBackgroundColor(Color.TRANSPARENT);
                }
                return true;
            }
        });


        mExpandableListView.setOnDragListener(new AdapterView.OnDragListener() {
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

                    /*Updates frequently while the dragged object is moving*/
                    case DragEvent.ACTION_DRAG_LOCATION:
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        int position = mExpandableListView.pointToPosition(x_cord, y_cord);

                        if (mExpandableListView.getItemAtPosition(position) != null) {
                            long packedPosition = mExpandableListView.
                                    getExpandableListPosition(position);
                            int itemType = ExpandableListView.
                                    getPackedPositionType(packedPosition);
                            int groupPosition = mExpandableListView.getPackedPositionGroup
                                    (mExpandableListView.getExpandableListPosition(position));

                            int childPosition = mExpandableListView.getPackedPositionChild
                                    (mExpandableListView.getExpandableListPosition(position));

                            /*If the item was dragged and now is above another object in the list,
                            * remove the backgroundcolor of the previous item*/
                            if(oldPos != position){
                                mExpandableListView.getChildAt(oldPos).
                                        setBackgroundColor(Color.TRANSPARENT);
                                mExpandableListView.getChildAt(oldPos).invalidate();
                                oldPos = position;

                            }

                            /*reAdjustedScreenPos is the calculated position to accurately
                            * navigate the list when it is scrolled down*/
                            int reAdjustedScreenPos = position-mExpandableListView.
                                    getFirstVisiblePosition();
                            oldPos = reAdjustedScreenPos;

                            /*Different background color depending on if drag is over Card
                            or a List element*/
                            if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {

                                Log.d("OVER", "List: " + mExpandableListTitle.get(groupPosition)
                                        .getName());

                                mExpandableListView.getChildAt(reAdjustedScreenPos).
                                        setBackgroundColor(Color.argb(100, 255, 204, 0));
                            }
                            else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {

                                Log.d("OVER", "List: " + mExpandableListTitle.get(groupPosition).
                                        getName() + " Card " + mExpandableListOverview.get
                                        (mExpandableListTitle.get(groupPosition).getName()).get
                                        (childPosition).getName());

                                mExpandableListView.getChildAt(reAdjustedScreenPos).
                                        setBackgroundColor(Color.argb(100, 179, 236, 255));

                            }

                        }
                        break;

                    case DragEvent.ACTION_DRAG_ENDED:

                        break;

                    /*Called when the user lets go of the dragged items*/
                    case DragEvent.ACTION_DROP:
                        /*Get Coords of drop location and find on which element the drop occurred*/
                        /*TODO, Drop within list*/
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        int point = mExpandableListView.pointToPosition(x_cord, y_cord);

                        /*Can probably refactor*/
                        if (mExpandableListView.getItemAtPosition(point) != null) {
                            long packedPosition = mExpandableListView.
                                    getExpandableListPosition(point);
                            int itemType = ExpandableListView.getPackedPositionType(packedPosition);
                            int groupPosition = mExpandableListView.getPackedPositionGroup
                                    (mExpandableListView.getExpandableListPosition(point));
                            int childPosition = mExpandableListView.getPackedPositionChild
                                    (mExpandableListView.getExpandableListPosition(point));



                            String groupId = mExpandableListTitle.get(groupPosition).getId();
                            ClipData.Item item = event.getClipData().getItemAt(0);
                            String cardId = item.getText().toString();

                            if(itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP){
                                Log.d("DROP", "DROPPED " + cardId + " IN " + groupId);
                                mHandler.moveCard(cardId,groupId);

                            }

                            else if(itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD){
                                String child = mExpandableListOverview.get(mExpandableListTitle.
                                        get(groupPosition).getName()).get(childPosition).getName();
                                /*might be unnecessary*/
                                String childPos = "";
                                if(childPosition == 0)childPos = "top";
                                else childPos += childPosition;
                                Log.d("DROP", "DROPPED " + cardId + " ON " + child + " POS " +
                                        childPos +
                                        "IN " + groupId);


                                /*Places the dragged card ABOVE the card on which it is dropped
                                * i.e if you drag the TOP most card, to the second TOP most card,
                                * there will be no difference since the target card is pushed down
                                * in the list.
                                * */
                                mHandler.moveCardWithinList(cardId,childPos);
                            }
                            mExpandableListView.getChildAt(oldPos).
                                    setBackgroundColor(Color.TRANSPARENT);
                            mExpandableListView.getChildAt(oldPos).invalidate();
                        }
                        return true;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    /**
     * TODO EVALUATE IF THIS METHOD IS STILL NEEDED SOMEHOW
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
        final TrelloCard longClickedCard = mExpandableListOverview.get(
                mExpandableListTitle.get(groupPosition).getName()).get(
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
                            mHandler.removeCard(longClickedCard.getId());
                            updateListView(mExpandableListTitle.get(groupPosition));

                            break;
                        case 2:
                            /*Quickfix to dynamically display names*/
                            List<String> titles = new ArrayList<String>();
                            for(TrelloList tl:mExpandableListTitle){
                                titles.add(tl.getName());
                            }
                            CharSequence [] cs = titles.toArray(new CharSequence[titles.size()]);
                            new AlertDialog.Builder(BoardActivity.this,R.style.AlertDialogStyle)
                                    .setItems(cs, new DialogInterface.OnClickListener(){
                                     @Override
                                     public void onClick(DialogInterface dialog, int which) {
                                         String listId = mExpandableListTitle.get(which).getId();
                                         Log.d("CHILDLONGCLICK",
                                                 "Moved Card" + longClickedCard.getName()
                                                 + " to List "
                                                         + mExpandableListTitle.get(which).getName()
                                         );
                                         mHandler.moveCard(longClickedCard.getId(),listId);
                                         updateListView(mExpandableListTitle.get(which));
                                         updateListView(mExpandableListTitle.get(groupPosition));
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
     *  mExpandableListView
     *
     *  Generates an AlertDialog to show popup with alternatives for actions
     *  (0)Add Card
     *  (1)...
     *
     *
     *  Add Card generates a new AlertDialog where the user can enter
     *  cardTitle and cardDescription for a new card and is prompted to confirm or cancel
     *  the creation.
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
                                final View view = getLayoutInflater().inflate
                                        (R.layout.add_card, null);
                                final EditText cardTitle = (EditText) view.findViewById
                                        (R.id.cardTitle);
                                final EditText cardDescription = (EditText) view.findViewById
                                        (R.id.cardDescription);

                                new AlertDialog.Builder(BoardActivity.this,
                                        R.style.AlertDialogStyle)
                                        .setView(view)
                                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                mHandler.addCard(cardTitle.getText().toString(),
                                                        cardDescription.getText().toString(),
                                                    mExpandableListTitle.get(groupPosition).
                                                            getId());

                                                //update view (Todo: outsource this)
                                                updateListView(mExpandableListTitle.
                                                        get(groupPosition));
                                                //mExpandableListView.invalidate();
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
        mHandler.fetchCards(list.getId(), list.getName());
    }

    /**
     * Initiates the mAdapter with data fields
     * @param listNames
     */
    private void initAdapter(List<TrelloList> listNames) {
        /*mTrelloLists and expandableListtitle will point to same, fix later*/
        mExpandableListTitle = listNames;
        mAdapter = new CustomExpandableListAdapter(this,mExpandableListTitle,
                mExpandableListOverview);
        mExpandableListView.setAdapter(mAdapter);
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
            mHandler.logout();
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            //Pass the mBoardId to new activity
            startActivity(login);
            return true;
        }

        if (id == R.id.button_view_members)
        {
            Intent intent = new Intent(BoardActivity.this, MembersBoardActivity.class);

            intent.putExtra(BoardActivity.BOARD_MEMBERS_EXTRA_ID, this.mBoardId);
            intent.putExtra(BoardActivity.BOARD_MEMBERS_EXTRA_NAME, this.mBoardName);

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
            String mBoardId = params[0];
            try {
                mHandler.fetchLists(mBoardId);
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

                mHandler.fetchCards(listId,listName);
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
     * @param mBoardId board the lists belong to
     */
    @Override
    public void listsCallback(List<TrelloList> lists, String mBoardId) {
        mTrelloLists = lists;
        for(TrelloList tl:lists){
            new GenerateCardsTask().execute(tl.getId(),tl.getName());
        }
        initAdapter(mTrelloLists);
    }

    /**
     * Callback from ApiHandler, sets the listName's List<TrelloCard>
     *
     * with cards. Tells the mAdapter that the dataset has been updated so that the
     * mExpandableListView can update
     *
     * @param cards fetched cards from trello
     * @param listName name of the lists where the cards belong
     */
    @Override
    public void cardsCallback(List<TrelloCard> cards, String listName) {
        for(TrelloList tl:mTrelloLists){
            if(tl.getName().equals(listName)){
                tl.setCards(cards);
                mExpandableListOverview.put(tl.getName(), tl.getCards());
            }
        }
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void membersBoardCallback(List<TrelloMember> members) {

    }


}
