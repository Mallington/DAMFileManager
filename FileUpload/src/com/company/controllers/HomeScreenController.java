package com.company.controllers;

import com.company.UITools.SceneUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Author: Mathew Allington
 */
public class HomeScreenController implements Initializable {

    @FXML
    AnchorPane mainPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }

    public Stage getStage(){
        return ((Stage)mainPane.getScene().getWindow());
    }

    public void assets(){
        SceneUtils.setView(getStage(), "FileViewScreen.fxml");
    }

}
