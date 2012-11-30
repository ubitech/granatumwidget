package recommender;

import java.util.HashMap;

public class ClicksScoreboard
{
    private HashMap<String, HashMap<String,Integer>> userScore = null;
    private static ClicksScoreboard o;
    
    public ClicksScoreboard()
    {
        this.userScore = new HashMap<String, HashMap<String, Integer>>();
    }
    
    public static ClicksScoreboard getScoreboard()
    {
        if(o == null)
            o = new ClicksScoreboard();

        return(o);   
    }
    
    public synchronized void addClick(String user, String key)
    {
        HashMap<String,Integer> clicksMap = userScore.get(user);
        
        if(clicksMap == null)
            clicksMap = new HashMap<String, Integer>();
        
        Integer clicks = clicksMap.get(key);
        if(clicks == null)
            clicks = new Integer(0);
        
        clicksMap.put(key, clicks.intValue() + 1);
        userScore.put(user, clicksMap);
    }
    
    public synchronized String getValues()
    {
        return this.userScore.toString();
    }
    
    public static void main(String[] args)
    {
        ClicksScoreboard c = ClicksScoreboard.getScoreboard();
        
        c.addClick("panos", "uri");
        c.addClick("panos", "uri");
        c.addClick("george", "uriA");        
        c.addClick("george", "uri");                
        System.out.println(c.getValues());
    }
}
