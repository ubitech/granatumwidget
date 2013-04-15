package services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@ManagedBean(name = "SemanticNetworkAnalysisBean")
@RequestScoped
public class SemanticNetworkAnalysisBean
implements Serializable
{
    private JSONArray degreesOfSeparationList;
    private int degreesOfSeparationLength;
    private LinkedList<JSONObject> keyplayersList;
    private int keyplayersLength;
    private LinkedList<JSONObject> clusterList;
    private int clusterLength;
    private boolean errorOccured;
    private String errorMessage;

    public boolean isErrorOccured() {
        return errorOccured;
    }

    public void setErrorOccured(boolean errorOccured) {
        this.errorOccured = errorOccured;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

        
    @ManagedProperty(value = "#{param.userID}")
    private String userID;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
    
    public LinkedList<JSONObject> getClusterList() {
        return clusterList;
    }

    public void setClusterList(LinkedList<JSONObject> clusterList) {
        this.clusterList = clusterList;
    }

    public int getClusterLength() {
        return clusterLength;
    }

    public void setClusterLength(int clusterLength) {
        this.clusterLength = clusterLength;
    }
    
    public LinkedList<JSONObject> getKeyplayersList() {
        return keyplayersList;
    }

    public void setKeyplayersList(LinkedList<JSONObject> keyplayersList) {
        this.keyplayersList = keyplayersList;
    }

    public int getKeyplayersLength() {
        return keyplayersLength;
    }

    public void setKeyplayersLength(int keyplayersLength) {
        this.keyplayersLength = keyplayersLength;
    }
        
    public JSONArray getDegreesOfSeparationList() {
        return degreesOfSeparationList;
    }

    public void setDegreesOfSeparationList(JSONArray degreesOfSeparationList) {
        this.degreesOfSeparationList = degreesOfSeparationList;
    }
 
    public int getDegreesOfSeparationLength() {
        return degreesOfSeparationLength;
    }

    public void setDegreesOfSeparationLength(int degreesOfSeparationLength) {
        this.degreesOfSeparationLength = degreesOfSeparationLength;
    }
    
    public String getDegreesOfSeparation()
    throws Throwable
    {
        
        JSONObject jobject = new JSONObject(getJSONStringURL("http://gaia.cybion.eu:8080/wp5-services/rest/visualize/path/10028/931"));

        this.degreesOfSeparationList = jobject.getJSONArray("object");
        
        System.out.println(((JSONObject)this.degreesOfSeparationList.get(0)).get("name"));
        
        return new String("Degrees of Separation");
    }

    public void getKeyPlayers()
    throws Throwable
    {
        Object[] oarray;
        System.out.println(" ----- " + this.userID);
        JSONObject jobject = new JSONObject(getJSONStringURL("http://gaia.cybion.eu:8080/wp5-services/rest/keyplayers/" + this.userID));
        
        if(jobject.getString("status").equals("NOK"))
        {
            this.errorOccured = true;
            this.errorMessage = new String("Cannot incorporate data from service now.");
            Logger.getLogger(SemanticNetworkAnalysisBean.class.getName()).log(Level.SEVERE, jobject.getString("message"));            
        }
        else
        {
            this.errorOccured = false;
            this.keyplayersList = new LinkedList<JSONObject>();
            oarray = jobject.getJSONArray("object").toArray();
        
            for(int i=0;i<oarray.length;i++)
            {
                JSONObject keyplayerObject = (JSONObject)oarray[i];
                System.out.println("SOUT:   " + ((JSONObject)keyplayerObject.get("personRank")).get("rank"));
                /*
                if(Integer.parseInt((String)((JSONObject)keyplayerObject.get("personRank")).get("rank"))==0)
                    keyplayerObject.put("prank_symbol", "http://www.granatum.org/pub/static/icons/social/prank_0.png");
                else if(Integer.parseInt((String)((JSONObject)keyplayerObject.get("personRank")).get("rank"))<50)
                    keyplayerObject.put("prank_symbol", "http://www.granatum.org/pub/static/icons/social/prank_1.png");
                else if(Integer.parseInt((String)((JSONObject)keyplayerObject.get("personRank")).get("rank"))>=50  &&
                        Integer.parseInt((String)((JSONObject)keyplayerObject.get("personRank")).get("rank"))<75)
                    keyplayerObject.put("prank_symbol", "http://www.granatum.org/pub/static/icons/social/prank_2.png");
                else if(Integer.parseInt((String)((JSONObject)keyplayerObject.get("personRank")).get("rank"))>=75  &&
                        Integer.parseInt((String)((JSONObject)keyplayerObject.get("personRank")).get("rank"))<100)
                    keyplayerObject.put("prank_symbol", "http://www.granatum.org/pub/static/icons/social/prank_3.png");                
                else if(Integer.parseInt((String)((JSONObject)keyplayerObject.get("personRank")).get("rank"))==100)
                    keyplayerObject.put("prank_symbol", "http://www.granatum.org/pub/static/icons/social/prank_4.png");
                */
                this.keyplayersList.add(keyplayerObject);
            }
            
            System.out.println(" ----- " + this.userID);
        }
    }    

    public void getClusteringService()
    throws Throwable
    {
        Object[] oarray;
        JSONObject jobject = new JSONObject(getJSONStringURL("http://gaia.cybion.eu:8080/wp5-services/rest/clustering/" + this.userID + "/5"));

        if(jobject.getString("status").equals("NOK"))
        {
            this.errorOccured = true;
            this.errorMessage = new String("Cannot incorporate data from service now.");
            Logger.getLogger(SemanticNetworkAnalysisBean.class.getName()).log(Level.SEVERE, jobject.getString("message"));            
        }
        else
        {        

            this.clusterList = new LinkedList<JSONObject>();
            oarray = jobject.getJSONArray("object").toArray();

            for(int i=0;i<oarray.length;i++)
                this.clusterList.add((JSONObject)oarray[i]);

            System.out.println(this.clusterList);
        }
    }    
        
    public String getJSONStringURL(String urlString)
    throws Throwable
    {
	String output;
        String jsonString = new String("");
	URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
 
        if(conn.getResponseCode() == 500)
            return new String("{\"object\": \"User id doesnt exist : 1\", \"message\": \"Cannot incorporate data from service now\",\"status\": \"NOK\"}");
        else if (conn.getResponseCode() != 200) {
            throw new RuntimeException("HTTP error code : "
					+ conn.getResponseCode());
	}
 
	BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
 
	while ((output = br.readLine()) != null)
		jsonString+=output;
 
	conn.disconnect();
 
        return jsonString;
    }
    
    public static void main(String[] args)
    {
        SemanticNetworkAnalysisBean s = new SemanticNetworkAnalysisBean();
        try {
            s.getClusteringService();
        } catch (Throwable ex) {
            Logger.getLogger(SemanticNetworkAnalysisBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
