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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@ManagedBean(name = "SemanticNetworkAnalysisBean")
@SessionScoped
public class SemanticNetworkAnalysisBean
implements Serializable
{
    private JSONArray degreesOfSeparationList;
    private int degreesOfSeparationLength;

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
    
    public String getJSONStringURL(String urlString)
    throws Throwable
    {
	String output;
        String jsonString = new String("");
	URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
 
        if (conn.getResponseCode() != 200) {
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
            s.getDegreesOfSeparation();
        } catch (Throwable ex) {
            Logger.getLogger(SemanticNetworkAnalysisBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
