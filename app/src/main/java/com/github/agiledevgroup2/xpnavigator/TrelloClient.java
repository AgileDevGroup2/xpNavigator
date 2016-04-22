package com.github.agiledevgroup2.xpnavigator;

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
     * @param handler http handler handler to process the endpoints response
     */
    public void getBoards(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("1/members/me/boards");
        Log.d("ClIENT_GETBOARDS", apiUrl);
        RequestParams params = new RequestParams();
        client.get(apiUrl, params, handler);
    }

    /**
     * Endpoint to get all Lists of a board
     * @param boardId boards id to fetch the lists from
     * @param handler http handler handler to process the endpoints response
     */
    public void getLists(String boardId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("1/boards/" + boardId + "/lists");
        Log.d("ClIENT_GETLIST", apiUrl);

        RequestParams params = new RequestParams();
        client.get(apiUrl, params, handler);
    }


    /**
     * Endpoint to get all cards of a board
     * @param boardId boards id to fetch the cards from
     * @param handler http handler handler to process the endpoints response
     */
    public void getCardsByBoard(String boardId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("1/boards/" + boardId + "/cards");
    }

    /**
     * Endpoint to get all cards of a list
     * @param listId lists id to fetch the cards from
     * @param handler http handler handler to process the endpoints response
     */
    public void getCards(String listId, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("1/lists/" + listId + "/cards");
        Log.d("ClIENT_GETCARDS", apiUrl);
        RequestParams params = new RequestParams();
        Log.d(TAG,("GETTING_CARDS_IN_CLIENT"));
        client.get(apiUrl,params,handler);
    }

}