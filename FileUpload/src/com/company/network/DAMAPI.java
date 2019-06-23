package com.company.network;

import com.company.data.*;

import java.io.IOException;
import java.util.List;

public class DAMAPI {
    //Singleton
    private static final DAMAPI DAM_API = new DAMAPI();

    //Constants
    final static String DEFAULT_PROTOCOL = "http://";
    final public static String DEFAULT_ADDRESS = "AllingtonServer";
    final public static int DEFAULT_IP = 86;
    final public static String DEFAULT_ENTRY_POINT = "/api";

    //
    private Path apiPath;
    //Variables
    private Session sessionKey = null;
    private boolean connected = false;


    private GeneralAPI<Job> jobAPI;
    private GeneralAPI<SMBCredentials> fileCredentials;
    private GeneralAPI<SMBCredentials> commit;
    private GeneralAPI<MenuResponse> contextMenu;
    private GeneralAPI<Session> authentication;
    private GeneralAPI<Boolean> jobAttributeUpdater;

    private DAMAPI(){
        apiPath = new Path(DEFAULT_PROTOCOL, DEFAULT_ADDRESS, DEFAULT_IP, DEFAULT_ENTRY_POINT);

        jobAPI = new GeneralAPI<>(apiPath,"/asset/GetAsset/{0}", Job.class);
        fileCredentials = new GeneralAPI<>(apiPath,"/asset/GetUploadPath", SMBCredentials.class);
        commit = new GeneralAPI<>(apiPath,"/asset/Commit", SMBCredentials.class);
        contextMenu = new GeneralAPI<>(apiPath,"/asset/GetMenu/{0}/{1}", MenuResponse.class);
        authentication = new GeneralAPI<>(apiPath,"/asset/GetUser/{0}/{1}", Session.class);
        jobAttributeUpdater = new GeneralAPI<>(apiPath,"/asset/Update/{0}/{1}/{2}", Boolean.class);

    }

    public Path getApiPath() {
        return apiPath;
    }

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

    public String getArchivePath(Job job) throws IOException {
        return NetworkUtils.getURL(apiPath.getCompletePath() +"/asset/GetArchivePath/"+job.getAssetId()).replace("\"","");
    }

    public static DAMAPI getDamApi() {
        return DAM_API;
    }

    public String checkoutAsset(int assetID) throws IOException {return NetworkUtils.getURL(apiPath.getCompletePath() +"/asset/CheckoutAsset/"+assetID+"/"+sessionKey.getUserID()).replace("\"","");}

    public SMBCredentials commitAsset(){
        try {
            return commit.fetch();
        } catch (IOException e) {
            return null;
        }
    }

    public Boolean updateJobAttribute(String attributeReference, String value, Job j) throws IOException {
        return jobAttributeUpdater.fetch(attributeReference, value, j.getJobNo()+"'");
    }
}
