package com.company.network;

import com.company.data.SMBCredentials;
import jcifs.smb1.smb1.NtlmPasswordAuthentication;
import jcifs.smb1.smb1.SmbFile;
import jcifs.smb1.smb1.SmbFileOutputStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class SMBCopy {
    private SMBCredentials credentials;

    private double progressStep = 0.001;

    private final byte[] BUFFER = new byte[10 * 8024];

    public SMBCopy(SMBCredentials credentials) {
        this.credentials = credentials;
    }

    public void copy(File toCopy, String destinationName){
            try{
                updateProgress(0.0);
                NtlmPasswordAuthentication authentication = new NtlmPasswordAuthentication(null, credentials.getUserName(), credentials.getPassWord());
                SmbFile destination = new SmbFile("smb:/"+credentials.getOtherPath()+destinationName, authentication);

                ByteArrayInputStream inputStream = null;
                SmbFileOutputStream sfos = null;
                try {

                    long count =0;
                    double nextUpdate =0;

                    long lengthOfFile =  toCopy.length();
                    byte[] data = readFileToByteArray(toCopy);
                    inputStream = new ByteArrayInputStream(data);
                    sfos = new SmbFileOutputStream(destination);
                    long total = 0;

                    while ((count = inputStream.read(BUFFER)) > 0) {
                        total += count;

                        double progress = total / (float) lengthOfFile;
                        if(progress>nextUpdate) {
                            updateProgress(total / (float) lengthOfFile);
                            nextUpdate += getProgressStep();
                        }

                        sfos.write(BUFFER,0,(int)count);

                    }
                    sfos.flush();
                    inputStream.close();
                    sfos.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }



                updateProgress(1.0);
            } catch(Exception e){
                System.out.println("File copy failed");
                e.printStackTrace();
                failed();
            }
    }

    private byte[] readFileToByteArray(File toCopy) throws IOException {
        return Files.readAllBytes(toCopy.toPath());
    }

    public void copyOnOtherThread(File toCopy, String destinationName){
        new Thread(()->copy(toCopy, destinationName)).start();
    }

    public double getProgressStep() {
        return progressStep;
    }

    public void setProgressStep(double progressStep) {
        this.progressStep = progressStep;
    }

    public abstract void updateProgress(double decimal);
    public abstract void failed();
    public abstract void completeJob();
}
