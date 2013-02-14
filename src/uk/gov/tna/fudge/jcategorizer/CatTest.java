/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jcategorizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import uk.gov.tna.fudge.jExtractor.Solr.SolrPostService;

/**
 *
 * @author steve
 */
public class CatTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ArrayList<String> lines=new ArrayList<>();
        File folder=new File("/home/steve/fudge/CatText");
        if(folder.isDirectory()){
            for (File fileEntry : folder.listFiles()) {
                lines.clear();

                try (BufferedReader br = new BufferedReader(new FileReader(fileEntry))) {
            // use br

                    String line=null;
                    while((line=br.readLine())!=null){
                        line=line.trim();
                        lines.add(line);
                    }
                }
                catch(Exception ioe){
                    System.exit(1);

                }
                String queryText="";
                if(lines.size()>0){
                    for(String s:lines){
                    queryText+=s+" ";
                    }

                }
                System.out.println(fileEntry.getName());
                createCategory(queryText);

            }
        }
        else{
            System.out.println("No such directory");
        }
    }

    private static void createCategory(String queryText) {
        //System.out.println(queryText);
        SolrPostService postie=new SolrPostService("http://localhost:8080/solr/discovery1");
        postie.querytest(queryText);
        
        
    }
}
