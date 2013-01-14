/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Main;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author steve
 */
public class SolrDocTest {
    static RefCache refCache;
    static CoveringDateCache dateCache;
    static Fetcher fetcher;
    SolrDoc testSolrDoc;
    MongoDoc testMongoDoc;
    
    public SolrDocTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        refCache=new RefCache();
        dateCache=new CoveringDateCache();
        fetcher=new Fetcher("192.168.0.6","27017","iadata","solrtest","informationasset","solrtestcoll");
    }
    
    @AfterClass
    public static void tearDownClass() {
        refCache.clear();
        dateCache.clear();
        fetcher.resetDB();
    }
    
    @Before
    public void setUp() {
        
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of checkIfDept method, of class SolrDoc.
     */
    @Test
    public void testCheckIfDeptTrue() {
        System.out.println("checkIfDept_true");
        SolrDoc trueinstance = new SolrDoc(new MongoDoc(fetcher.findOne("IAID", "C1"),refCache,dateCache,fetcher));
        boolean expResult = true;
        boolean result = trueinstance.checkIfDept();
        assertEquals(expResult, result);
        
    }
    
    @Test
    public void testCheckIfDeptFalse() {
        System.out.println("checkIfDept_false");
        SolrDoc trueinstance = new SolrDoc(new MongoDoc(fetcher.findOne("IAID", "D7738606"),refCache,dateCache,fetcher));
        boolean expResult = false;
        boolean result = trueinstance.checkIfDept();
        assertEquals(expResult, result);
        
    }


    /**
     * Test of getDepartment method, of class SolrDoc.
     */
    @Test
    public void testGetDepartment_String() {
        System.out.println("getDepartment");
        String ref = "ADM 12/4/5";
        String expResult = "ADM";
        String result = SolrDoc.getDepartment(ref);
        assertEquals(expResult, result);

    }

    /**
     * Test of writeXMLasString method, of class SolrDoc.
     */
    @Test
    public void testWriteXMLasString() {
        System.out.println("writeXMLasString");
        Integer batchid = 0;
        String savePath = "/";
        List<SolrDoc> docs = new ArrayList<SolrDoc>();
        docs.add(new SolrDoc(new MongoDoc(fetcher.findOne("IAID", "D7738606"),refCache,dateCache,fetcher)));
        String currDeptName = "BOB";
        boolean result=SolrDoc.writeXMLasString(batchid, savePath, docs, currDeptName);
        boolean expResult = true;
        assertEquals(expResult, result);

    }

    /**
     * Test of getIaid method, of class SolrDoc.
     */
    @Test
    public void testGetIaid() {
        System.out.println("getIaid");
        SolrDoc instance = new SolrDoc(new MongoDoc(fetcher.findOne("IAID", "D7738606"),refCache,dateCache,fetcher));
        String expResult = "D7738606";
        String result = instance.getIaid();
        assertEquals(expResult, result);

    }

    /**
     * Test of getDepartment method, of class SolrDoc.
     */
    @Test
    public void testGetDepartment_0args() {
        System.out.println("getDepartment");
        SolrDoc instance = new SolrDoc(new MongoDoc(fetcher.findOne("IAID", "D7738606"),refCache,dateCache,fetcher));
        String expResult = "CAB";
        String result = instance.getDepartment();
        assertEquals(expResult, result);
    }

    /**
     * Test of getParent method, of class SolrDoc.
     */
    @Test
    public void testGetParent() {
        System.out.println("getParent");
        SolrDoc instance = new SolrDoc(new MongoDoc(fetcher.findOne("IAID", "D7738606"),refCache,dateCache,fetcher));
        String expResult = "C11277869";
        String result = instance.getParent();
        assertEquals(expResult, result);
    }

    /**
     * Test of toSon method, of class SolrDoc.
     */
    @Test
    public void testToSon() {
        System.out.println("toSon");
        SolrDoc instance = new SolrDoc(new MongoDoc(fetcher.findOne("IAID", "D7738606"),refCache,dateCache,fetcher));
        String expResultSeries = "CAB 195";
        String result = (String)instance.toSon().get("SERIES");
        assertEquals(expResultSeries, result);
    }
}
