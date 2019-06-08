package com.company.windows;

import com.company.UITools.Resource;
import com.company.UITools.StageLoader;
import com.company.UITools.StageRunnable;
import com.company.controllers.HomeScreenController;
import com.company.controllers.LoadingScreenController;
import com.company.controllers.LoginWindowController;
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

    public void showAndWait(String[] args){
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

        while(!exit) try{Thread.sleep(100);} catch(Exception e){}
        controller.close();
    }

    public abstract boolean login(String username, String password);
}
