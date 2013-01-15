/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;

/**
 *
 * @author steve
 */

 
public class SolrPostService {
    SolrServer server;
    List<SolrServer> serverList;
    boolean distributed;
    
    public SolrPostService(String solrServerUrl){
        server = new HttpSolrServer(solrServerUrl);
        distributed=false;
    }
    
    public SolrPostService(List<String> solrServerUrls){
        serverList=new ArrayList<SolrServer>(solrServerUrls.size());
        for(String url : solrServerUrls){
            serverList.add(new HttpSolrServer(url));
        }
        distributed=true;
    }
    
    public void querytest(){
        SolrQuery query = new SolrQuery();
        query.setQuery("TITLE:Lord AND TITLE:Nelson AND DESCRIPTION:Logs AND SOURCELEVEL:6");
        query.addSortField("CATDOCREF", SolrQuery.ORDER.asc);
        query.setHighlight(true).setHighlightSnippets(1);
        query.setParam("hl.fl", "text");
        query.setParam("shards", "localhost:8080/solr/discovery1,localhost:8080/solr/discovery2");
        try {
            QueryResponse rsp = this.server.query( query );
            SolrDocumentList docs = rsp.getResults();
            System.out.println(docs.getNumFound());
            
            for(SolrDocument doc : docs){
                System.out.println(doc.toString());
            }
        } catch (SolrServerException ex) {
            //Logger.getLogger(SolrPostman.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SolrException "+ex.getMessage());
        }
        
        
        
    }
   public void postDocument(List<SolrInputDocument> docs, boolean commit){
       if(this.distributed){
           distribute(docs, commit);
           return;
       }
       try{
            server.add(docs);
            if(commit){
                server.commit();
            }
        }
        catch(SolrException se){
            System.out.println("Error posting test document");
            System.out.println(se.getMessage());
            
        }
        catch(SolrServerException sse){
            System.out.println("Error posting test document");
            System.out.println(sse.getMessage());
        }
        catch(IOException ioe){
            System.out.println("Error posting test document");
            System.out.println(ioe.getMessage());
            
        }
       
   }
   
   public void postDocument(SolrInputDocument doc){
        try{
            server.add(doc);
            server.commit();
        }
        catch(SolrException se){
            System.out.println("Error posting test document");
            System.out.println(se.getMessage());
            
        }
        catch(SolrServerException sse){
            System.out.println("Error posting test document");
            System.out.println(sse.getMessage());
        }
        catch(IOException ioe){
            System.out.println("Error posting test document");
            System.out.println(ioe.getMessage());
            
        }
       
   }
   
   private void distribute(List<SolrInputDocument> docs,boolean commit){
       List<List<SolrInputDocument>> docLists=new ArrayList<List<SolrInputDocument>>(serverList.size());
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
                serverList.get(i).add(docLists.get(i));
                if(commit){
                    serverList.get(i).commit();
                }
            }
        }
        catch(SolrException se){
            System.out.println("Error posting test document");
            System.out.println(se.getMessage());
            
        }
        catch(SolrServerException sse){
            System.out.println("Error posting test document");
            System.out.println(sse.getMessage());
        }
        catch(IOException ioe){
            System.out.println("Error posting test document");
            System.out.println(ioe.getMessage());
            
        }
       
       
   }
    
}
