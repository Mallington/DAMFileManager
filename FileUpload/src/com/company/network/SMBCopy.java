package com.company.network;

import com.company.data.SMBCredentials;
import jcifs.smb1.smb1.NtlmPasswordAuthentication;
import jcifs.smb1.smb1.SmbFile;
import jcifs.smb1.smb1.SmbFileOutputStream;

import java.io.ByteArrayInputStream;
import java.io.File;

public abstract class SMBCopy {
    private SMBCredentials credentials;
    public SMBCopy(SMBCredentials credentials) {
        this.credentials = credentials;
    }

    public void copy(File toCopy, String destinationName){
            try{
                updateProgress(0.0);
                NtlmPasswordAuthentication authentication = new NtlmPasswordAuthentication(null, credentials.getUserName(), credentials.getPassWord());
                SmbFile destination = new SmbFile("smb:/"+credentials.getOtherPath(), authentication);



                updateProgress(1.0);
            } catch(Exception e){
                System.out.println("File copy failed");
                e.printStackTrace();
                failed();
            }
    }

    public void copyOnOtherThread(File toCopy, String destinationName){
        new Thread(()->copy(toCopy, destinationName)).start();
    }



    public abstract void updateProgress(double decimal);
    public abstract void failed();
    public abstract void completeJob();
}
