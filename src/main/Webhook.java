/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Ivo
 */
public class Webhook {

    private String alias = "";
    private String id;
    private String endpoint;
    private int since = 0;
    private volatile boolean active = true;
    private volatile String dispatchTo = "";
    private boolean ready = false;
    
    public Webhook() {
        
        try {
            URL url = new URL("http://api.webhookinbox.com/create/");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            int responseCode = con.getResponseCode();
            if(responseCode != 200) return;
            InputStream inputStream = con.getInputStream();

            JSONObject resp = new JSONObject(Utils.convertStreamToString(inputStream));
            this.id = resp.getString("id");
            this.endpoint = resp.getString("base_url")+"in/";
            ready = true;
        } catch(Exception e){
            System.out.println("Failed at connection to create an Webhook Inbox");
            e.printStackTrace();
        }
        
    }
    
    public Webhook(boolean dummy) {
        this.id = "";
        this.endpoint = "";
    }
    
    public Webhook(String id) {
        this.id = id;
        this.endpoint = "http://api.webhookinbox.com/i/"+id+"/in/";
        this.ready = true;
    }
    
    public boolean connect(){
        if(!ready) return false;
        try {
            URL url = new URL("http://api.webhookinbox.com/i/"+this.id+"/items/?order=-created&max=1");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            int responseCode = con.getResponseCode();
            if(responseCode != 200) return false;
            InputStream inputStream = con.getInputStream();

            JSONObject resp = new JSONObject(Utils.convertStreamToString(inputStream));
            JSONArray items = resp.getJSONArray("items");
            if(items.isEmpty()){
                since = -1;
            }else{
                since = items.getJSONObject(0).getInt("id");
            }
            new Thread(() -> pollRequests()).start();
            return true;
        } catch(Exception e){
            System.out.println("Failed at connection to Webhook Inbox");
            e.printStackTrace();
            return false;
        }
    }
    
    public void pollRequests(){
        while(active){
            try {
                URL url = new URL("http://api.webhookinbox.com/i/"+id+"/items/?order=created"
                        + (since == -1 ? "" : ("&since=id:"+since)));
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");
                con.getResponseCode();
                InputStream inputStream = con.getInputStream();

                JSONObject resp = new JSONObject(Utils.convertStreamToString(inputStream));
                since = resp.getInt("last_cursor");
                if(!active) return;
                if(dispatchTo.isEmpty()) continue;
                resp.getJSONArray("items").forEach(i -> RequestToDo.dispatch(dispatchTo, (JSONObject) i));
                inputStream.close();
            } catch(Exception e){
            }
        }
    }

    public void setDispatchTo(String dispatchTo) {
        if(!dispatchTo.isEmpty() && !dispatchTo.startsWith("http")) dispatchTo = "http://"+dispatchTo;
        this.dispatchTo = dispatchTo;
    }
    
    

    public boolean isReady() {
        return ready;
    }

    public String getId() {
        return id;
    }

    public String getDispatchTo() {
        return dispatchTo;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getAlias() {
        return !alias.isEmpty() ? alias : id;
    }

    public String getAliasLiteral() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
    
    
    
}
