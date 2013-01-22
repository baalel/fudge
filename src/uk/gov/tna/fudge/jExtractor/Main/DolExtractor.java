/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Main;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author steve
 */
public class DolExtractor {
    
    private static Pattern desc_persname_re=Pattern.compile("<persname>(.+?)</persname>");
    private static Pattern desc_forename_re=Pattern.compile("forenames\\\">(.+?)</emph>");
    private static Pattern desc_surname_re=Pattern.compile("surname\\\">(.+?)</emph>");
    private static Pattern desc_surname2_re=Pattern.compile("<surname>(.+?)</surname>");
    private static Pattern desc_formerRef_re=Pattern.compile("formerreference\\\">(.+?)</emph>");
    private static Pattern desc_corpname_re=Pattern.compile("<corpname>(.+?)</corpname>");
    private static Pattern desc_regnumber_re=Pattern.compile("regno\\\">(.+?)<");
    private static Pattern desc_rank_re=Pattern.compile("rank\\\">(.+?)</emph>");
    private static Pattern desc_rating_re=Pattern.compile("rating\\\">(.+?)</emph>");
    private static Pattern desc_title_re=Pattern.compile("perstitle\\\">(.+?)</emph");
    private static Pattern desc_occupation_re=Pattern.compile("<occupation>(.+)</occupation>");
    private static Pattern desc_geo_re=Pattern.compile("<geogname>(.+?)</geogname>");
    private static Pattern desc_corp_re=Pattern.compile("corpname\"(.+?)1</emph>");
    private static Pattern desc_nation_re=Pattern.compile("nation\">(.+?)</emph>");
    private static Pattern desc_scope_re=Pattern.compile("scope(?:2?)\">(.+?)</emph>");
    private static Pattern desc_discharge_re=Pattern.compile("dischargeno\">(.+?)</emph>");
    private static Pattern desc_name1_re=Pattern.compile("name1\">Gudvang</emph>");
    private static Pattern desc_name2_re=Pattern.compile("name2\">(.+?)</emph>");
    private static Pattern desc_tonnage_re=Pattern.compile("size\">(.+?)</emph>");
    private static Pattern desc_num_re=Pattern.compile("(?:certno|num)\">(.+?)</emph>");
    private static Pattern desc_award_re=Pattern.compile("award\">(.+?)</emph>");
    private static Pattern desc_division_re=Pattern.compile("division\">(.+?)</emph>");
    private static Pattern desc_campaign_re=Pattern.compile("campaign\">(.+?)</emph>");
    private static Pattern desc_court_re=Pattern.compile("court\">(.+?)</emph>");
    private static Pattern desc_offence_re=Pattern.compile("offence\\\">(.+?)</emph>");
    private static Pattern desc_sentence_re=Pattern.compile("sentence\\\">(.+?)</emph>");
    private static Pattern desc_petition_re=Pattern.compile("petitioners\\\">(.+?)</emph>");
    private static Pattern desc_addressee_re=Pattern.compile("addressees\\\">(.+?)</emph>");
    private static Pattern coll_re=Pattern.compile("<colltype id=\\\"(.+?)\\\">,</colltype>");
    private static Pattern desc_aps=Pattern.compile("/emph>(.+?)$");
    private static Pattern desc_request=Pattern.compile("request\\\">(.+?)</emph>");
    private static Pattern desc_endorsement=Pattern.compile("endorsement\\\">(.+?)</emph>");
    private static Pattern desc_agenda=Pattern.compile("agenda\\\">(.+?)</emph>");
    private static Pattern desc_folio=Pattern.compile("folio\\\">(.+?)</emph>");
    private static Pattern desc_end_re=Pattern.compile(">(.+?)$");
    private static Pattern desc_tag_re=Pattern.compile("</?.+?>");
    private static Pattern desc_doctype=Pattern.compile("<emph altrender=\\\"doctype\\\">.+?</emph>");
    
    
    List<String> pers;
    List<String> ref;
    List<String> place;
    List<String> corp;
    List<String> subj;
    String description;
    String collType;
    
