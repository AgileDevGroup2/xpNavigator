package com.github.agiledevgroup2.xpnavigator;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import java.util.TimerTask;

/**
 * Created by indidev on 5/5/16.
 */
public class Timer {
    private static final String TAG = "Timer";
    private static Timer mSelf = new Timer();

    private boolean mLooperPrepared;
    private boolean mIsRunning;
    private long mTime; // set time in seconds
    private long mTimeLeft; // time left in seconds
    private View listener;
    private java.util.Timer mTimer;
    private Context mContext;

    private Timer() {
        mIsRunning = false;
        mTime = 3600;
        mTimeLeft = mTime;
        listener = null;
        mLooperPrepared = false;
        mTimer = new java.util.Timer();

        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!mLooperPrepared) {
                    Looper.prepare();
                    mLooperPrepared = true;
                }
                tick();
            }
        }, 0, 1000);
    }

    private void tick() {
        if (mIsRunning) {
            if (mTimeLeft > 0) mTimeLeft--;
            //Log.d(TAG, "tick");
            updateView();
            if (mTimeLeft == 0) {
                mIsRunning = false;

                //alarm
                if (mContext != null) {
                    final Vibrator vibr = (Vibrator) mContext
                            .getSystemService(Context.VIBRATOR_SERVICE);
                    vibr.vibrate(2000);
                    //TODO: maybe add audio and notification...
                }
            }
        }
    }

    private void updateView() {
        if (listener != null) {
            listener.post(new Runnable() {
                @Override
                public void run() {
                    NumberPicker pickers[] = {
                            (NumberPicker) listener.findViewById(R.id.hours),
                            (NumberPicker) listener.findViewById(R.id.minutes),
                            (NumberPicker) listener.findViewById(R.id.seconds)};

                    //update values
                    pickers[0].setValue(Timer.getHoursLeft());
                    pickers[1].setValue(Timer.getMinutesLeft());
                    pickers[2].setValue(Timer.getSecondsLeft());
                }
            });
        }
    }

    public static void registerView(View v) {
        mSelf.listener = v;
    }

    public static void removeView() {
        mSelf.listener = null;
    }

    public static void setTime(long seconds) {
        mSelf.mTime = seconds;
        mSelf.mTimeLeft = seconds;
    }

    public static void setTime(int h, int m, int s) {
        setTime(h * 3600 + m * 60 + s);
    }

    public static long getTime() {
        return mSelf.mTime;
    }

    public static long getTimeLeft() {
        return mSelf.mTimeLeft;
    }

    public static int getHoursLeft() {
        return (int) (mSelf.mTimeLeft / 3600);
    }

    public static int getMinutesLeft() {
        return (int) (mSelf.mTimeLeft % 3600 / 60);
    }

    public static int getSecondsLeft() {
        return (int) (mSelf.mTimeLeft % 60);
    }

    public static boolean isRunning() {
        return mSelf.mIsRunning;
    }

    public static void start() {
        mSelf.mIsRunning = true;
    }

    public static void pause() {
        mSelf.mIsRunning = false;
    }

    public static void reset() {
        mSelf.mIsRunning = false;
        mSelf.mTimeLeft = mSelf.mTime;
        mSelf.updateView();
    }

    public static void setContext(Context context) {
        mSelf.mContext = context;
    }
}
