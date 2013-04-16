
package services;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oauth.OAuthBasicClient;
import org.apache.xerces.impl.dv.util.Base64;

public class RelatedTopics
extends HttpServlet
{
    private String consumerKey = "UBITECH - Related Topics";
    private String consumerSecret = "ca18ea98e66f505ae6e575bec858d751";
    private OAuthBasicClient oauthClient = null;
    
    public void init()
    {
        
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        String operation = null;
        String search = null;
        
        try
        {
            operation = request.getParameter("op");

            if(operation==null)
            {
                oauthClient = new OAuthBasicClient(consumerKey, consumerSecret);                
                oauthClient.requestTokensOAuth();
                response.sendRedirect(oauthClient.getOauthAuthTokenURL()
                                    + "&oauth_token=" + oauthClient.getOauthToken() + "&"
                                    + "oauth_callback=" + java.net.URLEncoder.encode(request.getRequestURL().toString()
                                    + "?op=reqAccessOAuth" 
                                    //+ "&userID=" + request.getParameter("userID")
                                    + "&a1=" + Base64.encode((oauthClient.getOauthToken()).getBytes("UTF-8")) 
                                    + "&a2=" + Base64.encode((oauthClient.getOauthTokenSecret()).getBytes("UTF-8"))));
            }
            else if(operation.equals("reqAccessOAuth"))
            {
                oauthClient.requestAccessOAuth(new String(Base64.decode(request.getParameter("a1"))), new String(Base64.decode(request.getParameter("a2"))));
                String homeID = new String(oauthClient.getHomeDirectory());
                System.out.println("homeid=" + homeID);
                search = oauthClient.getTopics("5339").replace(' ', '_');
                
                System.out.println(search);
                if(oauthClient.getOauthTokenAccess()!=null)
                    this.forwardToPage("/jsfs/relatedIssues.jsf" + "?homeID=" + homeID + "&search=cancer_" + search, request, response);
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
