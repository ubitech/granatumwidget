package services;

import javax.faces.application.Application;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;


@ManagedBean(name = "helloBean")
@RequestScoped
public class helloBean
{
private int numControls;
private HtmlOutputText controlPanel;
private String htmlInput = "to be or <span style=\"color:yellow\"> not </span> to be";

    public String getHtmlInput() {
        return htmlInput;
    }

    public void setHtmlInput(String htmlInput) {
        this.htmlInput = htmlInput;
    }



public int getNumControls()
{
return numControls;
}
public void setNumControls(int numControls)
{
this.numControls = numControls;
}

    public HtmlOutputText getControlPanel() {
        return controlPanel;
    }

    public void setControlPanel(HtmlOutputText controlPanel) {
        this.controlPanel = controlPanel;
    }

public void addControls(ActionEvent actionEvent)
{
    Application application =
    FacesContext.getCurrentInstance().getApplication();
    List children = controlPanel.getChildren();
    children.clear();
    for (int count = 0; count < numControls; count++)
    {
        HtmlOutputText output = (HtmlOutputText)application.createComponent(HtmlOutputText.COMPONENT_TYPE);
        output.setValue(" lala " + count + " blabla");
        output.setStyle("color: blue;background-color:#00ff00;");    
        children.add(output);
    }
}
public String goodbye()
{
return "success";
}
}