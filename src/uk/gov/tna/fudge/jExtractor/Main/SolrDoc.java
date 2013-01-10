/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Main;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
 
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
    private String closureCode;
    private String openingDate;
    private String series;
    private String startdate;
    private String enddate;
    private String schema;
    private ArrayList<String> references;
    private ArrayList<String> people;
    private ArrayList<String> places;
    private ArrayList<String> heldbys;
    private ArrayList<String> corpBodys;
    private ArrayList<String> periods;
    private ArrayList<String> subjects;
    private String xmlRepresentation;
    
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
        this.closureCode=mdoc.closureCode;
        this.closureStatus=mdoc.closureStatus;
        this.closureType=mdoc.closureType;
        this.schema=mdoc.schema;
        this.department=SolrDoc.getDepartment(mdoc.catDocRef);
        this.series=SolrDoc.getSeries(mdoc.catDocRef);
        this.periods=SolrDoc.getPeriod(this.startdate,this.enddate);
        
    }
    
    private static Element buildElement(Document doc,String name,String value){
        Element ele = doc.createElement("field");
        ele.setAttribute("name", name);
        ele.appendChild(doc.createTextNode(value));
        return ele;
    }
    
    private Element buildXML(Document doc){


        // document elements
	Element solrdoc = doc.createElement("doc");
        solrdoc.appendChild(SolrDoc.buildElement(doc, "DREREFERENCE", this.drereference));
        solrdoc.appendChild(SolrDoc.buildElement(doc, "CATDOCREF", this.catDocRef));
        solrdoc.appendChild(SolrDoc.buildElement(doc, "TITLE", this.title));
        solrdoc.appendChild(SolrDoc.buildElement(doc, "DESCRIPTION", this.description));
        solrdoc.appendChild(SolrDoc.buildElement(doc, "STARTDATE", this.startdate));
        solrdoc.appendChild(SolrDoc.buildElement(doc, "ENDDATE", this.enddate));
        solrdoc.appendChild(SolrDoc.buildElement(doc, "DEPARTMENT", this.department));
        solrdoc.appendChild(SolrDoc.buildElement(doc, "SERIES", this.series));
        solrdoc.appendChild(SolrDoc.buildElement(doc, "SOURCELEVEL", this.sourceLevelId));
        solrdoc.appendChild(SolrDoc.buildElement(doc, "CLOSURESTATUS", this.closureStatus));
        solrdoc.appendChild(SolrDoc.buildElement(doc, "CLOSURETYPE", this.closureType));
        solrdoc.appendChild(SolrDoc.buildElement(doc, "CLOSURECODE", this.closureCode));
        solrdoc.appendChild(SolrDoc.buildElement(doc, "URLPARAMS", this.urlParams));
        solrdoc.appendChild(SolrDoc.buildElement(doc, "SCHEMA", this.schema));
        for(String subj : this.subjects){
            solrdoc.appendChild(SolrDoc.buildElement(doc, "SUBJECT", subj));
        }
        for(String pers : this.people){
            solrdoc.appendChild(SolrDoc.buildElement(doc, "PERSON", pers));
        }
        for(String place : this.places){
            solrdoc.appendChild(SolrDoc.buildElement(doc, "PLACE", place));
        }
        for(String corp : this.corpBodys){
            solrdoc.appendChild(SolrDoc.buildElement(doc, "CORPBODY", corp));
        }
        for(String held : this.heldbys){
            solrdoc.appendChild(SolrDoc.buildElement(doc, "HELDBY", held));
        }
        for(String period : this.periods){
            solrdoc.appendChild(SolrDoc.buildElement(doc, "PERIOD", period));
        }
        for(String ref : this.references){
            solrdoc.appendChild(SolrDoc.buildElement(doc, "REFERENCE", ref));
        }
             
        return solrdoc;
    }
    
    public static void writeXML(Integer batchid, ArrayList<SolrDoc> docs){
        try{
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        
        // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("add");
            doc.appendChild(rootElement);
            for(SolrDoc sdoc : docs)
            {
                Element solrElement=sdoc.buildXML(doc);
                rootElement.appendChild(solrElement);
                
            }
            
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("/opt/sprice/iadata/file-"+batchid.toString()+".xml"));
        //StreamResult result2 = new StreamResult(System.out);
            transformer.transform(source, result);
        }
        catch (ParserConfigurationException pce) {
            System.out.println(pce.getStackTrace());
	}
        catch (TransformerException tfe) {
            System.out.println(tfe.getStackTrace());
        }
        
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
