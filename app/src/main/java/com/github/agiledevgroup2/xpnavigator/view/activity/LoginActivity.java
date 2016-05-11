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
                createTimerDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * creates a new countdown timer dialog
     */
    public void createTimerDialog() {
        final AlertDialog.Builder timeDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.dialog_timer, null);

        //init dialog
        timeDialogBuilder.setTitle(getString(R.string.lbl_done));
        timeDialogBuilder.setView(dialogView);
        timeDialogBuilder.setPositiveButton(getString(R.string.lbl_done), null);

        NumberPicker pickers[] = {
                (NumberPicker) dialogView.findViewById(R.id.hours),
                (NumberPicker) dialogView.findViewById(R.id.minutes),
                (NumberPicker) dialogView.findViewById(R.id.seconds)};
        for (NumberPicker picker : pickers) {
            picker.setMinValue(0);
            picker.setMaxValue(59);

            picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    if (!Timer.isRunning()) {
                        NumberPicker pickers[] = {
                                (NumberPicker) dialogView.findViewById(R.id.hours),
                                (NumberPicker) dialogView.findViewById(R.id.minutes),
                                (NumberPicker) dialogView.findViewById(R.id.seconds)};
                        Timer.setTime(pickers[0].getValue(), pickers[1].getValue(), pickers[2].getValue());
                    }
                }
            });
        }
        //setup buttons
        ImageButton startB = (ImageButton) dialogView.findViewById(R.id.startButton);
        updateDialog(dialogView);
        startB.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                if (Timer.isRunning()) {
                    Timer.pause();
                } else {
                    Timer.start();
                }
                updateDialog(dialogView);
            }
        });
        ImageButton resetB = (ImageButton) dialogView.findViewById(R.id.resetButton);
        resetB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timer.reset();
                updateDialog(dialogView);
            }
        });

        //init values
        pickers[0].setValue(Timer.getHoursLeft());
        pickers[1].setValue(Timer.getMinutesLeft());
        pickers[2].setValue(Timer.getSecondsLeft());

        //register view
        Timer.registerView(dialogView);

        //create dialog
        AlertDialog timerDialog = timeDialogBuilder.create();
        timerDialog.show();
        timerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Timer.removeView();
            }
        });
    }

    /**
     * update the countdown timer dialogs elements <br>
     * <font style:color="red">Warning:</font> Use only the timer dialog!
     * @param dialogView dialog to update
     */
    @SuppressWarnings("deprecation")
    public void updateDialog(View dialogView) {
        ImageButton startB = (ImageButton) dialogView.findViewById(R.id.startButton);
        String resource = "android:drawable/ic_media_play";
        if (Timer.isRunning()) {
            resource = "android:drawable/ic_media_pause";
        }
        int identifier = getResources().getIdentifier(resource, null, null);
        startB.setImageDrawable(getResources().getDrawable(identifier));

        NumberPicker pickers[] = {
                (NumberPicker) dialogView.findViewById(R.id.hours),
                (NumberPicker) dialogView.findViewById(R.id.minutes),
                (NumberPicker) dialogView.findViewById(R.id.seconds)};
        for (NumberPicker picker : pickers) {
            if (Timer.isRunning()) picker.setEnabled(false);
            else picker.setEnabled(true);
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