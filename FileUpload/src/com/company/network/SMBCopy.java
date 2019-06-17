package com.company.network;

import com.company.data.SMBCredentials;
import jcifs.smb1.smb1.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
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
                NtlmPasswordAuthentication authentication = new NtlmPasswordAuthentication(null, credentials.getUserName(), credentials.getPassWord());
                SmbFile destination = new SmbFile("smb:/"+credentials.getOtherPath()+destinationName, authentication);

                try {

                    byte[] data = readFileToByteArray(toCopy);
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
                    SmbFileOutputStream outputStream = new SmbFileOutputStream(destination);

                    transfer(inputStream, outputStream, toCopy.length());
                    completeJob();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch(Exception e){
                failed();
            }
    }

    public void read(String fileToRead, File endDirectory) throws MalformedURLException {
        NtlmPasswordAuthentication authentication = new NtlmPasswordAuthentication(null, credentials.getUserName(), credentials.getPassWord());

        System.out.println("Reading file: "+"smb:/"+fileToRead+" with "+credentials.getUserName()+" and "+credentials.getPassWord());
        SmbFile destination = new SmbFile("smb:/"+fileToRead, authentication);

        try {
            SmbFileInputStream inputStream = new SmbFileInputStream(destination);
            FileOutputStream outputStream = new FileOutputStream(endDirectory);

            transfer(inputStream, outputStream, destination.length());
            completeJob();

        } catch (Exception e) {
            failed();
            e.printStackTrace();
        }
    }

    private void transfer(InputStream inputStream, OutputStream outputStream, long lengthOfFile) throws IOException {
        long count =0;
        double nextUpdate =0;
        long total = 0;

        start();

        while ((count = inputStream.read(BUFFER)) > 0) {
            total += count;

            double progress = total / (float) lengthOfFile;
            if(progress>nextUpdate) {
                updateProgress(total / (float) lengthOfFile);
                nextUpdate += getProgressStep();
            }

            outputStream.write(BUFFER,0,(int)count);

        }
        outputStream.flush();
        inputStream.close();
        outputStream.close();
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

    public abstract void start();
    public abstract void updateProgress(double decimal);
    public abstract void failed();
    public abstract void completeJob();
}
