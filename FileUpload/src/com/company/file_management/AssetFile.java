package com.company.file_management;

import com.company.data.Job;
import com.company.data.SMBCredentials;
import com.company.network.DAMAPI;
import com.company.network.SMBCopy;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class AssetFile {

    public enum AssetMode{
        VIEW_MODE, CHECKOUT_MODE, MEND_ASSET
    }

    public static String TEMPORARY_SAVE_DIRECTORY = "";

    private boolean checkedOut = false;
    private File tempFileLocation;

    private String originalDirectory;

    private SMBCopy smbCopy;

    private int assetID;
    private int jobID;

    private AssetMode fileMode;

    public static AssetFile checkoutFile(Job job, SMBCopy copier) throws CannotCheckoutException, IOException {
        return new AssetFile(job, copier,AssetMode.CHECKOUT_MODE);
    }
    public static AssetFile viewFile(Job job, SMBCopy copier) throws Exception {
        return new AssetFile(job,copier ,AssetMode.VIEW_MODE);
    }

    private AssetFile(Job job, SMBCopy copier, AssetMode mode) throws IOException,CannotCheckoutException {
        smbCopy = copier;
        assetID = job.getAssetId();
        jobID = job.getJobNo();
        fileMode = mode;

        System.out.println(0);
        saveTemporary(job);
        System.out.println(1);
        if(fileMode.equals(AssetMode.CHECKOUT_MODE)){
            //Need to add extra checks here, to see if asset can be checked out
            System.out.println(2);
            DAMAPI.getDamApi().checkoutAsset(assetID);
            System.out.println(3);
            checkedOut = true;
        }
    }

    public int getAssetID() {
        return assetID;
    }

    public File getTempFileLocation() {
        return tempFileLocation;
    }

    private void saveTemporary(Job job) throws IOException {
        originalDirectory = DAMAPI.getDamApi().getArchivePath(job);
        SMBCredentials copyCreds = DAMAPI.getDamApi().getSMBCredentials();
        if(smbCopy!=null &&copyCreds !=null) {
            tempFileLocation = new File(TEMPORARY_SAVE_DIRECTORY + "Temporary-" + UUID.randomUUID().toString() + ".pdf");

            smbCopy.setCredentials(copyCreds);
            smbCopy.read(originalDirectory, tempFileLocation);
        }
    }

    public void mendTemporaryFile(File actualLocation){
        tempFileLocation = actualLocation;
    }

    public boolean canBeCommitted(){return checkedOut;}


    public void commit() throws CannotCheckoutException {
        if(canBeCommitted()){
            SMBCredentials commitCredentials = DAMAPI.getDamApi().commitAsset();
            if(commitCredentials !=null){
                smbCopy.setCredentials(commitCredentials);

                //Not taking into account change in path

                smbCopy.copy(tempFileLocation, jobID+"_"+assetID+tempFileLocation.getName().substring(tempFileLocation.getName().lastIndexOf(".")));
            }
            else{
                throw new CannotCheckoutException("API Forbids checkout");
            }
        }
        else{
            throw new CannotCheckoutException("File is read only");
        }
    }

    public boolean isReadOnly(){
        return (fileMode.equals(AssetMode.VIEW_MODE));
    }

    public void dispose(){
        if(!checkedOut){
            tempFileLocation.delete();
        }
    }

    public static void main(String[] args) throws Exception {


    }
}
