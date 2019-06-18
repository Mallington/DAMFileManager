package com.company.file_management;

import com.company.data.Job;
import com.company.network.SMBCopy;

import java.awt.*;

public class AssetUtils {
    public static boolean quickView(Job jobToView, SMBCopy smbCopy){
        try {
            AssetFile file = AssetFile.viewFile(jobToView, smbCopy);
            System.out.println(file.getTempFileLocation());
            return openPDFAsset(file);
        }
        catch (Exception e){return false;}

    }

    public static boolean openPDFAsset(AssetFile file){
        Desktop dt = Desktop.getDesktop();
        try {
            dt.open(file.getTempFileLocation());
        } catch (Exception e) {
           return false;
        }

        file.getTempFileLocation().deleteOnExit();
        return true;
    }
}
