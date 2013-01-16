package services;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSON;
import net.sf.json.JSONObject;


public class LinkedBiomedicalDataSpace 
extends Service
{
    private String query;
    private String bindingNames[];
    private static LinkedBiomedicalDataSpace instance;
    private static int maxReturnedResults = 5;
    
    public LinkedBiomedicalDataSpace()
    {       
            setServiceURL("http://srvgal78.deri.ie:8080/graph/Granatum/sparql?output=CSV&query=");
    }

    private void formalizeQueryString(String query)
    {
        this.query = new String(query);
        this.query = this.query.replaceAll(" ", "%20");
        this.query = this.query.replaceAll("\\+", "%20");
        this.query = this.query.replaceAll("#", "%23");
        this.query = this.query.replaceAll("\\[", "%5B");
        this.query = this.query.replaceAll("\\]", "%5D");
        System.err.println("QUERY= " + this.query);                
    }
    
    public static LinkedBiomedicalDataSpace getSingleton()
    {
        if(instance == null)
            instance = new LinkedBiomedicalDataSpace();

        return instance;
    }

    public Collection searchRelatedPublications(String[] searchTerms)
    throws Throwable
    {
        formalizeQueryString("select distinct ?uri ?title " +
                             "where { ?uri a <http://chem.deri.ie/granatum/PublishedWork>. " +
                             "?uri <http://chem.deri.ie/granatum/title> ?title. } limit 2");
//                             "filter regex(?title,\"" + searchTerms[0] + "\",\"i\").} limit 5");

        bindingNames = new String[3];        
        bindingNames[0] = "index";
        bindingNames[1] = "uri";
        bindingNames[2] = "title";

        return(getAssociatedEntities());
    }    

    public Collection searchRelatedAssays(String[] searchTerms)
    throws Throwable
    {
        formalizeQueryString("select distinct ?uri ?title " +
                             "where { ?uri a <http://chem.deri.ie/granatum/Study>. " +
                             "?uri <http://chem.deri.ie/granatum/title> ?title. " + 
                             "filter regex(?title,\"" + searchTerms[0] + "\",\"i\").} limit 2");

        bindingNames = new String[3];        
        bindingNames[0] = "index";
        bindingNames[1] = "uri";
        bindingNames[2] = "title";
        return(getAssociatedEntities());
    }     
    
    public Collection searchSpecificChemoAgent(String searchTerm)
    throws Throwable
    {
        formalizeQueryString("SELECT distinct ?ChemoAgent ?Label ?sdf WHERE " + 
                             "{ ?ChemoAgent a <http://chem.deri.ie/granatum/ChemopreventiveAgent>. " +
                             "?ChemoAgent <http://www.w3.org/2000/01/rdf-schema#label> ?Label. " +
                             "?ChemoAgent <http://chem.deri.ie/granatum/sdf_file> ?sdf." +
                             "filter regex(?Label,\""+ searchTerm +"\",\"i\").} limit " + maxReturnedResults);

        bindingNames = new String[4];
        bindingNames[0] = "index";
        bindingNames[1] = "ChemoAgent";
        bindingNames[2] = "label";
        bindingNames[3] = "sdf";

        return(getAssociatedEntities());
    }

    public Collection searchSpecificProtein(String searchTerm)
    throws Throwable
    {
        formalizeQueryString("SELECT distinct ?ChemoAgent ?Label ?sdf WHERE " + 
                             "{ ?ChemoAgent a <http://chem.deri.ie/granatum/ChemopreventiveAgent>. " +
                             "?ChemoAgent <http://www.w3.org/2000/01/rdf-schema#label> ?Label. " +
                             "?ChemoAgent <http://chem.deri.ie/granatum/sdf_file> ?sdf." +
                             "filter regex(?Label,\""+ searchTerm +"\",\"i\").} limit " + maxReturnedResults);

        bindingNames = new String[4];
        bindingNames[0] = "index";
        bindingNames[1] = "ChemoAgent";
        bindingNames[2] = "label";
        bindingNames[3] = "sdf";

        return(getAssociatedEntities());
    }    
    
    public Collection searchSpecificMolecule(String searchTerm)
    throws Throwable
    {
        formalizeQueryString("SELECT ?mol ?label ?smile ?sameas WHERE " + 
                             "{ ?mol a <http://chem.deri.ie/granatum/Molecule>. " +
                             "?mol <http://www.w3.org/2000/01/rdf-schema#label> ?label. " + 
                             "?mol <http://bio2rdf.org/ns/bio2rdf#smiles> ?smile. " +
                             "?mol <http://bio2rdf.org/ns/bio2rdf#sameAs> ?sameas. " +
                             "filter regex(?label,\"" + searchTerm + "\",\"i\").} limit " + maxReturnedResults);

        bindingNames = new String[5];
        bindingNames[0] = "index";
        bindingNames[1] = "mol";
        bindingNames[2] = "label";
        bindingNames[3] = "smile";
        bindingNames[4] = "sameas";

        return(getAssociatedEntities());
    }
    
    
    public Collection searchPublications(String searchTerm)
    throws Throwable
    {
        formalizeQueryString("SELECT distinct ?pw ?title " +
                             "WHERE { ?pw a <http://chem.deri.ie/granatum/PublishedWork>.?pw <http://chem.deri.ie/granatum/title> ?title. " +
                             "filter regex(?title,\"ta\",\"i\")." + "} limit" + maxReturnedResults);

        bindingNames = new String[3];        
        bindingNames[0] = "index";
        bindingNames[1] = "pw";
        bindingNames[2] = "title";

        return(getAssociatedEntities());
    }
    
    protected Collection getAssociatedEntities()
    throws Throwable
    {
        StringBuffer msgsock = new StringBuffer();
        List<JSONObject> collection = null;
        String line;
        int index = 0;
        String lineParts[];
        JSONObject map;
        JSONObject list = new JSONObject();
        
        URL targetURL = new URL(getServiceURL() + query);
        collection = new LinkedList<JSONObject>();
        URLConnection connection = targetURL.openConnection();
        connection.setDoOutput(true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())); 

        reader.readLine();
        while((line = reader.readLine()) != null)
        {
            System.out.println("1");            
            System.out.println(line);            
            map = new JSONObject();
            
            lineParts = line.split(",");
            
            map.put(bindingNames[0], ""+(index++));
            for(int i=1;i<bindingNames.length;i++)
            {
                System.out.println(bindingNames[i] + " " + lineParts[i-1]);
                map.put(bindingNames[i], lineParts[i-1]);
            }

            //System.out.println(map);
            collection.add(map);
            System.out.println("2");            
        }
        
        reader.close();
        System.out.println("3");
        return collection;
    }
    
    public static void main(String[] args)
    {
        try {        
        
            LinkedBiomedicalDataSpace d = new LinkedBiomedicalDataSpace();            
            System.out.println(d.searchRelatedPublications(args));
            
        } catch (Throwable ex) {
            Logger.getLogger(LinkedBiomedicalDataSpace.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
