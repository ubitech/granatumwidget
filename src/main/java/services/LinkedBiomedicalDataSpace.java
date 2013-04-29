package services;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
    private String csvfile;

    public String getCsvfile() {
        return csvfile;
    }

    public void setCsvfile(String csvfile) {
        this.csvfile = csvfile;
    }        
    
    public LinkedBiomedicalDataSpace()
    {       
            setServiceURL("http://granatum.srvgal51.deri.ie/graph/Granatum/sparql?output=CSV&query=");
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
        int i;
        
        String queryStr = "select distinct ?uri ?title " +
                          "where { ?uri a <http://chem.deri.ie/granatum/PublishedWork>. " +
                          "?uri <http://chem.deri.ie/granatum/title> ?title. filter (";
        
        for(i=1;(i<searchTerms.length-1) && (i<2) ;i++)
        {
            queryStr += " regex(?title,\"" + searchTerms[i] + "\",\"i\") ||";
        }
    
        if(!searchTerms[i].equals(""))
            queryStr += " regex(?title,\"" + searchTerms[i] + "\",\"i\") ";
        
        queryStr += " ). } limit 6";
        formalizeQueryString(queryStr);
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

    public Collection searchArguments(String searchTerm)
    throws Throwable
    {
        formalizeQueryString("prefix dc: <http://purl.org/dc/elements/1.1/> " +
                             "prefix ao: <http://hq.ubitech.eu/ArgOntology.owl#> " +
                             "SELECT ?url ?title ?abstr ?arg  ?extSource " + 
                             "{ " + 
                             "?u ao:appearsIn ?url. " +
                             "?url dc:title ?title. " +
                             "?url ao:abstract ?abstr. " + 
                             "?url ao:externalsource ?extSource. " +                
                             "?u ao:argumentSentence ?arg. " +
                             "filter regex(?arg,\"" + searchTerm + "\",\"i\"). } limit 5");

        bindingNames = new String[6];
        bindingNames[0] = "index";
        bindingNames[1] = "url";
        bindingNames[2] = "title";
        bindingNames[3] = "abstr";
        bindingNames[4] = "arg";
        bindingNames[5] = "extSource";

        return(getAssociatedEntities());
    }    

    public Collection searchRelatedArguments(Collection uris)
    throws Throwable
    {
        String tempQuery = new String();

        Iterator iter = uris.iterator();
        
        tempQuery = tempQuery + " { ?a <http://www.w3.org/2000/01/rdf-schema#refersTo> " + " <" + ((String)iter.next()) + ">. } ";
        while(iter.hasNext())
        {
            String uri = (String)iter.next();
            tempQuery = tempQuery + " UNION { ?a <http://www.w3.org/2000/01/rdf-schema#refersTo> " + " <" + uri + ">. } ";
        }
        
        formalizeQueryString("prefix dc: <http://purl.org/dc/elements/1.1/> " +
                             "prefix ao: <http://hq.ubitech.eu/ArgOntology.owl#> " +
                             "SELECT ?title ?arg ?refersto " + 
                             "WHERE { " + 
                             tempQuery +
                             "?a ao:appearsIn ?docurl. " +
                             "?docurl dc:title ?title. " +
                             "?a ao:argumentSentence ?arg. " +
                             "?a <http://www.w3.org/2000/01/rdf-schema#refersTo> ?refersto. " +
                             "} limit 20");

        bindingNames = new String[4];
        bindingNames[0] = "index";
        bindingNames[1] = "title";
        bindingNames[2] = "arg";
        bindingNames[3] = "refersto";        

        return(getAssociatedEntities(uris));
    }

    public String getURIType(String uri)
    throws Throwable            
    {
        formalizeQueryString("prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                             "select ?type ?name " +
                             "where { " +                
                             "<" + uri + "> rdf:type ?type. " +
                             "<" + uri + "> <http://www.w3.org/2000/01/rdf-schema#label> ?name. " +
                             "} limit 1");        
        
        System.out.println("prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                             "select ?type ?name \n" +
                             "where { \n" +                
                             "<" + uri + "> rdf:type ?type. \n" +
                             "<" + uri + "> <http://www.w3.org/2000/01/rdf-schema#label> ?name. \n" +
                             "} limit 1\n\n");
        return(collectEntityType());
    }    

    protected String collectEntityType()
    throws Throwable
    {
        String line, uri, name;
        
        URL targetURL = new URL(getServiceURL() + "/query?output=csv&query=" + query);
        System.out.println(getServiceURL() + "/query?output=csv&query=" + query);
        URLConnection connection = targetURL.openConnection();
        connection.setDoOutput(true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())); 

        reader.readLine();
        line = reader.readLine();
        reader.close();
      
        if(line==null)
            return ("Void");
        else
        {
            System.out.println("Type for URI =" + line);
            uri  = new String(line.split(",")[0]);
            name = new String(line.split(",")[1]);
            return new String( ((uri.split("#").length>1) ? uri.split("#")[1] : (uri.split("/")[uri.split("/").length-1] ))+","+name );
        }
    }  
    
    protected void linkify(String url, JSONObject map)
    throws Throwable
    {
        String cat, name, bundle, tmpStr;
        
        bundle = this.getURIType(url);
        System.out.println(url + " - " + bundle);
                
        if(!bundle.equals("Void"))
        {
            cat  = bundle.split(",")[0];
            name = bundle.split(",")[1];
        }
        else
        {
            cat  = new String("Void");
            name = "name";
        }                 
        
        try { tmpStr = map.getString(cat); }        
        catch(net.sf.json.JSONException jsonexb) { tmpStr = new String(""); }

        map.put(cat, tmpStr + "<a href=\"" + url + "\" target=\"_new\">" + name + "</a> | ");        
        System.out.println(" Categoize: cat=" + cat + " name=" + name + "  str=" + tmpStr);        
    }    
    
    protected Collection getAssociatedEntities(Collection uris)
    throws Throwable
    {
        StringBuffer msgsock = new StringBuffer();
        LinkedList<JSONObject> collection = null;
        String line;
        String cat, name, bundle;
        int index = 0;
        String lineParts[];
        JSONObject map = new JSONObject();
        //JSONObject prMap = new JSONObject();
        map.put("arg", "");
        
        URL targetURL = new URL(getServiceURL() + "/query?output=csv&query=" + query);
        System.out.println(getServiceURL() + "/query?output=csv&query=" + query);
        collection = new LinkedList<JSONObject>();
        URLConnection connection = targetURL.openConnection();
        connection.setDoOutput(true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())); 
        String tmpStr;
        
        reader.readLine();
        while((line = reader.readLine()) != null)
        {
            System.out.println("1");            
            System.out.println("LINE= " + line);            

            lineParts = line.split(",");
 
            if(lineParts[1].equals(map.getString("arg")))
            {
                try { tmpStr = map.getString("refersto"); }
                catch(net.sf.json.JSONException jsonexa) { tmpStr = new String(""); }
                map.put("refersto", tmpStr + "," + lineParts[2]);

                linkify(lineParts[2], map);
            }
            else{

                map = new JSONObject();
                map.put("Void", "");
                map.put("Protein", "");
                map.put(bindingNames[0], ""+(index++));
                
                for(int i=1;i<bindingNames.length;i++)
                {
                    System.out.println("bindingNames.length= " + bindingNames.length);
                    System.out.println(bindingNames[i] + " " + lineParts[i-1]);
                    map.put(bindingNames[i], lineParts[i-1]);
                }
                /*
                bundle = this.getURIType(lineParts[2]);
                System.out.println(lineParts[2] + " - " + bundle);
                
                if(!bundle.equals("Void"))
                {
                    cat  = bundle.split(",")[0];
                    //cat = new String("Void");
                    name = bundle.split(",")[1];
                }
                else
                {
                    cat  = new String("Void");
                    name = "name";
                }                 

                try { tmpStr = map.getString(cat); }
                catch(net.sf.json.JSONException jsonexb) { tmpStr = new String(""); }

                map.put(cat, tmpStr + "<a href=\"" + lineParts[2] + "\" target=\"_new\">" + name + "</a> | ");
                System.out.println(" Categoize: cat=" + cat + " name=" + name + "  str=" + tmpStr);
                */
                
                linkify(lineParts[2], map);
                collection.add(map);
                System.out.println("2");            
            }
        }
        
        reader.close();
        System.out.println("3");
        return collection;
    }        
    
    public Collection searchSpecificChemoAgent(String searchTerm)
    throws Throwable
    {
        formalizeQueryString("");
        formalizeQueryString("SELECT distinct ?ChemoAgent ?Label ?sdf WHERE " + 
                             "{ ?ChemoAgent a <http://chem.deri.ie/granatum/ChemopreventiveAgent>. " +
                             "?ChemoAgent <http://www.w3.org/2000/01/rdf-schema#label> ?str. " +
                             "?ChemoAgent <http://chem.deri.ie/granatum/sdf_file> ?sdf. " +
                             "BIND(REPLACE(str(?str),\",\",\"\") AS ?Label). " +
                             "filter regex(?str,\""+ searchTerm +"\",\"i\").} limit " + maxReturnedResults);

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

    public Collection searchSpecificMoleculeByRules(String molweight, String coefficient)
    throws Throwable
    {
        formalizeQueryString("Select ?mol ?mol_label ?molWt ?smileStringIsomeric ?chebiMol " +
                             "WHERE " + 
                             "{ " +
                             "?mol a <http://chem.deri.ie/granatum/Molecule>. " +
                             "?mol <http://www.w3.org/2000/01/rdf-schema#label> ?mol_label . " +
                             "?mol <http://chem.deri.ie/granatum/molecularWeight> ?molWt . " +
                             "?mol <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/smilesStringIsomeric> ?smileStringIsomeric . " +
                             "?mol <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/chebiId> ?chebiMol . "+
                             "FILTER( <http://www.w3.org/2001/XMLSchema#double>( ?molWt ) >= " + molweight +  "). " +
                             "} limit " + maxReturnedResults);

        bindingNames = new String[6];
        bindingNames[0] = "index";
        bindingNames[1] = "mol";
        bindingNames[2] = "mol_label";
        bindingNames[3] = "molWt";
        bindingNames[4] = "smileStringIsomeric";
        bindingNames[5] = "chebiMol";

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
                             "filter regex(?str,\"" + searchTerm + "\",\"i\").} limit " + maxReturnedResults);

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
        JSONObject cfile = new JSONObject();
        JSONObject list = new JSONObject();
        
        URL targetURL = new URL(getServiceURL() + query);
        collection = new LinkedList<JSONObject>();
        URLConnection connection = targetURL.openConnection();
        connection.setDoOutput(true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())); 

        csvfile = new String("");
        reader.readLine();
        while((line = reader.readLine()) != null)
        {
            csvfile = csvfile + line + "\n";
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
            System.out.println(d.searchSpecificMoleculeByRules("1200.2", "4"));
            
        } catch (Throwable ex) {
            Logger.getLogger(LinkedBiomedicalDataSpace.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