    public DolExtractor(String desc, List pers, List place, List ref, List subj, List corp, String colltype){
        this.description=desc;
        this.ref=ref;
        this.pers=pers;
        this.subj=subj;
        this.place=place;
        this.corp=corp;
        
        this.collType=DolExtractor.getCollType(colltype);
        
    }
    
    public static String getCollType(String desc){
        String coll=null;
        Matcher collMatcher=DolExtractor.coll_re.matcher(desc);
        if(collMatcher.find()){
            coll=collMatcher.group(1);
        }
        return coll;
        
    }
    
    public String checkMetaData(){
        String descriptiveText="";
        if(this.collType==null){
            return "";
        }
        switch(this.collType){
            case "APS":
                descriptiveText=extractGenericDesc();
                break;
            case "Airwomen":
                descriptiveText=extractNameOnly();
                break;
            case "AliensRegCards":
                descriptiveText=extractNameOnly();
                break;
            case "AncestorsMagazine":
                descriptiveText=extractGenericDesc();
                break;
            case "AncientPetitions":
                descriptiveText=extractAncient();
                break;
            case "BritishWarMedal":
                descriptiveText=extractNameWithLocation();
                break;
            case "CabinetPapers":
                descriptiveText=extractCAB();
                break;
            case "CombatRepWW2":
                descriptiveText=extractCombatRep();
                break;
            case "Datasets":
                descriptiveText=extractGenericDesc();
                break;
            case "DeathDuty":
                descriptiveText=extractNameOccupationLocation();
                break;
            case "DixonScott":
                descriptiveText=extractGenericDesc();
                break;
            case "DomesdayBook":
                descriptiveText=extractDomesday();
                break;
            case "EdenPaper":
                descriptiveText=extractGenericDesc();
                break;
            case "FOI":
                descriptiveText=extractGenericDesc();
                break;
            case "FameWill":
                descriptiveText=extractFameWill();
                break;
            case "IrishMaps":
                descriptiveText=extractGenericDesc();
                break;
            case "LootedArt":
                descriptiveText=extractScopeOnly();
                break;
            case "MRR":
                descriptiveText=extractGenericDesc();
                break;
            case "MapPicture":
                descriptiveText=extractMapPicture();
                break;
            case "MedSeal":
                descriptiveText=extractMedSeal();
                break;
            case "Medal":
                descriptiveText=extractMedal();
                break;
            case "Miscellaneous":
                descriptiveText=extractMisc();
                break;
            case "MusterRolls":
                descriptiveText=extractMusterRolls();
                break;
            case "NavalOfficers":
                descriptiveText=extractNameLocationNumber();
                break;
            case "NavalReserve":
                descriptiveText=extractNameLocationNumber();
                break;
            case "NavyLandService":
                descriptiveText=extractNavyLandService();
                break;
            case "NavyList":
                descriptiveText=extractEndText();
                break;
            case "NursingService":
                descriptiveText=extractNameOnly();
                break;
            case "Olympic":
                descriptiveText=extractScopeOnly();
                break;
            case "PoorLaw":
                descriptiveText=extractScopeOnly();
                break;
            case "PrimeMin":
                descriptiveText=extractEndText();
                break;
            case "PrisonerInterview":
                descriptiveText=extractNameAndCorp();
                break;
            case "RAFOfficers":
                descriptiveText=extractNameOnly();
                break;
            case "RNOfficer":
                descriptiveText=extractNameOnly();
                break;
            case "RecHonours":
                descriptiveText=extractRecHonours();
                break;
            case "RoyalChelsea":
                descriptiveText=extractEndText();
                break;
            case "RoyalMarines":
                descriptiveText=extractNameWithNumberAndDivision();
                break;
            case "SeamenMedal":
                descriptiveText=extractNameWithDischarge();
                break;
            case "SeamenRegister":
                descriptiveText=extractNameLocationNumber();
                break;
            case "SeamenWill":
                descriptiveText=extractNameWithNumberAndCorp();
                break;
            case "SecurityService":
                descriptiveText=extractEndText();
                break;
            case "SecurityServiceKV":
                descriptiveText=extractEndText();
                break;
            case "ShippingSeamen":
                descriptiveText=extractEndText();
                break;
            case "ShipsExploration":
                descriptiveText=extractEndText();
                break;
            case "Squadron":
                descriptiveText=extractSquadron();
                break;
            case "Titanic":
                descriptiveText=extractTitanic();
                break;
            case "VictoriaCross":
                descriptiveText=extractVC();
                break;
            case "VolunteerReserve":
                descriptiveText=extractNameWithNumberAndDivision();
                break;
            case "Will":
                descriptiveText=extractNameWithLocation();
                break;
            case "WomensCorps":
                descriptiveText=extractNameWithLocation();
                break;
            case "Wrns":
                descriptiveText=extractNameOnly();
                break;
            case "prisoner":
                descriptiveText=extractPrisoner();
                break;
            default:
                break;         
        }
        return descriptiveText;
        
    }

