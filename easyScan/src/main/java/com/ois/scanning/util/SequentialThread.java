package com.ois.scanning.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.ois.scanning.AppLog;

import java.util.HashSet;

public abstract class SequentialThread extends HandlerThread{
    protected static final int EXIT_THREAD = 0;

    public static final int NORMAL_TASK = 0;
    public static final int PRIORITY_TASK = 1;
    public static final int SINGLE_TASK = 2;
    private Handler mWorkerHandler = null;
    private final Handler mNotifyHandler = new Handler(Looper.getMainLooper());

    private final HashSet<Integer> mTaskTypes = new HashSet<>();
    protected SequentialThread(final String name)
    {
        super(name);
    }
    protected abstract void onThreadStarted();


    protected abstract void onThreadComplete();

    protected abstract Runnable handleThreadTask(int type, Object params);

    @Override
    protected void onLooperPrepared()
    {
        super.onLooperPrepared();

        onThreadStarted();

	    Log.d(AppLog.TAG, "Create mWorkerHandler");
	    final Handler handler = new Handler(getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == EXIT_THREAD) {
                    // Try to destroy message loop
                    if (!quit()) {
                        // Hardly interrupt thread if something wrong with looper
                        interrupt();
                    }
                    return true;
                } else if (mTaskTypes.contains(msg.what)) {
                    final Runnable r = handleThreadTask(msg.what, msg.obj);
                    if (r != null) {
                        mNotifyHandler.post(r);
                    }
                    return true;
                } else {
                    // Default processing
                    return false;
                }
            }
        });

	    synchronized (this) {
		    mWorkerHandler = handler;
		    notifyAll();
	    }
    }

    @Override
    public void run() {
        try {
            super.run();
        } finally {
            // No post-looper overrides in HandlerThread :(
            onThreadComplete();
        }
    }

    public synchronized boolean isReady() {
	    return isAlive() && mWorkerHandler != null;
    }

    private void checkWorkingThread() {
        // Safe wait for thread started
        synchronized (this) {
            if (isAlive() && mWorkerHandler == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    // dummy
                }
            }
        }

        if (mWorkerHandler == null) {
            throw new IllegalStateException("Thread is not ready yet");
        }
    }

    protected void addThreadTask(int type, Object params, int priority, boolean allowDuplicate) {
        // Check reserved task type
        if (type == EXIT_THREAD) {
            throw new IllegalArgumentException("Task type cannot be 0");
        }

        checkWorkingThread();

	    synchronized (this)
        {
            // Remove all pending messages if any
            if (priority == SINGLE_TASK) {
                mWorkerHandler.removeMessages(type);
            } else if (allowDuplicate && params != null) {
	            mWorkerHandler.removeMessages(type, params);
            }

            // Create new message
            final Message msg = mWorkerHandler.obtainMessage(type, params);

            // Store task type (to remove all on finish)
            mTaskTypes.add(type);

            if (priority == PRIORITY_TASK) {
	            mWorkerHandler.sendMessageAtFrontOfQueue(msg);
            } else {
                mWorkerHandler.sendMessage(msg);
            }
        }
    }

    protected synchronized void removeThreadTask(int type, Object params) {
	    checkWorkingThread();
	    synchronized (this) {
		    mWorkerHandler.removeMessages(type, params);
	    }
    }

    public void finish()
    {
        if (!isAlive()) {
            return;
        }

        synchronized (this) {
            // Remove all pending messages if any
            for (int type : mTaskTypes) {
                if (type != EXIT_THREAD) {
                    mWorkerHandler.removeMessages(type);
                }
            }

            // Send message to quit
            mWorkerHandler.sendEmptyMessage(EXIT_THREAD);
        }

        try {
            join();
        } catch (InterruptedException ex)
        {

        }
    }
}
