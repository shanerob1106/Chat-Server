/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author shane
 */
public class Client {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8000;

    public static void main(String[] args) throws IOException {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;

        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Enter the Server IP Address (or press enter to use default)");
        String hostInput = scanner.nextLine();
        if(!hostInput.isEmpty()){
            host = hostInput;
        }
        
        System.out.println("Enter the server port number (or press enter to use default)");
        String portInput = scanner.nextLine();
        if(!portInput.isEmpty()){
            port = Integer.parseInt(portInput);
        }

        try (Socket socket = new Socket(host, port); BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Connected to server at " + host + ":" + port);

            // Start a separate thread to handle incoming messages from the server
            Thread receiverThread = new Thread(new MessageReceiver(input));
            receiverThread.start();

            // Read user input and send messages to the server
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            String message;
            while ((message = userInput.readLine()) != null) {
                output.println(message);
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

class MessageReceiver implements Runnable {

    private BufferedReader input;

    public MessageReceiver(BufferedReader input) {
        this.input = input;
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = input.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
