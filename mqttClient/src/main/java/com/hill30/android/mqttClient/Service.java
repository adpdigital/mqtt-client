package com.hill30.android.mqttClient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by michaelfeingold on 2/5/14.
 */
public class Service extends android.app.Service {

    public static final String TAG = "MQTT Service";
    public static final String BROKER_URL = "com.hill30.android.mqttClient.broker-url";
    public static final String USER_NAME = "com.hill30.android.mqttClient.user-name";
    public static final String PASSWORD = "com.hill30.android.mqttClient.password";
    public static final String TOPIC_NAME = "com.hill30.android.mqttClient.topic-name";
    public static final String CONNECTION_RETRY_INTERVAL = "com.hill30.android.mqttClient.connection_retry_interval";

    private Connection connection;
    private Timer reconnectTimer = new Timer();

    private int retry_interval = 5000; //milliseconds

    private BroadcastReceiver networkStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            networkAvailable = !intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            if (networkAvailable)
                Log.e(TAG, "Network restored. Reconnecting");
                try {
                    connection.connectIfNecessary();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
        }
    };
    private boolean networkAvailable = false;

    @Override
    public void onCreate() {
        HandlerThread connectionThread = new HandlerThread("mqttConnection", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        connectionThread.start();

        // todo: should the below be run on the connectionThread?
        try {
            connection = new Connection(connectionThread.getLooper(), this, getString(R.string.brokerURL));
        } catch (MqttException e) {
            e.printStackTrace();
        }

        registerReceiver(networkStatusReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkStatusReceiver);
    }

    public void onConnectFailure() {
        // todo: do something smarter about onConnectFailure attempts
        if (networkAvailable) {
            Log.e(TAG, String.format("Connection failure. Reconnecting in %d sec.", retry_interval/1000));
            reconnectTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                try {
                    connection.connectIfNecessary();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                }
            }, retry_interval);
        } else
            Log.e(TAG, "Connection failure. Network unavailable.");

    }

    @Override
    public IBinder onBind(final Intent intent) {
        return new ConnectionBinder(connection, intent);
    }
}
