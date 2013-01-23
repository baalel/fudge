/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Main;

/**
 *
 * @author steve
 */

import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.util.List;

/**
 * 
 * @author steve
 */
public class Fetcher {
    MongoClient mongoClient;
    DB sourceDb;
    DB storeDb;
    DBCollection sourceColl;
    DBCollection destColl;
    
    /**
     * Constructor to initialize the MongoDB Handler
     * @param server The mongo server FQDN or IP address
     * @param port The Mongo port
     * @param indb The Mongodb to retrieve the information assets from
     * @param outdb The Mongodb to store solr documents in
     * @param incoll The collection in indb to retrieve the information assets from
     * @param outcoll The collection in outdb to store the solr documents in
     */
    Fetcher(String server, String port, String indb, String outdb, String incoll, String outcoll)
    {
        Integer iPort=Integer.parseInt(port);
        try{
            mongoClient=new MongoClient( server , iPort );
            sourceDb=mongoClient.getDB(indb);
            storeDb=mongoClient.getDB(outdb);
            sourceColl=sourceDb.getCollection(incoll);
            destColl=storeDb.getCollection(outcoll);
            destColl.drop();
            System.out.println("Connected to Mongo");
            
        }
        catch (Exception e)
        {
            System.out.println("Unable to connect to Mongo "+ e.getMessage());
            System.exit(1);
        }
    }
    
    public Fetcher(String server, String port, String indb, String incoll)
    {
        Integer iPort=Integer.parseInt(port);
        try{
            mongoClient=new MongoClient( server , iPort );
            sourceDb=mongoClient.getDB(indb);
            storeDb=null;
            sourceColl=sourceDb.getCollection(incoll);
            destColl=null;
            destColl.drop();
            System.out.println("Connected to Mongo");
            
        }
        catch (Exception e)
        {
            System.out.println("Unable to connect to Mongo "+ e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Returns a cursor pointing to assets that match pattern in field
     * @param field the field to be searched on
     * @param pattern the pattern to look for
     * @return a cursor containing the result set
     */
    DBCursor findMany(String field, String pattern)
    {
        BasicDBObject query = new BasicDBObject(field, pattern);
        DBCursor cursor = sourceColl.find(query);
        cursor.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
        //cursor.addOption(Bytes.QUERYOPTION_EXHAUST);
        return cursor;
    }
    
    /**
     * A simple one field searcher, may be used for future expansion
     * @param field The field to search for pattern in
     * @param pattern The pattern to search for
     * @return a BSON DBObject containing the document found if any
     * will return null if no documents match the pattern
     */
    DBObject findOne(String field, String pattern)
    {
        BasicDBObject query = new BasicDBObject(field, pattern);
        DBObject doc=sourceColl.findOne(query);
        return doc;
    }
    
    /**
     * Pass in an assets ParentIAID to get back inheritable reference and date information
     * @param parent the Iaid to extract
     * @return a DBObject 
     */
    DBObject findParent(String parent)
    {
        BasicDBObject query=new BasicDBObject("IAID", parent);
        BasicDBObject fieldlist=new BasicDBObject("IAID",1).append("ParentIAID", 1).append("Reference", 1).append("SourceLevelId",1).append("CoveringDateFrom", 1).append("CoveringDateTo", 1).append("Title", 1);
        DBObject doc=sourceColl.findOne(query, fieldlist);
        
        return doc;
    }
    
    /**
     * Saves document to output collection
     * will update if already exists in collection or insert if not
     * @param doc 
     */
    void store(DBObject doc)
    {
        destColl.save(doc);
    }

    /**
     * Inserts the List of documents into the output collection
     * Will throw a duplicate document exception if a member of the
     * collection already exists in the mongodb collection
     * @param mongoDocs 
     */
    void store(List<DBObject> mongoDocs) {
        destColl.insert(mongoDocs);
    }
    
    public void resetDB()
    {
        destColl.drop();
        storeDb.dropDatabase();
        mongoClient.close();
    }
    
    public int docCount(){
        BasicDBObject query=new BasicDBObject("IAID", "/.+/");
        
        int doccount=sourceColl.find().count();
        
        return doccount;
        
    }
    
    public BasicDBObject loadCategory(String catid){
            return null;
    }
    
}
