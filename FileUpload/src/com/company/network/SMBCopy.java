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

    }



    public abstract void updateProgress(double decimal);
    public abstract void completeJob();
}
