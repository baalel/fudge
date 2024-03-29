/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Main;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bson.types.ObjectId;

/**
 *
 * @author sprice
 */
public class CachedMongoDoc implements IMongoDoc{
    private static Pattern title_re=Pattern.compile(">(.*)</");
    private static Pattern desc_re=Pattern.compile("<p>(.*)</p>");
    private static Pattern htmltag_re=Pattern.compile("<[^<]+?>");
    private static Pattern schema_re=Pattern.compile("=\\\"(.+)\\\"");
    private static String datetimepart="T00:00:00Z";


    
    String iaid;
    String parentIaid;
    Integer parentLevel;
    Integer sourceLevelId;
    String catDocRef;
    String closureStatus;
    String closureCode;
    String closureType;
    String startdate;
    String enddate;
    String openingdate;
    String title;
    String description;
    String schema;
    String reference;
    String urlParams;
    Tag tag;
    List<String> tags;
    ObjectId id;
    Reference ref;
    List<String> references;
    HeldBy held;
    List<String> heldbys;
    Person pers;
    List<String> peoples;
    Place place;
    List<String> places;
    CorporateBody corp;
    List<String> corpBodies;
    Subject subj;
    List<String> subjects;
    Fetcher fetcher;
    GeneralCache cache;
    
CachedMongoDoc(DBObject doc, GeneralCache gCache, Fetcher fetch)
    {
        fetcher=fetch;
        cache=gCache;
        parentIaid=(String)doc.get("ParentIAID");
        iaid=(String)doc.get("IAID");
        sourceLevelId=(Integer)doc.get("SourceLevelId");
        closureStatus=(String)doc.get("ClosureStatus");
        closureCode=(String)doc.get("ClosureCode");
        closureType=(String)doc.get("ClosureType");
        id=(ObjectId)doc.get("_id");
        title=createTitle(CachedMongoDoc.cleanTitle((String)doc.get("Title")));
        try{
            startdate=this.makeDate((String)doc.get("CoveringFromDate"), true);
            enddate=this.makeDate((String)doc.get("CoveringToDate"), false);
            openingdate=this.getOpeningDate((String)doc.get("RecordOpeningDate"));
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            System.out.println(this.iaid);
        }
        reference=(String)doc.get("Reference");

        held=new HeldBy((BasicDBList)doc.get("HeldBy"));
        pers=new Person((BasicDBList)doc.get("PersonalNames"));
        place=new Place((BasicDBList)doc.get("Places"));
        corp=new CorporateBody((BasicDBList)doc.get("CorporateNames"));
        subj=new Subject((BasicDBList)doc.get("Subjects"));
        tag=new Tag((BasicDBList)doc.get("Tag"));
        
        DBObject scopeContent=(DBObject)doc.get("ScopeContent");
        description=CachedMongoDoc.cleanDescription((String)scopeContent.get("Description"));
        BasicDBList occupation=(BasicDBList)scopeContent.get("Occupation");
        if(occupation!=null){
            subj.add(occupation);}
        BasicDBList organization=(BasicDBList)scopeContent.get("Organizations");
        if (organization!=null){
            corp.add(organization);}
        BasicDBList persName=(BasicDBList)scopeContent.get("PersonName");
        if(persName!=null){
            pers.add(persName);}
        BasicDBList placeName=(BasicDBList)scopeContent.get("PlaceName");
        if(placeName!=null){
            place.add(placeName);}
        schema=CachedMongoDoc.extractSchema((String)scopeContent.get("Schema"));
        
        catDocRef=makeCatDocRef();
        ref=new Reference(catDocRef);
        ref.append((String)doc.get("FormerReferencePro"));
        ref.append((String)doc.get("FormerReferenceDep"));
        references=ref.getValues();
        this.peoples=pers.getValues();
        this.places=place.getValues();
        this.corpBodies=corp.getValues();
        this.heldbys=held.getValues();
        this.subjects=subj.getValues();
        if(tag.data!=null){
            subjects.addAll(tag.getValues());
        }
        this.urlParams=makeUrlParams();
        
    }
    
    private DBObject toMongoSon()
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
        son.put("OpeningDate", getOpeningdate());
        son.put("Schema", schema);
        son.put("Catdocref",catDocRef);
        son.put("Reference", ref.values);
        
