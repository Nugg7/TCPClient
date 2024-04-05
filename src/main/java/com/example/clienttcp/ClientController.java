package com.example.clienttcp;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

import java.util.Timer;
import java.util.TimerTask;

// TODO: add an error label for when the client is disconnected and client tries to send messages or bids (since after connections bid textField is only numeric)

public class ClientController {
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
    private ScrollPane bidScrollPane;
    @FXML
    private Button conDisButton;
    static private Client client;
    public static boolean isConnected = false;

    private Timer timer;
    private TimerTask task;
    private static String user;
    public JSON4msg msg = new JSON4msg();

    //submit action for sign in
    public void submit(ActionEvent event) {
        user = UserName.getText(); //gets the username from the text field in the scene
        if (user != null && !(user.equals("admin")) && !(user.equals(""))) {
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
            }
            System.out.println("Sing-In Successful");
            SignInLabel.setStyle("-fx-fill: green;");
            SignInLabel.setText("Sing-In Successful");
            setUser(user);
        }
    }

    public void setUser(String username) {
        this.user = username;
    }

    public void connectDisconnect(ActionEvent event) {
        try{
            if (isConnected == false) {
                userConnection(event, msg, user);
                client.listenForMessages(chatPane, bidPane, statusText);
                bidTextField.setTextFormatter(new DecimalTextFormatter(0, 2)); // had to put these here because in any other place the TextField is not initialized yet
                conDisButton.setText("Quit");
                isConnected = true;
            } else {
                Platform.exit();
                System.exit(1);
            }
        }catch (Exception e){
            System.out.println("ERROR: connection to server failed");
        }
    }

    //switch scene function
    public void switchToAuctionScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Auction-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    public void userConnection(ActionEvent event, JSON4msg msg, String user) throws IOException {
        try {
            try{
                Socket socket = new Socket("localhost", 1234); //generates socket (ip address, port)
                client = new Client(socket, user); //connects to the server through the socket
            }
            catch (Exception e){
                throw new IOException();
            }
            msg.setProfile(user, client.getUuid()); //sets the user and the UUID in the json file
            client.sendMessage(msg.getProfile().toString()); //sends profile to server (username and UUID)
            statusText.setText("Status: Connected");
            chatTextField.clear();
            bidTextField.clear();
            bidTextField.setText("");
            System.out.println(msg.profileToString()); //debug to see the username and UUID in console
        } catch (IOException e) {
            // if connection is failed then show error message on scene
            statusText.setText("Status: Disconnected");
        }
    }

    public void sendChatMessage(ActionEvent event) {
        String message = chatTextField.getText();
        try {
            if (message != null && (!message.equals(""))) {
                msg.setMessage(message + "\u200e"); //invisible character in case message is only numbers
                client.sendMessage(msg.getProfile().toString());
                msg.resetMessage();
                chatTextField.clear();
            }
            else{
                throw new Exception();
            }
        } catch (Exception o) {
            if (statusText.getText().equals("Status: Disconnected")) {
                chatTextField.setText("ERROR: Disconnected from server");
                timer = new Timer();
                task = new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                chatTextField.clear();
                            }
                        });
                    }
                };
                timer.schedule(task, 1000);
            }
            else {
                chatTextField.setText("ERROR: couldn't send message");
                timer = new Timer();
                task = new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                chatTextField.clear();
                            }
                        });
                    }
                };
                timer.schedule(task, 1000);
            }
        }
    }

    public void sendBid(ActionEvent event) {
        String message = bidTextField.getText();
        if (message != null && !(message.equals(",0") || message.equals(".0"))) {
            try {
                if (message.substring(0).equals("0")) {
                    message = message.replace(message.substring(0),"");
                }
                message = message.replace(",", ".");
                message = message.replaceAll(" ", "");
                double value = Double.parseDouble(message);
                msg.setMessage(message);
                client.sendMessage(msg.getProfile().toString());
                bidTextField.clear();
            } catch (Exception e) {
                bidTextField.clear(); // This only works before connection since the textField accepts only numbers after connection
                if (statusText.getText().equals("Status: Disconnected")) {
                    bidTextField.setText("ERROR: Disconnected from server");
                    timer = new Timer();
                    task = new TimerTask() {
                        @Override
                        public void run() {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    bidTextField.clear();
                                }
                            });
                        }
                    };
                    timer.schedule(task, 1000);
                } else {
                    bidTextField.setText("ERROR: couldn't send bid");
                }
            }
        }
    }

    public static HBox createHBox(String message) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(5, 5, 5, 5));

        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);

        textFlow.setStyle("-fx-background-color: rgb(233,233,235)");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        hbox.getChildren().add(textFlow);

        return hbox;
    }

    public static void showMessage(String message, VBox vbox) {
        HBox hbox = createHBox(message);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vbox.getChildren().add(hbox);
            }
        });
    }

    public static void setDisconnected(Text text){
        text.setText("Status: Disconnected");
    }
}