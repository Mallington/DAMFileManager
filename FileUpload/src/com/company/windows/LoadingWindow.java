package com.company.windows;

import com.company.UI_tools.Resource;
import com.company.UI_tools.StageLoader;
import com.company.UI_tools.StageRunnable;
import com.company.controllers.LoadingScreenController;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoadingWindow {

private static StageRunnable<LoadingScreenController> SETUP =
    new StageRunnable<LoadingScreenController>() {
        @Override
        protected Resource<LoadingScreenController> setupStage(Stage stage) {
            Resource<LoadingScreenController> resource = new Resource<LoadingScreenController>("LoadingScreen.fxml");
            stage.initStyle(StageStyle.UNDECORATED);

            return resource;
        }
    };

private LoadingScreenController controller = null;
private String[] args;

    public LoadingWindow(String[] args) {
        this.args = args;
    }

    public void showLoadingScreen(){
        if(controller == null){
            controller = new StageLoader<LoadingScreenController>().open(args, SETUP);
        }
        else{
            controller.showWindow();
        }
    }

    public void showAndHide(int wait){
        showLoadingScreen();
        try {Thread.sleep(wait);} catch (InterruptedException e) {}
        hideLoadingScreen();
    }

    public void hideLoadingScreen(){
        if(controller !=null){
            controller.hideWindow();
        }
    }

    public LoadingScreenController getController() {
        return controller;
    }
}
