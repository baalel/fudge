/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Main;

/**
 *
 * @author steve
 */

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

public class JExtractor {
    
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
    
    JExtractor()
    {
        this.localProp = new Properties();
        this.sysProp =new Properties(System.getProperties());
        try{
            //InputStream in=new InputStream(JExtractor.class.getClassLoader().getResource("/Resource/TNAconf.properties"));
            //path /home/sprice/NetBeansProjects/jExtractor/src/uk/gov/tna/fudge/jExtractor/
            String path=sysProp.getProperty("user.dir")+"/Resources/Homeconf.properties";
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
        
    }
    
    public void run(String mode)
    {
        if ("PULL".equals(mode)){
            this.pull();
        }
        else if("PUSH".equals(mode)){
            this.push();
        }
        else if("POST".equals(mode)){
            this.post();
        }
        else{
            System.out.println("Supported modes are POST, PUSH and PULL");
        }
        
    }
    
    private void push()
    {
        String server="http://localhost:8080/solr/discoverytest";
        uk.gov.tna.fudge.jExtractor.Solr.SolrPostman postie;
        postie = new uk.gov.tna.fudge.jExtractor.Solr.SolrPostman(server);
        postie.querytest();
        
    }
    
    private void pull(){
 
        long startTime = System.nanoTime();
        long nowTime;
        Fetcher fetcher;
        RefCache parentCache=new RefCache();
        CoveringDateCache dateCache=new CoveringDateCache();
        Stack<String> workQueue=new Stack();
        
        List<SolrDoc> solrDocs=new ArrayList<SolrDoc>(5000);
        List<DBObject> mongoDocs=new ArrayList<DBObject>(5000) ;
        String workingDept="START";
        String oldDept;
        oldDept = "";
        List<String> deptList=new ArrayList<String>(500);
        Integer batchCounter=0;
        Integer docCounter=0;
        
        workQueue.push(localProp.getProperty("ROOT_NODE", "C0"));
        try{
            
            fetcher=new Fetcher( mongoServer,mongoPort,iaDatabase,solrDatabase,iaCollection,solrCollection);
            while(!workQueue.isEmpty()){
                String docid=workQueue.pop();
                DBCursor cursor=fetcher.findMany("ParentIAID", docid);
                try {
                    while(cursor.hasNext()) {
                        DBObject doc=cursor.next();
                        MongoDoc mdoc=new MongoDoc(doc,parentCache,dateCache,fetcher);                      
                        workQueue.push(mdoc.iaid);
                        SolrDoc sdoc=new SolrDoc(mdoc);
                        solrDocs.add(sdoc);
                        mongoDocs.add(sdoc.toSon());
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
                                oldDept=workingDept;
                            }
                            
                        }
                        if (docCounter%5000==0){
                            fetcher.store(mongoDocs);
                            mongoDocs.clear();
                            nowTime=System.nanoTime();
                            Double elapsed=1/((nowTime-startTime)/5000/1000000000.0);
                            Integer percentDone=docCounter/210000;
                            System.out.println("Processed "
                                    + docCounter.toString() 
                                    + " BatchID "
                                    + batchCounter.toString()
                                    + " completed "
                                    + percentDone+"% in "
                                    + elapsed.intValue()+" dps"
                                    +" Queue is "
                                    + workQueue.size());
                            startTime=nowTime;
                            //SolrDoc.writeXML(batchCounter,savePath, solrDocs);
                            SolrDoc.writeXMLasString(batchCounter,this.savePath, solrDocs, workingDept);
                            batchCounter++;
                            solrDocs.clear();
                            System.gc();
                            
                        }
                        
                    }
                } finally {
                    cursor.close();
                }
                
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        
    }
    
    public void post()
    {
        String server="http://localhost:8080/solr/discoverytest";
        uk.gov.tna.fudge.jExtractor.Solr.SolrPostman postie;
        SolrInputDocument sDoc;
        sDoc=new SolrInputDocument();
        sDoc.addField("CATDOCREF", "12345");
        sDoc.addField("DREREFERENCE", "12345");
        sDoc.addField("TITLE", "Test Document");
        sDoc.addField("DESCRIPTION", "A document to test solrj insert");
        sDoc.addField("SOURCELEVEL",8);
        sDoc.addField("PERIOD",new String[] { "aaa", "bbb", "ccc" });
        sDoc.addField("REFERENCE",new String[] { "aaa", "bbb", "ccc" });
        sDoc.addField("PERSON",new String[] { "aaa", "bbb", "ccc" });
        sDoc.addField("PLACE",new String[] { "aaa", "bbb", "ccc" });
        sDoc.addField("CORPBODY",new String[] { "aaa", "bbb", "ccc" });
        sDoc.addField("HELDBY",new String[] { "aaa", "bbb", "ccc" });
        sDoc.addField("DEPARTMENT","ADB");
        sDoc.addField("SERIES","ADB 123");
        sDoc.addField("STARTDATE","1805-01-20T00:00:00Z");
        sDoc.addField("ENDDATE","1815-01-20T00:00:00Z");
        sDoc.addField("URLPARAMS","0/1/2/3/4");
        sDoc.addField("CLOSURECODE",1);
        sDoc.addField("CLOSURETYPE","open");
        sDoc.addField("CLOSURESTATUS","open");
        
        postie = new uk.gov.tna.fudge.jExtractor.Solr.SolrPostman(server);
        postie.postDocument(sDoc);
    }
    
    public static void main(String[] args) {
        JExtractor indexer=new JExtractor();
        indexer.run("POST");
    }
}
