package services;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.sf.json.JSONObject;

@ManagedBean(name = "SearchMoleculeBean")
@SessionScoped
public class SearchMoleculeBean
{
    private String molweight;
    private String chemoproperty;
    private String compound;
    int molLength = 0;

    private List<JSONObject> molResults;

    public String getCompound() {
        return compound;
    }

    public void setCompound(String compound) {
        this.compound = compound;
    }

    public SearchMoleculeBean() {
    }

    public String getMolweight() {
        return molweight;
    }

    public void setMolweight(String molweight) {
        this.molweight = molweight;
    }

    public String getChemoproperty() {
        return chemoproperty;
    }

    public void setChemoproperty(String chemoproperty) {
        this.chemoproperty = chemoproperty;
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
    
    public void doSearch()
    {
        
    }
}
