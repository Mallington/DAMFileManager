package com.company;

import com.company.UI_tools.Resource;
import com.company.UI_tools.SceneUtils;
import com.company.UI_tools.StageLoader;
import com.company.UI_tools.StageRunnable;
import com.company.commandline.Parameter;
import com.company.commandline.ParameterParser;
import com.company.controllers.HomeScreenController;
import com.company.network.DAMAPI;
import com.company.windows.LoadingWindow;
import com.company.windows.LoginWindow;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Main {
    public static void main(String[] args) {
        //Parsing parameters
        ParameterParser parameterParser = formulateParams();
        parameterParser.parseParameters(args);

        DAMAPI.getDamApi().getApiPath().setProtocol(parameterParser.getID("protocol").getValue());
        DAMAPI.getDamApi().getApiPath().setAddress(parameterParser.getID("host").getValue());
        DAMAPI.getDamApi().getApiPath().setPort(parameterParser.getID("port").parseInteger());

        System.out.println(DAMAPI.getDamApi().getApiPath().getCompletePath());

            boolean quick = false;

            LoginWindow login = new LoginWindow() {
                @Override
                public boolean login(String username, String password) {
                    try {
                        return DAMAPI.getDamApi().connect(username, password);
                    } catch (Exception e) {
                        System.out.println("Connection to API Failed");
                        SceneUtils.displayOnPopupFXThread("Please check your connection or try setting the \"-host\" parameter");
                        return false;
                    }
                }
            };
            if (!quick) {
                login.showAndWait(args);
                new LoadingWindow(args).showAndHide(2000);
            } else {
                login.showAndWait(args, "mat", "mnimaih2c");
            }


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

            if (quick) Platform.runLater(() -> HomeScreenController.assets());
    }

    public static ParameterParser formulateParams(){
        return new ParameterParser().add(new Parameter("host", false)).add(new Parameter("port", false)).add(new Parameter("protocol", false));
    }

}
