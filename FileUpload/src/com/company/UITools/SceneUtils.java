package com.company.UITools;

import com.company.UITools.Resource;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SceneUtils<controller> {


    private static List<Resource> resourceCache = new ArrayList<>();

    private static List<EventHandler<ActionEvent>> actionEventsList(EventHandler<ActionEvent>... events){
        List<EventHandler<ActionEvent>> eventsList = new ArrayList<EventHandler<ActionEvent>>();

        for(EventHandler<ActionEvent> e : events) eventsList.add(e);
        return eventsList;
    }

    public Resource<controller> getResource(String FXML){
        Resource r = lookupCachedResource(FXML);

        if(r ==null) r= new Resource<>(FXML);

        return r;
    }


    public static void clearCache(){
        resourceCache = new ArrayList<Resource>();
    }
    public static void setView(Stage stage, String FXML){
        Resource resource = lookupCachedResource(FXML);

        try {
            if(resource == null) {
                resource = new Resource(FXML);
            }

            if(resource.getNode().getScene() == null){
                stage.setScene(new Scene(resource.getNode()));
            }
            else{
                stage.setScene(resource.getNode().getScene());
            }


         } catch (IOException e) {
            System.out.println("Failed to change view to: "+FXML);
            e.printStackTrace();
        }
    }

    public static void cacheResource(Resource r){
        resourceCache.add(r);
    }

    public static Resource lookupCachedResource(String resource){
        for(Resource r : resourceCache) if(r.getResourceLocation().equals(resource)) return r;
        return null;
    }

}
