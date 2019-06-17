package com.company.controllers;

import com.company.UI_tools.SceneUtils;
import com.company.data.MenuResponse;
import com.company.file_management.ManagementUtils;
import com.company.network.DAMAPI;
import com.company.data.Job;
import com.company.data.SMBCredentials;
import com.company.network.SMBCopy;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setupTable(jobTable, tableAttributes);

        jobSearchField.setOnKeyPressed(ke -> { if (ke.getCode().equals(KeyCode.ENTER)) { try { searchJob(); } catch (IOException e) {}}});
        Platform.runLater(()->{jobSearchField.getScene().setOnKeyPressed(ke -> {if (ke.getCode().equals(KeyCode.ESCAPE)) back(); }); });

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

                    try { displayJobs(currentJobs = DAMAPI.getDamApi().getAssetByJobID(finalJobID));
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
                String fetch = j.getByReference(info);
                field.setText((fetch ==null)? "-":fetch);
            }
            catch(Exception e) {
                e.printStackTrace();
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
                    row.setStyle("-fx-text-fill:  white");
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
                menu = new ContextMenu();
                for (MenuResponse men : DAMAPI.getDamApi().getActionables(j)) {
                    MenuItem menuItem = new MenuItem(men.getMenuText());

                    menuItem.setOnAction(getContextEventByName(currentJobs.get(row), men.getMenuText()));

                    menu.getItems().add(menuItem);
                }
            }
            catch (Exception e){
                System.out.println("Failed to build context menu");
            }

            return menu;
    }

    public EventHandler<ActionEvent> getContextEventByName(Job job, String name){
        Runnable toRun;

        switch (name){
            case "View Asset":
                toRun = ()->{
                    ManagementUtils.quickView(job, getCopyInstance());
                };
                break;

            default:
                toRun = ()-> SceneUtils.displayOnPopupFXThread("Operation is not yet supported.");
                break;
        }

        return event -> new Thread(toRun).start();
    }

    private void createColumn(TableView table ,String fieldName){
        TableColumn column = new TableColumn(fieldName);

        column.setCellFactory(col -> {
            return new TableCell<Job, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    try {
                        Job j = currentJobs.get(getTableRow().getIndex());
                        setText(j.getByReference(fieldName));

                        if(fieldName.equals("status")){
                            setStyle("-fx-background-color: "+j.getColour()+"; -fx-text-fill: white;");
                        }
                    }
                    catch (Exception e){}
                }
            };
        });
        table.getColumns().addAll(column);
    }

    private void clearTables(){
        jobTable.getItems().clear();
    }

    //Drag and drop handlers

    @FXML
    public void dragOver(DragEvent event){
        event.acceptTransferModes(TransferMode.COPY);
    }
    @FXML
    public void dropped(DragEvent event){
        List<File> files = event.getDragboard().getFiles();
        new Thread(()->{
                for(File file : files) handleDroppedFile(file);
            Platform.runLater(()->jobTable.getParent().setDisable(false));
        }).start();
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


    //Remote File handling
    private void uploadFile(File f, String name){
        SMBCopy copier = getCopyInstance();

        if(copier!=null){
            System.out.println("Copying as "+name+", from "+f.getPath());
            copier.copy(f, name);
        }
    }

    public SMBCopy getCopyInstance() {
        SMBCredentials credentials = getCredentials();

        if (credentials != null) {
            return new SMBCopy(credentials) {
                @Override
                public void start() {
                    Platform.runLater(()-> statusText.setText("Copying file"));
                }

                @Override
                public void updateProgress(double decimal) {
                    Platform.runLater(() -> setProgress(decimal));
                }

                @Override
                public void failed() {
                    Platform.runLater(() -> updateProgress(0.0));
                    statusText.setText("Copy Failed.");
                }

                @Override
                public void completeJob() {
                    Platform.runLater(() -> updateProgress(1.0));
                    statusText.setText("Copy Complete");
                }
            };
        } else {
            return null;
        }
    }
    private SMBCredentials getCredentials(){
        try { return DAMAPI.getDamApi().getSMBCredentials();} catch (Exception e) {return null;}
    }



}
