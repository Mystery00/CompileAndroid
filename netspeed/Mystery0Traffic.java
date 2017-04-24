package com.android.systemui.statusbar.policy;

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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

import com.android.systemui.R;

public class Mystery0Traffic extends LinearLayout
{
    private TextView send;
    private TextView received;
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
        LayoutInflater.from(context).inflate(R.layout.mystery0_traffic, this);
        send = (TextView) findViewById(R.id.send);
        received = (TextView) findViewById(R.id.received);
        mIntentReceiver = new Mystery0Traffic.Receiver();
        mRunnable = new Mystery0Traffic.Task();
        mHandler = new Handler();
        mTrafficStats=new TrafficStats();
        Mystery0Traffic.SettingsObserver settingsObserver = new Mystery0Traffic.SettingsObserver(mHandler);
        settingsObserver.observe();
        updateSettings();
    }

    class SettingsObserver extends ContentObserver
    {
        SettingsObserver(Handler handler)
        {
            super(handler);
        }

        void observe()
        {
            ContentResolver resolver = getContext().getContentResolver();
            resolver.registerContentObserver(Settings.System.getUriFor("status_bar_traffic"), false, this);

        }

        @Override
        public void onChange(boolean selfChange)
        {
            updateSettings();
        }
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        if (!mAttached)
        {
            mAttached = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
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

    private void updateSettings()
    {
        ContentResolver resolver = getContext().getContentResolver();

        showTraffic = (Settings.System.getInt(resolver, "status_bar_traffic", 1) == 1);

        if (showTraffic && getConnectAvailable())
        {
            if (mAttached)
                updateTraffic();

            setVisibility(View.VISIBLE);
        } else
            setVisibility(View.GONE);
    }

    class Receiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equals("android.net.conn.CONNECTIVITY_CHANGE"))
            {
                updateSettings();
            }
        }
    }

    public void updateTraffic()
    {
        mTrafficHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                send_speed = ((float) (mTrafficStats.getTotalTxBytes()) - totalTxBytes) / 1024.0f / 3.0f;
                received_speed = ((float) (mTrafficStats.getTotalRxBytes()) - totalRxBytes) / 1024.0f / 3.0f;
                totalRxBytes = (float) (mTrafficStats.getTotalRxBytes());
                totalTxBytes = (float) (mTrafficStats.getTotalTxBytes());
                DecimalFormat DecimalFormalism = new DecimalFormat("###0.0");
                if ((send_speed / 1024.0f) >= 1.0f)
                    send.setText(DecimalFormalism.format((double) (send_speed / 1024.0f)) + "MB/s ↑");
                else if (send_speed <= 0.0099)
                    send.setText(DecimalFormalism.format((double) (send_speed * 1024.0f)) + "B/s ↑");
                else
                    send.setText(DecimalFormalism.format((double) send_speed) + "KB/s ↑");

                if ((received_speed / 1024.0f) >= 1.0f)
                    received.setText(DecimalFormalism.format((double) (received_speed / 1024.0f)) + "MB/s ↓");
                else if (send_speed <= 0.0099)
                    received.setText(DecimalFormalism.format((double) (received_speed * 1024.0f)) + "B/s ↓");
                else
                    received.setText(DecimalFormalism.format((double) received_speed) + "KB/s ↓");

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
        } catch (Exception e)
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
}

