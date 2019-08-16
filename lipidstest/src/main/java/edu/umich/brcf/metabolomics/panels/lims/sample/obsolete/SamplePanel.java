package edu.umich.brcf.metabolomics.panels.lims.sample.obsolete;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.panel.Panel;


public class SamplePanel extends Panel {

	TabbedPanel tabbedPanel; 
		
	public SamplePanel(String id) {
		super(id);
//		add(new SampleSearch("sampleSrch"));
		final List tabs=new ArrayList();
		
		//tabs.add(new AbstractTab(new Model("Sample Search")) {
	 	//	   public Panel getPanel(String panelId)
	 	//	   {
	 	//		  return (new SampleSearchPanel(panelId));
	 	//	   }
		// });
		
		//tabs.add(new AbstractTab(new Model("Sample Entry")) {
	 	//	   public Panel getPanel(String panelId)
	 	//	   {
	 	//		 return (new SampleEntryPanel(panelId));
	 	//	   }
	 	//	 });  
		 add(tabbedPanel=new TabbedPanel("tabs", tabs));
	     tabbedPanel.setSelectedTab(0);
	     tabbedPanel.setOutputMarkupId(true);
	}
}
