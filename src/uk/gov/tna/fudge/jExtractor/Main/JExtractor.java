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
import java.util.Properties;
import java.util.Stack;

public class JExtractor {

    /**
     * @param args the command line arguments
     */
    //@SuppressWarnings("unchecked")
    public static void main(String[] args) {
        // TODO code application logic here
        Fetcher fetcher;
        RefCache parentCache=new RefCache();
        Stack<String> workQueue=new Stack();
        Properties prop = new Properties();
        Properties p =new Properties(System.getProperties());
        ArrayList<SolrDoc> solrDocs=new ArrayList<SolrDoc>(1000);
        String savePath;
        Integer batchCounter=0;
        int docCounter=0;
        try{
            //InputStream in=new InputStream(JExtractor.class.getClassLoader().getResource("/Resource/TNAconf.properties"));
            //path /home/sprice/NetBeansProjects/jExtractor/src/uk/gov/tna/fudge/jExtractor/
            String path=p.getProperty("user.dir")+"/Resources/TNAconf.properties";
            prop.load(new FileInputStream(path));
            
        }
        catch(IOException ex)
        {
            System.out.println("Unable to load Properties file " + ex.getMessage());
            System.exit(1);
        }
        workQueue.push(prop.getProperty("ROOT_NODE", "C0"));
        try{
            savePath=prop.getProperty("SAVE_PATH","/home/sprice/solrdoc");
            fetcher=new Fetcher(prop.getProperty("MONGO_SERVER", "localhost") ,
                    prop.getProperty("MONGO_PORT", "27017"),
                    prop.getProperty("MONGO_INDB","iadata"),
                    prop.getProperty("MONGO_OUTDB","solrdb"),
                    prop.getProperty("MONGO_INCOL","informationasset"),
                    prop.getProperty("MONGO_OUTCOL","solrout"));
            while(!workQueue.isEmpty()){
                String docid=workQueue.pop();
                DBCursor cursor=fetcher.findMany("ParentIAID", docid);
                try {
                    while(cursor.hasNext()) {
                        DBObject doc=cursor.next();
                        MongoDoc mdoc=new MongoDoc(doc,parentCache,fetcher);
                        //fetcher.store(mdoc.toMongoSon());
                        workQueue.push(mdoc.iaid);
                        SolrDoc sdoc=new SolrDoc(mdoc);
                        solrDocs.add(sdoc);
                        docCounter++;
                        if (docCounter%1000==0){
                            System.out.println("Processed "+ docCounter);
                            //SolrDoc.writeXML(batchCounter,savePath, solrDocs);
                            SolrDoc.writeXMLasString(batchCounter,savePath, solrDocs);
                            batchCounter++;
                            solrDocs.clear();
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
}
