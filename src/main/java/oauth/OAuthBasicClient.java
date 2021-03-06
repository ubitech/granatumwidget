package oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpParams;
import org.apache.xerces.impl.dv.util.Base64;
import java.io.StringReader;
import java.util.Random;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import xmlhandlers.XMLRPCResponseHandler;

public class OAuthBasicClient
{
    protected final String oauthRequestTokenURL = "http://www.granatum.org/bscw/bscw.cgi?op=OAuth&portal=1";
    protected final String oauthAuthTokenURL    = "http://www.granatum.org/bscw/bscw.cgi?op=OAuth&portal=1";
    protected final String oauthExecTokenURL    = "http://www.granatum.org/bscw/bscw.cgi";
    protected final String platformURL          = "http://www.granatum.org/bscw/bscw.cgi";    

    protected String consumerKey = null;
    protected String consumerSecret= null;    
    protected Object[] params;
    protected int oauthTimestamp = 1342167429;
    protected int oauthTimedelta = 1;
    protected String oauthTokenSecret = null;
    protected String oauthToken = null;
    protected String oauthTokenSecretAccess = null;
    protected String oauthTokenAccess = null;
    private long millisecond2second = 1000L;
    
    public OAuthBasicClient(String consumerKey, String consumerSecret)
    {
        this.consumerKey = new String(consumerKey);
        this.consumerSecret = new String(consumerSecret);
    }
    
    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
   
    public String getOauthTokenAccess() {
        return oauthTokenAccess;
    }

    public String getOauthTokenSecretAccess() {
        return oauthTokenSecretAccess;
    }
    
    public String getOauthAuthTokenURL() {
        return oauthAuthTokenURL;
    }

    public String getOauthRequestTokenURL() {
        return oauthRequestTokenURL;
    }
   
    public int getOauthTimedelta() {
        return oauthTimedelta;
    }

    public void setOauthTimedelta(int oauthTimedelta) {
        this.oauthTimedelta = oauthTimedelta;
    }

    public int getOauthTimestamp() {
        return oauthTimestamp;
    }

