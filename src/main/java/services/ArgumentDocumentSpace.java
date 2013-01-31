package services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.io.IOUtils;

public class ArgumentDocumentSpace
{
    public JSONObject retrieveArgumentsFromDocument()
    throws IOException
    {
        InputStream is = new FileInputStream("/home/ubiadmin/temp/AnnotationWebAppTmp/jsonOutput.txt");
        //InputStream is = new FileInputStream("C:\\Users\\user\\Desktop\\jsonAgntPlProteins2.txt");        
        String jsonTxt = IOUtils.toString(is);        
        JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonTxt);

        return(json);
    }

    public Collection retrieveArgumentsFromLinkedDataSpace(String searchterm)
    throws Throwable
    {
       /*
        InputStream is = new FileInputStream("C:\\Users\\user\\Desktop\\sampleJsn2.txt");
        String jsonTxt = IOUtils.toString(is);        
        JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonTxt);
        return(json);
        */
        LinkedBiomedicalDataSpace lbds = new LinkedBiomedicalDataSpace();
        return(lbds.searchArguments(searchterm));
        
    }    
    
    public static void main(String[] args)
    {
        ArgumentDocumentSpace a = new ArgumentDocumentSpace();
        try
        {
            System.out.println(a.retrieveArgumentsFromLinkedDataSpace("ca"));
        }   
        catch(Throwable t)
        {
            t.printStackTrace();
        }
    }
}
