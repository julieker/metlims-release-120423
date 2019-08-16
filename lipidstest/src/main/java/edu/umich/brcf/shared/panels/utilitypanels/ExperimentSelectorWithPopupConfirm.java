////////////////////////////////////////////////////
//ExperimentSelectorWithPopupConfirm.java
//Written by Jan Wigginton, Mar 21, 2017
////////////////////////////////////////////////////

package edu.umich.brcf.shared.panels.utilitypanels;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.model.ExperimentListModel;



public abstract class ExperimentSelectorWithPopupConfirm extends AccessLimitedPanel
	{
	@SpringBean
	private ExperimentService experimentService;
	
	DropDownChoice<String> editExperimentDrop;
	String selectedExperiment = null;
	String buttonLabel = "Confirm";
	String confirmMsg;
	
	private Link confirmLink, dummyLink;
	WebMarkupContainer container;
	
	public ExperimentSelectorWithPopupConfirm(String id) 
		{
		super(id);
		add(new FeedbackPanel("feedback"));
		
		ExperimentSelectorWithConfirmForm lde = new ExperimentSelectorWithConfirmForm("experimentSelectorForm");
		lde.setMultiPart(true);
		add(lde);
		}


	public class ExperimentSelectorWithConfirmForm extends Form 
		{	
		public ExperimentSelectorWithConfirmForm(String id)
		{
		super(id);
		
		container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);
		
		container.add(editExperimentDrop = buildEditExperimentDropdown("editExperimentDropdown", "selectedExperiment"));
		container.add(confirmLink = createConfirmLink("confirmLink"));
		container.add(dummyLink = createDummyLink("dummyButton"));
		
		add(container);
		}
	
	
	Link createDummyLink(String id)
		{
		Link lnk = new Link(id) 
			{
			@Override 
			public boolean isEnabled(){ return false; }
			
			@Override
			public boolean isVisible() { 	return selectedExperiment == null || !selectedExperiment.startsWith("EX"); }
			
			@Override
			public void onClick() { } 
			@Override // issue 464
			public MarkupContainer setDefaultModel(IModel model) 
			    {
				// TODO Auto-generated method stub
				return this;
			    }
			};
		
		lnk.setOutputMarkupId(true);
		return lnk;
		}
	
	
	Link createConfirmLink(String id) 
		{
		Link lnk = new Link(id) 
			{
			@Override
			public boolean isVisible()
				{
				return (!(selectedExperiment == null || !selectedExperiment.startsWith("EX"))); 
				}	
		
			@Override
			public void onClick()
				{
				String selectedId = getSelectedExperimentId();
				doSubmit(selectedId);
				setSelectedExperiment(null);
				}
			@Override // issue 464
			public MarkupContainer setDefaultModel(IModel model) 
			    {
				// TODO Auto-generated method stub
				return this;
			    }
			};
		
		confirmMsg = "Are you sure that you would like to remove all samples and metadata for experiment " + selectedExperiment + "?";
		lnk.add(new AttributeModifier("onclick", "return confirm('" + confirmMsg + "');" ));
		lnk.setOutputMarkupId(true);
		return lnk;
		}
	}
	
	
	private DropDownChoice buildEditExperimentDropdown(final String id, String propertyName)
		{
		editExperimentDrop =  new DropDownChoice(id,  new PropertyModel(this, propertyName), 
		new ExperimentListModel("both", experimentService, false, true));
		
		editExperimentDrop.setOutputMarkupId(true);
		editExperimentDrop.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForEditExperimentDrop"));			
		
		return editExperimentDrop;
		}
	
	
	private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(final String event,  final String response)
		{
		return new AjaxFormComponentUpdatingBehavior(event)
			{
			@Override
			protected void onUpdate(AjaxRequestTarget target)
				{
				switch (response)
					{
					case "updateForEditExperimentDrop" :
					
					confirmMsg = "Are you sure that you would like to remove all samples and metadata for experiment " + selectedExperiment + "?";
					confirmLink.add(new AttributeModifier("onclick", "return confirm('" + confirmMsg + "');" ));
					
					target.add(container);
					break;
					}
				}
			};
		}
	
	
	public String getSelectedExperiment()
		{
		return selectedExperiment;
		}
	
	public void setSelectedExperiment(String selectedExperiment)
		{
		this.selectedExperiment = selectedExperiment;
		}
		
	
	public String getSelectedExperimentId()
		{
		return StringParser.parseName(this.getSelectedExperiment());
		}
		
	
	public String getButtonLabel()
		{
		return this.buttonLabel;
		}
	
	
	public void setButtonLabel(String label)
		{
		this.buttonLabel = label;
		}
		
	
	public abstract void doSubmit(String selectedExperiment);
	}
	
