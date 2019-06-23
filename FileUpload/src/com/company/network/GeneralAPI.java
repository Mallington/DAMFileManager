package com.company.network;

import com.company.data.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class GeneralAPI<t>{
    private String params;

    private Path root;

    private Class<t> type;
    public GeneralAPI(Path root, String urlWithParams, Class<t> type){
        this.type = type;
        params = urlWithParams;
        this.root = root;
    }

    public  List<t> fetchList(String... param) throws Exception {
        String request = root.getCompletePath()+params;
        for(int i =0; i< param.length; i++) request = request.replace("{"+i+"}", param[i]);

        String response = NetworkUtils.getURL(request);
        ObjectMapper mapper = new ObjectMapper();

        if(type.equals(Job.class)) {
            return mapper.readValue(response, new TypeReference<List<Job>>() {
            });
        }
        else if(type.equals(SMBCredentials.class)){
           response = response.replace("WindowsPath","windowsPath").replace("OtherPath","otherPath");
            return mapper.readValue(response, new TypeReference<List<SMBCredentials>>() {
            });
        }else if(type.equals(MenuResponse.class)) {
            return mapper.readValue(response, new TypeReference<List<MenuResponse>>() {
            });
        }else if(type.equals(Session.class)) {
            return mapper.readValue(response, new TypeReference<List<Session>>() {
            });
        }

        else{
            return mapper.readValue(response, new TypeReference<List<Object>>() {
            });
        }

    }

    public  t fetch(String... param) throws IOException {
        String request = root.getCompletePath()+params;
        for(int i =0; i< param.length; i++) request = request.replace("{"+i+"}", param[i]);

        String response = NetworkUtils.getURL(request);
        ObjectMapper mapper = new ObjectMapper();

        if(type.equals(Job.class)) {
            return mapper.readValue(response, new TypeReference<Job>() {
            });
        }
        else if(type.equals(SMBCredentials.class)){
            response = response.replace("WindowsPath","windowsPath").replace("OtherPath","otherPath");
            return mapper.readValue(response, new TypeReference<SMBCredentials>() {
            });
        } else if(type.equals(MenuResponse.class)){
            return mapper.readValue(response, new TypeReference<MenuResponse>() {
            });
        }else if(type.equals(Session.class)){
            return mapper.readValue(response, new TypeReference<Session>() {
            });
        }
        else{
            return mapper.readValue(response, new TypeReference<Object>() {
            });
        }

    }



}
