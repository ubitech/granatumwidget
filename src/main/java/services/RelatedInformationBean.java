package services;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import net.sf.json.JSONObject;

@ManagedBean(name = "RelatedInformationBean")
@SessionScoped
public class RelatedInformationBean
implements Serializable
{

    private List<JSONObject> relatedPublicationList;
    private List<JSONObject> relatedAssaysList;
    private int relatedPublicationLength;
    private int relatedAssaysLength; 
    private int searchtermsLength;
    private String conf = "1";
    private String[] searchterms = null;
    private String searchString = null;

    private String homeID;

    public String gethomeID()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        this.homeID = (String) facesContext.getExternalContext().getRequestParameterMap().get("homeID");
        return this.homeID;
    }    
    
    public String getConf() {
        return conf;
    }

    public void setConf(String conf) {
        this.conf = conf;
    }

    public RelatedInformationBean()
    {            
    }

    public int getSearchtermsLength() {
        return (searchterms==null) ? 0 : searchterms.length;
    }

    public void setSearchtermsLength(int searchtermsLength) {
        this.searchtermsLength = searchtermsLength;
    }

    
    
    public List<JSONObject> getRelatedPublicationList() {
        return relatedPublicationList;
    }

    public void setRelatedPublicationList(List<JSONObject> relatedPublicationList) {
        this.relatedPublicationList = relatedPublicationList;
    }

    public List<JSONObject> getRelatedAssaysList() {
        return relatedAssaysList;
    }

    public void setRelatedAssaysList(List<JSONObject> relatedAssaysList) {
        this.relatedAssaysList = relatedAssaysList;
    }

    public int getRelatedPublicationLength() {
        return relatedPublicationLength;
    }

    public void setRelatedPublicationLength(int relatedPublicationLength) {
        this.relatedPublicationLength = relatedPublicationLength;
    }

    public int getRelatedAssaysLength() {
        return relatedAssaysLength;
    }

    public void setRelatedAssaysLength(int relatedAssaysLength) {
        this.relatedAssaysLength = relatedAssaysLength;
    }

    public String[] getSearchterms() {
        return searchterms;
    }

    public void setSearchterms(String[] searchterms) {
        this.searchterms = searchterms;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
    
    public void getUserSearchPreferences()
    {
        this.searchString = new String("a,sam");
    }
    
    public String getUserRelatedPublications()
    throws Throwable
    {
        this.getUserSearchPreferences();
        searchterms = searchString.split(",");
        LinkedBiomedicalDataSpace lbds = new LinkedBiomedicalDataSpace();
        relatedPublicationList = (List<JSONObject>)lbds.searchRelatedPublications(searchterms);
        //relatedPublicationList = (List<JSONObject>) lbds.getAssociatedEntities();
        System.out.println("4");
        return new String("");
    }

    public String getUserRelatedAssays()
    throws Throwable
    {
        this.getUserSearchPreferences();        
        searchterms = searchString.split(",");
        LinkedBiomedicalDataSpace lbds = new LinkedBiomedicalDataSpace();
        relatedAssaysList = (List<JSONObject>) lbds.searchRelatedAssays(searchterms);
        //relatedAssaysList = (List<JSONObject>) lbds.getAssociatedEntities();
        
        return new String("");
    }    
    
    public static void main(String[] arg)
    {
        RelatedInformationBean ab = new RelatedInformationBean();

        try {
//            ab.setSearchString("a,sam");
            ab.getUserRelatedPublications();
            System.out.println(ab.getRelatedPublicationList());
            ab.getUserRelatedAssays();
            System.out.println(ab.getRelatedAssaysList());
        } catch (Throwable ex) {
            Logger.getLogger(RelatedInformationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
