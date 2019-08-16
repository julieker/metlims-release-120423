////////////////////////////////////////////////////
// MyPDFPanel.java
// Written by Jan Wigginton, Jun 17, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.widgets;

import org.apache.wicket.IResourceListener;
import org.apache.wicket.markup.html.panel.Panel;

import edu.umich.brcf.shared.util.io.MyPdfResource;
 
public class MyPdfPanel extends Panel {
 
    private static final long serialVersionUID = 1L;
 
    public MyPdfPanel(String id) {
        super(id);
         
        setRenderBodyOnly(true);
        add(new DocumentInlineFrame("mypdf", (IResourceListener) new MyPdfResource("Tester")));
    }
}