package com.github.agiledevgroup2.xpnavigator.other;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Thread independent lock implementation, hope this works...
 */
public class ThreadIndependentLock implements Lock {

    private boolean mIsLocked;

    @Override
    public synchronized void lock() {
        while (mIsLocked) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                //should not occur, might throw an error...
            }
        }
        mIsLocked = true;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new InterruptedException("lockInterruptibly() is not Supported");
    }

    @Override
    public synchronized boolean tryLock() {
        if (mIsLocked) return false;

        mIsLocked = true;
        return true;
    }

    @Override
    public synchronized boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        long waited = 0;
        while (waited <= time) {
            if (!mIsLocked) {
                mIsLocked = true;
                return true;
            }
            Thread.sleep(10);
        }
        return false;
    }

    @Override
    public void unlock() {
        mIsLocked = false;
    }

    @NonNull
    @Override
    public Condition newCondition() {
        return null;
    }
}
