/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Solr;

import java.io.IOException;
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
    
    public SolrPostService(String solrServerUrl){
        server = new HttpSolrServer(solrServerUrl);
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
   public void postDocument(List<SolrInputDocument> docs){
       try{
            server.add(docs);
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
    
}
