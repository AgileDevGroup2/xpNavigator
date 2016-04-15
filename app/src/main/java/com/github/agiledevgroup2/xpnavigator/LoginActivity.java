package com.github.agiledevgroup2.xpnavigator;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import org.apache.http.Header;

import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoginActivity  extends OAuthLoginActionBarActivity<TrelloClient> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ((Button) findViewById(R.id.login_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getClient().connect();
            }
        });
    }


    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    // OAuth authenticated successfully, launch primary authenticated activity
    // i.e Display application "homepage"
    @Override
    public void onLoginSuccess() {
       initMainLayout();
    }

    // OAuth authentication flow failed, handle the error
    // i.e Display an error dialog or toast
    @Override
    public void onLoginFailure(Exception e) {
        System.out.println("err");
        e.printStackTrace();
    }

    protected void initMainLayout() {
        setContentView(R.layout.activity_main);

        test();
    }

    protected void test() {
        // SomeActivity.java
        TrelloClient client = TrelloApplication.getRestClient();
        client.getBoards(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode,
                                  Header[] headers,
                                  JSONObject response) {
                System.out.println("Json Object: ");
                System.out.println(response.toString());
            }
            @Override
            public void onSuccess(int statusCode,
                                  Header[] headers,
                                  JSONArray response) {
                System.out.println("Json Array: ");
                System.out.println(response.toString());
            }

            @Override
            public void onFailure(int statusCode,
                                  Header[] headers,
                                  java.lang.String responseString,
                                  java.lang.Throwable throwable){
                System.out.println("failed: " + responseString);
            }

            @Override
            public void onFailure(int statusCode,
                                  Header[] headers,
                                  java.lang.Throwable throwable,
                                  JSONObject response){
                System.out.println("failed: " + throwable.toString());
            }
        });
    }

}
