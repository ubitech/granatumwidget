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


public class ArgLinkedBiomedicalDataSpace 
extends Service
{
    private String query;
    private String bindingNames[];
    private static ArgLinkedBiomedicalDataSpace instance;
    private static int maxReturnedResults = 5;
    
    public ArgLinkedBiomedicalDataSpace()
    {       
//            setServiceURL("http://granatum.srvgal51.deri.ie/graph/Granatum");
            setServiceURL("http://srvgal78.deri.ie:8080");
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
    
    public static ArgLinkedBiomedicalDataSpace getSingleton()
    {
        if(instance == null)
            instance = new ArgLinkedBiomedicalDataSpace();

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

    public Collection searchArguments(String searchTerm)
    throws Throwable
    {
        formalizeQueryString("SELECT distinct ?pw ?title WHERE {  ?pw a <http://chem.deri.ie/granatum/PublishedWork>. ?pw <http://chem.deri.ie/granatum/title> ?title. filter regex (?title,\""+ searchTerm +"\", \"i\").} limit 3");

        bindingNames = new String[3];
        bindingNames[0] = "index";
        bindingNames[1] = "pw";
        bindingNames[2] = "title";
        
        return(getAssociatedEntities());
    }    

    protected Collection getAssociatedEntitiesAndTypes()
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
        
        URL targetURL = new URL(getServiceURL() + "/sparql?output=csv&query=" + query);
        System.out.println(getServiceURL() + "/sparql?output=csv&query=" + query);
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

    protected void linkifyExtr(String url, JSONObject map)
    throws Throwable
    {
        String cat, name, bundle, tmpStr;
        
        try { tmpStr = map.getString("Agents"); }        
        catch(net.sf.json.JSONException jsonexb) { tmpStr = new String(""); }

        map.put("Agents", tmpStr + "<a href=\"" + url + "\" target=\"_new\">" + url + "</a> | ");        
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

                linkifyExtr(lineParts[2], map);
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
                
                linkifyExtr(lineParts[2], map);
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
        
//        URL targetURL = new URL(getServiceURL() + query);
        URL targetURL = new URL(getServiceURL() + "/sparql?output=CSV&query=" + query);
        System.out.println(getServiceURL() + "/sparql?output=CSV&query=" + query);        
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
        
            ArgLinkedBiomedicalDataSpace d = new ArgLinkedBiomedicalDataSpace();            
            System.out.println(d.searchRelatedPublications(args));
            
        } catch (Throwable ex) {
            Logger.getLogger(ArgLinkedBiomedicalDataSpace.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
