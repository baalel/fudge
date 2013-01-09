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
    private String startdate;
    private String enddate;
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
        this.startdate=mdoc.startdate;
        this.enddate=mdoc.enddate;
        this.department=SolrDoc.getDepartment(mdoc.catDocRef);
        this.series=SolrDoc.getSeries(mdoc.catDocRef);
        this.period=SolrDoc.getPeriod(this.startdate,this.enddate);
        
    }
    
    
    private static String getDepartment(String ref){
        String dept;
        Matcher working=SolrDoc.dept_re.matcher(ref);
        if(working.find())
        {
            dept=working.group(1);
        }
        else{
            dept=ref;
        }
        return dept;
        
        
    }
    
    private static String getSeries(String ref){
        String series;
        Matcher working=SolrDoc.series_re.matcher(ref);
        if(working.find())
        {
            series=working.group(1);
        }
        else{
            series=ref;
        }
        return series;
    }
    
    private static ArrayList<String> getPeriod(String start, String end){
        ArrayList<String> periods = new ArrayList<String>();
        int startyear;
        int endyear;
        startyear=new Integer(start.substring(0, 4));
        endyear=new Integer(end.substring(0, 4));
        if((endyear<=1000)||(startyear<=1000)){
            periods.add("pre-1000");
        }
        if((endyear>=1000 & endyear<=1099)||(startyear>=1000 & startyear<=1099)){
            periods.add("1000-1099");
        }
        if((endyear>=1100 & endyear<=1199)||(startyear>=1100 & startyear<=1199)){
            periods.add("1100-1199");
        }
        if((endyear>=1200 & endyear<=1299)||(startyear>=1200 & startyear<=1299)){
            periods.add("1200-1299");
        }
        if((endyear>=1300 & endyear<=1399)||(startyear>=1300 & startyear<=1399)){
            periods.add("1300-1399");
        }
        if((endyear>=1400 & endyear<=1499)||(startyear>=1400 & startyear<=1499)){
            periods.add("1400-1499");
        }
        if((endyear>=1500 & endyear<=1599)||(startyear>=1500 & startyear<=1599)){
            periods.add("1500-1599");
        }
        if((endyear>=1600 & endyear<=1699)||(startyear>=1600 & startyear<=1699)){
            periods.add("1600-1699");
        }
        if((endyear>=1700 & endyear<=1799)||(startyear>=1700 & startyear<=1799)){
            periods.add("1700-1799");
        }
        if((endyear>=1800 & endyear<=1899)||(startyear>=1800 & startyear<=1899)){
            periods.add("1800-1899");
        }
        if((endyear>=1900 & endyear<=1924)||(startyear>=1900 & startyear<=1924)){
            periods.add("1900-1924");
        }
        if((endyear>=1925 & endyear<=1949)||(startyear>=1925 & startyear<=1949)){
            periods.add("1925-1949");
        }
        if((endyear>=1950)||(startyear>=1950)){
            periods.add("1950+");
        }
        
        return periods;
    }
    
}
