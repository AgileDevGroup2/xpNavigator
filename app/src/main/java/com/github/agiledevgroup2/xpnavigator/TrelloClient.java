package com.github.agiledevgroup2.xpnavigator;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TrelloApi;

import android.content.Context;

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
        System.out.println("Api Url: " + apiUrl);
        RequestParams params = new RequestParams();
        client.get(apiUrl, params, handler);
    }

    public void getLists(String boardId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("1/boards/" + boardId + "/lists");
        System.out.println("Api Url: " + apiUrl);
        RequestParams params = new RequestParams();
        client.get(apiUrl, params, handler);
    }

    public void getCardsByBoard(String boardId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("1/boards/" + boardId + "/cards");
    }

    public void getCards(String listId, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("1/lists/" + listId + "/cards");
        RequestParams params = new RequestParams();
        client.get(apiUrl,params,handler);
    }

}