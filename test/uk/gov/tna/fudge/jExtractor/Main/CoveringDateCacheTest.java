/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Main;

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
public class CoveringDateCacheTest {
    static CoveringDateCache dateCache;
    
    public CoveringDateCacheTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        dateCache=new CoveringDateCache();
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
     * Test of clear method, of class CoveringDateCache.
     */
    @Test
    public void testClear() {
        System.out.println("clear");
        CoveringDateCache instance = CoveringDateCacheTest.dateCache;
        instance.clear();
        Integer result=instance.endLookup.size();
        Integer expResult=1;
        assertEquals(result, expResult);
    }

    /**
     * Test of insertStart method, of class CoveringDateCache.
     */
    @Test
    public void testInsertStart() {
        System.out.println("insertStart");
        String docid = "C1";
        String docdate = "2100-12-31T23:59:59.9Z";
        CoveringDateCache instance = CoveringDateCacheTest.dateCache;
        instance.insertStart(docid, docdate);
        boolean result=instance.startLookup.containsKey("C1");
        boolean expResult=true;
        assertEquals(result,expResult);
    }

    /**
     * Test of insertEnd method, of class CoveringDateCache.
     */
    @Test
    public void testInsertEnd() {
        System.out.println("insertEnd");
        String docid = "C2";
        String docdate = "2100-12-31T23:59:59.9Z";
        CoveringDateCache instance = CoveringDateCacheTest.dateCache;
        instance.insertEnd(docid, docdate);
        boolean result=instance.endLookup.containsKey("C2");
        boolean expResult=true;
        assertEquals(result,expResult);
    }

    /**
     * Test of lookupStart method, of class CoveringDateCache.
     */
    @Test
    public void testLookupStart() {
        System.out.println("lookupStart");
        String docid = "C0";
        CoveringDateCache instance = CoveringDateCacheTest.dateCache;
        String expResult = "1000-01-01T00:00:00.0Z";
        String result = instance.lookupStart(docid);
        assertEquals(expResult, result);
    }

    /**
     * Test of lookupEnd method, of class CoveringDateCache.
     */
    @Test
    public void testLookupEnd() {
        System.out.println("lookupEnd");
        String docid = "C0";
        CoveringDateCache instance = CoveringDateCacheTest.dateCache;
        String expResult = "2100-12-31T23:59:59.9Z";
        String result = instance.lookupEnd(docid);
        assertEquals(expResult, result);
    }

    /**
     * Test of existsStart method, of class CoveringDateCache.
     */
    @Test
    public void testExistsStart() {
        System.out.println("existsStart");
        String docid = "Z0";
        CoveringDateCache instance = CoveringDateCacheTest.dateCache;
        boolean expResult = false;
        boolean result = instance.existsStart(docid);
        assertEquals(expResult, result);
    }

    /**
     * Test of existsEnd method, of class CoveringDateCache.
     */
    @Test
    public void testExistsEnd() {
        System.out.println("existsEnd");
        String docid = "C0";
        CoveringDateCache instance = CoveringDateCacheTest.dateCache;
        boolean expResult = true;
        boolean result = instance.existsEnd(docid);
        assertEquals(expResult, result);
    }
}
