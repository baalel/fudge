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
    static String urlParamPrefix="066/1/";
    
    UrlParamCache(){
        urlLookup=new HashMap<String, String>();
        //urlLookup.put("C0", "");
        
    }
    
    public void insert(String id, String reference)
    {
        if(id!=null && reference!=null){
            urlLookup.put(id, reference);
        }

    }
    
    public String lookup(String id)
    {
        return urlLookup.get(id);
    }
    
    public boolean exists(String key)
    {
        return urlLookup.containsKey(key);
    }
    
    public void clear(){
        this.urlLookup.clear();
        //urlLookup.put("C0", "");
        System.gc();
    }
    
}
