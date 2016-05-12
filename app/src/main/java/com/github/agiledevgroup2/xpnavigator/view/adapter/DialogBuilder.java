package com.github.agiledevgroup2.xpnavigator.view.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import com.github.agiledevgroup2.xpnavigator.R;
import com.github.agiledevgroup2.xpnavigator.model.Timer;

/**
 * Provides a function to build a timer dialog
 */
public class DialogBuilder {


    /**
     * creates a new countdown timer dialog
     * @param context context the dialog will be build in
     */
    public void createTimerDialog(final Context context) {
        final AlertDialog.Builder timeDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.dialog_timer, null);

        //init dialog
        timeDialogBuilder.setTitle(context.getString(R.string.timer_label));
        timeDialogBuilder.setView(dialogView);
        timeDialogBuilder.setPositiveButton(context.getString(R.string.lbl_done), null);

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
        updateDialog(dialogView, context);
        startB.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                if (Timer.isRunning()) {
                    Timer.pause();
                } else {
                    Timer.start();
                }
                updateDialog(dialogView, context);
            }
        });
        ImageButton resetB = (ImageButton) dialogView.findViewById(R.id.resetButton);
        resetB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timer.reset();
                updateDialog(dialogView, context);
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
     * <font style:color="red">Warning:</font> <b>Use only the timer dialog!</b>
     * @param dialogView dialog to update
     */
    @SuppressWarnings("deprecation")
    protected void updateDialog(View dialogView, Context context) {
        ImageButton startB = (ImageButton) dialogView.findViewById(R.id.startButton);
        String resource = "android:drawable/ic_media_play";
        if (Timer.isRunning()) {
            resource = "android:drawable/ic_media_pause";
        }
        int identifier = context.getResources().getIdentifier(resource, null, null);
        startB.setImageDrawable(context.getResources().getDrawable(identifier));

        NumberPicker pickers[] = {
                (NumberPicker) dialogView.findViewById(R.id.hours),
                (NumberPicker) dialogView.findViewById(R.id.minutes),
                (NumberPicker) dialogView.findViewById(R.id.seconds)};
        for (NumberPicker picker : pickers) {
            if (Timer.isRunning()) picker.setEnabled(false);
            else picker.setEnabled(true);
        }
    }
}
