/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Main;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Steve
 */
public class FetcherTest {
    static Fetcher fetcher;
    
    public FetcherTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        fetcher=new Fetcher("192.168.0.6","27017","iadata","solrtest","informationasset","solrtestcoll");
    }
    
    @AfterClass
    public static void tearDownClass() {
        fetcher.resetDB();
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of findMany method, of class Fetcher.
     */
    @Test
    public void testFindMany() {
        System.out.println("findMany");
        String field = "ParentIAID";
        String pattern = "C0";
        Fetcher instance = FetcherTest.fetcher;
        Integer expResult = 434;
        Integer result = instance.findMany(field, pattern).count();
        assertEquals(expResult, result);

    }

    /**
     * Test of findOne method, of class Fetcher.
     */
    @Test
    public void testFindOne() {
        System.out.println("findOne");
        String field = "IAID";
        String pattern = "D7738606";
        Fetcher instance = FetcherTest.fetcher;
        String expResult = "37";
        String result = (String)instance.findOne(field, pattern).get("Reference");
        assertEquals(expResult, result);
    }

    /**
     * Test of findParent method, of class Fetcher.
     */
    @Test
    public void testFindParent() {
        System.out.println("findParent");
        String parent = "C1";
        Fetcher instance = FetcherTest.fetcher;
        String expResult = "A";
        String result = (String)instance.findParent(parent).get("Reference");
        assertEquals(expResult, result);
    }

}
