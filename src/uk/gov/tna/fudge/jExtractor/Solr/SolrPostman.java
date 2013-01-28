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
import java.util.ArrayList;
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
    
    public SolrPostman(List<String> solrServerUrls){
        serverList=new ArrayList<>(solrServerUrls.size());
        server=new HttpSolrServer(solrServerUrls.get(0));
        for(String url : solrServerUrls){
            serverList.add(new HttpSolrServer(url));
        }
        distributed=true;
        createQueue();
    }
    
    private void createQueue(){
        this.workQueue=new Stack<>();
    }
    
    private void postDocument(List<SolrInputDocument> docs){
       if(this.distributed){
           distribute(docs, commit);
           return;
       }
       try{
            server.add(docs);
            //if(this.commit){
                server.commit();
            //}
        }
        catch(SolrException | SolrServerException | IOException se){
            System.out.println("***Error posting document***");
            System.out.println(se.getMessage());
            System.exit(1);
            
        }
       
   }
    
   private void distribute(List<SolrInputDocument> docs,boolean commit){
       List<List<SolrInputDocument>> docLists=new ArrayList<>(serverList.size());
       for(int i=0;i<serverList.size();i++){
           docLists.add(new ArrayList<SolrInputDocument>(5000/serverList.size()+1));
       }
       for(SolrInputDocument doc : docs){
           int s=Math.abs(((String)doc.getFieldValue("DREREFERENCE")).hashCode()%serverList.size());
           if(s<0 || s>1){
               System.out.println("ack");
           }
           docLists.get(s).add(doc);
           
       }
       try{
           for(int i=0;i<serverList.size();i++){
                SolrServer s=serverList.get(i);
                List<SolrInputDocument> l=docLists.get(i);
                //serverList.get(i).add(docLists.get(i));
                s.add(l);
                if(commit){
                    serverList.get(i).commit();
                }
            }
        }
        catch(SolrException se){
            System.out.println("Solr Error posting document");
            System.out.println(se.getMessage());
            System.exit(1);
            
        }
        catch(SolrServerException sse){
            System.out.println("Server Error posting document\nCheck your firewall");
            System.out.println(sse.getMessage());
            System.exit(1);
        }
        catch(IOException ioe){
            System.out.println("IO Error posting documents");
            System.out.println(ioe.getMessage());
            System.exit(1);
            
        }
       catch(IllegalArgumentException e){
           System.out.println("Failed to index to Solr\nCheck server spellings in config");
           System.out.print(e.getMessage());
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
