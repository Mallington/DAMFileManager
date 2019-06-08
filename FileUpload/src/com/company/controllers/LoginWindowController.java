package com.company.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginWindowController implements Initializable {
    @FXML
    private PasswordField password;

    @FXML
    private TextField login;

    @FXML
    private Text status;

    @FXML
    private Button loginButton;


    private EventHandler<KeyEvent> keyHandler = ke -> {
        if (ke.getCode().equals(KeyCode.ENTER)) loginButton.fire();
        else if(ke.getCode().equals(KeyCode.UP) || ke.getCode().equals(KeyCode.DOWN)){
            if(login.isFocused()) password.requestFocus();
            else login.requestFocus();
        }
    };
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Here");
        status.setText("Please Login");

        login.setOnKeyPressed(keyHandler);
        password.setOnKeyPressed(keyHandler);
    }

    public String getPassword() {
        return password.getText();
    }

    public String getLogin() {
        return login.getText();
    }

    public void setStatus(String statusText){
        Platform.runLater(()->status.setText(statusText));
    }

    public void setLoginListener(EventHandler<ActionEvent> handler){
        Platform.runLater(()->loginButton.setOnAction(handler));
    }

    public void close() {
        Platform.runLater(()->loginButton.getScene().getWindow().hide());
    }
}
