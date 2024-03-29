/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Main;

/**
 *
 * @author sprice
 */

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.solr.common.SolrInputDocument;
import uk.gov.tna.fudge.jExtractor.Solr.SolrPostman;

public class ThreadedExtractor implements Runnable{
    Properties localProp;
    Properties sysProp;
    String savePath;
    String mongoServer;
    String mongoPort;
    String iaDatabase;
    String iaCollection;
    String solrDatabase;
    String solrCollection;
    String solrWebServer;
    SolrPostman postie;
    String distribute;
    List<String> solrServerList;
    boolean doSolrPost;
    boolean doMongoStore;
    boolean doFileStore;
    boolean verbose;
    Thread worker;
    
    /**
     * Constructor for Application main class
     * Loads configuration from properties file in ./Resources directory
     * If distributed flag is true configures Solr servers in distributed mode.
     * @param cfgFileName The name of the configuration file to load
     */
    ThreadedExtractor(String cfgFileName, SolrPostman postie)
    {
        this.verbose=false;
        this.localProp = new Properties();
        this.sysProp =new Properties(System.getProperties());
        try{
            String path=sysProp.getProperty("user.dir")+"/Resources/"+cfgFileName+".properties";
            localProp.load(new FileInputStream(path));
            
        }
        catch(IOException ex)
        {
            System.out.println("Unable to load Properties file " + ex.getMessage());
            System.exit(1);
        }
        this.savePath=localProp.getProperty("SAVE_PATH","/home/sprice/solrdoc");
        this.mongoServer=localProp.getProperty("MONGO_SERVER", "localhost");
        this.mongoPort=localProp.getProperty("MONGO_PORT", "27017");
        this.iaDatabase=localProp.getProperty("MONGO_INDB","iadata");
        this.solrDatabase=localProp.getProperty("MONGO_OUTDB","solrdb");
        this.iaCollection=localProp.getProperty("MONGO_INCOL","informationasset");
        this.solrCollection=localProp.getProperty("MONGO_OUTCOL","solrout");
        this.solrWebServer=localProp.getProperty("SOLR_WEBSERVER","http://localhost:8080/solr/discoverytest");
        this.distribute=localProp.getProperty("DISTRIBUTE", "FALSE");
        this.doSolrPost=("TRUE".equals(localProp.getProperty("INDEXSOLR", "FALSE")));
        this.doMongoStore=("TRUE".equals(localProp.getProperty("MONGOSAVE", "FALSE")));
        this.doFileStore=("TRUE".equals(localProp.getProperty("FILESAVE", "FALSE")));
        String[] distservers=localProp.getProperty("DIST_SOLR_SERVERS", "http://localhost:8080/solr/discovery1,http://localhost:8080/solr/discovery2").split(",");
        this.solrServerList=new ArrayList<>(2);
        solrServerList.addAll(Arrays.asList(distservers));
        this.postie=postie;
        
    }
    
    /**
     * Main() calls this to start the application. Application mode is controlled by parameter
     * @param mode Determines which action the application should perform. Eventually should be set by
     * command line, but currently hard coded into main()
     */
    
    @Override
    public void run()
    {
        this.pull();
        
        while(postie.hasMore()){
            try {
                System.out.println("Waiting for Solr queue to process before exiting");
                Thread.sleep(2000L);
                } catch (InterruptedException ex) { }
        }
        System.exit(0);
        
    }
    
    
    
