package com.company.network;

import com.company.data.Job;
import com.company.data.MenuResponse;
import com.company.data.SMBCredentials;
import com.company.data.Session;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class GeneralAPI<t>{
    private String URL;
    private Class<t> type;
    public GeneralAPI(String urlWithParams, Class<t> type){
        this.type = type;
        URL = urlWithParams;
    }

    public  List<t> fetchList(String... param) throws Exception {
        String request = URL;
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
        String request = URL;
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
