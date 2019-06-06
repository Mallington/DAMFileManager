package com.company;

import com.company.controllers.FileViewScreenController;
import com.company.UITools.Resource;
import com.company.UITools.SceneUtils;
import com.company.UITools.StageLoader;
import com.company.UITools.StageRunnable;
import com.company.controllers.HomeScreenController;
import com.company.windows.LoadingWindow;
import javafx.stage.Stage;

public class Main {
    public static void main(String[] args) {
        /*StageLoader<FileViewScreenController> mainStage = new StageLoader<FileViewScreenController>();

        StageRunnable<FileViewScreenController> setup = new StageRunnable<FileViewScreenController>() {
            @Override
            protected Resource<FileViewScreenController> setupStage(Stage stage) {
                Resource<FileViewScreenController> resource =
                        new SceneUtils<FileViewScreenController>().getResource("FileViewScreen.fxml");

                stage.setMinWidth(300);
                stage.setMinHeight(300);

                return resource;
            }
        };

        FileViewScreenController fileViewScreenController = mainStage.open(args, setup); */

        new LoadingWindow(args).showAndHide(2000);

        StageLoader<HomeScreenController> mainStage = new StageLoader<HomeScreenController>();

        StageRunnable<HomeScreenController> setup = new StageRunnable<HomeScreenController>() {
            @Override
            protected Resource<HomeScreenController> setupStage(Stage stage) {
                Resource<HomeScreenController> resource =
                        new SceneUtils<HomeScreenController>().getResource("HomeScreen.fxml");

                stage.setMinWidth(300);
                stage.setMinHeight(300);

                return resource;
            }
        };

        HomeScreenController HomeScreenController = mainStage.open(args, setup);
    }

}
