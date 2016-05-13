package com.github.agiledevgroup2.xpnavigator.controller;

import android.os.AsyncTask;
import android.util.Log;

import com.github.agiledevgroup2.xpnavigator.other.ThreadIndependentLock;
import com.github.agiledevgroup2.xpnavigator.model.TrelloApplication;
import com.github.agiledevgroup2.xpnavigator.model.TrelloBoard;
import com.github.agiledevgroup2.xpnavigator.model.TrelloBoardMembers;
import com.github.agiledevgroup2.xpnavigator.model.TrelloCard;
import com.github.agiledevgroup2.xpnavigator.model.TrelloList;
import com.github.agiledevgroup2.xpnavigator.model.TrelloMember;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * Api handler providing methods for fetching data from trello and provides callbacks.
 */
public class ApiHandler extends JsonHttpResponseHandler {

    private static final String TAG = "ApiHandler";
    private static final ApiHandler SELF = new ApiHandler();
    protected enum State {NONE, BOARD, LIST, CARD, MEMBER, ORGANIZATION, NAME_TEAM, PUSH}

    protected State mCurState;

    private String mLName;

    protected ApiListener mListener;

    protected Lock mLock;

    private String mlastBoardId;

    TrelloBoardMembers boardMembers;

    /**
     * creates a new ApiHandler
     */
    private ApiHandler() {
        mListener = null;
        mCurState = State.BOARD;
        mLock = new ThreadIndependentLock();
    }

    /**
     * Change the api listener of this handler
     * @param listener new ApiListener
     */
    public static void setListener(ApiListener listener) {
        SELF.mListener = listener;
    }

    /**
     * Fetch boards, callback will be send to ApiListener.setBoards
     */
    public static void fetchBoards() {
        SELF.pFetchBoards();
    }

    private void pFetchBoards() {
        new AsyncExec(State.BOARD) {
            @Override
            protected void task(String... data) {
                TrelloApplication.getTrelloClient().getBoards(ApiHandler.this);
            }
        }.execute();
    }


    /**
     * Fetch Lists from a specific board, callback will be send to ApiListener.setLists
     * @param boardId board's id the lists should be fetched from
     */
    public static void fetchLists(String boardId) {
        SELF.pFetchLists(boardId);
    }

    private void pFetchLists(String boardId) {
        new AsyncExec(State.LIST) {
            @Override
            protected void task(String... data) {
                TrelloApplication.getTrelloClient().getLists(data[0], ApiHandler.this);
            }
        }.execute(boardId);
    }

    /**
     * Fetch Cards from a specific list, callback will be send to ApiListener.SetCards
     * @param listId list's id the cards should be fetched from
     */
    public static void fetchCards(String listId, String listName) {
        SELF.pFetchCards(listId, listName);
    }

    private void pFetchCards(String listId, String listName) {
        new AsyncExec(State.CARD) {
            @Override
            protected void task(String... data) {
                mLName = data[1];
                TrelloApplication.getTrelloClient().getCards(data[0], ApiHandler.this);
            }
        }.execute(listId, listName);
    }

    /**
     * Fetch datas of members after getting the organization
     * Wde have a callback after this to the Api listener
     */
    public static void fetchMembers() {
        SELF.pFetchMembers();
    }

    private void pFetchMembers() {

        new AsyncExec(State.MEMBER) {
            @Override
            protected void task(String... data) {
                TrelloApplication.getTrelloClient().getMembers(data[0], ApiHandler.this);
            }
        }.execute(mlastBoardId);
        //todo fix mlastBoardId! this should be given by a function parameter, not as an attribute!

    }

    /**
     * It's doing a request to the TrelloClient to get the organization of a board
     * @param boardId is the corresponding id of the board
     */
    public static void fetchOrganization(String boardId) {
        SELF.pFetchOrganization(boardId);
    }

    private void pFetchOrganization(String boardId) {

        new AsyncExec(State.ORGANIZATION) {
            @Override
            protected void task(String... data) {
        //todo fix mlastBoardId! this should be given by a function parameter, not as an attribute!
                mlastBoardId = data[0];
                TrelloApplication.getTrelloClient().getOrganization(data[0], ApiHandler.this);
            }
        }.execute(boardId);

    }

    /**
     * It's doing a request to the TrelloClient ti get the name of a team from a board
     * @param boardId is the corresponding id
     */
    public static void fetchNameBoardTeam(String boardId) {
        SELF.pFetchNameBoardTeam(boardId);
    }

    private void pFetchNameBoardTeam(String boardId) {

        new AsyncExec(State.NAME_TEAM) {
            @Override
            protected void task(String... data) {
                //todo fix mlastBoardId! this should be given by a function parameter, not as an attribute!
                mlastBoardId = data[0];
                TrelloApplication.getTrelloClient().getNameBoardTeam(data[0], ApiHandler.this);
            }
        }.execute(boardId);

    }

