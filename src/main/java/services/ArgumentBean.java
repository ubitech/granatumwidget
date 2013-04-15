package services;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.richfaces.event.FileUploadEvent;


@ManagedBean(name = "ArgumentBean")
@SessionScoped
public class ArgumentBean
implements Serializable
{
    private List<JSONObject> argumentsList;
    private List<String> agentNames;
    private List<JSONObject> chemoArgsList;    
    private List<JSONObject> proteinArgsList;     
    private List<JSONObject> relatedArgs;
    private int argumentsLength;
    private int filenameLengnth = 0;
    private int searchtermLength;
    private int relatedArgsLength = 0;
    private HashMap<String, String> colourTable;
    private String searchterm = null;
    private String filename = null;    
    private String sel = null;
    private String title = null;
    private String journal = null;
    private String pid = null;
    private String url = null;
    private LinkedList<String> relatedBox = null;
    private String paperFormatRDFXML = null;
    private HashMap<String, LinkedList<String>> listRelatedArgs = null;

    private String homeID;

    public String gethomeID()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        this.homeID = (String) facesContext.getExternalContext().getRequestParameterMap().get("homeID");
        return this.homeID;
    }       
    
    public String getPaperFormatRDFXML() {
        return paperFormatRDFXML;
    }

    public void setPaperFormatRDFXML(String paperFormatRDFXML) {
        this.paperFormatRDFXML = paperFormatRDFXML;
    }

    
    
    public List<JSONObject> getProteinArgsList() {
        return proteinArgsList;
    }

    public void setProteinArgsList(List<JSONObject> proteinArgsList) {
        this.proteinArgsList = proteinArgsList;
    }

    public List<JSONObject> getChemoArgsList() {
        return chemoArgsList;
    }

    public void setChemoArgsList(List<JSONObject> chemoArgsList) {
        this.chemoArgsList = chemoArgsList;
    }

    public List<JSONObject> getRelatedArgs() {
        return relatedArgs;
    }

    public void setRelatedArgs(List<JSONObject> relatedArgs) {
        this.relatedArgs = relatedArgs;
    }

    public int getRelatedArgsLength() {
        return relatedArgsLength;
    }

    public void setRelatedArgsLength(int relatedArgsLength) {
        this.relatedArgsLength = relatedArgsLength;
    }
    
    public HashMap<String, LinkedList<String>> getListRelatedArgs() {
        return listRelatedArgs;
    }

    public void setListRelatedArgs(HashMap<String, LinkedList<String>> listRelatedArgs) {
        this.listRelatedArgs = listRelatedArgs;
    }
   
    public LinkedList<String> getRelatedBox() {
        return relatedBox;
    }

    public void setRelatedBox(LinkedList<String> relatedBox) {
        this.relatedBox = relatedBox;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }   
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSel() {
        return sel;
    }

    
    public List<String> getAgentNames() {
        return agentNames;
    }

    public void setAgentNames(List<String> agentNames) {
        this.agentNames = agentNames;
    }

    public HashMap<String, String> getColourTable() {
        return colourTable;
    }

    public void setColourTable(HashMap<String, String> colourTable) {
        this.colourTable = colourTable;
    }

    public int getFilenameLengnth() {
        return (filename==null) ? 0 : filename.length();
    }

    public void setFilenameLengnth(int filenameLengnth) {
        this.filenameLengnth = filenameLengnth;
    }

    public int getSearchtermLength() {
        return (searchterm==null) ? 0 : searchterm.length();
    }

    public void setSearchtermLength(int searchtermLength) {
        this.searchtermLength = searchtermLength;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public void setSel(String sel) {
        this.sel = sel;
    }
    
    public ArgumentBean()
    {
        colourTable = new HashMap<String, String>();
        colourTable.put("Agents", "yellow");
        colourTable.put("Proteins", "red");
        colourTable.put("Diseases", "green");
        listRelatedArgs = new HashMap<String, LinkedList<String>>();
    }

    public String getSearchterm() {
        return searchterm;
    }

    public void setSearchterm(String searchterm) {
        this.searchterm = searchterm;
    }
    
    public List<JSONObject> getArgumentsList() {
        return argumentsList;
    }

    public void setArgumentsList(List<JSONObject> argumentsList) {
        this.argumentsList = argumentsList;
    }

    public int getArgumentsLength() {
        return argumentsLength;
    }

    public void setArgumentsLength(int argumentsLength) {
        this.argumentsLength = argumentsLength;
    }
    
    private String colorizeArgument(JSONObject jObject, String[] elementType)
    {
        String argument = jObject.getString("ArgumentSentence");
        
        this.paperFormatRDFXML+= "<rdf:Description>\n";
        this.paperFormatRDFXML+="\t<arg:argumentSentence>" + argument + "</arg:argumentSentence>";
        this.paperFormatRDFXML+= "</rdf:Description>\n";

        for(int i=0;i<elementType.length;i++)
        {
            try
            {    
                JSONArray elementArray = jObject.getJSONArray(elementType[i]);
                if(elementArray!=null)
                {
                    Iterator elementIter = elementArray.iterator();

                    while(elementIter.hasNext())
                    {
                        String elementFound = (String)((JSONObject)elementIter.next()).get("name");
                            argument = argument.replaceAll(elementFound, "<span style=\"background-color:" 
                                            + colourTable.get(elementType[i]) + "\">" + elementFound + "</span>");
                    }
                }
            }
            catch(net.sf.json.JSONException jsonex) {}
        }
        
        return argument;
    }    
/*
    private String colorizeArgument(JSONObject jObject, String elementType)
    {
        String argument = jObject.getString("ArgumentSentence");
        JSONArray elementArray = jObject.getJSONArray(elementType);
        Iterator elementIter = elementArray.iterator();
        
        while(elementIter.hasNext())
        {
            String elementFound = (String)((JSONObject)elementIter.next()).get("name");
            //JSONArray wordArray = jObject.getJSONArray(elementFound);
            //Iterator wordIter = wordArray.iterator();
            
            //while(wordIter.hasNext())
            //{
             //   String word = (String)((JSONObject)wordIter.next()).get(elementFound.substring(0, elementFound.length()-1));
                argument = argument.replaceAll(elementFound, "<span style=\"background-color:" 
                                + colourTable.get(elementType) + "\">" + elementFound + "</span>");
            //}
        }
        
        System.out.println(argument);
        return argument;
    }
    */ 
/*
    private String linkItemsFound(JSONArray jArray)
    throws Throwable
    {
        Iterator elementIter = jArray.iterator();
        String htmlString = new String("");
        JSONObject jobject;
        
        while(elementIter.hasNext())
        {
            String elementFound = (String)((JSONObject)elementIter.next()).get("name");
            LinkedBiomedicalDataSpace lbds = new LinkedBiomedicalDataSpace();
            LinkedList<JSONObject> lbdsResults = (LinkedList<JSONObject>)lbds.searchSpecificChemoAgent(elementFound);

            jobject = (JSONObject)lbdsResults.get(0);
            htmlString += "<a href=\"" + jobject.get("sdf") + "\">" + elementFound + "</a> | ";
        }
        
        System.out.println(htmlString);
        return htmlString;
    }    
  */  

    private String linkItemsFound(JSONArray jArray)
    throws Throwable
    {
        Iterator elementIter = jArray.iterator();
        String htmlString = new String("");
        JSONObject jobject;
        
        while(elementIter.hasNext())
        {
            jobject = (JSONObject)elementIter.next();
            String elementFound = (String)jobject.get("name");
            
            htmlString += "<a href=\"" + jobject.get("url") + "\" target=\"_new\">" + elementFound + "</a> | ";
            //relatedBox = relatedBox + "," + jobject.get("url");
            this.paperFormatRDFXML+= "<rdfs:refersTo>" + jobject.get("url") + "</rdfs:refersTo>\n";
            relatedBox.add((String)jobject.get("url"));
        }
        
        System.out.println(htmlString);
        return htmlString;
    }    

    public boolean getRDFDoc()
    throws IOException
    {
        FacesContext context = FacesContext.getCurrentInstance();        
        HttpServletResponse myResponse = (HttpServletResponse)context.getExternalContext().getResponse();
        
        PrintWriter pw = myResponse.getWriter();
        pw.println(this.paperFormatRDFXML);
        pw.flush();
        pw.close();
        
        return true;
    }
    
    public boolean getRelatedArguments()
    throws IOException, Throwable
    {
        /*
        System.out.println("TEst 1");
        FacesContext context = FacesContext.getCurrentInstance();
        System.out.println("TEst 2");
        System.out.println("ARTNO = " + context.getExternalContext().getRequestParameterMap().get("argno"));
        System.out.println("TEst 3");*/
        
        FacesContext context = FacesContext.getCurrentInstance();        
        HttpServletRequest myRequest = (HttpServletRequest)context.getExternalContext().getRequest();
        HttpSession mySession = myRequest.getSession();        
        String argno = (String) myRequest.getParameter("argno");

        ArgLinkedBiomedicalDataSpace lbds = new ArgLinkedBiomedicalDataSpace();
        LinkedList ll = new LinkedList();
        
        System.out.println("\n\n");
        System.out.println(argno);
        System.out.println(listRelatedArgs.get(""+argno));
        this.relatedArgs = (LinkedList<JSONObject>) lbds.searchRelatedArguments(listRelatedArgs.get(""+argno));
        
        context.getExternalContext().redirect("http://192.168.1.202:8080/GranatumWidget/jsfs/relatedArgs.jsf");
        
        return true;
    }

    
    
    public void presentRelatedArguments()
    throws Throwable
    {
        ArgLinkedBiomedicalDataSpace lbds = new ArgLinkedBiomedicalDataSpace();
        LinkedList ll = new LinkedList();
        ll.add("http://bio2rdf.org/ec:3.1.1.7");
        ll.add("http://bio2rdf.org/gl:G12854/cr1");        
        
        this.relatedArgs = (LinkedList<JSONObject>) lbds.searchRelatedArguments(ll);
    }
    
    public boolean getArgumentFromPaper()
    throws IOException, Throwable
    {
        ArgumentDocumentSpace ads = new ArgumentDocumentSpace();
        System.out.println(ads.retrieveArgumentsFromDocument());
        JSONObject res = ads.retrieveArgumentsFromDocument();
        Iterator iter = res.getJSONArray("Argumentation").iterator();
        String argitem;
        chemoArgsList = new LinkedList<JSONObject>();
        proteinArgsList = new LinkedList<JSONObject>();
        int i = 0;
        
        this.paperFormatRDFXML = new String("");
        argumentsList = new LinkedList<JSONObject>();

        this.title = new String(((JSONObject)res.getJSONArray("PublicationInfo").get(0)).getString("Title"));
        this.url   = new String(((JSONObject)res.getJSONArray("PublicationInfo").get(0)).getString("URL"));
        this.journal = new String(((JSONObject)res.getJSONArray("PublicationInfo").get(0)).getString("JournalTitle"));
        this.pid = new String(((JSONObject)res.getJSONArray("PublicationInfo").get(0)).getString("pId"));

        this.paperFormatRDFXML = "<rdf:RDF xmlns=\"http://chem.deri.ie/granatum/\"" +
                                "\txmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"" +
                                "\txmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"" +
                                "\txmlns:owl=\"http://www.w3.org/2002/07/owl#\"" +
                                "\txmlns:arg=\"http://hq.ubitech.eu/ArgOntology.owl#\"" +
                                "\txmlns:gr=\"http://chem.deri.ie/granatum/\"" +
                                "\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n";

        this.paperFormatRDFXML+= "<rdf:Description>\n";
        this.paperFormatRDFXML+= "\t<gr:title>" + this.title + "/<gr:title>\n";
        this.paperFormatRDFXML+= "/<rdf:Description>\n";
        
        while(iter.hasNext())
        {
            i+=1;
            this.relatedBox = new LinkedList<String>();            
            JSONObject jsonObject = ((JSONObject)iter.next());
            argumentsList.add(jsonObject);

            argitem = colorizeArgument(jsonObject, new String[]{"Agents", "Proteins"});
            jsonObject.set("ArgumentSentence", argitem);

            try
            { 
                jsonObject.set("Agents", linkItemsFound(jsonObject.getJSONArray("Agents"))); 
                chemoArgsList.add(jsonObject);
            }
            catch(net.sf.json.JSONException jsonexa) {
                System.out.println("---------------- blank");
                jsonObject.set("Agents","");
            }
            try
            { 
                jsonObject.set("Proteins", linkItemsFound(jsonObject.getJSONArray("Proteins")));
                proteinArgsList.add(jsonObject);
            }
            catch(net.sf.json.JSONException jsonexb) {jsonObject.set("Proteins","");}

            //jsonObject.set("RelatedBox", this.relatedBox);
            listRelatedArgs.put(""+i, relatedBox);
            jsonObject.set("argno", ""+i);
            System.out.println("\nArg=" + relatedBox + " " + i);
            
        }
        
        this.paperFormatRDFXML+="</rdf:RDF>";
        return true;
    }
    
    public void keywordSearch()
    throws Throwable
    {
        ArgumentDocumentSpace ads = new ArgumentDocumentSpace();
        Iterator iter = ads.retrieveArgumentsFromLinkedDataSpace(this.searchterm).iterator();
        argumentsList = new LinkedList<JSONObject>();
        
        while(iter.hasNext())
        {
            JSONObject jsonObject = ((JSONObject)iter.next());
            argumentsList.add(jsonObject);
        }
        
        System.out.println(" ++++++ " + argumentsList);        
    }
    
    public static void main(String[] arg)
    {
        ArgumentBean ab = new ArgumentBean();
        /*
        try {
            ab.keywordSearch();
        } catch (IOException ex) {
            Logger.getLogger(ArgumentBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    }
}
