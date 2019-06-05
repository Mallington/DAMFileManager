package com.company.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoadingScreenController implements Initializable {
    @FXML
    AnchorPane mainPane;

    @FXML
    Text infoText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Loading screen...");
        mainPane.requestFocus();
    }


    public void hideWindow(){
        Platform.runLater(()->{getStage().hide(); });
    }

    public void showWindow() {
        Platform.runLater(() -> {
            getStage().show();
        });
    }



    public void clicked(){

    }

    private Stage getStage(){
        return (Stage) mainPane.getScene().getWindow();
    }
}