    private String extractGenericDesc() {
        String descriptiveText;
        Matcher descMatcher=DolExtractor.desc_aps.matcher(description);
        if(descMatcher.find()){
            descriptiveText=descMatcher.group(1);
        }
        else{
            descriptiveText="";
        }
        return descriptiveText;  
    }


    private String extractNameOnly() {
        String fullname;
        String person;
        Matcher persMatcher=DolExtractor.desc_persname_re.matcher(description);
        while(persMatcher.find()){
            String forename;
            String surname;
            person=persMatcher.group(1);
            Matcher forenameMatcher=DolExtractor.desc_forename_re.matcher(person);
            if(forenameMatcher.find()){
                forename=forenameMatcher.group(1);
            }
            else{
                forename="";
            }
            Matcher surnameMatcher=DolExtractor.desc_surname_re.matcher(person);
            if(surnameMatcher.find()){
                surname=surnameMatcher.group(1);
            }
            else{
                surname="";
            }
            fullname=forename+" "+surname;
            fullname=fullname.trim();
            if(fullname.length()>0){
                this.pers.add(fullname);
            }
            
        }
        return "";
    }
    
    private String extractNameWithRank() {
        String fullname;
        String person;
        String rank;
        Matcher persMatcher=DolExtractor.desc_persname_re.matcher(description);
        if(persMatcher.find()){
            String forename;
            String surname;
            person=persMatcher.group(1);
            Matcher forenameMatcher=DolExtractor.desc_forename_re.matcher(person);
            if(forenameMatcher.find()){
                forename=forenameMatcher.group(1);
            }
            else{
                forename="";
            }
            Matcher surnameMatcher=DolExtractor.desc_surname_re.matcher(person);
            if(surnameMatcher.find()){
                surname=surnameMatcher.group(1);
            }
            else{
                surname="";
            }
            rank=this.extractRank();
            fullname=rank.trim()+" "+forename.trim()+" "+surname.trim();
            if(fullname.length()>0){
                this.pers.add(fullname);
            }          
        }
        return "";
    }
    
    private String extractNameWithRating() {
        String fullname;
        String person;
        String rank;
        Matcher persMatcher=DolExtractor.desc_persname_re.matcher(description);
        if(persMatcher.find()){
            String forename;
            String surname;
            person=persMatcher.group(1);
            Matcher forenameMatcher=DolExtractor.desc_forename_re.matcher(person);
            if(forenameMatcher.find()){
                forename=forenameMatcher.group(1);
            }
            else{
                forename="";
            }
            Matcher surnameMatcher=DolExtractor.desc_surname_re.matcher(person);
            if(surnameMatcher.find()){
                surname=surnameMatcher.group(1);
            }
            else{
                surname="";
            }
            rank=this.extractRank();
            fullname=rank.trim()+" "+forename.trim()+" "+surname.trim();
            if(fullname.length()>0){
                this.pers.add(fullname);
            }          
        }
        return "";
    }

    private String extractCabSurnameOnly() {
        Matcher mSurname=DolExtractor.desc_surname2_re.matcher(description);
        while(mSurname.find()){
            pers.add(mSurname.group(1));
        }
        return "";
    }
    
    private String extractFormerRef(){
        Matcher mRef=DolExtractor.desc_formerRef_re.matcher(description);
        while(mRef.find()){
            this.ref.add(mRef.group(1));
        }
        return "";
        
    }
    
