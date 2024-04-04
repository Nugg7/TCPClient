package com.example.clienttcp;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;

import java.io.IOException;

import java.net.Socket;

import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class ClientController{
    @FXML
    private TextField UserName;
    @FXML
    private TextField chatTextField;
    @FXML
    private Text SignInLabel;
    @FXML
    private Text statusText;
    @FXML
    private TextField bidTextField;
    @FXML
    public VBox chatPane;
    @FXML
    private VBox bidPane;
    @FXML
    private ScrollPane chatScrollPane;
    @FXML
    private Button conDisButton;

    static private Client client;
    static public String serverMsg = "";
    public static boolean isConnected = false;

    private Timer timer;
    private TimerTask task;
    private static String user;
    public JSON4msg msg = new JSON4msg();
    //submit action for sign in
    public void submit(ActionEvent event) {
        user = UserName.getText(); //gets the username from the text field in the scene
        if (user != null && !(user.equals("admin"))) {
            System.out.println("Sing-In Successful");
            SignInLabel.setStyle("-fx-fill: green;");
            SignInLabel.setText("Sing-In Successful");
            try {
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
                                    System.out.println("error in switchToAuctionScene try catch");
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                    }
                };
                timer.schedule(task, 1000);
            } catch (Exception e) {
                System.out.println("Sing-In Failed");
                SignInLabel.setStyle("-fx-fill: red;");
                SignInLabel.setText("Sing-In Failed");
                throw new RuntimeException(e);
            }
            setUser(user);
        }
    }
    public void setUser(String username){
        this.user = username;
    }
    public void connectDisconnect(ActionEvent event){
        if(isConnected == false){
            try {
                userConnection(event, msg, user);
            } catch (IOException e) {
                System.out.println("connection failure at connect function");
                throw new RuntimeException(e);
            }
            client.listenForMessages(chatPane);
            conDisButton.setText("QUIT");
            isConnected = true;
        }
        else{
            Platform.exit();
            System.exit(1);
        }
    }
    //switch scene function
    public void switchToAuctionScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Auction-view.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    public void userConnection(ActionEvent event, JSON4msg msg, String user) throws IOException{
        try{
            Socket socket = new Socket("localhost", 1234); //generates socket (ip address, port)
            client = new Client(socket, user); //connects to the server through the socket
            msg.setProfile(user, client.getUuid()); //sets the user and the UUID in the json file
            client.sendMessage(msg.getProfile().toString()); //sends profile to server (username and UUID)
            System.out.println("Status: Connected");
            statusText.setText("Status: Connected");
            chatTextField.clear();
            bidTextField.clear();
            System.out.println(msg.profileToString()); //debug to see the username and UUID in console
        }catch (IOException e){
            // if connection is failed then show error message on scene
            System.out.println("Status: Disconnected");
            statusText.setText("Status: Disconnected");
        }
    }

    public void sendChatMessage(ActionEvent event) {
        String message = chatTextField.getText();
        try {
            if (message!= null && (!message.equals(""))) {
                msg.setMessage(message);
                client.sendMessage(msg.getProfile().toString());
                msg.resetMessage();
                chatTextField.setText("");
            }
        } catch (Exception o){
            chatTextField.setText("ERROR: Disconnected from server");
        }
    }

    public static HBox createHBox(String message){
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(5,5,5,5));

        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);

        textFlow.setStyle("-fx-background-color: rgb(233,233,235)");
        textFlow.setPadding(new Insets(5,10,5,10));
        hbox.getChildren().add(textFlow);

        return hbox;
    }

    public static void showMessage(String message, int code, VBox vbox){
        HBox hbox = createHBox(message);
        Platform.runLater(new Runnable() {
            @Override
            public void run(){
                vbox.getChildren().add(hbox);
            }
        });
    }

    /*@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*chatPane.heightProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue){
                chatScrollPane.setVvalue((Double) newValue);
            }
        });
        VBox vbox = chatPane;
    }*/
}