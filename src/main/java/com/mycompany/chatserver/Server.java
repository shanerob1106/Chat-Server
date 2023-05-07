/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 *
 * @author shane
 */
public class Server {

    private static ArrayList<ClientHandler> clientsList = new ArrayList<>();

    public static String getLocalIPv4() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface currentInterface = interfaces.nextElement();
            Enumeration<InetAddress> addresses = currentInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress currentAddress = addresses.nextElement();
                if (!currentAddress.isLoopbackAddress() && currentAddress.getAddress().length == 4) {
                    return currentAddress.getHostAddress();
                }
            }
        }
        return null; // No valid IPv4 address found
    }

    public static void main(String[] args) throws IOException {
        int portNumber = 8000;
        String localIPv4 = getLocalIPv4();
        InetAddress serverAddress = InetAddress.getByName(localIPv4);

        try (ServerSocket serverSocket = new ServerSocket(portNumber, 50, serverAddress)) {
            System.out.println("Server listening on port " + portNumber);
            System.out.println("Server hosting on IP: " + localIPv4);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: ");

                ClientHandler clientThread = new ClientHandler(clientSocket, clientsList);
                clientsList.add(clientThread);
                new Thread(clientThread).start();
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + portNumber + " or listening for a connection " + e.getMessage());
        }
    }
}

class ClientHandler implements Runnable {

    private Socket clientSocket;
    private ArrayList<ClientHandler> clientsList;
    private PrintWriter out;

    public ClientHandler(Socket socket, ArrayList<ClientHandler> clientsList) {
        this.clientSocket = socket;
        this.clientsList = clientsList;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Client says: " + inputLine);
                broadcastMessage(inputLine);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to read from client socket: " + e);
        } finally {
            // Remove the client from the clientsList and close the socket
            clientsList.remove(this);
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Exception caught when trying to close client socket: " + e);
            }
        }
    }

    private void broadcastMessage(String message) {
        for (ClientHandler client : clientsList) {
            if (client != this) {
                if (client.out != null) {
                    client.out.println(message);
                }
            }
        }
    }
}
