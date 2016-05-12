package com.github.agiledevgroup2.xpnavigator.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;

//Library stuff
import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.github.agiledevgroup2.xpnavigator.R;
import com.github.agiledevgroup2.xpnavigator.model.Timer;
import com.github.agiledevgroup2.xpnavigator.model.TrelloClient;
import com.github.agiledevgroup2.xpnavigator.view.adapter.DialogBuilder;

/**
 * This class represents the apps main activity
 * TODO: might rename this class to something like "MainActivity"...
 */
public class LoginActivity  extends OAuthLoginActionBarActivity<TrelloClient> {

    public final static String TAG = "LoginActivity";

    /**
     * creates the view and initiates the login dialog
     *
     * @param savedInstanceState saved data to restore view (e.g. after rotating the phone)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((Button) findViewById(R.id.login_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getClient().connect();
            }
        });
        Timer.setContext(getApplicationContext());
    }


    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_timer:
                new DialogBuilder().createTimerDialog(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Callback method for successful login
     */
    @Override
    public void onLoginSuccess() {
        Intent boardList = new Intent(getApplicationContext(), BoardListActivity.class);

        startActivity(boardList); //start board list activity
    }

    /**
     * Callback method if login failed TODO: remove Generic Exception
     *
     * @param e cause of the error
     */
    @Override
    public void onLoginFailure(Exception e) {
        System.out.println("err");
        e.printStackTrace();
    }
}