    /**
     * Add a Card to Trello, callback is currently ignored TODO: change that
     * @param name name of the new card
     * @param desc description of the new card
     * @param listId the list associated with the new card
     */
    public static void addCard(String name, String desc, String listId) {
        SELF.pAddCard(name, desc, listId);
    }

    private void pAddCard(String name, String desc, String listId) {
        new AsyncExec() {
            @Override
            protected void task(String... data) {
                TrelloApplication.getTrelloClient()
                        .addCard(data[0], data[1], data[2], ApiHandler.this);
            }
        }.execute(name, desc, listId);
    }

    /**
     * Add a Card to Trello, callback is currently ignored TODO: change that
     * @param card new Card (name and listId have to be set!)
     */
    public static void addCard(TrelloCard card) {
        addCard(card.getName(), card.getDesc(), card.getListId());
    }

    /**
     * Remove a Card from Trello, callback is currently ignored TODO: change that
     * @param cardId id of card to remove from Trello
     */
    public static void removeCard(String cardId) {
        SELF.pRemoveCard(cardId);
    }

    private void pRemoveCard(String cardId) {
        new AsyncExec() {
            @Override
            protected void task(String... data) {
                TrelloApplication.getTrelloClient().removeCard(data[0], ApiHandler.this);
            }
        }.execute(cardId);
    }

    /**
     * Remove a Card from Trello, callback is currently ignored TODO: change that
     * @param card card to remove from Trello
     */
    public static void removeCard(TrelloCard card) {
        removeCard(card.getId());
    }

    /**
     * Move a card to another list, callback is currently ignored TODO: change that
     * @param cardId id of card to move
     * @param listId id of list to move card to
     */
    public static void moveCard(String cardId, String listId) {
        SELF.pMoveCard(cardId, listId);
    }

    private void pMoveCard(String cardId, String listId) {
        new AsyncExec() {
            @Override
            protected void task(String... data) {
                TrelloApplication.getTrelloClient().moveCard(data[0], data[1], ApiHandler.this);
            }
        }.execute(cardId, listId);
    }

    /**
     * Move a card within a list, placing it above another element in order to
     * give cards a prioritized order
     * callback is currently ignored TODO: change that
     * @param cardId id of card to move
     * @param position position in the list to move to
     */
    public static void moveCardWithinList(String cardId,String position){
        SELF.pMoveCardWithinList(cardId, position);
    }

    private void pMoveCardWithinList(String cardId,String position){

        new AsyncExec() {
            @Override
            protected void task(String... data) {
                TrelloApplication.getTrelloClient().moveCardWithinList(data[0], data[1], ApiHandler.this);
            }
        }.execute(cardId,position);
    }

    /**
     * Move a card to another list, callback is currently ignored TODO: change that
     * @param card card to move
     * @param list list to move card to
     */
    public static void moveCard(TrelloCard card, TrelloList list) {
        moveCard(card.getId(), list.getId());
    }

    /**
     * update a trello card
     * @param cardId id of card to update
     * @param name (new) name of card
     * @param desc (new) description of the card
     * @param listId (new) id of list the card belongs to
     */
    public static void updateCard(String cardId, String name, String desc, String listId) {
        SELF.pUpdateCard(cardId, name, desc, listId);
    }

    private void pUpdateCard(String cardId, String name, String desc, String listId) {
        new AsyncExec() {
            @Override
            protected void task(String... data) {
                TrelloApplication.getTrelloClient().updateCard(data[0], data[1], data[2]
                        , data[3], ApiHandler.this);
            }
        }.execute(cardId, name, desc, listId);
    }

    /**
     * update a trello card
     * @param card card to update
     */
    public static void updateCard(TrelloCard card) {
        updateCard(card.getId(), card.getName(), card.getDesc(), card.getListId());
    }
    /**
     * Failure handler if only one JSONObject is received
     * @param statusCode should be exactly what it sounds like...
     * @param headers http headers (not necessary)
     * @param response JSON Object containing the received data
     */
    @Override
    @SuppressWarnings("deprecation")
    public void onSuccess(int statusCode,
                          Header[] headers,
                          JSONObject response) {
        //cancel if null-response
        if (response == null) {
            handleFailure("JSONObject is null object");
            requestFinished();
            return;
        }

        if (mCurState == State.NAME_TEAM) {
            handleNameTeamBoard(response);
            mLock.unlock();
        }
        else {
            try {
                onSuccess(statusCode, headers, response.getJSONArray(""));
            } catch (JSONException e) {
                handleFailure(e.getMessage());
                mLock.unlock();
            }
        }
    }

