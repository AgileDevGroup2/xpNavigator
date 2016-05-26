package com.github.agiledevgroup2.xpnavigator.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.github.agiledevgroup2.xpnavigator.R;
import com.github.agiledevgroup2.xpnavigator.controller.ApiHandler;
import com.github.agiledevgroup2.xpnavigator.model.TrelloCard;
import com.github.agiledevgroup2.xpnavigator.view.adapter.DialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class EditCardActivity extends AppCompatActivity {

    public static final String CARD_EXTRA = "CARD";
    public static final String ALL_LIST_NAMES = "LISTS";
    public static final String ALL_LIST_IDS = "LIST_IDS";
    public static final String LIST_ID_1 = "LIST1";
    public static final String LIST_ID_2 = "LIST2";
    private static final String TAG = "EditCardActivity";

    private TrelloCard mCard;
    private TextView mNameEdt;
    private TextView mDescEdt;
    private TextView mHeadline;
    private Spinner mListSpnnr;
    private Switch mEdtSwtch;
    private Button mCnclBtn;
    private Button mSaveBtn;
    private String mListNames[];
    private String mListIds[];
    private int mIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);
        try {
            mCard = new TrelloCard(new JSONObject(getIntent().getStringExtra(CARD_EXTRA)));
        } catch (JSONException e) {
            //should never occur...
            Log.e(TAG, e.getMessage());
            finish();
            return;
        }

        mListNames = getIntent().getStringArrayExtra(ALL_LIST_NAMES);
        mListIds = getIntent().getStringArrayExtra(ALL_LIST_IDS);

        //init layout
        mNameEdt = (TextView) findViewById(R.id.name_edt);
        mDescEdt = (TextView) findViewById(R.id.desc_edt);
        mListSpnnr = (Spinner) findViewById(R.id.list_spinner);
        mEdtSwtch = (Switch) findViewById(R.id.edt_switch);
        mCnclBtn = (Button) findViewById(R.id.cancel_btn);
        mSaveBtn = (Button) findViewById(R.id.save_btn);
        mHeadline = (TextView) findViewById(R.id.headline);

        mEdtSwtch.setChecked(false);
        mListSpnnr.setEnabled(false);
        mNameEdt.setText(mCard.getName());
        mDescEdt.setText(mCard.getDesc());
        mCnclBtn.setVisibility(View.INVISIBLE);
        mSaveBtn.setVisibility(View.INVISIBLE);
        mHeadline.setText(mNameEdt.getText().toString());

        mListSpnnr.setEnabled(false);
        mNameEdt.setEnabled(false);
        mDescEdt.setEnabled(false);
        initSpinner();
        initListeners();

        /**
         *  set the logo
         */
        // enabling action bar app icon and behaving it as toggle button
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        this.getSupportActionBar().setDisplayUseLogoEnabled(true);

    }

    private void initSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditCardActivity.this,
                android.R.layout.simple_spinner_dropdown_item, mListNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mListSpnnr.setAdapter(adapter);

        mIndex = 0;
        for (int i = 0; i < mListIds.length; i++) {
            if (mCard.getListId().equals(mListIds[i])) {
                mIndex = i;
                Log.d(TAG, "index = " + i);
                break;
            }
        }
        mListSpnnr.setSelection(mIndex);
        mListSpnnr.invalidate();

        mListSpnnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onEdit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void initListeners() {

        //switch
        mEdtSwtch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mListSpnnr.setEnabled(isChecked);
                mNameEdt.setEnabled(isChecked);
                mDescEdt.setEnabled(isChecked);
                mNameEdt.setHint(isChecked?getString(R.string.card_title):"");
                mDescEdt.setHint(isChecked?getString(R.string.description):"");
            }
        });

        //name edit
        mNameEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                mHeadline.setText(mNameEdt.getText().toString());
                onEdit();
            }
        });

        //description edit
        mDescEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                onEdit();
            }
        });

        //cancel button
        mCnclBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //save button
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldList = mCard.getListId();
                mCard.setName(mNameEdt.getText().toString());
                mCard.setDesc(mDescEdt.getText().toString());
                mCard.setListId(mListIds[mListSpnnr.getSelectedItemPosition()]);

                String newList = mListIds[mListSpnnr.getSelectedItemPosition()];
                ApiHandler.updateCard(mCard);

                Intent result = new Intent();

                result.putExtra(LIST_ID_1, oldList);
                result.putExtra(LIST_ID_2, newList);

                setResult(Activity.RESULT_OK, result);
                finish();
            }
        });
    }

    private void onEdit() {
        if (!mNameEdt.getText().toString().equals(mCard.getName())
                || !mDescEdt.getText().toString().equals(mCard.getDesc())
                || mIndex != mListSpnnr.getSelectedItemPosition()) {
            mCnclBtn.setVisibility(View.VISIBLE);
            mSaveBtn.setVisibility(View.VISIBLE);
        } else {
            mCnclBtn.setVisibility(View.INVISIBLE);
            mSaveBtn.setVisibility(View.INVISIBLE);
        }

        mSaveBtn.setEnabled(!mNameEdt.getText().toString().isEmpty());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_card, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_timer:
                new DialogBuilder().createTimerDialog(this);
                return true;

            case R.id.action_logout:
                ApiHandler.logout();
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
                finish();
                return true;

            case R.id.action_delete:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage("Do you really want to delete this card?")
                        .setTitle("Delete Card");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete();
                    }
                });

                builder.setNegativeButton("no", null);

                AlertDialog dialog = builder.create();

                dialog.show();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void delete()  {
        Intent result = new Intent();
        result.putExtra(LIST_ID_1, mCard.getListId());
        ApiHandler.removeCard(mCard);

        setResult(Activity.RESULT_OK, result);
        finish();
    }
}
