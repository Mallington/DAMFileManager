package com.company.controllers;

import com.company.UITools.SceneUtils;
import com.company.network.GeneralAPI;
import com.company.data.Job;
import com.company.data.SMBCredentials;
import com.company.network.SMBCopy;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;

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
    TableView jobTable;

    @FXML
    TableView highLevelTable;

    private List<Job> currentJobs = null;


    public static final String[] topLevel = {"title", "jobNo", "bookType", "customer", "coverFinish", "extent", "headMargin", "backMargin", "spineBulk"};
    public static final String[] tableAttributes = {"assetId", "assetType", "height", "isbn", "width", "status"};

    private GeneralAPI<Job> jobAPI = new GeneralAPI<>("http://AllingtonServer:86/api/asset/GetAsset/{0}", Job.class);
    private GeneralAPI<SMBCredentials> fileCredentials = new GeneralAPI<>("http://AllingtonServer:86/api/asset/GetUploadPath", SMBCredentials.class);
    //private GraphicsOverlay dragArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable(highLevelTable, topLevel);
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
        Platform.runLater(()-> jobSearchField.requestFocus());

        Platform.runLater(()->jobSearchField.getScene().getWindow().centerOnScreen());
    }

    public void setProgress(double decimal){
        Platform.runLater(()->{
            progressBar.setProgress(decimal);
            progressPercentageText.setText((int)(100*decimal)+"%");
        });
    }

    public void searchJob() throws IOException {
        setProgress(0);
        String fieldVal = jobSearchField.getText().trim();
        int jobID = -1;
        try{ jobID = Integer.parseInt(fieldVal);} catch(Exception e){}

        if(fieldVal.matches("[0-9]{1,10}") && jobID !=-1){
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
        System.out.println("Finished");

    }

    public void displayHeaderInfo(Job j){
        highLevelTable.getItems().add(j);
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
    private void setupTable(TableView table, String[] attributes){
        for(String at : attributes) createColumn(table, at);
    }

    private void createColumn(TableView table ,String fieldName){
        TableColumn column = new TableColumn(fieldName);
        column.setCellValueFactory(new PropertyValueFactory<Job, String>(fieldName));
        table.getColumns().addAll(column);
    }

    private void clearTables(){
        jobTable.getItems().clear();
        highLevelTable.getItems().clear();
    }

    private void handleDroppedFile(File dropped){
        try {
            int focused = jobTable.getSelectionModel().getFocusedIndex();
            Platform.runLater(()->statusText.setText("Uploading \""+dropped.getName()+"\" (Asset No. "+currentJobs.get(focused).getAssetId()+")"));

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
            Platform.runLater(()->jobTable.getParent().setDisable(true));
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
                    Platform.runLater(()->updateProgress(decimal));
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
