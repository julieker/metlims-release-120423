package edu.umich.brcf.metabolomics.panels.lims.preparations;

import org.apache.wicket.markup.html.panel.Panel;



public class NewPreparationsPanel extends Panel
	{
	//TabbedPanel tabbedPanel; 
	NewNewPreparationsSearchPanel psp=new NewNewPreparationsSearchPanel("prepSearchPanel");
	
	public NewPreparationsPanel(String id) {
		super(id);
		
		add(psp);
		psp.setOutputMarkupId(true);
		
		//final List tabs=new ArrayList();
		 //tabs.add(new AbstractTab(new Model("Prep List")) {
	 	//	   public Panel getPanel(String panelId)
	 	//	   		{
	 	//		   	return (new NewPreparationsListPanel(panelId)
	 	//		 	{

		//			@Override
		//			protected void onIdClick(String prep, AjaxRequestTarget target) 
		//				{
		//				tabbedPanel.setSelectedTab(1);
		//				target.add(tabbedPanel);
		//				psp.setPreparation(prep);
		//				psp.updatePanels(prep);
		//				target.add(psp.pdh);
		//				target.add(psp.tabbedPanel);
		//				psp.tabbedPanel.setSelectedTab(0);
		//				target.add(psp);
		//				}
	 	//		 	});
	 	//	   }
	 	//	 });
		 //tabs.add(new AbstractTab(new Model("Prep Search")) {
	 	//	   public Panel getPanel(String panelId)
	 	//	   {
	 	//		 return (psp=new NewNewPreparationsSearchPanel(panelId));
	 	//	   }
 		// });
		 //add(tabbedPanel=new TabbedPanel("tabs", tabs));
	     //tabbedPanel.setSelectedTab(0);
	    // tabbedPanel.setOutputMarkupId(true);
			
	}

}