    /**
     * The meat of the application. Pull connects to mongo, extracts Information assets
     * then can write back to mongo, write to xml and/or index to solr.
     * On a reasonably fast PC with 8GB of RAM and a local Mongodb it takes approx 3hrs
     * to processes the current 21.5M Information Assets
     */
    private void pull(){
        Fetcher fetcher;

        RefCache parentCache=new RefCache();
        CoveringDateCache dateCache=new CoveringDateCache();
        UrlParamCache urlCache=new UrlParamCache();
        TitleCache titleCache=new TitleCache();
        GeneralCache cache=new GeneralCache();
        Stack<String> workQueue=new Stack<>();
        
        List<SolrDoc> solrDocs=new ArrayList<>(5000);
        List<DBObject> mongoDocs=new ArrayList<>(5000) ;
        List<SolrInputDocument> webDocs=new ArrayList<>(5000);
        long beginTime= System.nanoTime(); //stores job start time in nanoseconds
        long startTime = beginTime; //stored batch start time in nanoseconds
        long nowTime; //used to determine batch duration
        Long elapsedTime; //duration of batch in nanoseconds
        String workingDept="START";
        String oldDept;
        oldDept = "";
        List<String> deptList=new ArrayList<>(500);
        Integer batchCounter=0;
        Integer docCounter=0;
        
        workQueue.push(localProp.getProperty("ROOT_NODE", "C0"));
        try{
            
            fetcher=new Fetcher( mongoServer,mongoPort,iaDatabase,solrDatabase,iaCollection,solrCollection);
            int totalDocs=fetcher.docCount();
            while(!workQueue.isEmpty()){
                String docid=workQueue.pop();
                try (DBCursor cursor = fetcher.findMany("ParentIAID", docid)) {
                    while(cursor.hasNext()) {
                        DBObject doc=cursor.next();

                        IMongoDoc mdoc=new MongoDoc(doc,parentCache,dateCache,urlCache,titleCache,fetcher);                      

                        //IMongoDoc cmdoc=new CachedMongoDoc(doc,cache,fetcher);
                        if(mdoc.getSourceLevelId()!=7){
                            workQueue.push(mdoc.getIaid());
                        }
                        SolrDoc sdoc=new SolrDoc(mdoc);
                        solrDocs.add(sdoc);
                        mongoDocs.add(sdoc.toSon());
                        webDocs.add(sdoc.map());
                        docCounter++;
                        if(sdoc.checkIfDept()){
                            deptList.add(sdoc.getIaid());
                        }
                        else if(deptList.contains(sdoc.getParent())){
                            workingDept=sdoc.getDepartment();
                            if(!workingDept.equals(oldDept)){
                                System.out.println("Cleared cache changed from "+oldDept+" to "+workingDept);
                                
                                parentCache.clear();
                                dateCache.clear();
                                urlCache.clear();
                                titleCache.clear();
                                
                                cache.clear();
                                oldDept=workingDept;
                            }
                            
                        }
                        if (docCounter%5000==0){ //5000 is the number of documents per batch, seems to be a good compromise
                            if(this.doMongoStore){
                                fetcher.store(mongoDocs);
                            }
                            if(this.doFileStore){
                                SolrDoc.writeXMLasString(batchCounter,this.savePath, solrDocs, workingDept);
                            }
                            boolean commitFlag=((batchCounter+1)%20==0);
                            if(this.doSolrPost){
                                postie.queueSolrPost(webDocs, commitFlag);
                                
                            }
                            batchCounter++;
                            mongoDocs.clear();
                            solrDocs.clear();
                            webDocs.clear();
                            System.gc();
                            nowTime=System.nanoTime();
                            elapsedTime=nowTime-startTime;
                            //converts reported nanoseconds to documents per second based on batch size of 5000
                            Double dps=1/(elapsedTime/5000/1000000000.0); 
                            Integer percentDone=docCounter*100/totalDocs;
                            System.out.println("Processed "
                                    + docCounter.toString() 
                                    + " BatchID "
                                    + batchCounter.toString()
                                    + " completed "
                                    + percentDone+"% in "
                                    + dps.intValue()+" dps"
                                    +" Queue is "
                                    + workQueue.size());
                            startTime=nowTime; //updates start time for new batch
                            //SolrDoc.writeXML(batchCounter,savePath, solrDocs);
                            
                        }
                        
                    }

                }
                
            }
            if(this.doFileStore){
                SolrDoc.writeXMLasString(batchCounter,this.savePath, solrDocs, workingDept);
            }
            boolean commitFlag=true;
            if(this.doMongoStore){
                                fetcher.store(mongoDocs);
                            }
            if(this.doSolrPost){
                postie.queueSolrPost(webDocs, commitFlag);
                
            }
            nowTime=System.nanoTime();
            //converts reported nanoseconds to documents per second based on batch size of 5000
            Double elapsed=1/((nowTime-startTime)/(docCounter%5000)/1000000000.0); 
            Integer percentDone;
            percentDone = docCounter*100/totalDocs;
            System.out.println("Processed "
                    + docCounter.toString() 
                    + " BatchID "
                    + batchCounter.toString()
                    + " completed "
                    + percentDone+"% in "
                    + elapsed.intValue()+" dps"
                    +" Queue is "
                    + workQueue.size());
            
                           
            mongoDocs.clear();
            solrDocs.clear();
            webDocs.clear();
            cache.clear();
            System.gc();
             System.out.println("Job completed. "+ docCounter.toString()+" documents processed");
            elapsedTime = (nowTime-beginTime)/1000000000;
            System.out.println("Duration was: "+elapsedTime.toString()+" seconds");
            
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        finally{
            postie.requestStop();
        }
        
    }
    
    
    
    /**
     * Exports contents of Solr Server to file as XML files
     * @param fPath Path to export files to
     * @param batchsize number of documents per file
     * THIS IS CURRENTLY VERY SLOW and not fully implemented
     */
    
    
    /**
     * Imports XML files and indexes them to Solr
     * @param fPath Path to export files to
     * not yet Implemented
     */
    public void importXML(String fPath){
        
    }
    
    /**
     * Imports JSON files and indexes them to Solr
     * @param fPath Path to export files to
     * not yet implemented
     */
    public void importJSON(String fPath){
    
    }
    
    /**
     * Exports contents of Solr Server to file as JSON files
     * @param fPath Path to export files to
     * @param batchsize number of documents per file
     * THIS IS CURRENTLY VERY SLOW and not fully implemented
     */
    public void exportJSON(String fPath, int batchsize){
        
    }
    
}
