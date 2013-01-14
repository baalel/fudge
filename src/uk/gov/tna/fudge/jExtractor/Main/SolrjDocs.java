/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Main;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.solr.common.SolrInputDocument;
/**
 *
 * @author steve
 */
public class SolrjDocs {
    SolrInputDocument sDoc;
    List<SolrInputDocument> docCollection;
    
    public SolrjDocs(){
        docCollection=new ArrayList<SolrInputDocument>(5000);
        
    }
    
    public void add(SolrDoc s){
        docCollection.add(new SolrInputDocument(s.map()));
        
    }
    
    public void clear(){
        docCollection.clear();
    }
    
    public Iterator<SolrInputDocument> iterator(){
        //return new sIteratorClass();
        return docCollection.iterator();
    }
  
    
    /**
     *
     */
    public class sIteratorClass implements Iterator<SolrInputDocument>{ 
        @Override
        public void remove(){

        }

        @Override
        public SolrInputDocument next(){

            return null;
        }

        @Override
        public boolean hasNext(){

            return true;
        }
    }
    
    
}
