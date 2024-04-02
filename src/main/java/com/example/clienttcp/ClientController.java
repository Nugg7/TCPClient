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

    public void submit(ActionEvent event) throws IOException {
        JSONManager json = new JSONManager();
        user = UserName.getText();
        Client client = null;
        try{
            Socket socket = new Socket("localhost", 1234); //generates socket (ip address, port)
            client = new Client(socket, user);
            json.setProfile(user, client.getUuid());
            client.sendMessage(json.getProfile().toString()); //sends profile to server (username and UUID)
            System.out.println("connected to server");
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println(json.getProfile());

        Parent root = FXMLLoader.load(getClass().getResource("Auction-view.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void quit(ActionEvent event){
        Platform.exit();
    }
}