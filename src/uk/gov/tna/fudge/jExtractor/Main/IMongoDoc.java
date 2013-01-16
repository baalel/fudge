/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Main;

import java.util.List;
import org.bson.types.ObjectId;

/**
 *
 * @author sprice
 */
public interface IMongoDoc {
    public String getDreReference();
    public String getIaid();
    public String getParentIaid();
    public Integer getParentLevel();
    public Integer getSourceLevelId();
    public String getCatDocRef();
    public String getClosureStatus();
    public String getClosureCode();
    public String getClosureType();
    public String getStartdate();
    public String getEnddate();
    public String getTitle();
    public String getDescription();
    public String getSchema();
    public String getReference();
    public String getUrlParams();
    public List<String> getTags();
    public ObjectId getObjectId();
    public List<String> getReferences();
    public List<String> getHeldbys();
    public List<String> getPeoples();
    public List<String> getPlaces();
    public List<String> getCorpBodies();
    public List<String> getSubjects();
    
    
    
}
