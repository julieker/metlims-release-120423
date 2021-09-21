////////////////////////////////////////////////////
//WorkflowTrackingMainPanel.java
//Written by Jan Wigginton July 2015
////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.workflow_tracking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

import edu.umich.brcf.metabolomics.panels.lims.compounds.EditAliquot;
import edu.umich.brcf.metabolomics.panels.lims.compounds.InventoryDetailPanel;
import edu.umich.brcf.shared.layers.dto.AliquotDTO;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.panels.utilitypanels.EditDocumentPage;
import edu.umich.brcf.shared.panels.utilitypanels.ExperimentAssaySelectorPanel;
import edu.umich.brcf.shared.panels.utilitypanels.ExperimentOrDateSearchPanel;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.panels.utilitypanels.ReportUploadSelectorPanel;
import edu.umich.brcf.shared.util.io.StringUtils;


public class WorkflowTrackingMainPanel extends Panel
	{
	String clientOrProtocol = "Client Report"; 
	private DropDownChoice<String> clientOrProtocolDrop; 
	public WorkflowTrackingMainPanel(String id)
		{
		super(id);

		ModalWindow modal = ModalCreator.createScalingModalWindow("modal1", 0.4, 0.3,  (MedWorksSession) getSession());
		add(modal);
		add(clientOrProtocolDrop = buildClientOrProtocolDrop("clientOrProtocolDrop","clientOrProtocol"));
		add(new FeedbackPanel("feedback"));

		add(buildAssaySelector("selector", true, null));
		add(buildAssaySelector("selector2", true, null));
		add(buildUploadSelector("selector3", true, modal));
		add(buildReportsDateExpSelector("selector4", false, modal));
		}

	
	private ExperimentOrDateSearchPanel buildReportsDateExpSelector(final String id, final boolean isEnabled, ModalWindow modal1)
		{
		ExperimentOrDateSearchPanel panel = new ExperimentOrDateSearchPanel(id)
			{
			@Override
			public void doSubmit(String searchType, String selectedExperiment, Calendar fromDate, Calendar toDate)
				{
				if (StringUtils.isEmptyOrNull(searchType))
					return;

				Boolean searchByRunDate = (searchType == null ? false : searchType.equals("Run Date"));
				try
					{
					if (searchType.equals("Experiment"))
						setResponsePage(new SubmittedReportsPage("searchResults", (WebPage) this.getPage(), selectedExperiment, false));
					else					
						setResponsePage(new SubmittedReportsPage("searchResults", (WebPage) this.getPage(),fromDate, toDate, false, searchByRunDate, clientOrProtocol.equals("Client Report") ? true : false));					
					} 
				catch (Exception e) {  }
				}
			};

		panel.limitToSearchType("Upload Date");
		return panel;
		}
	

	private ExperimentAssaySelectorPanel buildAssaySelector(final String id, final Boolean isEnabled, final ModalWindow modal1)
		{
		return new ExperimentAssaySelectorPanel(id)
			{
			@Override
			public WebPage getResponsePage(String id2, WebPage backPage, String selectedExperiment, String selectedAssay)
				{
				if (id.equals("selector"))
					return ((WebPage) new SampleAssayTrackingPage("worklistLookup", selectedExperiment, backPage, selectedAssay));

				if (id.equals("selector3"))
					return ((WebPage) new EditDocumentPage(getPage(), selectedExperiment, selectedAssay, modal1, true, false));

				return ((WebPage) new WorklistOverviewPage( "worklistLookup", backPage, selectedExperiment, selectedAssay, false));
				}
			};
		}

	
	private ReportUploadSelectorPanel buildUploadSelector(final String id, final Boolean isEnabled, final ModalWindow modal1)
		{
		return new ReportUploadSelectorPanel(id)
			{
			@Override
			public boolean isEnabled() { return isEnabled; }

			@Override
			public WebPage getResponsePage(String id, WebPage backPage, String selectedExperiment, String assayId) {return null; }
			};
		}
	
	// issue 176	
	public String getClientOrProtocol ()
		{
		return clientOrProtocol ;
		}

 // issue 176
	public void setClientOrProtocol (String clientOrProtocol)
		{
		this.clientOrProtocol =  clientOrProtocol;
		}
	
	private DropDownChoice buildClientOrProtocolDrop(final String id,  final String propertyName)
		{
		DropDownChoice drp = new DropDownChoice(id, new PropertyModel(this, propertyName), Arrays.asList(new String[] {"Client Report", "Protocol Report" }))
			{
			
			};	
			drp.add(buildStandardFormComponentUpdateBehavior("change", "updatecClientOrProtocol" ));	
		return drp;
		}
	
	private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(String event, final String response)
	{
	 return new AjaxFormComponentUpdatingBehavior(event)
	    {
	    @Override
	    protected void onUpdate(AjaxRequestTarget target)
	    	{
	    	switch (response)
	        	{
	    	    case "updatecClientOrProtocol" :
	    	    	target.add(clientOrProtocolDrop);
	    	    	break;
	        	default : break;
	        	}
	    	}
	    };
	}  
	
	}
