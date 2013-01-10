/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Main;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
 
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
    //private String xmlRepresentation;
    
    SolrDoc(MongoDoc mdoc){
        this.drereference=mdoc.iaid;
        this.title=XMLHelper.safeText(mdoc.title);
        this.description=XMLHelper.safeText(mdoc.description);
        this.catDocRef=XMLHelper.safeText(mdoc.catDocRef);
        this.corpBodys=XMLHelper.safeText(mdoc.corpBodies);
        this.people=XMLHelper.safeText(mdoc.peoples);
        this.places=XMLHelper.safeText(mdoc.places);
        this.heldbys=XMLHelper.safeText(mdoc.heldbys);
        this.references=XMLHelper.safeText(mdoc.references);
        this.subjects=XMLHelper.safeText(mdoc.subjects);
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
    
    private Document buildXML(Document doc,Element root){


        // document elements
	Element solrdoc = doc.createElement("doc");
        root.appendChild(solrdoc);
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
        if(this.subjects!=null){
            for(String subj : this.subjects){
                solrdoc.appendChild(SolrDoc.buildElement(doc, "SUBJECT", subj));
            }
        }
        if(this.people!=null){
            for(String pers : this.people){
                solrdoc.appendChild(SolrDoc.buildElement(doc, "PERSON", pers));
            }
        }
        if(this.places!=null){
            for(String place : this.places){
                solrdoc.appendChild(SolrDoc.buildElement(doc, "PLACE", place));
            }
        }
        if(this.corpBodys!=null){
            for(String corp : this.corpBodys){
                solrdoc.appendChild(SolrDoc.buildElement(doc, "CORPBODY", corp));
            }
        }
        if(this.heldbys!=null){
            for(String held : this.heldbys){
                solrdoc.appendChild(SolrDoc.buildElement(doc, "HELDBY", held));
            }
        }
        if(this.periods!=null){
            for(String period : this.periods){
                solrdoc.appendChild(SolrDoc.buildElement(doc, "PERIOD", period));
            }
        }
        if(this.references!=null){
            for(String ref : this.references){
                solrdoc.appendChild(SolrDoc.buildElement(doc, "REFERENCE", ref));
            }
        }
             
        return doc;
    }
    
    public static void writeXML(Integer batchid, String savePath, ArrayList<SolrDoc> docs){
        try{
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        
        // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("add");
            doc.appendChild(rootElement);
            for(SolrDoc sdoc : docs)
            {
                doc=sdoc.buildXML(doc,rootElement);
                //rootElement.appendChild(solrElement);
                
            }
            
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(savePath+ "file-"+batchid.toString()+".xml"));
        //StreamResult result2 = new StreamResult(System.out);
            transformer.transform(source, result);
        }
        catch (ParserConfigurationException pce) {
            System.out.println(pce.getMessage());
            pce.printStackTrace();
            System.exit(1);
	}
        catch (TransformerException tfe) {
            System.out.println(tfe.getMessageAndLocation());
            tfe.printStackTrace();
            System.exit(1);
        }
        
    }
    
    public static String getDepartment(String ref){
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
    
    private static String buildXMLStringDoc(ArrayList<SolrDoc> docs){
        StringBuilder saveDoc=new StringBuilder();
        saveDoc.append("<add>\n");
        for(SolrDoc sdoc : docs){
            saveDoc.append("<doc>");
            if(sdoc.drereference!=null){
                saveDoc.append("<field name=").append("\"DREREFERENCE\">").append(sdoc.drereference).append("</field>");
            }
            if(sdoc.catDocRef!=null){
                saveDoc.append("<field name=").append("\"CATDOCREF\">").append(sdoc.catDocRef).append("</field>");
            }
            if(sdoc.title!=null){
                saveDoc.append("<field name=").append("\"TITLE\">").append(sdoc.title).append("</field>");
            }
            if(sdoc.description!=null){
                saveDoc.append("<field name=").append("\"DESCRIPTION\">").append(sdoc.description).append("</field>");
            }
            if(sdoc.startdate!=null){
                saveDoc.append("<field name=").append("\"STARTDATE\">").append(sdoc.startdate).append("</field>");
            }
            if(sdoc.enddate!=null){
                saveDoc.append("<field name=").append("\"ENDDATE\">").append(sdoc.enddate).append("</field>");
            }
            if(sdoc.department!=null){
                saveDoc.append("<field name=").append("\"DEPARTMENT\">").append(sdoc.department).append("</field>");
            }
            if(sdoc.series!=null){
                saveDoc.append("<field name=").append("\"SERIES\">").append(sdoc.series).append("</field>");
            }
            if(sdoc.sourceLevelId!=null){
                saveDoc.append("<field name=").append("\"SOURCELEVEL\">").append(sdoc.sourceLevelId.toString()).append("</field>");
            }
            if(sdoc.schema!=null){
                saveDoc.append("<field name=").append("\"SCHEMA\">").append(sdoc.schema).append("</field>");
            }
            if(sdoc.urlParams!=null){
                saveDoc.append("<field name=").append("\"URLPARAMS\">").append(sdoc.urlParams).append("</field>");
            }
            if(sdoc.closureType!=null){
                saveDoc.append("<field name=").append("\"CLOSURETYPE\">").append(sdoc.closureType).append("</field>");
            }
            if(sdoc.closureStatus!=null){
                saveDoc.append("<field name=").append("\"CLOSURESTATUS\">").append(sdoc.closureStatus).append("</field>");
            }
            if(sdoc.closureCode!=null){
                saveDoc.append("<field name=").append("\"CLOSURECODE\">").append(sdoc.closureCode).append("</field>");
            }

            if(sdoc.subjects!=null){
                for(String subj : sdoc.subjects){
                    saveDoc.append("<field name=").append("\"SUBJECT\">").append(subj).append("</field>");
                }
            }
            if(sdoc.people!=null){
                for(String pers : sdoc.people){
                    saveDoc.append("<field name=").append("\"PERSON\">").append(pers).append("</field>");
                }
            }
            if(sdoc.places!=null){
                for(String place : sdoc.places){
                    saveDoc.append("<field name=").append("\"PLACE\">").append(place).append("</field>");
                }
            }
            if(sdoc.corpBodys!=null){
                for(String corp : sdoc.corpBodys){
                    saveDoc.append("<field name=").append("\"CORPBODY\">").append(corp).append("</field>");
                }
            }
            if(sdoc.heldbys!=null){
                for(String held : sdoc.heldbys){
                    saveDoc.append("<field name=").append("\"HELDBY\">").append(held).append("</field>");
                }
            }
            if(sdoc.periods!=null){
                for(String period : sdoc.periods){
                    saveDoc.append("<field name=").append("\"PERIOD\">").append(period).append("</field>");
                }
            }
            if(sdoc.references!=null){
                for(String ref : sdoc.references){
                    saveDoc.append("<field name=").append("\"REFERENCE\">").append(ref).append("</field>\n");
                }
            }            
         
            saveDoc.append("</doc>\n");
        }
        saveDoc.append("</add>");
        return saveDoc.toString();
    }
    
    public static void writeXMLasString(Integer batchid, String savePath, ArrayList<SolrDoc> docs)
    {
        String docToWrite=SolrDoc.buildXMLStringDoc(docs);
        String pathToWrite=savePath+"File-"+batchid.toString()+".xml";
        try {
            PrintWriter out=new PrintWriter(pathToWrite);
            out.print(docToWrite);
            out.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to save batch "+batchid.toString());
            ex.printStackTrace();
            
        }
        
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
