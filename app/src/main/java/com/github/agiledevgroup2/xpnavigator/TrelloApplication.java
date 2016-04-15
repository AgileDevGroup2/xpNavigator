package com.github.agiledevgroup2.xpnavigator;

import android.content.Context;

/**
 * Class Handles the OAuth communication. <br/>
 *
 * TrelloClient client = TrelloApplication.getTrelloClient(); *
 */
public class TrelloApplication extends com.activeandroid.app.Application {
	private static Context context;

    /**
     * initiates the Trello application
     */
	@Override
	public void onCreate() {
		super.onCreate();
		TrelloApplication.context = this;
	}

	/**
	 * Method to get a trello client
	 * @return trello client to use endpoint calls
	 */
	public static TrelloClient getTrelloClient() {
		return (TrelloClient) TrelloClient.getInstance(TrelloClient.class, TrelloApplication.context);
	}
}