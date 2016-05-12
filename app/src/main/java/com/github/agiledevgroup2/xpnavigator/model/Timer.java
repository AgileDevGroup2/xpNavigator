package com.github.agiledevgroup2.xpnavigator.model;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Looper;
import android.os.Vibrator;
import android.view.View;
import android.widget.NumberPicker;

import com.github.agiledevgroup2.xpnavigator.R;

import java.util.TimerTask;

/**
 * Representation of a countdown timer <br>
 *     setContext(Context context) should be called whenever the context changes, regardless if the
 *     timer is used or not <br>
 *     TODO: change this somehow...
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
                doAlarm();
            }
        }
    }

    private void doAlarm() {

        if (mContext != null) {
            final Vibrator vibr = (Vibrator) mContext
                    .getSystemService(Context.VIBRATOR_SERVICE);
            vibr.vibrate(2000);
            //TODO: maybe add audio and notification...,
            //Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            //Ringtone r = RingtoneManager.getRingtone(mContext, notification);
            //r.play();
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

    /**
     * register a view which should be notified on change (e.g. dialog with countdown clock)
     * @param v view to register
     */
    public static void registerView(View v) {
        mSelf.listener = v;
    }

    /**
     * remove registered view, should be called before the registered view is destroyed
     */
    public static void removeView() {
        mSelf.listener = null;
    }

    /**
     * set the time of the timer (does not stop the timer!)
     * @param seconds seconds to set the timer to
     */
    public static void setTime(long seconds) {
        mSelf.mTime = seconds;
        mSelf.mTimeLeft = seconds;
    }

    /**
     * set the time of the timer (does not stop the timer!)
     * @param h hours
     * @param m minutes
     * @param s seconds
     */
    public static void setTime(int h, int m, int s) {
        setTime(h * 3600 + m * 60 + s);
    }

    /**
     * get the timers set time (not the time left!)
     * @return initial countdown time in seconds
     */
    public static long getTime() {
        return mSelf.mTime;
    }

    /**
     * get the time left of the countdown
     * @return time left in seconds
     */
    public static long getTimeLeft() {
        return mSelf.mTimeLeft;
    }

    /**
     * get hours of countdown left
     * @return hours left
     */
    public static int getHoursLeft() {
        return (int) (mSelf.mTimeLeft / 3600);
    }

    /**
     * get minutes of countdown left
     * @return minutes left
     */
    public static int getMinutesLeft() {
        return (int) (mSelf.mTimeLeft % 3600 / 60);
    }

    /**
     * get seconds of countdown left
     * @return seconds left
     */
    public static int getSecondsLeft() {
        return (int) (mSelf.mTimeLeft % 60);
    }

    /**
     * indicates if the countdown is running or not
     * @return whether the countdown is running or not
     */
    public static boolean isRunning() {
        return mSelf.mIsRunning;
    }

    /**
     * start/resume the countdown
     */
    public static void start() {
        mSelf.mIsRunning = true;
    }

    /**
     * pause the countdown
     */
    public static void pause() {
        mSelf.mIsRunning = false;
    }

    /**
     * stop and reset the countdown
     */
    public static void reset() {
        mSelf.mIsRunning = false;
        mSelf.mTimeLeft = mSelf.mTime;
        mSelf.updateView();
    }

    /**
     * set the current context, should be called whenever the context changes
     * TODO: find a nicer way to do that!
     * @param context current context
     */
    public static void setContext(Context context) {
        mSelf.mContext = context;
    }
}
