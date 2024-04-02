package com.example.clienttcp;

import java.io.*;
import java.net.*;
import java.util.UUID;
public class Client {

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;
    private UUID uuid;

    public Client(Socket socket, String username){
        this.username = username;
        this.uuid = UUID.randomUUID();
        try{
            this.socket = socket;
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("Client connected successfully");
        }catch(IOException e){
            System.out.println("Error creating a client at Client class");
            e.printStackTrace();
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String message){
        try{
            writer.write(message);
            writer.newLine();
            writer.flush();
        }catch(IOException e){
            System.out.println("Error sending a message at Client class");
            e.printStackTrace();
        }
    }

    public void listenForMessages(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                while (socket.isConnected()) {
                    try {
                        message = reader.readLine();
                        System.out.println(message);
                    } catch (IOException e) {
                        System.out.println("Error reading a message at Client class Listen for message method");
                        closeEverything(socket, reader, writer);
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket clientSocket, BufferedReader breader, BufferedWriter bwriter){
        try {
            clientSocket.close();
            breader.close();
            bwriter.close();
        } catch (IOException e) {
            System.out.println("error at closeEverything method");
            e.printStackTrace();
        }
    }
}
