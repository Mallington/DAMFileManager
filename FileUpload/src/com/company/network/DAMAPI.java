package com.company.network;

import com.company.data.Job;
import com.company.data.MenuResponse;
import com.company.data.SMBCredentials;
import com.company.data.Session;
import javafx.scene.control.ContextMenu;

import java.io.IOException;
import java.util.List;

public class DAMAPI {
    //Singleton
    private static final DAMAPI DAM_API = new DAMAPI();

    //Constants
    final public static String ENTRY_POINT = "http://AllingtonServer:86/api";

    //Variables
    private Session sessionKey = null;
    private boolean connected = false;


    private GeneralAPI<Job> jobAPI = new GeneralAPI<>(ENTRY_POINT+"/asset/GetAsset/{0}", Job.class);
    private GeneralAPI<SMBCredentials> fileCredentials = new GeneralAPI<>(ENTRY_POINT+"/asset/GetUploadPath", SMBCredentials.class);
    private GeneralAPI<MenuResponse> contextMenu = new GeneralAPI<>(ENTRY_POINT+"/asset/GetMenu/{0}/{1}", MenuResponse.class);
    private GeneralAPI<Session> authentication = new GeneralAPI<>(ENTRY_POINT+"/asset/GetUser/{0}/{1}", Session.class);

    private DAMAPI(){}

    public boolean connect(String username, String password) throws Exception {
        return connected = ((sessionKey = authentication.fetch(username,password)) !=null);
    }

    public boolean isConnected() {
        return connected;
    }

    public List<Job> getAssetByJobID(int id) throws Exception {
        return jobAPI.fetchList(""+id);
    }

    public SMBCredentials getSMBCredentials() throws IOException {
        return fileCredentials.fetch();
    }

    public List<MenuResponse> getActionables(Job j) throws Exception {
        return  contextMenu.fetchList(sessionKey.getUserName() + "", j.getAssetId() + "");
    }

    public String getArchiveaPath(Job job) throws IOException {
        return NetworkUtils.getURL(ENTRY_POINT+"/asset/GetArchivePath/"+job.getAssetId()).replace("\"","");
    }

    public static DAMAPI getDamApi() {
        return DAM_API;
    }
}
