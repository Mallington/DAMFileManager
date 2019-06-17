package com.company.file_management;

import com.company.data.Job;
import com.company.network.DAMAPI;
import com.company.network.SMBCopy;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

public class AssetFile {

    public static String TEMPORARY_SAVE_DIRECTORY = "";

    private boolean checkedOut;
    private File tempFileLocaton;

    private String originalDirectory;

    private SMBCopy smbCopy;

    public static AssetFile checkoutFile(Job job, SMBCopy copier) throws CannotCheckoutException, IOException {
        return new AssetFile(job, copier,false);
    }
    public static AssetFile viewFile(Job job, SMBCopy copier) throws Exception {
        return new AssetFile(job,copier ,true);
    }

    private AssetFile(Job job, SMBCopy copier, boolean readOnly) throws IOException,CannotCheckoutException {
        smbCopy = copier;
        saveTemporary(job);

        if(!readOnly){
            //to do
        }
    }

    public File getTempFileLocaton() {
        return tempFileLocaton;
    }

    private void saveTemporary(Job job) throws IOException {
        originalDirectory = DAMAPI.getDamApi().getArchiveaPath(job);

        if(smbCopy!=null) {
            tempFileLocaton = new File(TEMPORARY_SAVE_DIRECTORY + "Temporary-" + UUID.randomUUID().toString() + ".pdf");

            smbCopy.read(originalDirectory, tempFileLocaton);
        }
    }

    public boolean canBeCommitted(){return checkedOut;}


    public void commit() throws CannotCheckoutException {
        if(canBeCommitted()){
        }
        else{
            throw new CannotCheckoutException("File is read only");
        }
    }

    public void dispose(){
        if(!checkedOut){
            tempFileLocaton.delete();
        }
    }

    public static void main(String[] args) throws Exception {


    }
}
