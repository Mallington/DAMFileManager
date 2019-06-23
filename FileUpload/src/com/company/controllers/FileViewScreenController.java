package com.company.controllers;


import animatefx.animation.*;
import com.company.UI_tools.FilePicker;
import com.company.UI_tools.SceneUtils;
import com.company.data.MenuResponse;
import com.company.file_management.AssetFile;
import com.company.file_management.AssetManager;
import com.company.file_management.AssetUtils;
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
import javafx.scene.layout.BorderPane;
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

    @FXML
    BorderPane mainPane;

    public static final String[] topLevel = {"title", "jobNo", "bookType", "customer", "coverFinish", "extent", "headMargin", "backMargin", "spineBulk"};
    public static final String[] tableAttributes = {"assetId", "assetType", "height", "isbn", "width", "status"};

    private int currentJobID =-1;

    AssetManager assetManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assetManager = new AssetManager(getCopyInstance());

        if(assetManager == null) System.out.println("Failed to init asset manager");

        setupTable(jobTable, tableAttributes);
        setupTimer();

        jobSearchField.setOnKeyPressed(ke -> { if (ke.getCode().equals(KeyCode.ENTER)) { try { searchJob(); } catch (IOException e) {}}});
        Platform.runLater(()->{jobSearchField.getScene().setOnKeyPressed(ke -> {if (ke.getCode().equals(KeyCode.ESCAPE)) back(); }); });

        Platform.runLater(()-> jobSearchField.requestFocus());
        Platform.runLater(()->jobSearchField.getScene().getWindow().centerOnScreen());

    }

    public void setProgress(double decimal){

            progressBar.setProgress(decimal);
            progressPercentageText.setText((int)(100*decimal)+"%");
    }

    public void back(){
        SceneUtils.setView((Stage)jobSearchField.getScene().getWindow(), "HomeScreen.fxml");
    }

    public void updateView(boolean deepRefresh){
        if(currentJobID>=0) {
            try {
                displayJobs(DAMAPI.getDamApi().getAssetByJobID(currentJobID), deepRefresh);
            } catch (Exception e) {
                System.out.println("caught");
                Platform.runLater(() -> statusText.setText("Search Failed (No Connection)"));
            }
        }
    }

    public void searchJob() throws IOException {
        String fieldVal = jobSearchField.getText().trim();
        int jobID = -1;
        try{ jobID = Integer.parseInt(fieldVal);} catch(Exception e){}

        if(fieldVal !=null &&fieldVal.matches("[0-9]{1,10}") && jobID !=-1){
                setProgress(0);
                statusText.setText("Fetching");
                setProgress(0.5);
                 currentJobID =jobID;
                new Thread(()->Platform.runLater(()->updateView(true))).start();

        } else if(fieldVal.equals("")){
            jobSearchField.requestFocus();
        }
        else{
            SceneUtils.displayOnPopupFXThread("Invalid Input");
            jobSearchField.setText(null);
            statusText.setText("Invalid Input");

            new Shake(jobSearchField).play();
        }
    }

    public void displayHeaderInfo(Job j, boolean deepRefresh){

        for(String info : topLevel){
            try {
                TextField field = (TextField) jobSearchField.getScene().lookup("#" + info);

                //For editing of attributes
                if(field.isEditable()) {
                    field.setOnKeyPressed(ke -> {
                        if (ke.getCode().equals(KeyCode.ENTER)) {
                            try {
                                field.setStyle("-fx-border-color: green;");
                                DAMAPI.getDamApi().updateJobAttribute(info, field.getText(), j);
                                updateViewInABit();

                            } catch (IOException e) {
                                SceneUtils.displayOnPopupFXThread("Failed to set attribute: " + info);
                            }
                        }
                        else{
                            field.setStyle("-fx-border-color: red;");
                        }
                    });
                }

                String fetch = j.getByReference(info);
                if(deepRefresh) new FadeIn(field).play();
                field.setText((fetch ==null)? "-":fetch);
            }
            catch(Exception e) {
                e.printStackTrace();
                System.out.println("Failed to populate: "+info);
            }
        }
    }
    public void displayJobs(List<Job> jobs, boolean deepRefresh){
        if(jobs.size()>0) {
            displayHeaderInfo(jobs.get(0), deepRefresh);

            if(deepRefresh){
                System.out.println("Deep");
                clearTables();
                for(Job j : jobs) jobTable.getItems().add(j);
                jobTable.getSelectionModel().select(0);
               jobTable.refresh();
                setProgress(1.0);
                statusText.setText("Found "+jobs.size()+" asset(s)");
                //Animation on refresh
                new FadeIn(jobTable).play();
            }
            else {
                int prevSelect = jobTable.getSelectionModel().getSelectedIndex();

                boolean changed = false;

                for (int i = 0; i < jobs.size(); i++) {
                    if (!jobs.get(i).matchesStatus(jobTable.getItems().get(i))) {
                        jobTable.getItems().set(i, jobs.get(i));
                        System.out.println("Update "+i);
                        changed = true;
                    }
                }
                if(changed) {

                        jobTable.getSelectionModel().select(prevSelect);
                        jobTable.refresh();
                }
            }
        }
        else{
                statusText.setText("No Results");
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

    public void setupTimer(){
       /* new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateView(false);
            }
        }, 0, 300); */

      /*  ScheduledService svc = new ScheduledService<Void>() {
            protected Task createTask() {
                return new Task() {
                    protected Void call() {
                        System.out.println(jobTable.getItems().size());
                        try {
                            updateView(false);
                        }
                        catch (Exception e){
                            System.out.println("Failed to update the view");
                        }
                        return null;
                    }
                };
            }
        };
        svc.setPeriod(Duration.millis(500));
        svc.start(); */


    }

    public ContextMenu buildMenu(int row){
            ContextMenu menu = null;
            try {
                Job j = jobTable.getItems().get(row);
                menu = new ContextMenu();
                for (MenuResponse men : DAMAPI.getDamApi().getActionables(j)) {
                    MenuItem menuItem = new MenuItem(men.getMenuText());

                    menuItem.setOnAction(getContextEventByName(jobTable.getItems().get(row), men.getMenuText()));

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
                    if(!assetManager.viewAsset(job)) SceneUtils.displayOnPopupFXThread("Failed to view asset ID: "+job.getAssetId()+"\n There may be no asset, try uploading one.");
                };
                break;

            case "Checkout Asset":
                toRun = ()->{
                    AssetFile assetFile = assetManager.checkoutJob(job);

                    if(assetFile !=null) {
                        AssetUtils.openPDFAsset(assetFile);
                        updateViewInABit();
                    }
                    else SceneUtils.displayOnPopupFXThread("Failed to checkout asset ID: "+job.getAssetId()+"\n By accident? Or mad hacking skillz!?");
                };
                break;

            case "Commit":
                toRun = ()->{
                    if(assetManager.commitCheckout(job)) {
                        updateViewInABit();
                    }
                    else {
                        SceneUtils.displayOnPopupFXThread("Failed to commit asset ID: "+job.getAssetId()+"\n Please specify the location of the file you'd like to commit.");
                        FilePicker picker =  new FilePicker(".pdf", "PDF File", "Untitled");
                        Stage stage = (Stage)jobTable.getScene().getWindow();
                        Platform.runLater(()->{
                            File toGet = picker.getFile( stage, FilePicker.OPEN);

                            //need to add "Mend Asset"
                            assetManager.checkoutJob(job);

                            new Thread(()->assetManager.getAssetFile(job).mendTemporaryFile(toGet)).start();
                        });

                    }
                };
                break;

            case "Upload Asset":
                toRun = ()->{
                    Stage stage = (Stage)jobTable.getScene().getWindow();

                   FilePicker picker =  new FilePicker(".pdf", "PDF File", "Untitled");

                   Platform.runLater(()->{
                       File toUpload = picker.getFile(stage, FilePicker.OPEN);
                        new Thread(()->{
                            uploadFile(toUpload, job.getJobNo()+"_"+job.getAssetId()+".pdf");
                            updateViewInABit();
                       }).start();
                   });
                };
                break;

            default:
                toRun = ()-> SceneUtils.displayOnPopupFXThread("Operation is not yet supported.");
                break;
        }

        return event -> new Thread(toRun).start();
    }

    private void updateViewInABit(){
        new Thread(()->{

            try{Thread.sleep(2500); } catch (Exception e){}
            Platform.runLater(()-> updateView(false));

        }).start();
    }

    private void createColumn(TableView table ,String fieldName){
        TableColumn column = new TableColumn(fieldName);

        column.setCellFactory(col -> {
            return new TableCell<Job, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    try {
                        Job j = jobTable.getItems().get(getTableRow().getIndex());
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
            //May hold up drag (beware)
            updateView(false);
            Platform.runLater(()-> {if(jobTable.getItems()!=null && dropped !=null)statusText.setText("Uploading \""+dropped.getName()+"\" (Asset No. "+jobTable.getItems().get(focused).getAssetId()+")");});

            //File upload
            uploadFile(dropped, jobTable.getItems().get(focused).getJobNo()+"_"+jobTable.getItems().get(focused).getAssetId()+dropped.getName().substring(dropped.getName().lastIndexOf(".")));

            Platform.runLater(()->statusText.setText("Done."));

            updateViewInABit();
        }
        catch(Exception e){
            SceneUtils.displayOnPopupFXThread("Please search for a jobAPI");
        }
    }


    //Remote File handling
    private void uploadFile(File f, String name){
        SMBCopy copier = getCopyInstance();

        if(copier!=null){
            copier.setCredentials(getCredentials());
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
                    Platform.runLater(() -> {
                        updateProgress(0.0);
                        statusText.setText("Copy Failed.");
                    });
                }

                @Override
                public void completeJob() {
                    Platform.runLater(() -> {
                        updateProgress(1.0);
                        statusText.setText("Copy Complete");
                    });
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