    public void setOauthTimestamp(int oauthTimestamp) {
        this.oauthTimestamp = oauthTimestamp;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    public String getOauthTokenSecret() {
        return oauthTokenSecret;
    }

    public void setOauthTokenSecret(String oauthTokenSecret) {
        this.oauthTokenSecret = oauthTokenSecret;
    }

    
    public String getPlatformURL()
    {
        return new String(this.platformURL);
    }
    
    public int retrieveObjectID(String source)
    {
        return 0;
    }

    public void requestTokensOAuth()
    {
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(oauthRequestTokenURL);
        int statusCode = 0;
        
        while(statusCode!=200)
        {
                System.out.println("Status code=" + statusCode);
                try {
                  Random rand = new Random();
                  oauthTimestamp = (int) (System.currentTimeMillis() / millisecond2second);
                    
                  method.setRequestHeader("Authorization", "OAuth oauth_signature=\"" + consumerSecret + "%26\",oauth_consumer_key=\"" + consumerKey + "\",oauth_signature_method=\"PLAINTEXT\",oauth_nonce=\"" + rand.nextInt() + "\",oauth_timestamp=" + oauthTimestamp);                
//                  method.setRequestHeader("Authorization", "OAuth oauth_nonce=\"" + rand.nextInt() + "\",oauth_consumer_key=\"" + consumerKey + "\",oauth_signature_method=\"PLAINTEXT\",oauth_token=\"" + accessTokenSecret + "\",oauth_signature=\"" + consumerSecret + "%26"+ accessToken + "\",oauth_timestamp=" + oauthTimestamp );
                  
                  System.out.println("AUTH= " + "OAuth oauth_signature=\"" + consumerSecret + "%26\",oauth_consumer_key=\"" + consumerKey + "\",oauth_signature_method=\"PLAINTEXT\",oauth_nonce=\"2376346\",oauth_timestamp=" + oauthTimestamp);
                  statusCode = client.executeMethod(method);
                  byte[] responseBody = method.getResponseBody();
                  String reqString = new String(responseBody);

                  if (statusCode == 200)
                  {
                        oauthTokenSecret  = new String(reqString.split("&")[0].split("=")[1]);
                        oauthToken        = new String(reqString.split("&")[1].split("=")[1]);
                        System.out.println(oauthToken + " " + oauthTokenSecret);          
                  }
                  else if(statusCode == 401)
                  {
                        oauthTimestamp = Integer.parseInt((reqString.split(":")[0].split("=")[1]).substring(1, reqString.split(":")[0].split("=")[1].length()-1));
                        oauthTimedelta = Integer.parseInt(reqString.split(":")[1].trim().split(" ")[1]);
                        oauthTimestamp = Math.abs(oauthTimedelta) + oauthTimestamp + 1;
                        System.out.println("oauthTimestamp  " + oauthTimestamp);
                  }
                } 
                catch (HttpException e)
                {
                    System.err.println("Fatal protocol violation: " + e.getMessage());
                    e.printStackTrace();
                } 
                catch (IOException e) 
                {
                    System.err.println("Fatal transport error: " + e.getMessage());
                    e.printStackTrace();
                } 
                finally 
                {
                  // Release the connection.
                  method.releaseConnection();
                }
        }        
    }

    public void requestAccessOAuth(String oauthToken, String oauthTokenSecret)
    {
        HttpClient client = new HttpClient();
        GetMethod method  = new GetMethod(oauthRequestTokenURL);
        int statusCode = 0;
        
        while(statusCode!=200)
        {
                try
                {
                  method.setRequestHeader("Authorization", "OAuth oauth_signature=\"" + consumerSecret + "%26" + oauthTokenSecret + "\", oauth_consumer_key=\"" + consumerKey + "\",oauth_signature_method=\"PLAINTEXT\",oauth_nonce=\"2376347\",oauth_timestamp=" + oauthTimestamp + ",oauth_token=" + oauthToken);
                  System.out.println("OAuth oauth_signature=\"" + consumerSecret + "%26" + oauthTokenSecret + "\", oauth_consumer_key=\"" + consumerKey + "\",oauth_signature_method=\"PLAINTEXT\",oauth_nonce=\"2376347\",oauth_timestamp=" + oauthTimestamp + ",oauth_token=" + oauthToken);
                  statusCode = client.executeMethod(method);
                  byte[] responseBody = method.getResponseBody();
                  String reqString = new String(responseBody);
                  System.out.println("response = " + reqString);

                  if (statusCode == 200)
                  {
                        oauthTokenSecretAccess  = new String(reqString.split("&")[1].split("=")[1]);
                        oauthTokenAccess        = new String(reqString.split("&")[0].split("=")[1]);
                        System.out.println("oauthTokenAccess=" + oauthTokenAccess + "  oauthTokenSecretAccess=" + oauthTokenSecretAccess);
                  }
                  else if(statusCode == 401)
                  {
                        //System.out.println("reqString = " + reqString.split("\n")[1]);
                        System.out.println("respString=" + reqString + "oauthTimestamp=" + oauthTimestamp);
                        oauthTimedelta = Integer.parseInt(reqString.split("\n")[1].split("\\+")[1].split(":")[0]);
                        
                        /*
                        oauthTimestamp = Integer.parseInt((reqString.split(":")[0].split("=")[1]).substring(1, reqString.split(":")[0].split("=")[1].length()-1));
                        oauthTimedelta = Integer.parseInt(reqString.split(":")[1].trim().split(" ")[1]);
                        */ 
                        oauthTimestamp = Math.abs(oauthTimedelta) + oauthTimestamp + 1;
                        System.out.println(" -requestAccessOAuth");
                        
                  }
                } 
                catch (HttpException e)
                {
                    System.err.println("Fatal protocol violation: " + e.getMessage());
                    e.printStackTrace();
                } 
                catch (IOException e) 
                {
                    System.err.println("Fatal transport error: " + e.getMessage());
                    e.printStackTrace();
                } 
                finally 
                {
                  // Release the connection.
                  method.releaseConnection();
                }
        }        
    }
        
    public String uploadFileFromURL(String objectID, String name, String url, String accessToken, String accessTokenSecret)
    {
        int statusCode = 0;
        String reqString = null;
        String respString = null;
                
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(oauthExecTokenURL);

        while(statusCode!=200 && statusCode != 500)
        {
            try
            {
              Random rand = new Random();
              oauthTimestamp = (int) (System.currentTimeMillis() / millisecond2second);
              method.setRequestHeader("Content-Type", "text/xml");                
              method.setRequestHeader("User-Agent", "Apache XML RPC 3.1.3 (Jakarta Commons httpclient Transport)");
              method.setRequestHeader("Authorization", "OAuth oauth_nonce=\"" + rand.nextInt() + "\",oauth_consumer_key=\"" + consumerKey + "\",oauth_signature_method=\"PLAINTEXT\",oauth_token=\"" + accessTokenSecret + "\",oauth_signature=\"" + consumerSecret + "%26"+ accessToken + "\",oauth_timestamp=" + oauthTimestamp );
              System.out.println("OAuth oauth_nonce=\"1705890416\",oauth_consumer_key=\"" + consumerKey + "\",oauth_signature_method=\"PLAINTEXT\",oauth_token=\"" + accessTokenSecret + "\",oauth_signature=\"" + consumerSecret + "%26"+ accessToken + "\",oauth_timestamp=" + oauthTimestamp );

              reqString = new String("<?xml version='1.0' encoding='UTF-8'?>\n<methodCall>\n<methodName>uploadurl</methodName>\n"
                      + "<params>\n"
                      + "<param>\n<value>" + objectID + "</value>\n</param>\n"
                      + "<param>\n<value>" + name + "</value>\n</param>\n"
                      + "<param>\n<value>" + url + "</value>\n</param>\n"
                      + "</params>\n" 
                      + "</methodCall>");
              
              System.out.println("respString=" + reqString);
              method.setRequestEntity(new StringRequestEntity(reqString,"text/xml","UTF-8"));
              statusCode = client.executeMethod(method);
              System.out.println(" ------------- " + statusCode);
              byte[] responseBody = method.getResponseBody();
              respString = new String(responseBody);

              if (statusCode == 200)
              {
                    System.out.println("respString=" + respString);
              }
              else if(statusCode == 401)
              {
                  /*
                    System.out.println("respString=" + respString);
                    oauthTimestamp = Integer.parseInt((reqString.split(":")[0].split("=")[1]).substring(1, reqString.split(":")[0].split("=")[1].length()-1));
                    oauthTimedelta = Integer.parseInt(reqString.split(":")[1].trim().split(" ")[1]);
                    oauthTimestamp = Math.abs(oauthTimedelta) + oauthTimestamp + 1;
                    */
                        System.out.println("respString=" + reqString + "oauthTimestamp=" + oauthTimestamp);
                        oauthTimedelta = Integer.parseInt(reqString.split("\n")[1].split("\\+")[1].split(":")[0]);
                        
                        /*
                        oauthTimestamp = Integer.parseInt((reqString.split(":")[0].split("=")[1]).substring(1, reqString.split(":")[0].split("=")[1].length()-1));
                        oauthTimedelta = Integer.parseInt(reqString.split(":")[1].trim().split(" ")[1]);
                        */ 
                        oauthTimestamp = Math.abs(oauthTimedelta) + oauthTimestamp + 1;
              }
            }
            catch (HttpException e)
            {
                System.err.println("Fatal protocol violation: " + e.getMessage());
                e.printStackTrace();
            } 
        
            catch (IOException e)
            {
                System.err.println("Fatal transport error: " + e.getMessage());
                e.printStackTrace();
            } 
            finally
            {
                method.releaseConnection();
            }
        }
                
        return new String(getValueFromXMLResponse(respString, 0));
        
    }

    public String getHomeDirectory()
    {
        int statusCode = 0;
        String reqString = null;
        String respString = null;
                
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(oauthExecTokenURL);

        while(statusCode!=200 && statusCode != 500)
        {
            try
            {
              Random rand = new Random();
              oauthTimestamp = (int) (System.currentTimeMillis() / millisecond2second);
                
              method.setRequestHeader("Content-Type", "text/xml");                
              method.setRequestHeader("User-Agent", "Apache XML RPC 3.1.3 (Jakarta Commons httpclient Transport)");
              method.setRequestHeader("Authorization", "OAuth oauth_nonce=\"" + rand.nextInt() + "\",oauth_consumer_key=\""+ "\", oauth_consumer_key=\"" + consumerKey + "\",oauth_signature_method=\"PLAINTEXT\",oauth_token=\"" + oauthTokenSecretAccess + "\",oauth_signature=\"" + consumerSecret + "%26" +  oauthTokenAccess + "\",oauth_timestamp=" + oauthTimestamp);
              //System.out.println("OAuth oauth_nonce=\"1705890416\",oauth_consumer_key=\"Ubitech_Annotator\",oauth_signature_method=\"PLAINTEXT\",oauth_token=\"" + oauthTokenSecretAccess + "\",oauth_signature=\"" + consumerSecret + "%26"+  oauthTokenAccess + "\",oauth_timestamp=" + oauthTimestamp);

              reqString = new String("<?xml version='1.0' encoding='UTF-8'?>\n<methodCall>\n<methodName>get_attributes</methodName>\n"
                      + "<params/>\n" 
                      + "</methodCall>");
              
              method.setRequestEntity(new StringRequestEntity(reqString,"text/xml","UTF-8"));
              statusCode = client.executeMethod(method);
              byte[] responseBody = method.getResponseBody();
              respString = new String(responseBody);

              if (statusCode == 200)
              {
                    System.out.println("respString=" + respString);
              }
              else if(statusCode == 401)
              {
                    System.out.println("respString=" + respString);
                    oauthTimestamp = Integer.parseInt((reqString.split(":")[0].split("=")[1]).substring(1, reqString.split(":")[0].split("=")[1].length()-1));
                    oauthTimedelta = Integer.parseInt(reqString.split(":")[1].trim().split(" ")[1]);
                    oauthTimestamp = Math.abs(oauthTimedelta) + oauthTimestamp + 1;
              }
            }
            catch (HttpException e)
            {
                System.err.println("Fatal protocol violation: " + e.getMessage());
                e.printStackTrace();
            } 
        
            catch (IOException e)
            {
                System.err.println("Fatal transport error: " + e.getMessage());
                e.printStackTrace();
            } 
            finally
            {
                method.releaseConnection();
            }
        }
                
        return new String(getValueFromXMLResponse(respString, 0));
    }
    

    public String getTopics(String id)
    {
        int statusCode = 0;
        String reqString = null;
        String respString = null;
                
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(oauthExecTokenURL);

        while(statusCode!=200 && statusCode != 500)
        {
            try
            {
              method.setRequestHeader("Content-Type", "text/xml");                
              method.setRequestHeader("User-Agent", "Apache XML RPC 3.1.3 (Jakarta Commons httpclient Transport)");
              method.setRequestHeader("Authorization", "OAuth oauth_nonce=\"1705890416\",oauth_consumer_key=\""+ "\", oauth_consumer_key=\"" + consumerKey + "\",oauth_signature_method=\"PLAINTEXT\",oauth_token=\"" + oauthTokenSecretAccess + "\",oauth_signature=\"" + consumerSecret + "%26" +  oauthTokenAccess + "\",oauth_timestamp=" + oauthTimestamp);
              //System.out.println("OAuth oauth_nonce=\"1705890416\",oauth_consumer_key=\"Ubitech_Annotator\",oauth_signature_method=\"PLAINTEXT\",oauth_token=\"" + oauthTokenSecretAccess + "\",oauth_signature=\"" + consumerSecret + "%26"+  oauthTokenAccess + "\",oauth_timestamp=" + oauthTimestamp);

              reqString = new String("<?xml version='1.0' encoding='UTF-8'?>\n<methodCall>\n<methodName>get_attributes</methodName>\n"
                      + "<params>\n"
                      + "<param>\n<value>" + id + "</value>\n</param>\n"
                      + "<param>\n<value><array><data><value>bscw:keywords</value></data></array></value>\n</param>\n"
                      + "</params>\n" 
                      + "</methodCall>");

              
              method.setRequestEntity(new StringRequestEntity(reqString,"text/xml","UTF-8"));
              statusCode = client.executeMethod(method);
              byte[] responseBody = method.getResponseBody();
              respString = new String(responseBody);

              if (statusCode == 200)
              {
                    System.out.println("respString=" + respString);
              }
              else if(statusCode == 401)
              {
                    System.out.println("respString=" + respString);
                    oauthTimestamp = Integer.parseInt((reqString.split(":")[0].split("=")[1]).substring(1, reqString.split(":")[0].split("=")[1].length()-1));
                    oauthTimedelta = Integer.parseInt(reqString.split(":")[1].trim().split(" ")[1]);
                    oauthTimestamp = Math.abs(oauthTimedelta) + oauthTimestamp + 1;
              }
            }
            catch (HttpException e)
            {
                System.err.println("Fatal protocol violation: " + e.getMessage());
                e.printStackTrace();
            } 
        
            catch (IOException e)
            {
                System.err.println("Fatal transport error: " + e.getMessage());
                e.printStackTrace();
            } 
            finally
            {
                method.releaseConnection();
            }
        }
                
        return new String(getValueFromXMLResponse(respString, 0));
    }
    
    
    public String getValueFromXMLResponse(String xmlData, int index)
    {
        SAXParserFactory factory;
        SAXParser saxParser;
        XMLRPCResponseHandler handler = null;
        factory = SAXParserFactory.newInstance();
        
        try
        {
            saxParser = factory.newSAXParser();
            handler = new XMLRPCResponseHandler();
            saxParser.parse(new InputSource(new StringReader(xmlData)), handler);
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }     
        
        return ((handler.getValues().size()<=index)? new String("") : handler.getValues().get(index));
    }
    
    public static void main(String[] args)
    {
        String reqString = new String("OAuth Error\n oauth_token: timedelta +94:");
        System.out.println(reqString);
        System.out.println("reqString = " + reqString.split("\n")[1].split("\\+")[1].split(":")[0]);
        /*
        try {
            OAuthCBasiclient c = new OAuthCBasiclient();
            System.out.println(c.retrieveSIOCfromPlatform("13638", "4c3015550d243d5a6bfa2545b473d570", "fa33d7934013ecb9155712e7d0d29456%3a5386"));
        } catch (MalformedURLException ex) {
            Logger.getLogger(OAuthCBasiclient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OAuthCBasiclient.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
}
