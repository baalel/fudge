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
        serverList=new ArrayList<>(solrServerUrls.size());
        server=new HttpSolrServer(solrServerUrls.get(0));
        for(String url : solrServerUrls){
            serverList.add(new HttpSolrServer(url));
        }
        distributed=true;
    }
    
    public void querytest(){
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.addSortField("DREREFERENCE", SolrQuery.ORDER.asc);
        query.setRows(100);
        query.setStart(0);
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
    
        public void querytest(String queryText){
        SolrQuery query = new SolrQuery();
        query.setQuery(queryText);
        query.addSortField("DREREFERENCE", SolrQuery.ORDER.asc);
        query.setRows(10);
        query.setStart(0);
        //query.setParam("shards", "http://localhost:8080/solr/discovery1,http://localhost:8080/solr/discovery2");
        try {
            QueryResponse rsp = this.server.query( query );
            SolrDocumentList docs = rsp.getResults();
            System.out.println(docs.getNumFound());
            
            //for(SolrDocument doc : docs){
            //    System.out.println(doc.toString());
            //}
        } catch (SolrServerException ex) {
            //Logger.getLogger(SolrPostman.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SolrException "+ex.getMessage());
        }      
    }
    
    public void tagCategoryQuery(String queryText, String tag){
        List<SolrInputDocument> results=new ArrayList<>();
        SolrQuery query = new SolrQuery();
        query.setQuery(queryText);
        query.addField("DREREFERENCE");
        query.setStart(0);
        query.setRows(0);
        try {
            QueryResponse rsp = this.server.query( query );
            Long docCount=rsp.getResults().getNumFound();
            query.setRows(docCount.intValue());
            rsp=this.server.query( query );
            SolrDocumentList docs = rsp.getResults();
            for(SolrDocument doc : docs){
                doc.addField("TAXONOMY", tag);
                SolrInputDocument inDoc=SolrPostService.reindexMapper(doc);
                results.add(inDoc);
            }
            try{
                server.add(results);
                server.commit();
            }
            catch(SolrException | SolrServerException se){
                
            }
            catch(IOException ioe){
            System.out.println("Error posting test document");
            System.out.println(ioe.getMessage());
            System.exit(1);
        }
        } catch (SolrServerException ex) {
            System.out.println("SolrException "+ex.getMessage());
        }   
        
    }
    
    public void exportTest(String savePath, int batchSize){
        
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.addSortField("DREREFERENCE", SolrQuery.ORDER.asc);
        //query.setParam("shards", "localhost:8080/solr/discovery1,localhost:8080/solr/discovery2");
        query.setRows(0);
        
        try {
            long totalSize=this.server.query( query ).getResults().getNumFound();
            query.setRows(batchSize);
            for(long batchCount=0;batchCount<totalSize/batchSize;batchCount++){
                query.setStart((int)(batchSize*batchCount));

                    QueryResponse rsp = this.server.query( query );
                    SolrDocumentList docs = rsp.getResults();
                    System.out.println(docs.getNumFound());

                    for(SolrDocument doc : docs){
                        System.out.println(doc.toString());
                    }
            }
        } catch (SolrServerException ex) {
        //Logger.getLogger(SolrPostman.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SolrException "+ex.getMessage());
        } 
        catch(NullPointerException npe){
            System.out.println("Server not set");
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
        catch(SolrException | SolrServerException | IOException se){
            System.out.println("Error posting test document");
            System.out.println(se.getMessage());
            System.exit(1);
            
        }
       
   }
   
   public void postDocument(SolrInputDocument doc, boolean commit){
       if(this.distributed){
           distribute(doc, commit);
           return;
       }
        try{
            server.add(doc);
            if(commit){
                server.commit(false,false);
            }
        }
        catch(SolrException | SolrServerException | IOException se){
            System.out.println("Error posting test document");
            System.out.println(se.getMessage());
            
        }
       
   }
   /**
    * This method divides a set of SolrInputDocuments amongst the known
    * SolrServers, distributing is done by the String.hash() of the DREREFERENCE
    * If the server list remains the same, then a particular document
    * should always be sent to the same server.
    * @param docs A List of SolrInputDocuments to post to solr
    * @param commit a boolean flag that determines whether a commit is performed
    *               after the documents have been sent
    */
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
   
    private void distribute(SolrInputDocument doc ,boolean commit){
       
       int s=Math.abs(((String)doc.getFieldValue("DREREFERENCE")).hashCode()%serverList.size()); 
       try{
           
                serverList.get(s).add(doc);
                if(commit){
                    serverList.get(s).commit();
                }
            
        }
        catch(SolrException se){
            System.out.println("Solr Error posting test document");
            System.out.println(se.getMessage());
            
        }
        catch(SolrServerException sse){
            System.out.println("Server Error posting test document");
            System.out.println(sse.getMessage());
        }
        catch(IOException ioe){
            System.out.println("IO Error posting test document");
            System.out.println(ioe.getMessage());
            
        }
       
       
   }
    static SolrInputDocument reindexMapper(SolrDocument oldDoc){
        SolrInputDocument newDoc=new SolrInputDocument();
        List<String> fields=new ArrayList<>();
        fields.add("DREREFERENCE");
        fields.add("CATDOCREF");
        fields.add("TITLE");
        fields.add("DESCRIPTION");
        fields.add("PERIOD");
        fields.add("STARTDATE");
        fields.add("ENDDATE");
        fields.add("DEPARTMENT");
        fields.add("SERIES");
        fields.add("SCHEMA");
        fields.add("URLPARAMS");
        fields.add("SOURCELEVEL");
        fields.add("CLOSURECODE");
        fields.add("CLOSURESTATUS");
        fields.add("CLOSURETYPE");
        fields.add("HELDBY");
        fields.add("PLACE");
        fields.add("PERSON");
        fields.add("REFERENCE");
        fields.add("SUBJECT");
        fields.add("TAXONOMY");
        
        for(String fieldname : fields){
            newDoc.addField(fieldname, (String)oldDoc.getFieldValue(fieldname));
        }
        return newDoc;
        
    }
    
}
