/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Main;

/**
 *
 * @author Steve
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SolrPostman {
    
    private String solrserver;
    private static String charset = "UTF-8";
    private String commit;
    private String query;
    private URLConnection urlConnection;
    
    
    SolrPostman(String solrUrl){
        try{
            urlConnection=new URL(solrUrl).openConnection();
            query="";
            commit="";
            this.solrserver=solrUrl;
            this.solrserver="http://localhost:8983/solr/update";
            try{
                commit = URLEncoder.encode("commit=true", SolrPostman.charset);
                query=String.format("param1=%s", commit);
            }
            catch(UnsupportedEncodingException e)
            {
                System.out.println("This shouldn't happen");
            }
            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true); // Triggers POST.
            urlConnection.setRequestProperty("accept-charset", SolrPostman.charset);
            urlConnection.setRequestProperty("content-type", "text/xml");

        }
        catch(IOException ioe){
            System.out.println("Unable to open connection to solr" + ioe.getMessage());
            ioe.printStackTrace();
            System.exit(1);
            
        }
        
    }
    public String post(String docs){
            OutputStreamWriter writer = null;
            try {
                writer = new OutputStreamWriter(urlConnection.getOutputStream(), charset);
                writer.write(query); // Write POST query string (if any needed).
            } 
            finally {
                if (writer != null){
                    try {
                        writer.close();
                    }
                    catch (IOException logOrIgnore) {}
                }
                
                
                final char[] buffer=new char[8000];
                final StringBuilder out = new StringBuilder();
                try {
                    InputStream result = urlConnection.getInputStream();
                    final Reader in = new InputStreamReader(result, "UTF-8");
                    try {
                        for (;;) {
                          int rsz = in.read(buffer, 0, buffer.length);
                          if (rsz < 0)
                            break;
                          out.append(buffer, 0, rsz);
                        }
                    }
                    finally {
                      in.close();
                    }
                }
                catch (UnsupportedEncodingException ex) {
                  System.out.println("This shouldn't happen");
                }
                catch (IOException ioe) {
                    System.out.println("Unable to open connection to solr" + ioe.getMessage());
                    ioe.printStackTrace();
                    System.exit(1);
                }
                return out.toString();
                
            }
    }
    
}
