package com.example.clienttcp;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;

import java.io.IOException;

import java.net.Socket;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TODO:
 * Admin Sign In:
 * - After typing admin in signInTextField change only signInLabel to "enter the password or type exit to go back as normal user"
 * - if successful show products-view,fxml (still todo : has series of text fields and a button to switch scene)
 * - after entering products and confirming, show Admin-view.fxml
  */

public class ClientController  {
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
    private VBox chatPane;
    @FXML
    private Text errorText;
    @FXML
    private VBox bidPane;
    @FXML
    private ScrollPane chatScrollPane;
    @FXML
    private ScrollPane bidScrollPane;
    @FXML
    private Button conDisButton;
    @FXML
    private AnchorPane chatAnchor;
    @FXML
    private AnchorPane bidAnchor;
    @FXML
    private PasswordField passField;
    @FXML
    private Button setProductsButton;
    @FXML
    private TextField p1,p2,p3,p4,p5,p6,p7,p8,p9,p10;
    @FXML
    private Button startAuctionButton;

    static private Client client;
    public static boolean isConnected = false;

    private Timer timer;
    private TimerTask task;
    private static String user;
    private static String pass = "admin1234";

    public JSON4msg msg = new JSON4msg();
    private static JSONArray products = new JSONArray();

    //submit action for sign in
    public void submit(ActionEvent event) {
        user = UserName.getText(); //gets the username from the text field in the scene
        user = user.trim();
        if (user != null && !(user.equals("ADMIN")) && !(user.equals(""))) {
            try {
                timer = new Timer();
                task = new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    switchToAuctionScene(event);
                                } catch (IOException e) {
                                    SignInLabel.setText("error in switchToAuctionScene try catch");
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
        else if((user.equals(""))){
            SignInLabel.setStyle("-fx-fill: red;");
            SignInLabel.setText("Please enter a username");
        }
        else if (user.equals("ADMIN") && passField.getText().equals("")) {
            passField.setVisible(true);
            SignInLabel.setText("Please enter a password");
        }
        else {
            String password = passField.getText();
            if (password.equals(pass) && UserName.getText().equals("ADMIN")) {
                try {
                    switchToProcductsScene(event);
                } catch (IOException e) {
                    System.out.println("Sing-In Failed");
                    SignInLabel.setStyle("-fx-fill: red;");
                    SignInLabel.setText("Sing-In Failed");
                }
            }
            else {
                SignInLabel.setStyle("-fx-fill: red;");
                SignInLabel.setText("Wrong credentials");
            }
        }
    }

    public void setUser(String username) {
        this.user = username;
    }

    public void connectDisconnect(ActionEvent event) throws IOException {
        try {
            if (isConnected == false) {
                userConnection(event, msg, user);
                client.listenForMessages(chatPane, bidPane, statusText, errorText, chatAnchor, bidAnchor, chatScrollPane, bidScrollPane);
                bidTextField.setTextFormatter(new DecimalTextFormatter(0, 2)); // had to put these here because in any other place the TextField is not initialized yet
                conDisButton.setText("Quit");
                isConnected = true;
            } else {
                Platform.exit();
                System.exit(1);
            }
        } catch (Exception e) {
            showError("connection to server failed");
        }
    }

    public Stage getStage(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        return stage;
    }

    //switch scene function
    public void switchToAuctionScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Auction-view.fxml"));
        Stage stage = getStage(event);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() // whenever closed with x on top bar, stops the process in the ide
        {
            public void handle(WindowEvent e){
                try {
                    System.exit(0);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void switchToProcductsScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("products-view.fxml"));
        Stage stage = getStage(event);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() // whenever closed with x on top bar, stops the process in the ide
        {
            public void handle(WindowEvent e){
                try {
                    System.exit(0);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void switchToAdmin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Admin-view.fxml"));
        Stage stage = getStage(event);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() // whenever closed with x on top bar, stops the process in the ide
        {
            public void handle(WindowEvent e){
                try {
                    System.exit(0);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void userConnection(ActionEvent event, JSON4msg msg, String user) throws IOException {
        try {
            try {
                Socket socket = new Socket("localhost", 1234); //generates socket (ip address, port)
                client = new Client(socket, user); //connects to the server through the socket
            } catch (Exception e) {
                throw new IOException();
            }
            msg.setProfile(user, client.getUuid()); //sets the user and the UUID in the json file
            client.sendMessage(msg.getProfile().toString()); //sends profile to server (username and UUID)
            if (!(user.equals("ADMIN")))
                statusText.setText("Status: Connected");
            else
                statusText.setText("Auction Status: Started");
            System.out.println(msg.profileToString()); //debug to see the username and UUID in console
        } catch (IOException e) {
            // if connection is failed then show error message on scene
            statusText.setText("Status: Disconnected");
            showError("connection to server failed");
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
            } else {
                throw new Exception();
            }
        } catch (Exception o) {
            if (statusText.getText().equals("Status: Disconnected")) {
                showError("Disconnected from server");
            } else {
                showError("Couldn't send message");
            }
        }
    }

    public void sendBid(ActionEvent event) {
        String message = bidTextField.getText();
        if (message != null && !(message.equals(",0") || message.equals(".0"))) {
            try {
                if (message.substring(0).equals("0")) {
                    message = message.replace(message.substring(0), "");
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
                    showError("Disconnected from server");
                } else {
                    showError("Couldn't send bid");
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

    public static void showMessage(String message, VBox vbox, AnchorPane anchor, ScrollPane sp) {
        HBox hbox = createHBox(message);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vbox.getChildren().add(hbox);
                if (vbox.getHeight() > 290) {
                    anchor.setMinHeight(vbox.getHeight() + 74);
                    sp.setVvalue(vbox.getHeight());
                }
                System.out.println("anchor:" + anchor.getHeight() + "vbox:" + vbox.getHeight());
            }
        });
    }

    public static void setDisconnected(Text text) {
        text.setText("Status: Disconnected");
    }

    public void showError(String error) {
        errorText.setText("ERROR: " + error);
        errorText.setTextAlignment(TextAlignment.CENTER);
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        errorText.setText("");
                    }
                });
            }
        };
        timer.schedule(task, 1000);
    }

    public static void setErrorText(Text text) {
        text.setText("ERROR: Disconnected by the server");
        text.setTextAlignment(TextAlignment.CENTER);
    }
    public void setProducts(ActionEvent event) {
        String prods = getProducts();
        String[] sortedProds = prods.split("/");
        for (String prod : sortedProds){
            if (prods != null && !(prod.equals(""))){
                JSONObject prodToSend = new JSONObject();
                prodToSend.put("username", "PRODUCT");
                prodToSend.put("message", prod);
                products.add(prodToSend);
            }
        }
        try {
            switchToAdmin(event);
        } catch (IOException e) {
            System.out.println("error switching to admin");
        }
    }

    public String getProducts(){
        String pr1 = p1.getText();
        String pr2 = p2.getText();
        String pr3 = p3.getText();
        String pr4 = p4.getText();
        String pr5 = p5.getText();
        String pr6 = p6.getText();
        String pr7 = p7.getText();
        String pr8 = p8.getText();
        String pr9 = p9.getText();
        String pr10 = p10.getText();
        String result = "" + pr1 + "/" +  pr2 + "/" +  pr3 + "/" +  pr4 + "/" +  pr5 + "/" +  pr6 + "/" +  pr7 + "/" +  pr8 + "/" +  pr9 + "/" +  pr10;
        return result;
    }

    public void startAuction(ActionEvent event){
        try {
            userConnection(event, msg, user);
            client.listenForMessages(chatPane, bidPane, statusText, errorText, chatAnchor, bidAnchor, chatScrollPane, bidScrollPane);
            for (Object j : products){
                client.sendMessage(j.toString());
            }        
        } catch (IOException e) {
            System.out.println("error in startAuction method");
        }
    }
}