        return son;
        
    }
    
    private String makeDate(String date, boolean isStartDate){
        String workingDate=date;
        if (workingDate!=null && workingDate.length()==8){
            workingDate=CachedMongoDoc.convertDate(workingDate, isStartDate);
            if(isStartDate){
                cache.insertStart(this.iaid, workingDate);
            }
            else{
                cache.insertEnd(this.iaid, workingDate);
            }
            return workingDate;
        }
        else if(workingDate==null || workingDate.length()<8){
            boolean flag=false;
            while(!flag){
                if(isStartDate){
                    if(cache.existsStart(this.parentIaid)){
                        workingDate=cache.lookupStart(this.parentIaid);
                        cache.insertStart(this.iaid, workingDate);
                        break;
                    }
                    else{
                        flag=false;
                        String workingid=this.parentIaid;
                        while(!flag){
                            DBObject doc=fetcher.findParent(workingid);
                            String pDate=(String)doc.get("CoveringDateTo");
                            if(pDate!=null && pDate.length()==8)
                            {
                                flag=true;
                                workingDate=CachedMongoDoc.convertDate(pDate,isStartDate);
                                cache.insertStart(workingid, workingDate);
                                if(this.sourceLevelId<7){
                                    cache.insertStart(this.iaid, workingDate);
                                }
                                break;
                            }
                            else{
                                workingid=(String)doc.get("ParentIAID");
                                if(cache.existsStart(workingid)){
                                    workingDate=cache.lookupStart(workingid);
                                    flag=true;
                                    break;
                                }
                            }
                            
                            
                        }
                        //todo
                    }
                    
                }
                else{
                    if(cache.existsEnd(this.parentIaid)){
                        workingDate=cache.lookupEnd(this.parentIaid);
                        cache.insertEnd(this.iaid,workingDate);
                        //flag=true;
                        break;
                    }
                    else{
                        
                        String workingid=this.parentIaid;
                        while(!flag){
                            DBObject doc=fetcher.findParent(workingid);
                            String pDate=(String)doc.get("CoveringDateTo");
                            if(pDate!=null && pDate.length()==8)
                            {                   
                                workingDate=CachedMongoDoc.convertDate(pDate,isStartDate);
                                cache.insertEnd(workingid, workingDate);
                                if(this.sourceLevelId<7){
                                    cache.insertEnd(this.iaid, workingDate);
                                }
                                flag=true;
                                break;
                            }
                            else{
                                workingid=(String)doc.get("ParentIAID");
                                if(cache.existsStart(workingid)){
                                    workingDate=cache.lookupEnd(workingid);
                                    flag=true;
                                    break;
                                }
                            }
                            
                        }
                        //todo
                    }
                }
                
            }
        }
        
        
        return workingDate;
    }
    
    private String makeCatDocRef() {
        //does parent exist in cache?
        //what level am I?
        String parent=this.parentIaid;
        int level=this.sourceLevelId;
        String workingid=this.iaid;
        String currentRef=this.reference;
        StringBuilder catreference=new StringBuilder("");
        
        //String workingRef;
        if(cache.existsRef(parent)){
            if(level==6){
                catreference.append(cache.lookupRef(parent)).append("/").append(currentRef);
                cache.insertRef(workingid, catreference.toString());
                return catreference.toString();
            }
            else if(level==7){
                catreference.append(cache.lookupRef(parent)).append("/").append(currentRef);
                return catreference.toString();
            }
            else if(level==3)
            {   
                catreference.append(cache.lookupRef(parent)).append(" ").append(currentRef);
                cache.insertRef(workingid, catreference.toString());
                return catreference.toString();
            }
            else{
                catreference.append(cache.lookupRef(parent));
                cache.insertRef(workingid, catreference.toString());
                return catreference.toString();
            }                     
        }
        else{
            
            while(!"C0".equals(workingid)){
                if (level==6||level==7){
                    catreference.insert(0, currentRef);
                    catreference.insert(0,"/");
                }
                else if (level==3){
                    catreference.insert(0, currentRef);
                    catreference.insert(0, " ");
                }
                else if(level==1){
                    catreference.insert(0,currentRef);
                }
                //go get parent
                if("C0".equals(parent)){
                    this.cache.insertRef(iaid, catreference.toString());
                    return catreference.toString();
                }
                DBObject doc=fetcher.findParent(parent);
                workingid=(String)doc.get("IAID");
                parent=(String)doc.get("ParentIAID");
                currentRef=(String)doc.get("Reference");
                level=(Integer)doc.get("SourceLevelId");
                if(workingid.equals(this.parentIaid)){
                    this.parentLevel=level;
                }
                
                
            }
            
        }
        this.cache.insertRef(iaid, reference.toString());
        return reference.toString();
        //throw new UnsupportedOperationException("Not yet implemented");
    }
    
    private String makeUrlParams(){
        StringBuilder temp=new StringBuilder(this.iaid);
        if(cache.existsUrl(this.parentIaid)){
            Integer currentLevel=this.sourceLevelId-1;
            if(this.parentLevel==null){
                DBObject doc=fetcher.findParent(this.parentIaid);
                this.parentLevel=(Integer)doc.get("SourceLevelId");
                while(currentLevel>this.parentLevel){
                    temp.insert(0,"0/");
                    currentLevel--;
                }
                
            }
            temp.insert(0, cache.lookupUrl(this.parentIaid)+'/');
        }
        else if("C0".equals(this.parentIaid)){
            temp.insert(0, "066/1/");
        }
        else{
            Integer currentLevel=this.sourceLevelId;
            String currentParent=this.parentIaid;
            while (currentLevel!=1){
                currentLevel--;
                DBObject doc=fetcher.findParent(currentParent);
                while((currentLevel-(Integer)doc.get("SourceLevelId"))>0){
                    temp.insert(0, "0/");
                    currentLevel--;
                    
                }
                temp.insert(0, (String)doc.get("IAID")+"/");
                
            }
            temp.insert(0, "066/1/");   
        }
        
        cache.insertUrl(this.iaid, temp.toString());
        
        
        return temp.toString();
    }
    
    private static String cleanTitle(String dirty)
    {
        String clean;
        Matcher working = CachedMongoDoc.title_re.matcher(dirty);
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
        Matcher working=CachedMongoDoc.htmltag_re.matcher(dirty);
        if(working.find())
        {
            clean=working.replaceAll(" ");
        }
        else{
            clean=dirty;
        }
        return clean;
    }
    /**
     * Removes the unwanted markup from scopec ontent descriptions 
     * @param dirty the raw text from the information asset description
     * @return the description cleaned of markup
     */
    private static String cleanDescription(String dirty)
    {
        String clean="";
        Matcher working=CachedMongoDoc.desc_re.matcher(dirty);
        while(working.find()){
            //cf C1095691 description
            clean+=" "+CachedMongoDoc.removeTags(working.group(1)).trim();
            //clean=MongoDoc.removeTags(clean);
        }
        return clean;
    }
    
    private static String extractSchema(String rawschema){
        String schema="";
        if(rawschema==null || "".equals(rawschema)){
            return null;
        }
        Matcher working=CachedMongoDoc.schema_re.matcher(rawschema);
        if(working.find()){
            schema=working.group(1);
        }
        
        return schema;
    }
    
    private String createTitle(String dbTitle){
        if(dbTitle!=null && !"".equals(dbTitle)){
            cache.insertTitle(this.iaid, dbTitle);
            return dbTitle;
        }
        else{
            String workingId=this.getParentIaid();
            String workingTitle="";
            if(cache.existsTitle(workingId)){
                    workingTitle=cache.lookupTitle(workingId);
                    
            }
            else{
                boolean flag=false;
                while(!flag){

                    DBObject doc=fetcher.findParent(workingId);

                    workingTitle=CachedMongoDoc.cleanTitle((String)doc.get("Title"));
                    if(!"".equals(workingTitle)){
                        flag=true;
                    }
                    else{
                        workingId=(String)doc.get("ParentIAID");
                        if(cache.existsTitle(workingId)){
                            workingTitle=cache.lookupTitle(workingId);
                            flag=true;
                        }
                    }


                }
            }
            cache.insertTitle(this.iaid, workingTitle);
            return workingTitle;
        }
        
        
        
        
    }
    
    /**
     * Converts a yyyymmdd date string into Solr's Tri date format
     * yyyy-mm-ddThh:mm:ss.sZ
     * 
     * @param coveringdate the informationasset date string
     *  isEndDate a flag to specify if this is a startdate or enddate to convert
     * @return the tri-date formated date string
     */
    private static String convertDate(String coveringDate,boolean isStartDate)
    {
        StringBuilder newDate=new StringBuilder();
        String day,month,year;
        if(coveringDate!=null && coveringDate.length()==8){
            year=coveringDate.substring(0,4);
            month=coveringDate.substring(4,6);
            day=coveringDate.substring(6, 8);
        }
        else if(!isStartDate){
            year="2100";
            month="12";
            day="31";
        }
        else{
            year="1000";
            month="01";
            day="01";           
        }
        
        newDate.append(year).append("-").append(month).append("-").append(day).append(CachedMongoDoc.datetimepart);
        return newDate.toString();    
    }
     /**
     * @return the iaid
     */
    @Override
    public String getDreReference() {
        return iaid;
    }
     /**
     * @return the iaid
     */
    @Override
    public String getIaid() {
        return iaid;
    }

    /**
     * @return the parentIaid
     */
    @Override
    public String getParentIaid() {
        return parentIaid;
    }

    /**
     * @return the parentLevel
     */
    @Override
    public Integer getParentLevel() {
        return parentLevel;
    }

    /**
     * @return the sourceLevelId
     */
    @Override
    public Integer getSourceLevelId() {
        return sourceLevelId;
    }

    /**
     * @return the catDocRef
     */
    @Override
    public String getCatDocRef() {
        return catDocRef;
    }

    /**
     * @return the closureStatus
     */
    @Override
    public String getClosureStatus() {
        return closureStatus;
    }

    /**
     * @return the closureCode
     */
    @Override
    public String getClosureCode() {
        return closureCode;
    }

    /**
     * @return the closureType
     */
    @Override
    public String getClosureType() {
        return closureType;
    }

    /**
     * @return the startdate
     */
    @Override
    public String getStartdate() {
        return startdate;
    }

    /**
     * @return the enddate
     */
    @Override
    public String getEnddate() {
        return enddate;
    }
    
    /**
     * @return the openingdate
     */
    @Override
    public String getOpeningdate() {
        return this.openingdate;
    }

    /**
     * @return the title
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * @return the description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * @return the schema
     */
    @Override
    public String getSchema() {
        return schema;
    }

    /**
     * @return the reference
     */
    @Override
    public String getReference() {
        return reference;
    }

    /**
     * @return the urlParams
     */
    @Override
    public String getUrlParams() {
        return urlParams;
    }

    /**
     * @return the tags
     */
    @Override
    public List<String> getTags() {
        return tags;
    }

    /**
     * @return the id
     */
    @Override
    public ObjectId getObjectId() {
        return id;
    }

    /**
     * @return the references
     */
    @Override
    public List<String> getReferences() {
        return references;
    }

    /**
     * @return the heldbys
     */
    @Override
    public List<String> getHeldbys() {
        return heldbys;
    }

    /**
     * @return the peoples
     */
    @Override
    public List<String> getPeoples() {
        return peoples;
    }

    /**
     * @return the places
     */
    @Override
    public List<String> getPlaces() {
        return places;
    }

    /**
     * @return the corpBodies
     */
    @Override
    public List<String> getCorpBodies() {
        return corpBodies;
    }

    /**
     * @return the subjects
     */
    @Override
    public List<String> getSubjects() {
        return subjects;
    }
    
    private String getOpeningDate(String roDate) {
        if(roDate==null || roDate.length()!=19){
            return null;
        }
        else{
            StringBuilder newDate=new StringBuilder();
            String day,month,year;
            day=roDate.substring(0, 2);
            month=roDate.substring(3, 5);
            year=roDate.substring(6,10);
            newDate.append(year).append("-").append(month).append("-").append(day).append(MongoDoc.getDatetimepart());
            return newDate.toString();    
            
        }
    }
    
    private class Entity{
        DBObject data;
        protected List<String> values;
        
        Entity(BasicDBList rawData){
            data=rawData;
            values=new ArrayList<>();
        }
        
        public List<String> getValues()
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
        
        public boolean add(BasicDBList newItems){
            if(newItems==null){return false;}
            for(int i=0;i<newItems.size();i++){
                try{
                    String item=(String)newItems.get(i);
                    values.add(item);
                }
                catch(Exception e){
                    System.out.println(e.getStackTrace());
                    return false;
                }
                
            }
            return true;
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
                this.values.add((String)entry.get( "Corporate_Body_Name_Text"));
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
        protected List<String> values;
        
        Reference(String rawData){
            values=new ArrayList<>();
            values.add(rawData);
        }
        
        void append(String ref)
        {
            if(ref!=null&&ref.length()>0){
                values.add(ref);
            }
        }
        
        public List<String> getValues(){
            return values;
        }
        
    }
    
    private class Tag extends Entity{
        protected List<String> tags;
        
        Tag(BasicDBList rawData){
            super(rawData);
            if (rawData==null){
                return;
            }
            String entry;
            for(Object o : rawData)
            {
                entry=(String)o;
                this.values.add(entry);
            }
            
        }
    }
}
