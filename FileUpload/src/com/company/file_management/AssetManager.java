package com.company.file_management;

import com.company.data.Job;
import com.company.network.SMBCopy;

import java.util.ArrayList;
import java.util.List;

public class AssetManager {
    private SMBCopy smbCopy;
    private List<AssetFile> assetFileList = new ArrayList<>();

    public AssetManager(SMBCopy smbCopy) {
        this.smbCopy = smbCopy;
    }

    public AssetFile getAssetFile(Job job){
        for(AssetFile file : assetFileList) if(file.getAssetID() == job.getAssetId()) return file;
        return null;
    }

    public AssetFile popAssetFile(Job job){
        for(int i =0; i< assetFileList.size();i++) if(assetFileList.get(i)!=null &&assetFileList.get(i).getAssetID() == job.getAssetId()) {
            return assetFileList.remove(i);
        }
        return null;
    }

    public AssetFile checkoutJob(Job job){
        AssetFile asset = popAssetFile(job);
        try {
            if (asset == null) {
                System.out.println("Creating new asset");
                asset = AssetFile.checkoutFile(job, smbCopy);
            }
            else if(asset.isReadOnly()){
                System.out.println("Asset is read-only, disposing!");
                asset.dispose();
                asset = AssetFile.checkoutFile(job, smbCopy);
            }

            assetFileList.add(asset);

            return asset;
        }
        catch (Exception e){
            System.out.println("Failed to checkout job");
         return null;
        }
    }

    public boolean commitCheckout(Job job){
        AssetFile asset = popAssetFile(job);

        if(asset !=null && asset.canBeCommitted()){
            try {
                asset.commit();
                asset.dispose();
                return true;
            } catch (CannotCheckoutException e) {
                System.out.println("Failed to checkout asset");
                assetFileList.add(asset);
            }
        }
        return false;
    }

    public AssetFile fetchViewAsset(Job job) throws Exception {
        AssetFile assetFile = getAssetFile(job);
        if (assetFile == null) assetFileList.add(assetFile = AssetFile.viewFile(job, smbCopy));
        return assetFile;
    }

    public boolean viewAsset(Job job){
        try {
            return AssetUtils.openPDFAsset(fetchViewAsset(job));
            }
        catch (Exception e){
            System.out.println("Failed to init read-only asset");
            return false;
        }
    }
}
