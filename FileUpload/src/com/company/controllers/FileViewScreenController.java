package com.company.controllers;

import com.company.UITools.SceneUtils;
import com.company.data.MenuResponse;
import com.company.network.GeneralAPI;
import com.company.data.Job;
import com.company.data.SMBCredentials;
import com.company.network.SMBCopy;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FileViewScreenController implements Initializable {
    @FXML
    TextField jobSearchField;

    @FXML
    Text statusText;

    @FXML
    Text progressPercentageText;

    @FXML
    ProgressBar progressBar;

    @FXML
    TableView<Job> jobTable;


    private List<Job> currentJobs = null;


    public static final String[] topLevel = {"title", "jobNo", "bookType", "customer", "coverFinish", "extent", "headMargin", "backMargin", "spineBulk"};
    public static final String[] tableAttributes = {"assetId", "assetType", "height", "isbn", "width", "status"};

    private GeneralAPI<Job> jobAPI = new GeneralAPI<>("http://AllingtonServer:86/api/asset/GetAsset/{0}", Job.class);
    private GeneralAPI<SMBCredentials> fileCredentials = new GeneralAPI<>("http://AllingtonServer:86/api/asset/GetUploadPath", SMBCredentials.class);
    private GeneralAPI<MenuResponse> contextMenu = new GeneralAPI<>("http://AllingtonServer:86/api/asset/GetMenu/{0}/{1}", MenuResponse.class);
    //private GraphicsOverlay dragArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setupTable(jobTable, tableAttributes);

        jobSearchField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                try {
                    searchJob();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Platform.runLater(()->{jobSearchField.getScene().setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ESCAPE)) {
               back();
            }
        }); });




        Platform.runLater(()-> jobSearchField.requestFocus());

        Platform.runLater(()->jobSearchField.getScene().getWindow().centerOnScreen());
    }

    public void setProgress(double decimal){
        Platform.runLater(()->{
            progressBar.setProgress(decimal);
            progressPercentageText.setText((int)(100*decimal)+"%");
        });
    }

    public void back(){
        SceneUtils.setView((Stage)jobSearchField.getScene().getWindow(), "HomeScreen.fxml");
    }

    public void searchJob() throws IOException {
        setProgress(0);
        String fieldVal = jobSearchField.getText().trim();
        int jobID = -1;
        try{ jobID = Integer.parseInt(fieldVal);} catch(Exception e){}

        if(fieldVal !=null &&fieldVal.matches("[0-9]{1,10}") && jobID !=-1){
                statusText.setText("Fetching");
                setProgress(0.5);
            int finalJobID = jobID;
            new Thread(()->{
                    clearTables();

                    try { displayJobs(currentJobs = jobAPI.fetchList( finalJobID +""));
                        setProgress(1.0);
                    } catch (Exception e) {
                    Platform.runLater(()->statusText.setText("Search Failed (No Connection)"));
                }

                }).start();

        } else if(fieldVal.equals("")){
            jobSearchField.requestFocus();
        }
        else{
            SceneUtils.displayOnPopupFXThread("Invalid Input");
            jobSearchField.setText(null);
            statusText.setText("Invalid Input");
        }
    }

    public void displayHeaderInfo(Job j){

        for(String info : topLevel){
            try {
                TextField field = (TextField) jobSearchField.getScene().lookup("#" + info);
                field.setText(j.getByReference(info));
            }
            catch(Exception e) {
                System.out.println("Failed to populate: "+info);
            }
        }
    }
    public void displayJobs(List<Job> jobs){
        if(jobs.size()>0) {
            displayHeaderInfo(jobs.get(0));
            for(Job j : jobs) {
                jobTable.getItems().add(j);
            }
            jobTable.getSelectionModel().select(0);
            Platform.runLater(()->statusText.setText("Found "+jobs.size()+" asset(s)"));
        }
        else{
            Platform.runLater(()->{
                statusText.setText("No Results");
            });
            SceneUtils.displayOnPopupFXThread("No results");
        }
    }
    private void setupTable(TableView<Job> table, String[] attributes){

        table.setRowFactory(tv -> {
            TableRow<Job> row = new TableRow<>();
            row.setOnDragOver(event -> {
                if (!row.isEmpty()) {
                    table.getSelectionModel().select(row.getIndex());
                    row.setBackground(new Background(new BackgroundFill(Color.valueOf("#d7df23"), new CornerRadii(3.0), Insets.EMPTY)));
                }
            });
            row.setOnDragExited(event -> {
                table.getSelectionModel().select(row.getIndex());
                row.setBackground(Background.EMPTY);
            });
            row.setOnDragDropped(event -> {
                table.getSelectionModel().select(row.getIndex());
                row.setBackground(Background.EMPTY);
            });

            row.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(event.getButton().equals(MouseButton.SECONDARY) &&!row.isEmpty()) row.setContextMenu(buildMenu(row.getIndex()));
                }
            });
            return row ;
        });


        for(String at : attributes) createColumn(table, at);
    }

    public ContextMenu buildMenu(int row){
            ContextMenu menu = null;
            try {
                Job j = currentJobs.get(row);
                List<MenuResponse> menuResponses = contextMenu.fetchList(j.getJobNo() + "", j.getAssetId() + "");
                menu = new ContextMenu();
                for (MenuResponse men : menuResponses) menu.getItems().add(new MenuItem(men.getMenuText()));
            }
            catch (Exception e){
                System.out.println("Failed to build context menu");
            }

            return menu;
    }

    private void createColumn(TableView table ,String fieldName){
        TableColumn column = new TableColumn(fieldName);
        column.setCellValueFactory(new PropertyValueFactory<Job, String>(fieldName));
        table.getColumns().addAll(column);
    }

    private void clearTables(){
        jobTable.getItems().clear();
    }

    private void handleDroppedFile(File dropped){
        try {
            int focused = jobTable.getSelectionModel().getFocusedIndex();
            Platform.runLater(()-> {if(currentJobs!=null && dropped !=null)statusText.setText("Uploading \""+dropped.getName()+"\" (Asset No. "+currentJobs.get(focused).getAssetId()+")");});

            //File upload
            uploadFile(dropped, currentJobs.get(focused).getJobNo()+"_"+currentJobs.get(focused).getAssetId()+dropped.getName().substring(dropped.getName().lastIndexOf(".")));

            Platform.runLater(()->statusText.setText("Done."));
        }
        catch(Exception e){
            SceneUtils.displayOnPopupFXThread("Please search for a jobAPI");
        }
    }

    @FXML
    public void dragOver(DragEvent event){
        event.acceptTransferModes(TransferMode.COPY);
    }
    @FXML
    public void dropped(DragEvent event){
        List<File> files = event.getDragboard().getFiles();
        new Thread(()->{
            //Platform.runLater(()->jobTable.getParent().setDisable(true));
                for(File file : files) handleDroppedFile(file);
            Platform.runLater(()->jobTable.getParent().setDisable(false));
        }).start();
    }

    private void uploadFile(File f, String name){
        SMBCredentials credentials = getCredentials();

        if(credentials !=null){
            SMBCopy copier = new SMBCopy(credentials) {
                @Override
                public void updateProgress(double decimal) {
                    Platform.runLater(()->setProgress(decimal));
                }

                @Override
                public void failed() {
                    Platform.runLater(()->updateProgress(0.0));
                    statusText.setText("Copy Failed.");
                }

                @Override
                public void completeJob() {
                    Platform.runLater(()->updateProgress(1.0));
                    statusText.setText("Copy Complete");
                }
            };
            System.out.println("Copying as "+name+", from "+f.getPath());
            copier.copy(f, name);
        }
    }

    private SMBCredentials getCredentials(){
        try {
            System.out.println(fileCredentials.fetch());
            return fileCredentials.fetch();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}
