////////////////////////////////////////////////////
// NewSearchByExperimentPanel.java
// Written by Jan Wigginton, Aug 6, 2016
// Updated by Julie Keros, October 12, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.panels.experimentsearch.obsolete;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.widgets.AjaxExperimentField;


public abstract class NewSearchForExperimentPanel extends Panel
	{
	@SpringBean 
	private ExperimentService experimentService;

	private String exp, input, searchType = "Id";
	private Boolean showButton = true;
	

	public NewSearchForExperimentPanel(String id)
		{
		super(id);
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new NewSearchForExperimentForm("searchByExperimentForm"));
		}

	
	public final class NewSearchForExperimentForm extends Form 
		{
		final IndicatingAjaxButton selectButton;
		
		public NewSearchForExperimentForm(final String id)
			{
			super(id);
			
			add(selectButton = buildSelectButton("selectButton"));
			
			AjaxExperimentField expNameField;
			add(expNameField = new AjaxExperimentField("exp")
				{
				@Override
				public boolean isVisible()  { return "Name".equals(getSearchType()); }
				});
			expNameField.setOutputMarkupId(true);
			expNameField.add(buildFormSubmitBehavior(false));
			
			DropDownChoice<String> expIdDrop;
			add(expIdDrop = (DropDownChoice<String>) buildDropdown("expDrop").setOutputMarkupId(true));
			expIdDrop.add(buildFormSubmitBehavior(true));
			}
		
		
		private DropDownChoice buildDropdown(String id)
			{
			LoadableDetachableModel <List<String>> expListModel = new LoadableDetachableModel<List<String>>() 
				{
				@Override
				protected List<String> load() { return experimentService.expIdsByInceptionDate();  }
				};
				
			return new DropDownChoice<String>(id, new PropertyModel<String>(this, "exp"), expListModel)
				{
				@Override
				public boolean isVisible()  { return "Id".equals(getSearchType()); }
				};
			}
		
		
		
		private IndicatingAjaxButton buildSelectButton(String id)
			{
			return new IndicatingAjaxButton(id)
				{
				@Override
				public boolean isVisible() { return showButton; }
				
				@Override
				protected void onSubmit(AjaxRequestTarget target) 
					{
					System.out.println("Form submit button");
					try
						{
						if(experimentService.isValidExperimentSearch(exp))
							NewSearchForExperimentPanel.this.onSelect(exp, target);
						else
							doError(target);
						}
					catch (Exception e) { doError(target); }
					}
				
				@Override
				protected void onError(AjaxRequestTarget target, Form<?> arg1) 
					{
					doError(target);
					}
				};
			}
		
		
		private AjaxFormSubmitBehavior buildFormSubmitBehavior(final boolean isDrop)
		 	{
		 	return new AjaxFormSubmitBehavior(this, "change") 
				{
				protected void onSubmit(AjaxRequestTarget target) 
					{
					System.out.println("Form submit behaviour");
				
					input = isDrop ? exp : ((AutoCompleteTextField)(this.getComponent())).getInput();
					
					if (experimentService.isValidExperimentSearch(input))
						{
						String expId = isDrop ? exp : StringParser.parseId(input);
						setExp(expId);
						target.add(this.getComponent());
							 
						if ( experimentService.isNameLoad(input) && !isDrop && "Name".equals(searchType))
							{
							Experiment expTmp = experimentService.loadByName(input);
					        onSelect(expTmp.getExpID(), target);
						   	}
						else
						     {
						     onSelect(expId, target);
						     }
						
						 if (selectButton != null)
					           target.add(selectButton);
						}
					else
						{	 
						System.out.println("Invalid experiment search" + exp);
					    doError(target);
						}    
					}
				};
		 	}
					
					
		
		public String getSearchType() { return searchType; }
		public void setSearchType(String st) { searchType = st; }
		
		public String getExp() { return exp; }
		public void setExp(String e) { exp = e; }
		}

	
	private void doError(AjaxRequestTarget target)
		{
		String output = (input == null ? "" : input);
		NewSearchForExperimentPanel.this.error("Can't find experiment (" + output + "). Please verify that the search id (or name) is valid.");
		target.add(NewSearchForExperimentPanel.this.get("feedback"));
		}
	
	
	public void setExp(String input) 
		{ 
		this.exp=(input == null ? null : input.trim()); 
		}
	
	public String getExp() 
		{ 
		return exp; 
		}
	
	public Boolean getShowButton()
		{
		return showButton;
		}

	public String getSearchType()
		{
		return searchType;
		}

	public void setSearchType(String searchType)
		{
		this.searchType = searchType;
		}
	
	public void setShowButton(Boolean showButton)
		{
		this.showButton = showButton;
		}

	
	protected abstract void onSelect(String exp, AjaxRequestTarget target);
	}

	
