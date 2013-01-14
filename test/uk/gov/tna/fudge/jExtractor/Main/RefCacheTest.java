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
public class RefCacheTest {
    static RefCache refCache;
    
    public RefCacheTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        refCache=new RefCache();
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
     * Test of insert method, of class RefCache.
     */
    @Test
    public void testInsert() {
        System.out.println("insert");
        String id = "C1";
        String reference = "A";
        RefCache instance = RefCacheTest.refCache;
        instance.insert(id, reference);
        String expResult="A";
        String result=(String)refCache.refLookup.get("C1");
        assertEquals(expResult,result);
    }

    /**
     * Test of lookup method, of class RefCache.
     */
    @Test
    public void testLookup() {
        System.out.println("lookup");
        String id = "C2";
        RefCache instance = RefCacheTest.refCache;
        refCache.refLookup.put("C2", "ACT");
        String expResult = "ACT";
        String result = instance.lookup(id);
        assertEquals(expResult, result);
    }

    /**
     * Test of exists method, of class RefCache.
     */
    @Test
    public void testExists() {
        System.out.println("exists");
        String key = "C100";
        RefCache instance = RefCacheTest.refCache;
        refCache.refLookup.put("C2", "ACT");
        boolean expResult = false;
        boolean result = instance.exists(key);
        assertEquals(expResult, result);
    }

    /**
     * Test of clear method, of class RefCache.
     */
    @Test
    public void testClear() {
        System.out.println("clear");
        RefCache instance = RefCacheTest.refCache;
        instance.clear();
        boolean result=refCache.refLookup.containsKey("C1");
        boolean expResult=false;
        assertEquals(expResult, result);
    }
}
