package com.example.clienttcp;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.fxml.Initializable;
import java.io.IOException;

import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientController{
    @FXML
    private TextField UserName;
    @FXML
    private Button SignInButton;
    String user;
    //submit action for sign in
    public void submit(ActionEvent event) throws IOException {
        //connects to server and sends the profile to be saved in the server
        JSONManager json = new JSONManager();
        user = UserName.getText(); //gets the username from the text field in the scene
        Client client = null;
        try{
            Socket socket = new Socket("localhost", 1234); //generates socket (ip address, port)
            client = new Client(socket, user); //conntects to the server through the socket
            json.setProfile(user, client.getUuid()); //sets the user and the UUID in the json file
            client.sendMessage(json.getProfile().toString()); //sends profile to server (username and UUID)
            System.out.println("connected to server");
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println(json.getProfile()); //debug to see the username and UUID in console

        //after sign in changes to auction scene
        switchToAuctionScene(event);
    }
    //onclick event to close the program with quit button in auction scene
    public void quit(ActionEvent event){
        Platform.exit();
    }
    //switch scene function
    public void switchToAuctionScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Auction-view.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}