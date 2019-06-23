package com.company.controllers;

import animatefx.animation.*;
import com.company.UI_tools.SceneUtils;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Author: Mathew Allington
 */
public class HomeScreenController implements Initializable {

    @FXML
    AnchorPane mainPane;

    @FXML
    TilePane appPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mainPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.DIGIT1)) assets();
            }
        });
        Platform.runLater(()->mainPane.requestFocus());
        Platform.runLater(()->new ZoomIn(appPane).play());
    }

    public Stage getStage(){
        return ((Stage)mainPane.getScene().getWindow());
    }

    public void assets(){
        SceneUtils.setView(getStage(), "FileViewScreen.fxml");
    }

}
