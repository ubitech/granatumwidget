package services;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.sf.json.JSONObject;

@ManagedBean(name = "InSilicoBean")
@RequestScoped
public class InSilicoBean 
{

    int molLength = 0;
    String molweight;
    String coefficient;
    boolean hasResult;
    private List<JSONObject> molResults;    

    public InSilicoBean() {
    }

    public int getMolLength() {
        return molLength;
    }

    public void setMolLength(int molLength) {
        this.molLength = molLength;
    }

    public List<JSONObject> getMolResults() {
        return molResults;
    }

    public void setMolResults(List<JSONObject> molResults) {
        this.molResults = molResults;
    }

    public String getMolweight() {
        return molweight;
    }

    public void setMolweight(String molweight) {
        this.molweight = molweight;
    }

    public String getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(String coefficient) {
        this.coefficient = coefficient;
    }

    public boolean isHasResult() {
        return hasResult;
    }

    public void setHasResult(boolean hasResult) {
        this.hasResult = hasResult;
    }
   
    public void doSearch()
    {
        LinkedBiomedicalDataSpace d = new LinkedBiomedicalDataSpace();

        try
        {
            d.searchSpecificMoleculeByRules(molweight, coefficient);
            molResults = (List<JSONObject>) d.getAssociatedEntities();
            this.hasResult = true;
        } 
        catch (Throwable ex)
        {
            java.util.logging.Logger.getLogger(InSilicoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }

} //EoC
