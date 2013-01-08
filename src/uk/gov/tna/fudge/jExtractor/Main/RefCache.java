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
public class RefCache {
    Map<String,String> refLookup;
    
    RefCache()
    {
        refLookup = new HashMap<String,String>();
    }
    
    public void insert(String id, String reference)
    {
        if(id!=null && reference!=null){
            refLookup.put(id, reference);
        }

    }
    
    public String loopup(String id)
    {
        return refLookup.get(id);
    }
    
    public boolean exists(String key)
    {
        return refLookup.containsValue(key);
    }

}
