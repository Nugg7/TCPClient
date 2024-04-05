package com.example.clienttcp;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class ClientMain extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("Client-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Client");
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

    public static void main(String[] args) {
        launch();
    }
}