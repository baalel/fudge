/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Main;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bson.types.ObjectId;

/**
 *
 * @author steve
 */
public class MongoDoc {
    private static Pattern title_re=Pattern.compile(">(.*)</");
    private static Pattern desc_re=Pattern.compile("<p>(.*)</p>");
    private static Pattern htmltag_re=Pattern.compile("<[^<]+?>");
    private static String datetimepart="T00:00:00Z";


    
    String iaid;
    String parentIaid;
    Integer sourceLevelId;
    String catDocRef;
    String closureStatus;
    String closureCode;
    String closureType;
    String startdate;
    String enddate;
    String title;
    String description;
    String schema;
    String reference;
    ObjectId id;
    Reference ref;
    ArrayList<String> references;
    HeldBy held;
    ArrayList<String> heldbys;
    Person pers;
    ArrayList<String> peoples;
    Place place;
    ArrayList<String> places;
    CorporateBody corp;
    ArrayList<String> corpBodies;
    Subject subj;
    ArrayList<String> subjects;
    Fetcher fetcher;
    RefCache cache;
    
    
    MongoDoc(DBObject doc, RefCache parentCache,Fetcher fetch)
    {
        fetcher=fetch;
        cache=parentCache;
        parentIaid=(String)doc.get("ParentIAID");
        iaid=(String)doc.get("IAID");
        sourceLevelId=(Integer)doc.get("SourceLevelId");
        closureStatus=(String)doc.get("ClosureStatus");
        closureCode=(String)doc.get("ClosureCode");
        closureType=(String)doc.get("ClosureType");
        id=(ObjectId)doc.get("_id");
        title=MongoDoc.cleanTitle((String)doc.get("Title"));
        startdate=MongoDoc.convertDate((String)doc.get("CoveringFromDate"),false);
        enddate=MongoDoc.convertDate((String)doc.get("CoveringToDate"),true);
        reference=(String)doc.get("Reference");

        held=new HeldBy((BasicDBList)doc.get("HeldBy"));
        pers=new Person((BasicDBList)doc.get("Peoples"));
        place=new Place((BasicDBList)doc.get("Places"));
        corp=new CorporateBody((BasicDBList)doc.get("CorporateBodys"));
        subj=new Subject((BasicDBList)doc.get("Subjects"));
        
        DBObject scopeContent=(DBObject)doc.get("ScopeContent");
        description=MongoDoc.cleanDescription((String)scopeContent.get("Description"));
        String occupation=(String)scopeContent.get("Occupation");
        if(occupation!=null){
            subj.add(occupation);}
        String organization=(String)scopeContent.get("Organizations");
        if (organization!=null){
            corp.add(organization);}
        String persName=(String)scopeContent.get("PersonName");
        if(persName!=null){
            pers.add(persName);}
        String placeName=(String)scopeContent.get("PlaceName");
        if(placeName!=null){
            place.add(placeName);}
        schema=(String)scopeContent.get("Schema");
        
        catDocRef=makeCatDocRef();
        ref=new Reference(catDocRef);
        ref.append((String)doc.get("FormerReferencePro"));
        ref.append((String)doc.get("FormerReferenceDept"));
        
    }
    
    DBObject toMongoSon()
    {
        DBObject son=new BasicDBObject("_id",id);
        son.put("ParentIAID",parentIaid);
        son.put("IAID",iaid);
        son.put("SourceLevelId", sourceLevelId);
        son.put("closureStatus", closureStatus);
        son.put("closureCode", closureCode);
        son.put("closureType", closureType);
        son.put("Title", title);
        son.put("Description", description);
        son.put("Person", pers.values);
        son.put("Place", place.values);
        son.put("CorpBody", corp.values);
        son.put("Subject", subj.values);
        son.put("Heldby", held.values);
        son.put("StartDate", startdate);
        son.put("EndDate", enddate);
        son.put("Schema", schema);
        
        return son;
        
    }
    
