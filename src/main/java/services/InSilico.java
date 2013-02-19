package services;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oauth.OAuthBasicClient;
import org.apache.xerces.impl.dv.util.Base64;

public class InSilico
extends HttpServlet
{
    private final String consumerKey    = "Ubitech_Annotator";
    private final String consumerSecret = "67f079a9d0538024956855912406435b";    
    private String homeFolder = null;
    private String csvfile = null;
    
    private OAuthBasicClient oauthClient = null;

    public InSilico()
    {
        super();
    }
    
    public void init()
    {
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
                oauthClient = new OAuthBasicClient(consumerKey, consumerSecret);        
                homeFolder = request.getParameter("homeFolder");
                oauthClient.requestTokensOAuth();
                System.out.println("oauth 1 st step " + oauthClient.getOauthToken());
                response.sendRedirect(oauthClient.getOauthAuthTokenURL()
                                    + "&oauth_token=" + oauthClient.getOauthToken() + "&"
                                    + "oauth_callback=" + java.net.URLEncoder.encode(request.getRequestURL().toString()
                                    + "?op=reqAccessOAuth" 
                                    + "&homeFolder=" + request.getParameter("homeFolder")
                                    + "&a1=" + Base64.encode((oauthClient.getOauthToken()).getBytes("UTF-8")) 
                                    + "&a2=" + Base64.encode((oauthClient.getOauthTokenSecret()).getBytes("UTF-8"))));
            }
            else if(operation.equals("reqAccessOAuth"))
            {
                oauthClient.requestAccessOAuth(new String(Base64.decode(request.getParameter("a1"))), new String(Base64.decode(request.getParameter("a2"))));
                if(oauthClient.getOauthTokenAccess()!=null)
                    this.forwardToPage("/jsfs/insilico.jsf?homeFolder=" + request.getParameter("homeFolder"), request, response);
            }
            else if(operation.equals("storeDoc"))
            {                
                
                String searchid = oauthClient.uploadFileFromURL(homeFolder, "test" , "http://granatum.ubitech.eu/GranatumWidget/InSilico?op=exposeDoc", oauthClient.getOauthTokenAccess(), oauthClient.getOauthTokenSecretAccess());
                this.forwardToPage("http://lisis.cs.ucy.ac.cy:9000/GRANATUMFileLoader_widget.py?action=authorize&searchid=" + searchid, request, response);
            }
            else if(operation.equals("exposeDoc"))
            {
                //String csvfile = request.getParameter("csvfile");
                PrintWriter pw = response.getWriter();
                System.out.println(new String(Base64.decode(csvfile)));
                pw.println(new String(Base64.decode(csvfile)));
                pw.flush();
                pw.close();
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
