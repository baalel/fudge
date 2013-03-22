/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Main;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import uk.gov.tna.fudge.jExtractor.Solr.SolrPostman;
import org.apache.solr.common.SolrInputDocument;
import uk.gov.tna.fudge.jExtractor.Solr.SolrPostService;


/**
 *Reads files extracted from Mongo using extractor and posts to Solr
 * @author sprice
 */
public class IndexFromFiles {
    Properties localProp;
    Properties sysProp;
    String savePath;
    String solrWebServer;
    SolrPostService postie;
    String distribute;
    List<String> solrServerList;
    boolean doSolrPost;
    boolean doMongoStore;
    boolean doFileStore;
    boolean verbose;
    List<File> filesToIndex;
    
    public IndexFromFiles(){
        
    }

    IndexFromFiles(Properties localProp) {
        solrWebServer=localProp.getProperty("SOLR_WEBSERVER","http://localhost:8080/solr/discoverytest");
        this.distribute=localProp.getProperty("DISTRIBUTE", "FALSE");
        this.doSolrPost=("TRUE".equals(localProp.getProperty("INDEXSOLR", "FALSE")));
        this.savePath=localProp.getProperty("SAVE_PATH","/home/sprice/solrdoc");
        String[] distservers=localProp.getProperty("DIST_SOLR_SERVERS", "http://localhost:8080/solr/discovery1,http://localhost:8080/solr/discovery2").split(",");
        this.solrServerList=new ArrayList<>(2);
        solrServerList.addAll(Arrays.asList(distservers));
        if(!"TRUE".equals(distribute)){
            this.postie=new SolrPostService(this.solrWebServer);
        }
        else{
            this.postie=new SolrPostService(solrServerList);
        }
        
    }
    
    public void load() {
        this.filesToIndex=new ArrayList<>();
        File folder=new File(this.savePath);
        File[] listOfFiles = folder.listFiles();
        for( File f : listOfFiles){
            if(f.getName().endsWith(".xml") || f.getName().endsWith(".XML")){
                filesToIndex.add(f);
            }
        }
        
    }
    
    public void run() {
        for(File f : filesToIndex){
            System.out.println(f.getName());
        }
        
    }
    
    
    
    
    
}
