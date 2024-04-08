package com.example.clienttcp;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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

    public void sendMessage(String message) throws IOException {
        try{
            writer.write(message);
            writer.newLine();
            writer.flush();
        }catch(IOException e){
            System.out.println("Error sending a message at Client class");
            throw new IOException();
        }
    }

    public void listenForMessages(VBox chatPane, Text highestBid, VBox bidPane, Text text, Text error, AnchorPane chatAnc, AnchorPane bidAnc, ScrollPane chatSP, ScrollPane bidSP, Text auctionProduct) {
        try{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String message = "";
                    while (socket.isConnected()) {
                        try {
                            try {
                                message = reader.readLine();
                            } catch (IOException e){
                                closeEverything(socket, reader, writer);
                                ClientController.setDisconnected(text);
                                ClientController.setErrorText(error);
                                System.out.println("Connection error: you were disconnected"); // To change with pop up message that closes the client
                                break;
                            }
                            JSONObject JSONMessage = parseMessage(message);
                            int messageCode = codeReader(JSONMessage);
                            VBox panel = null;
                            AnchorPane anchor = null;
                            ScrollPane sp = null;
                            switch (messageCode) {
                                case 1:
                                    panel = chatPane;
                                    anchor = chatAnc;
                                    sp = chatSP;
                                    break;
                                case 2:
                                    ClientController.setProduct(auctionProduct, JSONMessage.get("MESSAGE").toString());
                                    if (JSONMessage.get("MESSAGE").toString().equals("Ended"));
                                        ClientController.setHighestBidText(highestBid, 0);
                                    break;
                                case 3:
                                    panel = bidPane;
                                    anchor = bidAnc;
                                    sp = bidSP;
                                    break;
                                case 4:
                                    ClientController.setHighestBidText(highestBid, (double) JSONMessage.get("MESSAGE"));
                                    break;
                                case 5:
                                    ClientController.setHighestBidText(highestBid, 0);
                                    break;
                            }
                            if (messageCode == 1 || messageCode == 3) {
                                String parsedMessage = (String) JSONMessage.get("MESSAGE");
                                ClientController.showMessage(parsedMessage, panel, anchor, sp);
                            }
                        } catch (Exception e) {
                            System.out.println("Error reading a message at Client class Listen for message method");
                            closeEverything(socket, reader, writer);
                        }
                    }
                }
            }).start();
        }catch(Exception e) {
            System.out.println("Connection error: you were disconnected");
        }
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
        int code = 0;
        String message = (String)jobj.get("CODE");
        switch(message){
            case "MESSAGE":
                code = 1;
            break;
            case "AUCTION":
                code = 2;
            break;
            case "BID":
                code = 3;
            break;
            case "PRODUCT":
                code = 4;
            break;
            case "RESET":
                code = 5;
            break;
        }
        return code;
    }
}
