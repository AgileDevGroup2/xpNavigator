package com.github.agiledevgroup2.xpnavigator;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TrelloApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 */
public class TrelloClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TrelloApi.class; // Change this
	public static final String REST_URL = "https://api.trello.com"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "81e692a4c7305deef90c59e2a79393bc";       // Change this
	public static final String REST_CONSUMER_SECRET = "462a62c3b68ee9675470d94eb0cc903c9876efc3fe985ad24aeeb83470c9f00e"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://xpNavigator"; // Change this (here and in manifest)

	public TrelloClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getBoards(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("1/members/me/boards");
        System.out.println("Api Url: " + apiUrl);
        RequestParams params = new RequestParams();
        client.get(apiUrl, params, handler);
	}


}