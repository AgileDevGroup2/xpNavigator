package com.github.agiledevgroup2.xpnavigator;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

//Library stuff
import org.apache.http.Header;
import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

//JSON
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class represents the apps main activity
 * Todo: might rename this class to something like "MainActivity"...
 */
public class LoginActivity  extends OAuthLoginActionBarActivity<TrelloClient> {

    /**
     * creates the view and initiates the login dialog Todo: Statemachine for views?
     * @param savedInstanceState saved data to restore view (e.g. after rotating the phone)
     */
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

    /**
     * Callback method for successful login
     */
    @Override
    public void onLoginSuccess() {
       initMainLayout();
    }

    /**
     * Callback method if login failed Todo: remove Generic Exception
     * @param e cause of the error
     */
    @Override
    public void onLoginFailure(Exception e) {
        System.out.println("err");
        e.printStackTrace();
    }

    /**
     * Method to initiate the main layout Todo: maybe using a state machine for different layouts?
     */
    protected void initMainLayout() {
        setContentView(R.layout.activity_main);

        test();
    }

    /**
     * This is only a test, should move such calls into another class
     * The test fetches a users groups and displays them on the display as text items
     */
    protected void test() {
        // SomeActivity.java
        TrelloClient client = TrelloApplication.getTrelloClient();
        client.getBoards(new JsonHttpResponseHandler() {

            /**
             * Failure handler if only one JSONObject is received
             * @param statusCode should be exactly what it sounds like...
             * @param headers http headers (not necessary)
             * @param response JSON Object containing the received data
             */
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

            /**
             * Failure handler if a JSON Array is received
             * @param statusCode should be exactly what it sounds like...
             * @param headers http headers (not necessary)
             * @param response JSON Array containing the received data
             */
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

            /**
             * Failure handler with string response
             * @param statusCode should be exactly what it sounds like...
             * @param headers http headers (not necessary)
             * @param responseString explanation why it failed
             */
            @Override
            public void onFailure(int statusCode,
                                  Header[] headers,
                                  java.lang.String responseString,
                                  java.lang.Throwable throwable){
                System.out.println("failed: " + responseString);
            }

            /**
             * Failure handler with JSON response
             * @param statusCode should be exactly what it sounds like...
             * @param headers http headers (not necessary)
             * @param response explanation why it failed
             */
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
