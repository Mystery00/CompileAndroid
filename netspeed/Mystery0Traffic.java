package com.android.systemui.statusbar.policy;

import java.text.DecimalFormat;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.android.systemui.R;

public class Mystery0Traffic extends TextView
{
    private boolean mAttached;
    Handler mHandler;
    private final BroadcastReceiver mIntentReceiver;
    Runnable mRunnable;
    Handler mTrafficHandler;
    TrafficStats mTrafficStats;
    boolean showTraffic;
    float send_speed;
    float received_speed;
    float totalRxBytes;
    float totalTxBytes;

    class SettingsObserver extends ContentObserver
    {
        SettingsObserver(Handler handler)
        {
            super(handler);
        }

        void observe()
        {
            ContentResolver resolver = getContext().getContentResolver();
            resolver.registerContentObserver(Settings.System.getUriFor("Settings.ACTION_NETWORK_SETTINGS"), false, this);

        }
        @Override
        public void onChange(boolean selfChange)
        {
            updateSettings();
        }
    }

    public Mystery0Traffic(Context context)
    {
        this(context, null);
    }

    public Mystery0Traffic(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public Mystery0Traffic(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mIntentReceiver = new Receiver();
        mRunnable = new Task();
        mHandler = new Handler();
        SettingsObserver settingsObserver = new SettingsObserver(mHandler);
        mTrafficStats = new TrafficStats();
        settingsObserver.observe();
        updateSettings();
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        if (!mAttached)
        {
            mAttached = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            getContext().registerReceiver(mIntentReceiver, filter, null, getHandler());
        }
        updateSettings();
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        if (mAttached)
        {
            getContext().unregisterReceiver(mIntentReceiver);
            mAttached = false;
        }
    }

    class Receiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
            {
                updateSettings();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    public void updateTraffic()
    {
        mTrafficHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                send_speed = ((float) (TrafficStats.getTotalTxBytes()) - totalTxBytes) / 1024.0f / 3.0f;
                received_speed = ((float) (TrafficStats.getTotalRxBytes()) - totalRxBytes) / 1024.0f / 3.0f;
                totalRxBytes = (float) (TrafficStats.getTotalRxBytes());
                totalTxBytes = (float) (TrafficStats.getTotalTxBytes());
                DecimalFormat DecimalFormalism = new DecimalFormat("##0.#");
                String show_send;
                String show_received;
                if ((send_speed / 1024.0f) >= 1.0f)
                    show_send=DecimalFormalism.format((double) (send_speed / 1024.0f)) + "MB/s ↑";
                else if (send_speed <= 0.0099)
                    show_send=DecimalFormalism.format((double) (send_speed * 1024.0f)) + "B/s ↑";
                else
                    show_send=DecimalFormalism.format((double) send_speed) + "KB/s ↑";

                if ((received_speed / 1024.0f) >= 1.0f)
                    show_received=DecimalFormalism.format((double) (received_speed / 1024.0f)) + "MB/s ↓";
                else if (send_speed <= 0.0099)
                    show_received=DecimalFormalism.format((double) (received_speed * 1024.0f)) + "B/s ↓";
                else
                    show_received=DecimalFormalism.format((double) received_speed) + "KB/s ↓";

                setText(show_send + " " + show_received);
                update();
                super.handleMessage(msg);
            }
        };
        mTrafficHandler.sendEmptyMessage(0);
    }

    private boolean getConnectAvailable()
    {
        try
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            return connectivityManager.getActiveNetworkInfo().isConnected();
        } catch (Exception ignored)
        {
        }

        return false;
    }

    public void update()
    {
        mTrafficHandler.removeCallbacks(mRunnable);
        mTrafficHandler.postDelayed(mRunnable, 1000);
    }

    class Task implements Runnable
    {
        @Override
        public void run()
        {
            mTrafficHandler.sendEmptyMessage(0);
        }
    }

    private void updateSettings()
    {
        ContentResolver resolver = getContext().getContentResolver();

        showTraffic = (Settings.System.getInt(resolver, "Settings.ACTION_NETWORK_SETTINGS", 1) == 1);

        if (showTraffic && getConnectAvailable())
        {
            if (mAttached)
                updateTraffic();

            setVisibility(View.VISIBLE);
        } else
            setVisibility(View.GONE);
    }
}

