
package services;

public abstract class Service
{
    private String serviceURL = null;

    public String getServiceURL() {
        return serviceURL;
    }

    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }
       
}
