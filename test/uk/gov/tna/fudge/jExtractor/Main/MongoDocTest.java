/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Main;

import com.mongodb.DBObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author Steve
 */
public class MongoDocTest {
    static MongoDoc mdoc;
    static Fetcher fetcher;
    static RefCache refCache;
    static CoveringDateCache dateCache;
    
    public MongoDocTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        fetcher=new Fetcher("192.168.0.6","27017","iadata","solrtest","informationasset","solrtestcoll");
        refCache=new RefCache();
        dateCache=new CoveringDateCache();
        mdoc=new MongoDoc(fetcher.findOne("IAID", "C1"),refCache,dateCache,fetcher);
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of toMongoSon method, of class MongoDoc.
     */
    @Test
    public void testToMongoSon() {
        System.out.println("toMongoSon");
        MongoDoc instance = new MongoDoc(fetcher.findOne("IAID", "D7738606"),refCache,dateCache,fetcher);
        String expResult = "2006";
        String result = (String)instance.toMongoSon().get("closureCode");
        assertEquals(expResult, result);
    }
}
