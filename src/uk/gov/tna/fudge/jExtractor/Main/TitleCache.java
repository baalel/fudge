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
public class TitleCache {
    Map<String, String> titleLookup;
    static String titlePrefix="Untitled Document";
    
    public TitleCache(){
        titleLookup=new HashMap<>();
        titleLookup.put("C0", TitleCache.titlePrefix);
    }
    
    
    public void insert(String id, String title)
    {
        if(id!=null && title!=null){
            titleLookup.put(id, title);
        }

    }
    
    public String lookup(String id)
    {
        return titleLookup.get(id);
    }
    
    public boolean exists(String id)
    {
        return titleLookup.containsKey(id);
    }
    
    public void clear(){
        this.titleLookup.clear();
        titleLookup.put("C0", TitleCache.titlePrefix);
        System.gc();
    }
    
}
