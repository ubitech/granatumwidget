
package caching;

import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

public class Cache
{
    private Map<String, LinkedList<String>> cacheMap;

    public Map<String, LinkedList<String>> getCacheMap() {
        return cacheMap;
    }

    public void setCacheMap(Map<String, LinkedList<String>> cacheMap) {
        this.cacheMap = cacheMap;
    }
    
    
}
