/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jcategorizer;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrDocumentList;
import uk.gov.tna.fudge.jExtractor.Main.Fetcher;

/**
 *
 * @author steve
 */
public class Category {
    private String catId;
    private String catName;
    private String catDescription;
    private String queryText;
    private SolrDocumentList members;
    private float threshold;
    private SolrQuery query;
    private Fetcher fetch;
    
    public Category(Fetcher fetcher, String catid){
        this.fetch=fetcher;
        this.catId=catid;
        //load Category from Mongo
        
    }
    
    public void loadCategory(){
        BasicDBObject catData=fetch.loadCategory(catId);
        
        
    }

    /**
     * @return the catName
     */
    public String getCatName() {
        return catName;
    }

    /**
     * @return the queryText
     */
    public String getQueryText() {
        return queryText;
    }

    Boolean processDoc(DBObject doc) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
}
