/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatserver;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ClientGui extends JFrame {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8000;

    private JPanel mainPanel;
    private JTextArea messageArea;
    private JTextField inputField;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientGui() {
        super("Chat Client");

        mainPanel = new JPanel(new BorderLayout());
        messageArea = new JTextArea(20, 50);
        messageArea.setEditable(false);
        mainPanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        inputField = new JTextField();
        inputField.addActionListener(e -> {
            String message = inputField.getText();
            if (!message.isEmpty()) {
                messageArea.append("You: " + message + "\n");
                inputField.setText("");
                out.println(message);
            }
        });

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> {
            String message = inputField.getText();
            if (!message.isEmpty()) {
                messageArea.append("You: " + message + "\n");
                inputField.setText("");
                out.println(message);
            }
        });

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        connectToServer();
        startListening();
    }

    private void connectToServer() {
        
        String host = JOptionPane.showInputDialog("Enter IP Address");
        
        try {
            socket = new Socket(host, DEFAULT_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }

    private void startListening() {
        new Thread(() -> {
            try {
                String response;
                while ((response = in.readLine()) != null) {
                    messageArea.append("Client: " + response + "\n");
                }
            } catch (IOException e) {
                System.err.println("Error reading from server: " + e.getMessage());
            }
        }).start();
    }

    public static void main(String[] args) {
        new ClientGui();
    }
}