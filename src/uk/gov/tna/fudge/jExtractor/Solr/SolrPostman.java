/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Solr;

/**
 *
 * @author Steve
 */

import java.io.IOException;
import java.util.List;
import java.util.Stack;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;

public class SolrPostman implements Runnable{
    
    SolrServer server;
    List<SolrServer> serverList;
    boolean distributed;
    private volatile Stack<List<SolrInputDocument>> workQueue;
    List<SolrInputDocument> workingBatch;
    private volatile boolean stopRequested = false;
    private volatile boolean commit=false;
    
    public SolrPostman(String solrServerUrl){
        server = new HttpSolrServer(solrServerUrl);
        
        distributed=false;
        createQueue();
    }
    
    
    
    private void createQueue(){
        this.workQueue=new Stack<>();
    }
    
    private void postDocument(List<SolrInputDocument> docs){
       try{
           if(docs.size()>0){
            server.add(docs);
            if(this.commit){
                server.commit();
            }
           }
        }
        catch(SolrException | SolrServerException | IOException se){
            System.out.println("***Error posting document***");
            System.out.println(se.getMessage());
            System.exit(1);
            
        }
       
   }
    
   
   
    public void requestStop() {
        stopRequested = true;
    }
    
    public void queueSolrPost(List<SolrInputDocument> docs, boolean commit){
        workQueue.add(docs);
        this.commit=commit;
        
        
    }
   
    @Override
    public void run(){
        int counter=1;
        try {
            while(!stopRequested || !workQueue.empty()){
                if(!workQueue.empty()){
                    System.out.println("Postman Posting, Queue is "+workQueue.size());
                    workingBatch=workQueue.pop();
                    this.commit=(counter%20==0);
                    this.postDocument(workingBatch);
                    counter++;
                }
                else{
                    System.out.println("Postman Sleeping");
                    Thread.sleep(5000L);

                }
            }
        }
        catch(InterruptedException iex) {}
    }
    
    public boolean hasMore(){
        return !workQueue.empty();
    }
    
    
    
    
}
