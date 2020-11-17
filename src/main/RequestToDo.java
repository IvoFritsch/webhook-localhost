/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Ivo
 */
public class RequestToDo implements Runnable {
    
    
    private static final ExecutorService REQUESTS_QUEUE = Executors.newFixedThreadPool(3);

    private final String endpoint;
    private final String method;
    private String query;
    private final String body;
    
    private final JSONArray headers;
    
    public static void dispatch(String endpoint, JSONObject req){
        System.out.println("Dispatching to "+endpoint);
        REQUESTS_QUEUE.submit(new RequestToDo(endpoint, req));
    }
    
    public RequestToDo(String endpoint, JSONObject req) {
        this.endpoint = endpoint;
        this.headers = req.getJSONArray("headers");
        this.method = req.getString("method");
        if(req.has("query")){
            this.query = "?" + req.getString("query");
        } else {
            query = "";
        }
        if(req.has("body")){
            this.body = req.getString("body");
        } else {
            this.body = null;
        }
    }
    
    @Override
    public void run() {
        try {
            URL url = new URL(endpoint + this.query);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(this.method);
            headers.forEach(he -> {
                JSONArray h = (JSONArray) he;
                con.setRequestProperty(h.getString(0), h.getString(1));
            });
            if(method.toUpperCase().equals("POST") && this.body != null)con.setDoOutput(true);
            con.connect();
            if(method.toUpperCase().equals("POST") && this.body != null){
                try(OutputStream os = con.getOutputStream()) {
                    os.write(this.body.getBytes());
                }
            }
            con.getResponseCode();
            con.disconnect();
        } catch (Exception e){
            e.printStackTrace();
        }
        
        
    }
    
}
