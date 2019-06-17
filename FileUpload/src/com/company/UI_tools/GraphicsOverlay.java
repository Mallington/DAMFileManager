package com.company.UI_tools;

import com.company.controllers.WindowsFunctions;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
/*
Not yet functional
 */
public class GraphicsOverlay {
    private String fxmlResource;
    private Node baseLayer;

    public GraphicsOverlay(String fxmlResource, Node baseLayer) throws IOException {
        this.fxmlResource = fxmlResource;
        this.baseLayer = baseLayer;
        Stage inputStage = new Stage();
        inputStage.initOwner(baseLayer.getScene().getWindow());
        inputStage.setScene(new Scene(new Resource<WindowsFunctions>("").getNode()));
        inputStage.showAndWait();
        }

    }


