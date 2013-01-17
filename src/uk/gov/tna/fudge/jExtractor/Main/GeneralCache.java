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
public class GeneralCache {
Map<String, String> urlLookup;
Map<String, String> startLookup;
Map<String, String> endLookup;
Map<String, String> refLookup;
static String urlParamPrefix="066/1/";
    
    GeneralCache(){
        urlLookup=new HashMap<>();
        startLookup = new HashMap<>();
        endLookup = new HashMap<>();
        refLookup = new HashMap<>();
        //urlLookup.put("C0", "");
        startLookup.put("C0", "1000-01-01T00:00:00.0Z");
        endLookup.put("C0", "2100-12-31T23:59:59.9Z");
        
    }
    
    public void insertUrl(String id, String reference)
    {
        if(id!=null && reference!=null){
            urlLookup.put(id, reference);
        }

    }
    
    public void insertRef(String id, String reference)
    {
        if(id!=null && reference!=null){
            refLookup.put(id, reference);
        }

    }
    
    
    public void clear(){
        this.urlLookup.clear();
        this.startLookup.clear();
        this.endLookup.clear();
        this.refLookup.clear();
        //urlLookup.put("C0", "");
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
    
    public String lookupUrl(String docid)
    {
        return urlLookup.get(docid);
    }
    
    public String lookupRef(String docid)
    {
        return refLookup.get(docid);
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
    public boolean existsRef(String docid)
    {
        return refLookup.containsKey(docid);
    }
    public boolean existsUrl(String docid)
    {
        return urlLookup.containsKey(docid);
    }
    
}