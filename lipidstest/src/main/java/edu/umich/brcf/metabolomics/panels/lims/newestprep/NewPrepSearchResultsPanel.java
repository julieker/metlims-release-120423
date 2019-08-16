package edu.umich.brcf.metabolomics.panels.lims.newestprep;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.panels.lims.preparations.NewNewPrepMethodsPanel;
import edu.umich.brcf.metabolomics.panels.lims.preparations.NewPrepDetailHeader;
import edu.umich.brcf.metabolomics.panels.lims.preparations.NewPreparationDetailPanel;
import edu.umich.brcf.metabolomics.panels.lims.preparations.NewPreparationsListPanel;
import edu.umich.brcf.shared.layers.domain.Preparation;
import edu.umich.brcf.shared.layers.service.SamplePrepService;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.widgets.METWorksPctSizableModal;



public class NewPrepSearchResultsPanel extends Panel 
	{
	@SpringBean
	SamplePrepService samplePrepService;
	
	TabbedPanel tabbedPanel; 

	//NewNewPreparationsSearchPanel pp;
	NewPreparationsListPanel sidePanel;
	NewPrepDetailHeader pdh;

	String preparation= "SP00606";

	
	public NewPrepSearchResultsPanel(String id) 
		{
		super(id);
	///	pp=this;
		setOutputMarkupId(true);
        add(new FeedbackPanel("feedback"));
		add(new NewPreparationForm("preparationForm"));
		}
	
	
	public final class NewPreparationForm extends Form 
		{
		public NewPreparationForm(final String id) 
			{
			super(id);
			
			
			METWorksPctSizableModal modal2= new METWorksPctSizableModal("modal2", .35, .2);
	        modal2.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
	        	{
	            public void onClose(AjaxRequestTarget target)
	            	{
	            	String date = DateUtils.dateAsString(new Date(), "MM/dd/yyyy");
	            	updatePanels(preparation);
		        //	target.add(pp);
		        	target.add(pdh);
        			target.add(tabbedPanel);
        			target.add(sidePanel);
		        	tabbedPanel.setSelectedTab(tabbedPanel.getSelectedTab());
	            	}
	        	});
	        add(modal2);

	        add(sidePanel = buildSidePanel("sidePanel"));
	        sidePanel.setOutputMarkupId(true);
	        
	     	final List tabs=new ArrayList();
			
			tabs.add(new AbstractTab(new Model("Prep Details")) 
				{
		 		public Panel getPanel(String panelId)
		 		   	{
		 			return (new NewPreparationDetailPanel(panelId, getPreparation()));
		 		   	}
		 		});
			 
			tabs.add(new AbstractTab(new Model("Edit Prep Methods")) 
				{
	 		   	public Panel getPanel(String panelId)
	 		   		{
	 		   		return (new NewNewPrepMethodsPanel(panelId, getPreparation()));
	 		   		}
				});
		   
		    add(tabbedPanel=new TabbedPanel("tabs", tabs));
	        tabbedPanel.setSelectedTab(0);
			tabbedPanel.setOutputMarkupId(true);

			add(pdh=new NewPrepDetailHeader("pdh", new CompoundPropertyModel(null)));
			pdh.setOutputMarkupId(true);
			
		    updatePanels(null); 
			}	

		private NewPreparationsListPanel buildSidePanel(String id)
			{
			return (new NewPreparationsListPanel(id)
		   		{
				@Override
				public boolean isVisible()
					{
					return (sidePanel.getPreps().size() > 1);
					}
				
		   		@Override
		   		protected void onIdClick(String prep, AjaxRequestTarget target) 
		   			{
		   			setPreparation(prep);
	            	updatePanels(preparation);
		       // 	target.add(pp);
		        	target.add(pdh);
        			target.add(tabbedPanel);
        			target.add(sidePanel);
		        	tabbedPanel.setSelectedTab(tabbedPanel.getSelectedTab());
		   			}
		   		});
			}
		}
	
	public void setPreparation(String preparation)
		{
		this.preparation=preparation;
		}
	
	public String getPreparation()
		{
		return "SP00607";
		}
	
	public void updatePanels(String preparation)
		{
		System.out.println("Getting prep" + getPreparation());
		tabbedPanel.setVisible((preparation!=null));
		if(preparation!=null)pdh.setDefaultModel(new CompoundPropertyModel(samplePrepService.loadPreparationByID(getPreparation())));
		pdh.setVisible((preparation!=null));
		}
	
	private Iterator getSamplePrepChoices(String input)
		{
		List<String> choices = new ArrayList();
		for (Preparation prep : samplePrepService.allSamplePreparations()) 
			{
			final String prepTitle = prep.getTitle();
			if (prepTitle.toUpperCase().contains(input.toUpperCase()))
				choices.add(prepTitle + " (" + prep.getPrepID()+")");
			}
		
		return choices.iterator();
		}
	
	
	private String parseValue(String temp)
		{
		if (temp.indexOf("(")>0)
			temp = temp.substring(temp.lastIndexOf("(") + 1, temp.lastIndexOf(")"));
		
		return temp;
		}
	
	private boolean verifyFormat(String format, String input) {
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
	}
	
	
	
	

	protected void processResultList(List <Preparation> list, AjaxRequestTarget target)	
		{
		if (list.size() > 0)
			setPreparation(list.get(0).getPrepID());
		sidePanel.setPrepsByPrepList(list);
		target.add(sidePanel);
		}
	}


