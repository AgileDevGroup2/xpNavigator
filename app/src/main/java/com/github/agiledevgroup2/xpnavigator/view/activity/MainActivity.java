package com.github.agiledevgroup2.xpnavigator.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.codepath.oauth.OAuthLoginActionBarActivity;

import java.util.List;


import android.widget.RelativeLayout;
import android.widget.TextView;

//Library stuff
import com.github.agiledevgroup2.xpnavigator.controller.ApiHandler;
import com.github.agiledevgroup2.xpnavigator.controller.ApiListener;
import com.github.agiledevgroup2.xpnavigator.R;
import com.github.agiledevgroup2.xpnavigator.model.TrelloBoard;
import com.github.agiledevgroup2.xpnavigator.model.TrelloBoardMembers;
import com.github.agiledevgroup2.xpnavigator.model.TrelloCard;
import com.github.agiledevgroup2.xpnavigator.model.TrelloClient;
import com.github.agiledevgroup2.xpnavigator.model.TrelloList;

//JSON


/**
 * This class represents the apps main activity
 * TODO: might rename this class to something like "MainActivity"...
 */
public class MainActivity  extends OAuthLoginActionBarActivity<TrelloClient> implements ApiListener {

    public final static String BOARD_EXTRA_ID = "BOARD_ID";
    public final static String BOARD_EXTRA_NAME = "BOARD_NAME";

    private ApiHandler handler;

    LinearLayout linearLayout;
    RelativeLayout relativeLayout;
    ImageView imageView;
    TextView textView;
    RelativeLayout.LayoutParams lp;
    LinearLayout.LayoutParams llparam;
    Toolbar toolbar;
    Button button;


    /**
     * creates the view and initiates the login dialog
     *
     * @param savedInstanceState saved data to restore view (e.g. after rotating the phone)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // invoke
        showLogo();

        setupBackgroundColor();
        setUpImageView();
        setTextViewGreeting();
        addListenerOnButtonClick();

    }

    /**
     *  Show the APP logo
     */
    public void showLogo(){

        // enabling action bar app icon and behaving it as toggle button
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        this.getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    /**
     *  Add Listener on button
     *  Also Check the xml file for sahpe_button.xml in xml folder
     *  for changes
     */
    public void addListenerOnButtonClick(){

        final Context context = this;

        button =   ((Button) findViewById(R.id.login_button));
        if (button != null) {
            button.setText("login");
            //button.setBackgroundColor(Color.rgb(111,168,220));
            button.setTextColor(Color.WHITE);
            button.setAllCaps(true);
            button.setTextAppearance(android.R.style.TextAppearance_Medium);
            button.setTextSize((float) 28);
            button.setTypeface(button.getTypeface(), Typeface.NORMAL);

            //set margin and padding
            lp = new RelativeLayout.LayoutParams(textView.getLayoutParams());
            lp.setMargins(270, 1150, 50, 90); //left, top, right, bottom
            button.setLayoutParams(lp);
            button.setPaddingRelative(10, 10, 10, 10);

            //set button width and height in px.
            button.getLayoutParams().width = Integer.parseInt("450");
            button.getLayoutParams().height = Integer.parseInt("170");
        }

        ((Button) findViewById(R.id.login_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getClient().connect();
            }
        });
        handler = new ApiHandler(this);



                // move to HomeActivity or another page
                // Intent goToNextActivity = new Intent(context, BoardListActivity.class);
                 //startActivity(goToNextActivity);

    }


    /**
     *  Text
     */

    public void setTextViewGreeting(){

        textView = (TextView) findViewById(R.id.greetingTrello);
        textView.setText("WELCOME TO TRELLO!");
        textView.setTextAppearance(android.R.style.TextAppearance_Large);
        textView.setEms(5);
        textView.setAllCaps(true);
        textView.setTextColor(Color.rgb(26,92,129));
        textView.setTextSize((float) 28);
        textView.setTypeface(textView.getTypeface(), Typeface.NORMAL);
        textView.setTextAppearance(View.TEXT_ALIGNMENT_CENTER);


        if (textView != null) {
        //set margin and padding
        lp = new RelativeLayout.LayoutParams(textView.getLayoutParams());
        lp.setMargins(120, 980, 80, 90); //left, top, right, bottom
        textView.setLayoutParams(lp);
        textView.setPaddingRelative(5, 10, 10, 10);

        //set button width and height in px.
        textView.getLayoutParams().width = Integer.parseInt("800");
        textView.getLayoutParams().height=Integer.parseInt("200");

         }
    }


    /**
     *  Image view setting
     *  Using LinearLayout to set a fix img
     */
    public void  setUpImageView() {

        imageView = (ImageView) findViewById(R.id.imageView);

        if (imageView != null) {
            imageView.setImageResource(R.drawable.taco1);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setBaselineAlignBottom(true);

            // set the Max Height and Width
            imageView.setMaxHeight(370);
            imageView.setMaxWidth(358);

            // Set margins and padding
            llparam = new LinearLayout.LayoutParams(imageView.getLayoutParams());
            llparam.setMargins(30, 90, 30, 40);
            imageView.setLayoutParams(llparam);
            imageView.getWidth();
            imageView.setPaddingRelative(10, 10, 10, 10);
            imageView.getAdjustViewBounds();
        }
    }

    /**
     * Set background
     */

    public void setupBackgroundColor() {
        // set background for the application
        View view = findViewById(R.id.content);
        assert view != null;
        view.setBackgroundColor(Color.rgb(240,246,250));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            handler.logout();
            recreate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method for successful login
     */
    @Override
    public void onLoginSuccess() {
        initMainLayout();
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

    /**
     * Method to initiate the main layout
     */
    protected void initMainLayout() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initiateBoards();

    }

    protected void initiateBoards(){
        //JSONArray boards = api.getBoards(); // see test
        handler.fetchBoards();
    }


    /**
     * Adding buttons for each board dynamically
     *
     * @param buttonText The name of the board received from the JSON "name"
     */
    public void addBoardButton(String buttonText, String boardId) {

         /*Generate a button for each TrelloBoard*/
        final Button boardButton = new Button(getApplicationContext());
        final String id = boardId;
        final String nameBoard = buttonText;


        RelativeLayout btnLayout = (RelativeLayout) findViewById(R.id.buttonLayout);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        btnLayout.addView(boardButton, lp);
        boardButton.setText(boardButton.getText() + " " + buttonText);


     /*   linearLayout = (LinearLayout) findViewById(R.id.buttonLayout);

        LinearLayout.LayoutParams linearParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        linearLayout.addView(boardButton, linearParam);
        boardButton.setText(boardButton.getText() + " " + buttonText); */



        /*Event Handler for each boardButton*/
        boardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*Launch new Activity for the clicked board*/
                Intent clickedBoard = new Intent(getApplicationContext(), BoardActivity.class);
                //Pass the BoardID to new activity
                clickedBoard.putExtra(BOARD_EXTRA_ID, id);
                clickedBoard.putExtra(BOARD_EXTRA_NAME, nameBoard);
                startActivity(clickedBoard);

            }
        });

    }


    @Override
    public void boardsCallback(List<TrelloBoard> boards) {
        for(TrelloBoard b:boards){
            addBoardButton(b.getName(),b.getId());
        }

    }

    @Override
    public void listsCallback(List<TrelloList> lists, String boardId) {

    }

    @Override
    public void cardsCallback(List<TrelloCard> cards, String listId) {

    }

    @Override
    public void membersBoardCallback(TrelloBoardMembers boardMembers) {

    }


}