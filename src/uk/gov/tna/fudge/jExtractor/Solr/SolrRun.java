/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Solr;

/**
 *
 * @author steve
 */
public class SolrRun {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        SolrPostman postie=new SolrPostman("http://localhost:8080/solr/discoverytest");
        postie.querytest();
    }
}