    private String extractLocation() {
        String location;
        Matcher mGeog=DolExtractor.desc_geo_re.matcher(description);
        while(mGeog.find()){
            location=mGeog.group(1);
            this.place.add(location);
        }
        return "";
    }
        
    private String extractAncient() {
        String petitioner;
        String addressee;
        String descriptiveText;
        Matcher mPetitioner=DolExtractor.desc_petition_re.matcher(description);
        if(mPetitioner.find()){
            petitioner=mPetitioner.group(1);
            this.pers.add(petitioner);
        }
        Matcher mAddressee=DolExtractor.desc_addressee_re.matcher(description);
        if(mAddressee.find()){
            addressee=mAddressee.group(1);
            this.pers.add(addressee);
        }
        Matcher mRequest=DolExtractor.desc_request.matcher(description);
        if(mRequest.find()){
            descriptiveText=mRequest.group(1);
        }
        else{descriptiveText="";}
        Matcher mEndorsement=DolExtractor.desc_endorsement.matcher(description);
        if(mEndorsement.find()){
            descriptiveText+=mEndorsement.group(1);
        }
        this.extractNameOnly();
        this.extractLocation();
        
        return descriptiveText;
    }

    private String extractNameWithLocation() {
        this.extractNameOnly();
        this.extractLocation();
        return "";
    }

    private String extractCAB() {
        this.extractCabSurnameOnly();
        this.extractFormerRef();
        String desc=this.extractAgenda();
        
        return desc;
        
    }
    
    private String extractRank(){
        Matcher mRank=DolExtractor.desc_rank_re.matcher(description);
        String rankText;
        if(mRank.find()){
            rankText=mRank.group(1);
        }
        else{
            rankText="";
        }
        return rankText;
    }
    
    private String extractCorp(){
        Matcher mCorp=DolExtractor.desc_corp_re.matcher(description);
        while(mCorp.find()){
            this.corp.add(mCorp.group(1));
        }
        return "";
    }

    private String extractCombatRep() {
        extractCorp();
        extractNameWithRank();
        return "";
    }


    private String extractNameOccupationLocation() {
        extractNameWithLocation();
        extractOccupation();
        return "";
    }
    
    private String extractOccupation(){
        Matcher mOccupation =DolExtractor.desc_occupation_re.matcher(description);
        if(mOccupation.find()){
            this.subj.add(mOccupation.group(1));
        }
        return "";
    }
    
    private String extractPersOnly(){
        Matcher mPerson=DolExtractor.desc_persname_re.matcher(description);
        while(mPerson.find()){
            this.pers.add(mPerson.group(1));
        }
        return "";
    }
    
    private String extractFolio(){
        Matcher mFolio=DolExtractor.desc_folio.matcher(description);
        if(mFolio.find()){
            this.subj.add(mFolio.group(1));
        }
        return "";
    }


    private String extractDomesday() {
        extractLocation();
        extractPersOnly();
        extractFolio();
        return "";
    }


    private String extractFameWill() {
        String descText="Will ";
        extractPersOnly();
        descText+=extractEndText();
        return descText;
        
    }
    
    private String extractEndText(){
        String descText="";
        Matcher mEndText=DolExtractor.desc_end_re.matcher(description);
        if(mEndText.find()){
            descText=mEndText.group(1);
        }
        return descText;
    }


    private String extractScopeOnly() {
        String descText="";
        Matcher mScope=DolExtractor.desc_scope_re.matcher(description);
        if(mScope.find()){
            descText=mScope.group(1);
        }
        return descText;
    }


    private String extractMapPicture() {
        String descText=DolExtractor.cleanDescriptionText(description);
        extractLocation();
        
        return descText;
    }

    private String extractMedSeal() {
        String descText=DolExtractor.cleanDescriptionText(description);
        extractLocation();
        return descText;
    }

    private String extractMedal() {
        this.extractNameWithRank();
        this.extractCorp();
        this.extractReg();
        return "";
    }
    
    private String extractReg(){
        Matcher mReg=DolExtractor.desc_regnumber_re.matcher(description);
        while(mReg.find()){
            ref.add(mReg.group(1));
        }
        return "";
    }

