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
    private static Pattern desc_corpname_re=Pattern.compile("<corpname>(.+?)</corpname>");
    private static Pattern desc_regnumber_re=Pattern.compile("regno\\\">(.+?)<");
    private static Pattern desc_rank_re=Pattern.compile("rank\\\">(.+?)<>");
    private static Pattern desc_title_re=Pattern.compile("perstitle\\\">(.+?)</emph");
    private static Pattern desc_occupation_re=Pattern.compile("<occupation>(.+)</occupation>");
    private static Pattern desc_geo_re=Pattern.compile("<geogname>(.+)</geogname>");
    private static Pattern desc_rating_re=Pattern.compile( "rating\">(.+?)</emph>");
    private static Pattern desc_corp_re=Pattern.compile("corpname\"(.+?)1</emph>");
    private static Pattern desc_nation_re=Pattern.compile("nation\">(.+?)</emph>");
    private static Pattern desc_scope_re=Pattern.compile("scope\">(.+?)</emph>");
    private static Pattern desc_discharge_re=Pattern.compile("dischargeno\">R284299</emph>");
    private static Pattern desc_name1_re=Pattern.compile("name1\">Gudvang</emph>");
    private static Pattern desc_name2_re=Pattern.compile("name2\">(.+?)</emph>");
    private static Pattern desc_tonnage_re=Pattern.compile("size\">(.+?)</emph>");
    private static Pattern desc_num_re=Pattern.compile("num\">(.+?)</emph>");
    private static Pattern desc_award_re=Pattern.compile("award\">(.+?)</emph>");
    private static Pattern desc_division_re=Pattern.compile("division\">(.+?)</emph>");
    private static Pattern desc_campaign_re=Pattern.compile("campaign\">(.+?)</emph>");
    private static Pattern desc_court_re=Pattern.compile("court\">(.+?)</emph>");
    private static Pattern desc_offence_re=Pattern.compile("offence\\\">(.+?)</emph>");
    private static Pattern coll_re=Pattern.compile("<colltype id=\\\"(.+?)\\\">,</colltype>");
    
    List pers;
    List ref;
    List place;
    List corp;
    List subj;
    String description;
    String collType;
    
    public DolExtractor(String desc, List pers, List place, List ref, List subj, List corp){
        this.description=desc;
        this.ref=ref;
        this.pers=pers;
        this.subj=subj;
        this.place=place;
        this.corp=corp;
        
        this.collType=DolExtractor.getCollType(desc);
        
    }
    
    public static String getCollType(String desc){
        String coll=null;
        Matcher collMatcher=DolExtractor.coll_re.matcher(desc);
        if(collMatcher.find()){
            coll=collMatcher.group(1);
        }
        return coll;
        
    }
    
    public void checkMetaData(){
        if(this.collType==null){
            return;
        }
        switch(this.collType){
            case "APS":
                break;
            case "Airwomen":
                break;
            case "AliensRegCards":
                break;
            case "AncestorsMagazine":
                break;
            case "AncientPetitions":
                break;
            case "BritishWarMedal":
                break;
            case "CabinetPapers":
                break;
            case "CombatRepWW2":
                break;
            case "Datasets":
                break;
            case "DeathDuty":
                break;
            case "DixonScott":
                break;
            case "DomesdayBook":
                break;
            case "EdenPaper":
                break;
            case "FOI":
                break;
            case "FameWill":
                break;
            case "IrishMaps":
                break;
            case "LootedArt":
                break;
            case "MRR":
                break;
            case "MapPicture":
                break;
            case "MedSeal":
                break;
            case "Medal":
                break;
            case "Miscellaneous":
                break;
            case "MusterRolls":
                break;
            case "NavalOfficers":
                break;
            case "NavalReserve":
                break;
            case "NavyLandService":
                break;
            case "NavyList":
                break;
            case "NursingService":
                break;
            case "Olympic":
                break;
            case "PoorLaw":
                break;
            case "PrimeMin":
                break;
            case "PrisonerInterview":
                break;
            case "RAFOfficers":
                break;
            case "RNOfficer":
                break;
            case "RecHonours":
                break;
            case "RoyalChelsea":
                break;
            case "RoyalMarines":
                break;
            case "SeamenMedal":
                break;
            case "SeamenRegister":
                break;
            case "SeamenWill":
                break;
            case "SecurityService":
                break;
            case "SecurityServiceKV":
                break;
            case "ShippingSeamen":
                break;
            case "ShipsExploration":
                break;
            case "Squadron":
                break;
            case "Titanic":
                break;
            case "VictoriaCross":
                break;
            case "VolunteerReserve":
                break;
            case "Will":
                break;
            case "WomensCorps":
                break;
            case "Wrns":
                break;
            case "prisoner":
                break;
            default:
                break;
            
            
                
                
                
            
        }
        
    }
    
}
