/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Main;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Steve
 */
public class SolrDoc {
    private static Pattern dept_re=Pattern.compile("^(\\w+)");
    private static Pattern series_re=Pattern.compile("^(\\w+\\s?\\w?)");
    
    private String drereference;    
    private String title;
    private String description;
    private String catDocRef;
    private String department;
    private String urlParams;
    private String sourceLevelId;
    private String closureType;
    private String closureStatus;
    private String openingDate;
    private String series;
    private ArrayList<String> references;
    private ArrayList<String> people;
    private ArrayList<String> places;
    private ArrayList<String> heldbys;
    private ArrayList<String> corpBodys;
    private ArrayList<String> period;
    private ArrayList<String> subjects;
    
    SolrDoc(MongoDoc mdoc){
        this.drereference=mdoc.iaid;
        this.title=mdoc.title;
        this.description=mdoc.description;
        this.catDocRef=mdoc.catDocRef;
        this.corpBodys=mdoc.corpBodies;
        this.people=mdoc.peoples;
        this.places=mdoc.places;
        this.heldbys=mdoc.heldbys;
        this.references=mdoc.references;
        this.subjects=mdoc.subjects;
        this.department=this.getDepartment(mdoc.catDocRef);
        this.series=this.getSeries(mdoc.catDocRef);
        
    }
    
    
    private String getDepartment(String ref){
        String dept="";
        
        return dept;
        
    }
    
    private String getSeries(String ref){
        String series="";
        
        return series;
    }
    
}
