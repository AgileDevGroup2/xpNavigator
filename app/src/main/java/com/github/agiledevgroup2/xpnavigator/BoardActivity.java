package com.github.agiledevgroup2.xpnavigator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BoardActivity extends AppCompatActivity {
    private String boardId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);




        Intent previousIntent = getIntent();
        boardId = previousIntent.getStringExtra(LoginActivity.BOARD_EXTRA_ID);
        /*Check that boardID was transferred*/
        Toast.makeText(getApplicationContext(),boardId, Toast.LENGTH_SHORT).show();
        test();

    }

    /*ADDED JUST TO SEE IF IT WORKS*/
    protected void test() {
        // SomeActivity.java
        TrelloClient client = TrelloApplication.getTrelloClient();
        client.getLists(boardId,new JsonHttpResponseHandler() {

            /**
             * Failure handler if only one JSONObject is received
             * @param statusCode should be exactly what it sounds like...
             * @param headers http headers (not necessary)
             * @param response JSON Object containing the received data
             */
            @Override
            @SuppressWarnings("deprecation")
            public void onSuccess ( int statusCode,
                                    Header[] headers,
                                    JSONObject response){
                System.out.println("Json Object: ");
                System.out.println(response.toString());


                /*Change this later on*/
                TextView text = (TextView) findViewById(R.id.cardTextView);
                try {
                    text.setText(text.getText().toString() + response.getString("name") + "\n");

                } catch (JSONException e) {
                    System.err.println(e.toString());
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
            public void onSuccess ( int statusCode,
                                    Header[] headers,
                                    JSONArray response){

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

            /**
             * Failure handler with string response
             * @param statusCode should be exactly what it sounds like...
             * @param headers http headers (not necessary)
             * @param responseString explanation why it failed
             */
            @Override
            @SuppressWarnings("deprecation")
            public void onFailure ( int statusCode,
                                    Header[] headers,
                                    java.lang.String responseString,
                                    java.lang.Throwable throwable){
                System.out.println("FAILURE: " + responseString);
            }

            /**
             * Failure handler with JSON response
             * @param statusCode should be exactly what it sounds like...
             * @param headers http headers (not necessary)
             * @param response explanation why it failed
             */
            @Override
            @SuppressWarnings("deprecation")
            public void onFailure ( int statusCode,
                                    Header[] headers,
                                    java.lang.Throwable throwable,
                                    JSONObject response){
                System.out.println("FAILURE: " + throwable.toString());
            }
        });
    }

}
