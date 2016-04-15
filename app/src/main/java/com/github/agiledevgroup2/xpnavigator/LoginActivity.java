package com.github.agiledevgroup2.xpnavigator;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.Header;

import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Iterator;

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

    /**
     * This is only a test, we should not do it that way...
     */
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

                TextView text = (TextView) findViewById(R.id.main_text);
                try {
                    text.setText(text.getText().toString() + response.getString("name") + "\n");
                } catch (JSONException e) {
                    System.err.println(e.toString());
                }
            }
            @Override
            public void onSuccess(int statusCode,
                                  Header[] headers,
                                  JSONArray response) {

                //itterate over all objects of the json array
                for (int i = 0; i < response.length(); i++) {
                    try {
                        // forward success call to handle each object separately
                        onSuccess(statusCode, headers, response.getJSONObject(i));
                    } catch (JSONException e) {
                        System.err.println(e.toString());
                    }
                }
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
