package com.example.clienttcp;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

import java.net.Socket;

import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

public class ClientController{
    @FXML
    private TextField UserName;
    @FXML
    private Button SignInButton;
    @FXML
    private Text SignInLabel;

    private Client client;

    private Timer timer;
    private TimerTask task;
    String user;
    //submit action for sign in
    public void submit(ActionEvent event) {
        //connects to server and sends the profile to be saved in the server
        JSON4msg msg = new JSON4msg();
        user = UserName.getText(); //gets the username from the text field in the scene
        if (user != null && !(user.equals("admin"))) {
            try {
                userConnection(event, msg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(msg.profileToString()); //debug to see the username and UUID in console
    }
    //onclick event to close the program with quit button in auction scene
    public void quit(ActionEvent event){
        Platform.exit();
        System.exit(0);
    }
    //switch scene function
    public void switchToAuctionScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Auction-view.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void userConnection(ActionEvent event, JSON4msg msg) throws IOException{
        try{
            Socket socket = new Socket("localhost", 1234); //generates socket (ip address, port)
            client = new Client(socket, user); //connects to the server through the socket
            msg.setProfile(user, client.getUuid()); //sets the user and the UUID in the json file
            client.sendMessage(msg.getProfile().toString()); //sends profile to server (username and UUID)
            System.out.println("connected to server");
            SignInLabel.setStyle("-fx-fill: green;");
            SignInLabel.setText("Connection Successful");

            //after sign in, use timer to change the label and update the scene
            timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //after sign in changes to auction scene
                                switchToAuctionScene(event);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
            };
            timer.schedule(task, 1000);
        }catch (IOException e){
            // if connection is failed then show error message on scene
            System.out.println("Failed to connect to server");
            SignInLabel.setStyle("-fx-fill: red;");
            SignInLabel.setText("Connection Failed");
        }
    }
}