/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Main;

import java.util.HashMap;
import java.util.Map;

/**
 *Used to cache parent dates to save excessive mongodb lookups
 * this can be memory hungry if document collection is large
 * use Clear method to clear out cache when switching department branches
 * @author steve
 */
public class CoveringDateCache {
    Map<String,String> startLookup;
    Map<String,String> endLookup;
    
    /**
     * Constructor
     * Sets up caches and pre-warms with the root Node
     */
    CoveringDateCache()
    {
        startLookup = new HashMap<>();
        endLookup = new HashMap<>();
        startLookup.put("C0", "1000-01-01T00:00:00.0Z");
        endLookup.put("C0", "2100-12-31T23:59:59.9Z");
    }
    
    /**
     * Clears contents of cache to remove unneeded values after
     * department change to free up memory
     */
    public void clear(){
        this.startLookup.clear();
        startLookup.put("C0", "1000-01-01T00:00:00.0Z");
        this.endLookup.clear();      
        endLookup.put("C0", "2100-12-31T23:59:59.9Z");
        System.gc();
        
    }
    
    /**
     * Adds the startdate value of the currently processing asset
     * @param docid the document iaid to be cached
     * @param docdate the startdate of the document being added to the cache 
     */
    public void insertStart(String docid, String docdate)
    {
        if(docid!=null && docdate!=null){
            startLookup.put(docid, docdate);
        }

    }
     /**
     * Adds the enddate value of the currently processing asset
     * @param docid the document iaid to be cached
     * @param docdate the enddate of the document being added to the cache 
     */
    public void insertEnd(String docid, String docdate)
    {
        if(docid!=null && docdate!=null){
            endLookup.put(docid, docdate);
        }

    }
    
    /**
     * find the cached startdate for the specified document
     * @param docid the iaid of the document to retrieve the cached start date of
     * @return the startdate from the cache
     */
    public String lookupStart(String docid)
    {
        return startLookup.get(docid);
    }
    
    /**
     * find the cached enddate for the specified document
     * @param docid the iaid of the document to retrieve the cached end date of
     * @return the enddate from the cache
     */    
    public String lookupEnd(String docid)
    {
        return endLookup.get(docid);
    }
    
    /**
     * Checks if the document's startdate has been cached
     * @param docid the iaid of the document to look for
     * @return true if this document is cached, false if not
     */
    public boolean existsStart(String docid)
    {
        return startLookup.containsKey(docid);
    }
    
    /**
     * Checks if the document's enddate has been cached
     * @param docid the iaid of the document to look for
     * @return true if this document is cached, false if not
     */
    public boolean existsEnd(String docid)
    {
        return endLookup.containsKey(docid);
    }
}