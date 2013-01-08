/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor;

/**
 *
 * @author steve
 */

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class Fetcher {
    MongoClient mongoClient;
    DB sourceDb;
    DB storeDb;
    DBCollection sourceColl;
    DBCollection destColl;
    
    Fetcher(String server, String port, String indb, String outdb, String incoll, String outcoll)
    {
        Integer iPort=Integer.parseInt(port);
        try{
            mongoClient=new MongoClient( server , iPort );
            sourceDb=mongoClient.getDB(indb);
            storeDb=mongoClient.getDB(outdb);
            sourceColl=sourceDb.getCollection(incoll);
            destColl=storeDb.getCollection(outcoll);
            System.out.println("Connected to Mongo");
            
        }
        catch (Exception e)
        {
            System.out.println("Unable to connect to Mongo "+ e.getMessage());
            System.exit(1);
        }
    }
    
    DBCursor findMany(String field, String pattern)
    {
        BasicDBObject query = new BasicDBObject(field, pattern);
        DBCursor cursor = sourceColl.find(query);
        return cursor;
    }
    
    DBObject findOne(String field, String pattern)
    {
        BasicDBObject query = new BasicDBObject(field, pattern);
        DBObject doc=sourceColl.findOne(query);
        return doc;
    }
    
    void store(DBObject doc)
    {
        destColl.insert(doc);
    }
    
}
