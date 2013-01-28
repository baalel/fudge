/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Main;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sprice
 */
public class UrlParamCache {
    Map<String, String> urlLookup;
    Map<String, Integer> levelLookup;
    static String urlParamPrefix="066/1/";
    
    UrlParamCache(){
        urlLookup=new HashMap<>();
        levelLookup=new HashMap<>();
        //urlLookup.put("C0", "");
        
    }
    
    public void insert(String id, String reference)
    {
        if(id!=null && reference!=null){
            urlLookup.put(id, reference);
        }

    }
    
    public void insertLevel(String id, Integer reference)
    {
        if(id!=null && reference!=null){
            levelLookup.put(id, reference);
        }

    }
    
    public String lookup(String id)
    {
        return urlLookup.get(id);
    }
    
    public Integer lookupLevel(String id)
    {
        return levelLookup.get(id);
    }
    
    public boolean exists(String key)
    {
        return urlLookup.containsKey(key);
    }
    
    public boolean existsLevel(String key)
    {
        return levelLookup.containsKey(key);
    }
    
    public void clear(){
        this.urlLookup.clear();
        this.levelLookup.clear();
        //urlLookup.put("C0", "");
        System.gc();
    }
    
}
