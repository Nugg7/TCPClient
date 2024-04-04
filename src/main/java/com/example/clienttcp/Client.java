package com.example.clienttcp;

import javafx.scene.layout.VBox;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.*;
import java.util.UUID;
public class Client {

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;
    private UUID uuid;
    private int messageCode;
    private JSON4msg parser = new JSON4msg();

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

    public void listenForMessages(VBox vbox){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                while (socket.isConnected()) {
                    try {
                        message = reader.readLine();
                        JSONObject JSONMessage = parseMessage(message);
                        int messageCode = codeReader(JSONMessage);
                        String parsedMessage = (String)JSONMessage.get("MESSAGE");
                        ClientController.showMessage(parsedMessage, messageCode, vbox);
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

    public JSONObject parseMessage(String message){
        try {
            JSONObject parsedMessage = parser.parse(message);
            return parsedMessage;
        } catch (ParseException e) {
            System.out.println("Error parsing JSON message at Client class parseMessage");
        }
        return null;
    }

    public int codeReader(JSONObject jobj){
        int code;
        String message = (String)jobj.get("CODE");
        switch(message){
            case "MESSAGE":
                code = 1;
            break;
            case "AUCTION":
                code = 2;
            break;
            default:
                code = 1;
            break;
        }
        return code;
    }
}
