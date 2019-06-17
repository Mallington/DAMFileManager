package com.company.windows;

import com.company.UI_tools.Resource;
import com.company.UI_tools.StageLoader;
import com.company.UI_tools.StageRunnable;
import com.company.controllers.LoginWindowController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public abstract class LoginWindow {
    private LoginWindowController controller;
    private StageLoader<LoginWindowController> loader;

    public boolean exit = false;
    private static StageRunnable<LoginWindowController> SETUP =
            new StageRunnable<LoginWindowController>() {
                @Override
                protected Resource<LoginWindowController> setupStage(Stage stage) {
                    Resource<LoginWindowController> resource = new Resource<LoginWindowController>("LoginWindow.fxml");
                    stage.initStyle(StageStyle.UNDECORATED);

                    return resource;
                }
            };

    public LoginWindow(){
        loader = new StageLoader<LoginWindowController>();
    }

    public void showAndWait(String[] args, String userName, String password){
        controller = loader.open(args, SETUP);

        controller.setLoginListener(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.setStatus("Logging in...");
                if(login(controller.getLogin(), controller.getPassword())){
                    controller.setStatus("Login Succeeded");
                    exit = true;
                }
                else{
                    controller.setStatus("Login failed");
                }
            }
        });

        Platform.runLater(()->{
            controller.setLogin(userName);
            controller.setPassword(password);
        });

        while(!exit) try{Thread.sleep(100);} catch(Exception e){}
        controller.close();
    }

    public void showAndWait(String[] args){
        showAndWait(args, null, null);
    }

    public abstract boolean login(String username, String password);
}
