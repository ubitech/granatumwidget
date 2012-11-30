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

@ManagedBean(name = "SearchBean")
@RequestScoped
public class SearchBean {

    /**
     * Creates a new instance of SearchBean
     */
    private String annotations;
    private String searchterm;
    int chemoLength = 0;
    int molLength = 0;
    private List<JSONObject> allResults;
    private List<JSONObject> chemoResults;
    private List<JSONObject> molResults;    

    public SearchBean() {
    }

    public String getAnnotations() {
        return annotations;
    }

    public void setAnnotations(String annotations) {
        this.annotations = annotations;
    }

    public String getSearchterm() {
        return searchterm;
    }

    public void setSearchterm(String searchterm) {
        this.searchterm = searchterm;
    }

    public int getChemoLength() {
        return chemoLength;
    }

    public void setChemoLength(int chemoLength) {
        this.chemoLength = chemoLength;
    }

    public int getMolLength() {
        return molLength;
    }

    public void setMolLength(int molLength) {
        this.molLength = molLength;
    }

    public List<JSONObject> getAllResults() {
        return allResults;
    }

    public void setAllResults(List<JSONObject> allResults) {
        this.allResults = allResults;
    }

    public List<JSONObject> getChemoResults() {
        return chemoResults;
    }

    public void setChemoResults(List<JSONObject> chemoResults) {
        this.chemoResults = chemoResults;
    }

    public List<JSONObject> getMolResults() {
        return molResults;
    }

    public void setMolResults(List<JSONObject> molResults) {
        this.molResults = molResults;
    }

    public String getBasicQueryInterfaceURL()
    {
        return new String("http://granatum.ubitech.eu/bqi/jsfs/search.jsf?searchterm=" + this.searchterm 
                          + "&selection=" + this.annotations);
    }    
    
    public void doSearch()
    {
        LinkedBiomedicalDataSpace d = new LinkedBiomedicalDataSpace();
        
        if (annotations.trim().equalsIgnoreCase("1"))
        {            
            try
            {
                d.searchSpecificChemoAgent(searchterm);                
                chemoResults = (List<JSONObject>) d.getAssociatedEntities();
                System.out.println(chemoResults);
            } catch (Throwable ex)
            {
                java.util.logging.Logger.getLogger(SearchBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }

        if (annotations.trim().equalsIgnoreCase("2"))
        {            
            try
            {
                d.searchSpecificMolecule(searchterm);
                molResults = (List<JSONObject>) d.getAssociatedEntities();
            } catch (Throwable ex) {
                java.util.logging.Logger.getLogger(SearchBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }    
    }


/*
    
    
    private void parseMockXMLFile(String filename){
        try{
            JAXBContext jc = JAXBContext.newInstance( "eu.granatum.xsd" );
            Unmarshaller u = jc.createUnmarshaller();
            Object f = u.unmarshal( new File( "C:\\projects\\granatumbqi\\target\\test-classes\\"+filename ) );
            Sparql sparql =(Sparql) f;
            List<Object> list = sparql.getHeadOrResults();

            for (int i = 0; i < list.size(); i++) {
                Object o =  list.get(i);
                if (o instanceof Sparql.Head){
                    Sparql.Head head = (Sparql.Head) o;

                }
                if (o instanceof Sparql.Results){
                    //set results
                    Sparql.Results results = (Sparql.Results) o;
                    allResults = results.getResult();
                    //Reset Lists
                    documentResults = new ArrayList<String>();
                    imageResults = new ArrayList<String>();
                    forumResults = new ArrayList<String>();
                    clinicalResults = new ArrayList<String>();
                    drugResults = new ArrayList<String>();  
                    chemoResults = new ArrayList<String>();
                    
                    for (int j = 0; j < allResults.size(); j++) {
                        String res = allResults.get(j);
                        
                        String.Binding binding = res.getBinding().get(0);
                        if (binding.getUri().trim().startsWith("http://chem.deri.ie/granatum/PublishedWork")) {
                            //add to document
                            documentResults.add(res);
                        }
                        
                        if (binding.getUri().trim().startsWith("http://chem.deri.ie/granatum/diagram2D")) {
                            //add them to images
                            imageResults.add(res);
                        }                        
                        if (binding.getUri().trim().startsWith("http://chem.deri.ie/granatum/ForumPost")) {
                            //add them to images
                            forumResults.add(res);
                        }                        
                        if (binding.getUri().trim().startsWith("http://chem.deri.ie/granatum/ClinicalTrial")) {
                            //add them to images
                            clinicalResults.add(res);
                        }
                        if (binding.getName().trim().startsWith("Drug")) {
                            //add them to drugs
                            drugResults.add(res);
                        }
                        if (binding.getName().trim().startsWith("ChemoAgent")) {
                            chemoResults.add(res);
                        }                        
                        
                    }
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//EoM
*/

} //EoC
