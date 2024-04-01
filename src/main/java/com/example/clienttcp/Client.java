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

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }
}
