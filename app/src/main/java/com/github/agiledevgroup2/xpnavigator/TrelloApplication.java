package com.github.agiledevgroup2.xpnavigator;

import android.content.Context;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 *
 *     RestClient client = TrelloApplication.getRestClient();
 *     // use client to send requests to API
 *
 */
public class TrelloApplication extends com.activeandroid.app.Application {
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		TrelloApplication.context = this;
	}

	public static RestClient getRestClient() {
		return (RestClient) RestClient.getInstance(RestClient.class, TrelloApplication.context);
	}
}