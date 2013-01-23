/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Main;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;


/**
 *
 * @author Steve
 */
public class MongoDocTest {
    static MongoDoc mdoc;
    static Fetcher fetcher;
    static RefCache refCache;
    static UrlParamCache urlCache;
    static TitleCache titleCache;
    static CoveringDateCache dateCache;
    
    public MongoDocTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        fetcher=new Fetcher("192.168.0.6","27017","iadata","solrtest","informationasset","solrtestcoll");
        refCache=new RefCache();
        dateCache=new CoveringDateCache();
        urlCache=new UrlParamCache();
        titleCache=new TitleCache();
        mdoc=new MongoDoc(fetcher.findOne("IAID", "C1"),refCache,dateCache,urlCache,titleCache,fetcher);
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
}
