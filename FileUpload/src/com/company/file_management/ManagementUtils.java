package com.company.file_management;

import com.company.data.Job;
import com.company.network.SMBCopy;

import java.awt.*;

public class ManagementUtils {
    public static boolean quickView(Job jobToView, SMBCopy smbCopy){
        try {
            AssetFile file = AssetFile.viewFile(jobToView, smbCopy);
            System.out.println(file.getTempFileLocaton());
            Desktop dt = Desktop.getDesktop();
            try {
                dt.open(file.getTempFileLocaton());
            } catch (Exception e) {
                e.printStackTrace();
            }

            file.getTempFileLocaton().deleteOnExit();
            return true;
        }
        catch (Exception e){return false;}

    }
}