    private String makeCatDocRef() {
        //does parent exist in cache?
        //what level am I?
        String parent=this.parentIaid;
        int level=this.sourceLevelId;
        String workingid=this.iaid;
        String currentRef=this.reference;
        
        String workingRef;
        if(cache.exists(parent)){
            if(level==6 ||level==7){
                workingRef=cache.loopup(parent)+"/" + currentRef;
                cache.insert(workingid, workingRef);
            }
            else if(level==3)
            {   
                workingRef=cache.loopup(parent) + " " +currentRef;
                cache.insert(workingid, workingRef);
            }
            else{
                workingRef=cache.loopup(parent);
                cache.insert(workingid, workingRef);
            }                     
        }
        else{
            StringBuilder reference=new StringBuilder("");
            while(!"C0".equals(parent)){
                if (level==6||level==7){
                    reference.insert(0, currentRef);
                    reference.insert(0,"/");
                }
                else if (level==3){
                    reference.insert(0, currentRef);
                    reference.insert(0, " ");
                }
                else if(level==1){
                    reference.insert(0,currentRef);
                }
                //go get parent
                DBObject doc=fetcher.findOne("IAID", parent);
                workingid=(String)doc.get("IAID");
                parent=(String)doc.get("ParentIAID");
                currentRef=(String)doc.get("Reference");
                
                
            }
            
        }
        
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    private static String cleanTitle(String dirty)
    {
        String clean;
        Matcher working = MongoDoc.title_re.matcher(dirty);
        if(working.find())
        {
            clean=working.group(1);
        }
        else{
            clean="";
        }
        return clean;
    }
    
    private static String removeTags(String dirty)
    {
        String clean;
        Matcher working=MongoDoc.htmltag_re.matcher(dirty);
        if(working.find())
        {
            clean=working.replaceAll(" ");
        }
        else{
            clean="";
        }
        return clean;
    }
    
    private static String cleanDescription(String dirty)
    {
        String clean;
        Matcher working=MongoDoc.desc_re.matcher(dirty);
        if(working.find())
        {
            clean=working.group(1);
            clean=MongoDoc.removeTags(clean);
        }
        else{
            clean="";
        }
        return clean;
    }
    
    private static String convertDate(String coveringDate,boolean isEndDate)
    {
        StringBuilder newDate=new StringBuilder();
        String day,month,year;
        if(coveringDate!=null && coveringDate.length()==8){
            year=coveringDate.substring(0,4);
            month=coveringDate.substring(4,6);
            day=coveringDate.substring(6, 8);
        }
        else if(isEndDate){
            year="2100";
            month="12";
            day="31";
        }
        else{
            year="1000";
            month="01";
            day="01";           
        }
        
        newDate.append(year).append("-").append(month).append("-").append(day).append(MongoDoc.datetimepart);
        return newDate.toString();    
    }
    
    private class Entity{
        DBObject data;
        protected ArrayList<String> values;
        
        Entity(BasicDBList rawData){
            data=rawData;
            values=new ArrayList<String>();
        }
        
        public ArrayList<String> getValues()
        {
            return values;
        }
        
        public boolean add(String newItem)
        {
            if(newItem!=null && newItem.length()>0){
                boolean rc=values.add(newItem);
                return rc;
            }
            return false;
        }
        
    }
    
    private class CorporateBody extends Entity{
        
        CorporateBody(BasicDBList rawData){
            super(rawData);
            if(rawData==null)
            {
                return;
            }
            for(Object o : rawData)
            {
                BasicDBObject entry=(BasicDBObject)o;
                this.values.add((String)entry.get("CorporateNames"));
            }
        }
        
        
        
    }
    
    private class Person extends Entity{
        
        Person(BasicDBList rawData){
            super(rawData);
            if(rawData==null)
            {
                return;
            }
            for(Object o : rawData)
            {
                BasicDBObject entry=(BasicDBObject)o;
                StringBuilder personName=new StringBuilder();
                String sn_text=entry.getString("Surname_Text")!=null?entry.getString("Surname_Text"):"";
                personName = personName.append(sn_text).append(" ");
                String pt_text=entry.getString("Pretitle_Text")!=null?entry.getString("Pretitle_Text"):"";
                personName = personName.append(pt_text).append(" ");
                String fn_text=entry.getString("Forename_Text")!=null?entry.getString("Forename_Text"):"";
                personName = personName.append(fn_text).append(" ");
                String per_text=entry.getString("Person_Title")!=null?entry.getString("Person_Title"):"";
                personName = personName.append(per_text).append(" ");
                
                this.values.add(personName.toString());
            }
        }
           
    }
    
    private class Subject extends Entity{
        Subject(BasicDBList rawData){
            super(rawData);
            if(rawData==null)
            {
                return;
            }
            for(Object o : rawData)
            {
                BasicDBObject entry=(BasicDBObject)o;
                this.values.add((String)entry.get("Subject_Term_Text"));
            }
        }
        
    }
    
    private class Place extends Entity{
        Place(BasicDBList rawData){
            super(rawData);
            if(rawData==null)
            {
                return;
            }
            for(Object o : rawData)
            {
                BasicDBObject entry=(BasicDBObject)o;
                StringBuilder placeName=new StringBuilder();
                String pn_desc=entry.getString("Description")!=null?entry.getString("Description"):"";
                placeName = placeName.append(pn_desc).append(" ");
                String pn_text=entry.getString("Place_Name_Text")!=null?entry.getString("Place_Name_Text"):"";
                placeName = placeName.append(pn_text).append(" ");
                String par_text=entry.getString("Parish_Text")!=null?entry.getString("Parish_Text"):"";
                placeName = placeName.append(par_text).append(" ");
                String twn_text=entry.getString("Town_Text")!=null?entry.getString("Town_Text"):"";
                placeName = placeName.append(twn_text).append(" ");
                String cou_text=entry.getString("County_Text")!=null?entry.getString("County_Text"):"";
                placeName = placeName.append(cou_text).append(" ");
                String con_text=entry.getString("Country_Text")!=null?entry.getString("Country_Text"):"";
                placeName = placeName.append(con_text).append(" ");
                
                this.values.add(placeName.toString());
            }
        }
        
    }
    
    private class HeldBy extends Entity{
        
        HeldBy(BasicDBList rawData){
            super(rawData);
            if(rawData==null)
            {
                return;
            }
            for(Object o : rawData)
            {
                BasicDBObject entry=(BasicDBObject)o;
                this.values.add((String)entry.get("Corporate_Body_Name_Text"));
            }
        }
        
    }
    
    private class Reference {
        ArrayList<String> refList;
        Reference(String rawData){
            refList=new ArrayList<String>();
            refList.add(rawData);
        }
        
        void append(String ref)
        {
            refList.add(ref);
        }
        
    }
}
