package services;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oauth.OAuthBasicClient;
import org.apache.xerces.impl.dv.util.Base64;

public class Clusterusers
extends HttpServlet
{
    private String consumerKey = "UBITECH - Clusterusers";
    private String consumerSecret = "dbba330d44ff8fa827182343b2a5d30f";
    private OAuthBasicClient oauthClient = null;
    
    public void init()
    {
        oauthClient = new OAuthBasicClient(consumerKey, consumerSecret);        
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        String operation = null;

        
        try
        {
            operation = request.getParameter("op");

            if(operation==null)
            {
                oauthClient.requestTokensOAuth();
                System.out.println("oauth 1 st step " + oauthClient.getOauthToken());
                response.sendRedirect(oauthClient.getOauthAuthTokenURL()
                                    + "&oauth_token=" + oauthClient.getOauthToken() + "&"
                                    + "oauth_callback=" + java.net.URLEncoder.encode(request.getRequestURL().toString()
                                    + "?op=reqAccessOAuth" 
                                    + "&userID=" + request.getParameter("userID")                                               
                                    + "&a1=" + Base64.encode((oauthClient.getOauthToken()).getBytes("UTF-8")) 
                                    + "&a2=" + Base64.encode((oauthClient.getOauthTokenSecret()).getBytes("UTF-8"))));
            }
            else if(operation.equals("reqAccessOAuth"))
            {
                oauthClient.requestAccessOAuth(new String(Base64.decode(request.getParameter("a1"))), new String(Base64.decode(request.getParameter("a2"))));
                if(oauthClient.getOauthTokenAccess()!=null)
                    this.forwardToPage("/jsfs/clusterusers.jsf?" + request.getParameter("userID"), request, response);
            }
        } 
        catch(Throwable t)
        {            
            t.printStackTrace();
            this.forwardToPage("/errorPage.jsp?errormsg=" + t.getMessage(), request, response);
        }
    }

    private void forwardToPage(String url, HttpServletRequest req, HttpServletResponse resp)
    throws IOException, ServletException
    {
        RequestDispatcher dis;
        
        dis = getServletContext().getRequestDispatcher(url);
        dis.forward(req, resp);
        
        return;
    }        
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        processRequest(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        processRequest(request, response);
    }
}