    private String extractMisc() {
        extractNameOnly();
        extractLocation();
        String descText=DolExtractor.cleanDescriptionText(description);
        return descText;
    }

    private String extractMusterRolls() {
        extractNameWithRating();
        extractCorp();
        return "";
    }

    private String extractNameLocationNumber() {
        extractNameWithRank();
        extractLocation();
        extractServiceNumber();
        return "";
    }
    
    private String extractServiceNumber(){
        Matcher mNum=DolExtractor.desc_num_re.matcher(description);
        while(mNum.find()){
            ref.add(mNum.group(1));
        }
        return "";
    }


    private String extractNavyLandService() {
        extractNameWithRating();
        extractServiceNumber();
        return "";
    }



    private String extractNameAndCorp() {
        extractNameOnly();
        extractCorp();
        return "";
    }


    private String extractRecHonours() {
        extractNameWithRank();
        extractCorp();
        extractLocation();
        extractAward();
        return "";
        
    }
    
    private String extractAward(){
        Matcher mAward=DolExtractor.desc_award_re.matcher(description);
        while(mAward.find()){
            this.subj.add(mAward.group(1));
        }
        return "";
    }



    private String extractNameWithNumberAndCorp() {
        extractNameOnly();
        extractServiceNumber();
        return "";
        
    }
    
    private String extractNameWithNumberAndDivision(){
        extractNameOnly();
        extractServiceNumber();
        extractDivision();
        return "";
    }
    
    private String extractDivision(){
        Matcher mDivision=DolExtractor.desc_division_re.matcher(description);
        if(mDivision.find()){
            this.corp.add(mDivision.group(1));
        }
        return "";
    }

    private String extractNameWithDischarge() {
        extractNameOnly();
        extractDischarge();
        return "";
    }
    
    private String extractDischarge(){
        Matcher mDischarge=DolExtractor.desc_discharge_re.matcher(description);
        if(mDischarge.find()){
            this.ref.add(mDischarge.group(1));
        }
        return "";
    }

    private String extractSquadron() {
        Matcher mNum=DolExtractor.desc_num_re.matcher(description);
        if(mNum.find()){
            String squadron="Squadron "+mNum.group(1);
            corp.add(squadron);
            return squadron;
        }
        return "";
    }

    private String extractTitanic() {
        extractNameOnly();
        return extractScopeOnly();
    }

    private String extractVC() {
        extractNameOnly();
        extractCorp();
        extractLocation();
        extractCampaign();
        return "";
    }
    
    private String extractCampaign(){
        Matcher mCampaign=DolExtractor.desc_campaign_re.matcher(description);
        if(mCampaign.find()){
            this.subj.add(mCampaign.group(1));
        }
        return "";
    }
            

    private String extractPrisoner() {
        extractNameWithLocation();
        extractOffenceAndSentenceAndCourt();
        return DolExtractor.cleanDescriptionText(description);
    }
    
    private String extractOffenceAndSentenceAndCourt(){
        Matcher mOffence=DolExtractor.desc_offence_re.matcher(description);
        Matcher mSentence=DolExtractor.desc_sentence_re.matcher(description);
        Matcher mCourt=DolExtractor.desc_court_re.matcher(description);
        if(mOffence.find()){
            this.subj.add(mOffence.group(1));
        }
        if(mSentence.find()){
            this.subj.add(mSentence.group(1));
        }
        if(mCourt.find()){
            this.corp.add(mCourt.group(1));
        }
        
        return "";
    }

    private String extractAgenda() {
        String descText="";
        Matcher mAgenda=DolExtractor.desc_agenda.matcher(description);
        if(mAgenda.find()){
            descText=mAgenda.group(1);
        }
        return descText;
    }

    
    private static String cleanDescriptionText(String dirty){
        String clean;
        Matcher working=DolExtractor.desc_doctype.matcher(dirty);
        if(working.find())
        {
            clean=working.replaceAll("");
        }
        else{
            clean=dirty;
        }
        working=DolExtractor.desc_tag_re.matcher(clean);
        if(working.find())
        {
            clean=working.replaceAll(" ");
        }
        else{
            clean=dirty;
        }
        return clean.trim();
    }

    
}
