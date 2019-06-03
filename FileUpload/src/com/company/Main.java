package com.company;

import com.company.Controllers.FileViewScreenController;
import com.company.UITools.Resource;
import com.company.UITools.SceneUtils;
import com.company.UITools.StageLoader;
import com.company.UITools.StageRunnable;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.stage.Stage;

public class Main {

    public static void main(String[] args) {
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

        //new ObjectMapper().writeValueAsString(myItem);
    }

}
