package org.monscat;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Objects;

public class MqttUtil {
    MemoryPersistence persistence = new MemoryPersistence();

    MqttClient client;
    MqttConnectOptions connOpts;

    public boolean connectTo(String broker, String clientId, String username, String password) {
        try {
            client = new MqttClient(broker, clientId, persistence);

            connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setAutomaticReconnect(true);
            if(username != null && !username.isEmpty() && password != null && !password.isEmpty()){
                connOpts.setUserName(username);
                connOpts.setPassword(password.toCharArray());
            }

            client.connect(connOpts);

            return true;
        } catch (MqttException me) {
            printErrorLog(me);
            return false;
        }
    }

    public boolean subscribeTo(String topic) {
        try {
            client.subscribe(topic, 2);
            return true;
        } catch (MqttException me) {
            printErrorLog(me);
            return false;
        }
    }

    public boolean publishMessage(String topic, String content) {
        try {
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(2);

            client.publish(topic, content.getBytes(), 2, false);

            return true;
        } catch (MqttException me) {
            printErrorLog(me);
            return false;
        }
    }

    public boolean disconnectMqttClient() {
        try {
            client.disconnect();
            return true;
        } catch (MqttException me) {
            printErrorLog(me);
            return false;
        }
    }

    public void onMessage() {
        client.setCallback(new MqttCallback() {

            @Override
            public void connectionLost(Throwable cause) { //Called when the client lost the connection to the broker
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {//Called when a outgoing publish is complete
            }
        });
    }

    private void printErrorLog(MqttException me) {
        System.out.println("reason " + me.getReasonCode());
        System.out.println("msg " + me.getMessage());
        System.out.println("loc " + me.getLocalizedMessage());
        System.out.println("cause " + me.getCause());
        System.out.println("excep " + me);
        me.printStackTrace();
    }
}
