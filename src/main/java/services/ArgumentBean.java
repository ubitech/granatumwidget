package services;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


@ManagedBean(name = "ArgumentBean")
@SessionScoped
public class ArgumentBean
implements Serializable
{
    private List<JSONObject> argumentsList;
    private List<String> agentNames;
    private int argumentsLength;
    private int filenameLengnth = 0;
    private int searchtermLength;
    private HashMap<String, String> colourTable;
    private String searchterm = null;
    private String filename = null;    
    private String sel = null;

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

    private String colorizeArgument(JSONObject jObject)
    {
        String argument = jObject.getString("ArgumentSentence");
        JSONArray elementArray = jObject.getJSONArray("ElementsFound");        
        Iterator elementIter = elementArray.iterator();
        
        while(elementIter.hasNext())
        {
            String elementFound = (String)((JSONObject)elementIter.next()).get("name");
            JSONArray wordArray = jObject.getJSONArray(elementFound);
            Iterator wordIter = wordArray.iterator();
            
            while(wordIter.hasNext())
            {
                String word = (String)((JSONObject)wordIter.next()).get(elementFound.substring(0, elementFound.length()-1));
                argument = argument.replaceAll(word, "<span style=\"background-color:" 
                                + colourTable.get(elementFound) + "\">" + word + "</span>");
            }
        }
        
        System.out.println(argument);
        return argument;
    }
    
    public void getArgumentFromPaper()
    throws IOException
    {
        ArgumentDocumentSpace ads = new ArgumentDocumentSpace();
        Iterator iter = ads.retrieveArgumentsFromDocument().getJSONArray("Argumentation").iterator();
        Iterator agentIter;
        argumentsList = new LinkedList<JSONObject>();
        agentNames = new LinkedList<String>();
        
        while(iter.hasNext())
        {
            JSONObject jsonObject = ((JSONObject)iter.next());
            argumentsList.add(jsonObject);
            System.out.println(jsonObject.getString("ArgumentSentence"));
            colorizeArgument(jsonObject);
            jsonObject.set("ArgumentSentence", colorizeArgument(jsonObject));


/*            
            agentIter = jsonObject.getJSONArray("Agents").iterator();
            while(agentIter.hasNext()){
                String n = (String)(((JSONObject)agentIter.next()).get("Agent"));
                System.out.println(n);
                agentNames.add(n);
            }
            */
//            System.out.println(jsonObject.getJSONArray("Agents").getJSONObject(0).getString("Agent"));
        }
        
//        System.out.println(" ++++++ " + argumentsList);
    }
    
    public void keywordSearch() throws IOException
    {/*
        LinkedBiomedicalDataSpace d = null;
            
        try
        {
            d = new LinkedBiomedicalDataSpace();
            d.searchSpecificChemoAgent(searchterm);                
            argumentsList = (List<JSONObject>) d.getAssociatedEntities();
            System.out.println(argumentsList);
        }
        catch (Throwable ex)
        {
            java.util.logging.Logger.getLogger(SearchBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        ArgumentDocumentSpace ads = new ArgumentDocumentSpace();
        Iterator iter = ads.retrieveArgumentsFromLinkedDataSpace().getJSONArray("Results").iterator();
        argumentsList = new LinkedList<JSONObject>();
        
        while(iter.hasNext())
        {
            JSONObject jsonObject = ((JSONObject)iter.next());
            argumentsList.add(jsonObject);
//            System.out.println(jsonObject.getJSONArray("Agents").getJSONObject(0).getString("Agent"));
        }
        
        System.out.println(" ++++++ " + argumentsList);        
    }
    
    public static void main(String[] arg)
    {
        ArgumentBean ab = new ArgumentBean();
        try {
            ab.keywordSearch();
        } catch (IOException ex) {
            Logger.getLogger(ArgumentBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
