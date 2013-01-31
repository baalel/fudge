/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jcategorizer;


import com.mongodb.BasicDBList;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import uk.gov.tna.fudge.jExtractor.Main.Fetcher;
import uk.gov.tna.fudge.jExtractor.Solr.SolrPostService;

/**
 *Loads Category Queries from Mongo
 * Executes queries against Solr Reference Core
 * Updates search Cores with Taxonomy tags
 * @author steve
 */
public class JCategorizer {
    Fetcher catFetcher;
    Fetcher docFetcher;
    List<String> catList;
    List<Category> categories;
    Boolean verboseFlag;
    String solrServer;
    SolrPostService solrService;
    
    
    public JCategorizer(String config, boolean verbose){
        catFetcher=new Fetcher("localhost","27017","catdb","catcol");
        docFetcher=new Fetcher("localhost","27017","solrdb","solrcollection");
        solrServer="http://localhost:8080/solr/discoverytest";
        catList=new ArrayList<>();
        verboseFlag=verbose;
        
    }
    
    public void run(){
        DBCursor curs=docFetcher.findAll();
        while(curs.hasNext()){
            DBObject doc=curs.next();
            for(Category cat: categories){
                Boolean hit=cat.processDoc(doc);
                if(hit){
                    BasicDBList taxTags;
                    if(doc.containsField("TAXONOMY")){
                        taxTags=(BasicDBList)doc.get("TAXONOMY");
                    }
                    else{
                        taxTags=new BasicDBList();
                    }
                    taxTags.add(cat.getCatName());
                    doc.put("TAXONOMY", taxTags);
                }
            }
            docFetcher.store(doc);
                    
        }
        
        
    }
    
    public void load(String dataSource){
        switch (dataSource) {
            case "MONGO":
                getCatsFromMongo();
                break;
            case "FILE":
                getCatsFromFile();
                break;
            case "SOLR":
                getCatsFromSolr();
                break;
            default:
                System.out.println("Category source should be MONGO, FILE or SOLR");
                System.exit(1);
                break;
            
        }
        for(String cat : this.catList){
            Category c=new Category(catFetcher,cat);
            c.loadCategory();
            categories.add(c);
            System.out.println("Created Category "+c.getCatName());
            
        }
        
    }
    
    private void getCatsFromMongo(){

        catList.add("TESTCAT");
        categories.add(new Category(docFetcher,"TESTCAT"));

    }
    
    private void getCatsFromFile(){
        catList.add("TESTCAT");
        categories.add(new Category(docFetcher,"TESTCAT"));
    }
    
    private void getCatsFromSolr(){
        catList.add("TESTCAT");
        categories.add(new Category(docFetcher,"TESTCAT"));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        

        // create the command line parser
        CommandLineParser parser = new PosixParser();
        // create the Options
        Options options;
        options = new Options();
        options.addOption("h","help",false,"displays this help message");
        options.addOption("d", "data-source", true, "The source of the category definitions");
        options.addOption("c","config-file",true, "The config file to read");
        options.addOption("v","verbose",false,"increases information reported to stdout");
        HelpFormatter formatter = new HelpFormatter();
        boolean verboseFlag;
        String cfgFile;
        String dataSource;
        dataSource = "";
        try{
            // parse the command line arguments
            CommandLine line = parser.parse( options, args );
            if( line.hasOption( "help" ) ) {
                
                formatter.printHelp( "jExtractor", options );
            }
            if(line.hasOption("data-source")){
                dataSource=line.getOptionValue("action").toUpperCase();
            }
            else{
                System.out.println("No datasource specified");
                formatter.printHelp( "jExtractor", options );
                System.exit(0);
            }
            if(line.hasOption("config-file")){
                cfgFile=line.getOptionValue("config-file");
            }
            else{
                System.out.println("No Config file specified");
                System.out.println("Using default, TNAConfig");
                cfgFile="TNAConfig";
            }
            verboseFlag=line.hasOption("verbose");
            JCategorizer catMaker=new JCategorizer(cfgFile, verboseFlag);
            catMaker.load(dataSource);
            catMaker.run();
            
        } catch (org.apache.commons.cli.ParseException ex) {
            //Logger.getLogger(JCategorizer.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Unable to parse command line options");
            System.out.println("Reason: "+ex.getMessage());
            System.exit(1);
        }
        
    }
}
