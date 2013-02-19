package services;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.sf.json.JSONObject;
import org.apache.xerces.impl.dv.util.Base64;

@ManagedBean(name = "InSilicoBean")
@RequestScoped
public class InSilicoBean 
{

    int molLength = 0;
    String molweight;
    String coefficient;
    boolean hasResult;
    String csvfile;
    String galaxyFileLoaderURL = "http://lisis.cs.ucy.ac.cy:9000/GRANATUMFileLoader_widget.py?action=authorize";
    private List<JSONObject> molResults;    

    public InSilicoBean() {
    }

    public String getCsvfile() {
        return csvfile;
    }

    public void setCsvfile(String csvfile) {
        this.csvfile = csvfile;
    }

    public String getGalaxyFileLoaderURL() {
        return galaxyFileLoaderURL;
    }

    public void setGalaxyFileLoaderURL(String galaxyFileLoaderURL) {
        this.galaxyFileLoaderURL = galaxyFileLoaderURL;
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
/*
    public void sentToWorkflowTool()
    throws IOException
    {
        System.out.println("In sentToWorkflowTool");
        FacesContext context = FacesContext.getCurrentInstance();        
        HttpServletRequest myRequest = (HttpServletRequest)context.getExternalContext().getRequest();
        HttpServletResponse myResponse = (HttpServletResponse)context.getExternalContext().getResponse();        
        HttpSession mySession = myRequest.getSession();        

        String homeFolder = (String) myRequest.getParameter("homeFolder");
        
        myResponse.sendRedirect("./InSilico?op=storeDoc&csvfile=" + Base64.encode(this.csvfile.getBytes("UTF-8")));
        
    }
  */  
    public void doSearch()
    {
        LinkedBiomedicalDataSpace d = new LinkedBiomedicalDataSpace();

        try
        {
            d.searchSpecificMoleculeByRules(molweight, coefficient);
            molResults = (List<JSONObject>) d.getAssociatedEntities();
            this.csvfile = Base64.encode(d.getCsvfile().getBytes("UTF-8"));
            this.hasResult = true;
        } 
        catch (Throwable ex)
        {
            java.util.logging.Logger.getLogger(InSilicoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }

} //EoC
