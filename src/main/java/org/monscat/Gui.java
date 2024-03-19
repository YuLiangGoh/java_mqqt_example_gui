package org.monscat;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javax.swing.*;
import java.awt.*;

public class Gui {
    // Main Gui
    public JFrame frame = new JFrame("MQTT");
    public JPanel mainPanel = new JPanel();

    // Broker Form
    public JPanel brokerForm = new JPanel();
    public JPanel brokerPanel = new JPanel();
    public JLabel brokerLabel = new JLabel("Broker : ");
    public JTextField brokerField = new JTextField(20);
    public JPanel clientIdPanel = new JPanel();
    public JLabel clientIdLabel = new JLabel("Client ID : ");
    public JTextField clientIdField = new JTextField(10);
    public JPanel subscribedTopicPanel = new JPanel();
    public JLabel subscribedTopicLabel = new JLabel("Subscribed Topic : ");
    public JTextField subscribedTopicField = new JTextField(10);
    public JButton connectButton = new JButton("Connect");
    public JButton disconnectButton = new JButton("Disconnect");

    // Text Area
    public JTextArea textArea = new JTextArea(10, 30);
    public JScrollPane textScrollPane = new JScrollPane(textArea);

    // Send Message Panel
    public JPanel sendMessagePanel = new JPanel();
    public JPanel topicPanel = new JPanel();
    public JLabel topicLabel = new JLabel("Topic : ");
    public JTextField topicField = new JTextField(10);
    public JPanel messagePanel = new JPanel();
    public JLabel messageLabel = new JLabel("Message : ");
    public JTextField messageField = new JTextField(20);
    public JButton sendButton = new JButton("Send");

    MqttUtil mqttUtil = new MqttUtil();

    public void displayFormGui() {
        frame.setSize(650, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(brokerForm(), BorderLayout.NORTH);
        mainPanel.add(textScrollPane(), BorderLayout.CENTER);
        mainPanel.add(sendMessagePanel(), BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    public JPanel brokerForm() {
        brokerForm.setLayout(new BoxLayout(brokerForm, BoxLayout.Y_AXIS));

        brokerPanel.setLayout(new BoxLayout(brokerPanel, BoxLayout.X_AXIS));
        brokerField.setText("tcp://mqtt.eclipseprojects.io:1883");
        brokerPanel.add(brokerLabel);
        brokerPanel.add(brokerField);

        clientIdPanel.setLayout(new BoxLayout(clientIdPanel, BoxLayout.X_AXIS));
        clientIdField.setText("user1");
        clientIdPanel.add(clientIdLabel);
        clientIdPanel.add(clientIdField);

        subscribedTopicPanel.setLayout(new BoxLayout(subscribedTopicPanel, BoxLayout.X_AXIS));
        subscribedTopicField.setText("Topic 1");
        subscribedTopicPanel.add(subscribedTopicLabel);
        subscribedTopicPanel.add(subscribedTopicField);

        connectButton.addActionListener(e -> {
            connectButton.setEnabled(false);
            String broker = brokerField.getText();
            String clientId = clientIdField.getText();
            String topic = subscribedTopicField.getText();

            if (mqttUtil.connectTo(broker, clientId, topic)) {
                textArea.append("Successfully connected. \n");
                textArea.append("Broker : " + broker + "\n");
                textArea.append("Client ID : " + clientId + "\n");
                textArea.append("Subscribed to : " + topic + "\n");
                textArea.append("Listening for messages...\n");
                brokerField.setEnabled(false);
                clientIdField.setEnabled(false);
                subscribedTopicField.setEnabled(false);
                connectButton.setEnabled(true);
                connectButton.setVisible(false);
                disconnectButton.setVisible(true);

                handleMessages();
            } else {
                connectButton.setEnabled(true);
                connectButton.setVisible(true);
                disconnectButton.setVisible(false);
                textArea.append("Failed to connect to " + broker + "\n");
            }
        });

        disconnectButton.setVisible(false);
        disconnectButton.addActionListener(e -> {
            if (mqttUtil.disconnectMqttClient()) {
                textArea.append("Disconnected from " + brokerField.getText() + "\n");
                brokerField.setEnabled(true);
                clientIdField.setEnabled(true);
                subscribedTopicField.setEnabled(true);
                connectButton.setVisible(true);
                disconnectButton.setVisible(false);
            } else {
                connectButton.setVisible(false);
                disconnectButton.setVisible(true);
                textArea.append("Failed to disconnect from " + brokerField.getText() + "\n");
            }
        });

        brokerForm.add(brokerPanel);
        brokerForm.add(clientIdPanel);
        brokerForm.add(subscribedTopicPanel);
        brokerForm.add(connectButton);
        brokerForm.add(disconnectButton);

        return brokerForm;
    }

    public void handleMessages(){
        mqttUtil.client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                textArea.append("Connection lost. \n");
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                textArea.append("Message arrived. \n");
                textArea.append("Topic : " + s + "\n");
                textArea.append("Message : " + mqttMessage.toString() + "\n");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                textArea.append("Delivery complete. \n");
            }
        });
    }

    public JScrollPane textScrollPane() {
        textArea.setEnabled(false);
        textArea.setLineWrap(true);
        return textScrollPane;
    }

    public JPanel sendMessagePanel() {
        sendMessagePanel.setLayout(new BoxLayout(sendMessagePanel, BoxLayout.X_AXIS));

        topicPanel.setLayout(new BoxLayout(topicPanel, BoxLayout.X_AXIS));
        topicField.setText("Topic 1");
        topicPanel.add(topicLabel);
        topicPanel.add(topicField);

        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
        messagePanel.add(messageLabel);
        messagePanel.add(messageField);

        sendButton.addActionListener(e -> {
            String topic = topicField.getText();
            String message = messageField.getText();

            if (mqttUtil.publishMessage(topic, message)) {
                messageField.setText("");
                textArea.append("Message sent. \n");
                textArea.append("Topic : " + topic + "\n");
                textArea.append("Message : " + message + "\n");
            } else {
                textArea.append("Failed to send message. \n");
            }
        });

        sendMessagePanel.add(topicPanel);
        sendMessagePanel.add(messagePanel);
        sendMessagePanel.add(sendButton);

        return sendMessagePanel;
    }
}
