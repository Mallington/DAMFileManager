package com.company;

import com.company.controllers.FileViewScreenController;
import com.company.UITools.Resource;
import com.company.UITools.SceneUtils;
import com.company.UITools.StageLoader;
import com.company.UITools.StageRunnable;
import javafx.stage.Stage;

public class Main {
    private  static String[] arguments = null;

    public static String[] getArguments() {
        return arguments;
    }

    public static void main(String[] args) {
        arguments = args;
        StageLoader<FileViewScreenController> mainStage = new StageLoader<FileViewScreenController>();

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


        FileViewScreenController fileViewScreenController = mainStage.open(args, setup);
    }

}
