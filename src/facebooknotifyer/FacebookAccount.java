/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facebooknotifyer;

import java.io.Serializable;

/**
 * katkat
 * @author Filip
 */
public class FacebookAccount implements Serializable{
    private String id;
    private String key;
    private String title;
    private String lastUpdated;
    static final long serialVersionUID = 10275539472837495L;
    
    public FacebookAccount(String id, String key) {
        this.id = id;
        this.key = key;
        this.title="";
    }

    public FacebookAccount(String id, String key, String title) {
        this.id = id;
        this.key = key;
        this.title = title;
        this.lastUpdated = "Never";
    }

    public String getKey() {
        return key;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }
    
    @Override
    public String toString() {
        if(title!=null & !title.isEmpty()) {
            return id+" - "+key+" - "+title;
        }else {
            return id+" - "+key;
        }
    }
}   
