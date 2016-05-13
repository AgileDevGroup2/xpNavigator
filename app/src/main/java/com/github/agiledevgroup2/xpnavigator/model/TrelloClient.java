package com.github.agiledevgroup2.xpnavigator.model;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TrelloApi;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * This class is responsible for communicating with the Trello API.
 */
public class TrelloClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TrelloApi.class;
    public static final String REST_URL = "https://api.trello.com";
    public static final String REST_CONSUMER_KEY = "81e692a4c7305deef90c59e2a79393bc";
    public static final String REST_CONSUMER_SECRET =
            "462a62c3b68ee9675470d94eb0cc903c9876efc3fe985ad24aeeb83470c9f00e";
    public static final String REST_CALLBACK_URL = "oauth://xpNavigator";
    private static final String TAG = "TrelloClient";
    /**
     * creates a new client
     * @param context the TrelloApplication
     */
    public TrelloClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET,
                REST_CALLBACK_URL);
    }

    //-------------------------
    // Endpoints from here on
    //-------------------------

    /**
     * Endpoint to get all boards of a user.
     * @param handler http handler to process the endpoints response
     */
    public void getBoards(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("1/members/me/boards");
        Log.d("ClIENT_GETBOARDS", apiUrl);
        client.get(apiUrl, handler);
    }

    /**
     * Endpoint to get all Lists of a board
     * @param boardId boards id to fetch the lists from
     * @param handler http handler to process the endpoints response
     */
    public void getLists(String boardId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("1/boards/" + boardId + "/lists");
        Log.d("ClIENT_GETLIST", apiUrl);

        client.get(apiUrl, handler);
    }

    public void getMembers (String boardId, AsyncHttpResponseHandler handler)
    {
        String apiUrl = getApiUrl("1/boards/" + boardId + "/members");
        Log.d("ClIENT_GETMEMBERS", apiUrl);

        client.get(apiUrl, handler);
    }

    public void getNameBoardTeam (String boardId, AsyncHttpResponseHandler handler)
    {
        String apiUrl = getApiUrl("1/boards/" + boardId + "/organization/displayName");
        Log.d("ClIENT_GETNAMEBOARDTEAM", apiUrl);

        client.get(apiUrl, handler);
    }

    public void getOrganization (String boardId, AsyncHttpResponseHandler handler)
    {
        String apiUrl = getApiUrl("1/boards/" + boardId + "/organization/memberships");
        Log.d("ClIENT_GETORGANIZATION", apiUrl);

        client.get(apiUrl, handler);
    }


    /**
     * Endpoint to get all cards of a board
     * @param boardId boards id to fetch the cards from
     * @param handler http handler to process the endpoints response
     */
    public void getCardsByBoard(String boardId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("1/boards/" + boardId + "/cards");
    }

    /**
     * Endpoint to get all cards of a list
     * @param listId lists id to fetch the cards from
     * @param handler http handler to process the endpoints response
     */
    public void getCards(String listId, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("1/lists/" + listId + "/cards");
        Log.d("ClIENT_GETCARDS", apiUrl);
        Log.d(TAG,("GETTING_CARDS_IN_CLIENT"));
        client.get(apiUrl, handler);
    }

    /**
     * Add a new card to a list
     * @param name name of the new card
     * @param desc description of the new card
     * @param listId list id the card should be added to
     * @param handler http handler to process the endpoints response
     */
    public void addCard(String name, String desc, String listId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("1/cards");

        //create parameter list
        RequestParams params = new RequestParams();
        params.put("name", name);
        if (!desc.isEmpty()) params.put("desc", desc);
        params.put("due", "null");
        params.put("idList", listId);

        client.post(apiUrl, params, handler);
    }

    /**
     * Delete a card from trello (only cards id is important)
     * @param cardId id of card to delete (only cards id is important)
     * @param handler http handler to process the endpoints response
     */
    public void removeCard(String cardId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("1/cards/" + cardId);

        client.delete(apiUrl, handler);
    }

    /**
     * Move card to another list
     * @param cardId id of card to move
     * @param listId id of list to move card to
     * @param handler http handler to process the endpoints response
     */
    public void moveCard(String cardId, String listId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("1/cards/" + cardId);
        RequestParams params = new RequestParams();
        params.put("idList", listId);

        client.put(apiUrl, params, handler);
    }

    /**
     * move card within a list
     * @param cardId id of card to move
     * @param position new position of the card
     * @param handler http handler to process the endpoints response
     */
    public void moveCardWithinList(String cardId, String position
            , AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("1/cards/" + cardId + "/pos");
        RequestParams params = new RequestParams();
        params.put("value", position);

        client.put(apiUrl, params, handler);
    }

    /**
     * update a card
     * @param cardId id of card to update
     * @param name (new) name of card
     * @param desc (new) description of the card
     * @param listId (new) id of list the card belongs to
     * @param handler http handler to process the endpoints response
     */
    public void updateCard(String cardId, String name, String desc, String listId
            , AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("1/cards/" + cardId);
        RequestParams params = new RequestParams();
        params.put("name", name);
        params.put("idList", listId);
        if (!desc.isEmpty()) params.put("desc", desc);

        client.put(apiUrl, params, handler);
    }
}