    /**
     * Failure handler if a JSON Array is received
     * @param statusCode should be exactly what it sounds like...
     * @param headers http headers (not necessary)
     * @param response JSON Array containing the received data
     */
    @Override
    @SuppressWarnings("deprecation")
    public void onSuccess(int statusCode,
                          Header[] headers,
                          JSONArray response) {
        //cancel if null-response
        if (response == null) {
            handleFailure("JSONArray is null object");
        } else {

            switch (mCurState) {
                case BOARD:
                    handleBoard(response);
                    break;
                case LIST:
                    handleLists(response);
                    break;
                case CARD:
                    handleCards(response);
                    break;
                case MEMBER:
                    handleMembers(response);
                    break;
                case ORGANIZATION:
                    handleOrganization(response);
                    break;
                default:
                    Log.d(TAG, "should refresh");
            }
        }
        requestFinished();
    }



    /**
     * Failure handler with string response
     * @param statusCode should be exactly what it sounds like...
     * @param headers http headers (not necessary)
     * @param responseString explanation why it failed
     */
    @Override
    @SuppressWarnings("deprecation")
    public void onFailure(int statusCode,
                          Header[] headers,
                          java.lang.String responseString,
                          java.lang.Throwable throwable) {
        handleFailure(responseString);
        requestFinished();
    }

    /**
     * Failure handler with JSON response
     * @param statusCode should be exactly what it sounds like...
     * @param headers http headers (not necessary)
     * @param response explanation why it failed
     */
    @Override
    @SuppressWarnings("deprecation")
    public void onFailure(int statusCode,
                          Header[] headers,
                          java.lang.Throwable throwable,
                          JSONObject response) {
        handleFailure(throwable.getMessage());
        requestFinished();
    }

    /**
     * handle fetching failures
     * @param message error message
     */
    protected void handleFailure(String message) {
        Log.e(TAG, message);

        if (mCurState == State.NAME_TEAM)
        {
            if (mListener != null) mListener.failureCallback(message);
        }
        requestFinished();
    }

    /**
     * creates a list of boards and sends it to the listeners callback method for bords
     * @param jsonArray json array given by the api call
     */
    protected void handleBoard(JSONArray jsonArray) {
        //itterate over all objects of the json array
        List<TrelloBoard> boards = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                boards.add(new TrelloBoard(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                Log.v(TAG, e.getMessage());
            }
        }

        //use listener callback
        if (mListener != null) mListener.boardsCallback(boards);
    }

    private void handleNameTeamBoard(JSONObject response) {

        Log.d("test55", "toto");
        String name = "";

        try {
            name = response.getString("_value");
        } catch (JSONException e) {
            Log.v(TAG, e.getMessage());
        }

        //use listener callback
        if (mListener != null) mListener.nameBoardTeamCallback(name);
    }

    protected void requestFinished() {
        mCurState = State.NONE;
        mLock.unlock();
    }

    /**
     * creates a list of trello lists and sends it to the listeners callback method for lists
     * @param jsonArray json array given by the api call
     */
    protected void handleLists(JSONArray jsonArray) {
        //itterate over all objects of the json array
        List<TrelloList> lists = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                lists.add(new TrelloList(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                Log.v(TAG, e.getMessage());
            }
        }

        //use listener callback
        if (mListener != null) mListener.listsCallback(lists,"");

    }

    protected void handleMembers (JSONArray jsonArray) {
        Log.d("test24", "ok");
        for(int i = 0 ; i < jsonArray.length(); i++)
        {

            try {
                boardMembers.addMember(new TrelloMember(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                Log.v(TAG, e.getMessage());
            }
        }

        if (boardMembers != null)
            Log.d("test8", String.valueOf(boardMembers.getmListMembers().size()));
        else
            Log.d("test8", "ERROR");

        if (mListener != null) mListener.membersBoardCallback(boardMembers);
    }

    protected void handleOrganization(JSONArray jsonArray) {

        boardMembers = new TrelloBoardMembers(mlastBoardId);

        try {
            boardMembers.setOrganization(jsonArray);
        } catch (JSONException e) {
            Log.v(TAG, e.getMessage());
        }

        mLock.unlock();
        this.fetchMembers();
    }

    /**
     * creates a list of cards and sends it to the listeners callback method for cards
     * @param jsonArray json array given by the api call
     */
    protected void handleCards(JSONArray jsonArray) {
        //itterate over all objects of the json array
        List<TrelloCard> cards = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                cards.add(new TrelloCard(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                Log.v(TAG, e.getMessage());
            }
        }

        //use listener callback
        if (mListener != null) mListener.cardsCallback(cards, mLName);
    }

    /**
     * logout from Trello
     */
    public static void logout() {
        TrelloApplication.getTrelloClient().clearAccessToken();
    }

    /**
     * Abstract class for asynchronous tasks
     */
    private abstract class AsyncExec extends AsyncTask<String, Void, Void> {
       private State lockedState;
        public AsyncExec() {
            lockedState = State.NONE;
        }
        public AsyncExec(State lockedState) {
            this.lockedState = lockedState;
        }
        protected Void doInBackground(String... data) {
            mLock.lock();
            mCurState = lockedState;
            task(data);
            return null;
        }
        protected void onProgressUpdate(Void... v) { }
        protected void onPostExecute(Void v) { }

        protected abstract void task(String... data);
    }
}

