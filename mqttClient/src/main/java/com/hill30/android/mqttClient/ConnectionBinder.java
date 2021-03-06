package com.hill30.android.mqttClient;

import android.content.*;
import android.os.Binder;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;

class ConnectionBinder extends Binder {
    private final Connection connection;
    private final String topic;
    private ServiceConnection.MessageListener messageListener;

    public ConnectionBinder(Connection connection, Intent intent) {
        this.connection = connection;
        topic = intent.getStringExtra(Service.TOPIC_NAME);
    }

    public void connect() throws MqttException, IOException {
        connection.connect(this, topic);
    }

    public void onMessageReceived(String message) {
        if (messageListener != null)
            messageListener.onMessageArrived(message);
    }

    public void send(String message) throws IOException {
        connection.send(topic, message);
    }

    public void listener(ServiceConnection.MessageListener listener) {
        messageListener = listener;
    }

    public void disconnectBinder() {
        connection.unregisterSubscriber(topic);
    }
}
