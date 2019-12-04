// Revisited : October 2016 (JW)
package edu.umich.brcf.metabolomics.panels.lims.compounds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.basic.Label;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.validator.StringValidator;
import org.springframework.dao.EmptyResultDataAccessException;

import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.domain.CompoundName;
import edu.umich.brcf.metabolomics.layers.service.CompoundNameService;
import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.metabolomics.layers.service.InventoryService;
import edu.umich.brcf.shared.layers.domain.Inventory;


public class CompoundDetail  extends Panel{
	
	@SpringBean
	CompoundNameService compoundNameService;
	
	@SpringBean
	CompoundService compoundService;
	
	@SpringBean
	InventoryService invService;
	
	CompoundDetail prntPanel;
	TabbedPanel tabbedPanel;                        
	EmptyPanel emptyPanel;
	Link invSrch;
	
	public CompoundDetail(String id)
		{
		super(id);
		prntPanel=this;
		setOutputMarkupId(true);
		add(new FeedbackPanel("feedback"));
		add(new CompoundDetailForm("compoundDetailForm"));
		}

	public final class CompoundDetailForm extends Form 
		{
		public CompoundDetailForm(final String id) 
			{
			super(id);
			final List tabs=new ArrayList();
            setCompound(compoundService.loadCompoundById("C00794"));
			
            tabs.add(new AbstractTab(new Model("Detail")) 
            	{
            	public Panel getPanel(String panelId)
            		{
            		return new CompoundDetailPanel(panelId, getCompound().getCid(), true);
            		}
            	});
			                                
			 tabs.add(new AbstractTab(new Model("Inventory")) 
				 {
				 public Panel getPanel(String panelId)
					 {
					 return new InventoryDetailPanel(panelId, getCompound());
					 }
				 });
			 
			 add(tabbedPanel=new TabbedPanel("tabs", tabs));
			 tabbedPanel.setSelectedTab(0);
			 tabbedPanel.setOutputMarkupId(true);
			 
			 final String cidFormat =   "(C)\\d{1}|(C)\\d{2}|(C)\\d{3}|(C)\\d{4}|(C)\\d{5}|(CA)\\d{1}|(CA)\\d{2}|(CA)\\d{3}|(CA)\\d{4}";
			 final String nvIdFormat =  "(NV)\\d{1}|(NV)\\d {2}|(NV)\\d{3}|(NV)\\d{4}|(NV)\\d{5}";

			 final AutoCompleteSettings settings = new AutoCompleteSettings();
			 settings.setUseSmartPositioning(true);
			 settings.setPreselect(true);
			
			 final AutoCompleteTextField field = new AutoCompleteTextField("cname", new Model(""), settings) 
				{
				@Override
				protected Iterator getChoices(String input) 
					{
					if (Strings.isEmpty(input)) 
						return Collections.EMPTY_LIST.iterator();
					
					input=input.replaceAll("'", "''");
					List<String> choices = new ArrayList<String>(); 
					String name="";
					try
						{
						if (verifyFormat(cidFormat,input.toUpperCase()))
							for (String cid : compoundService.getMatchingCids(input.toUpperCase())) 
								choices.add(cid);
						else if (verifyFormat(nvIdFormat,input.toUpperCase()))
							for (String invId : invService.getMatchingInvIds(input.toUpperCase())) 
								choices.add(invId);
						else
							{
							for (CompoundName cmpName : compoundNameService.getMatchingNames(input)) 
								{
								name = cmpName.getName();
								input=input.replaceAll("''", "'");
								if (name.toUpperCase().contains(input.toUpperCase()))
									choices.add(name);
								}
							}
						}
					catch(IllegalStateException ie) { System.out.println("Name is "+name); }
				
					return choices.iterator(); 
					}
				};
				
			add(field.add(StringValidator.maximumLength(300)));
		
			
			final Label label = new Label("hiddenvalue", field.getModel());
			label.setVisible(false);
			label.setOutputMarkupId(true);
			add(label);
			
			field.add(new AjaxFormSubmitBehavior(this, "change") 
				{
				protected void onSubmit(AjaxRequestTarget target) 
					{
					String input = field.getInput();
					ValidateInput(input, target, label);
					String cFormat="(C)\\d{5}|(CA)\\d{4}", iFormat="(NV)\\d{5}";
//					if (verifyFormat(cFormat,input.toUpperCase())){
//						setCompound(cmpdService.loadCompoundById(input.toUpperCase()));
//					}
//					else if (verifyFormat(iFormat,input.toUpperCase())){
//						Inventory inv=invService.loadById(input.toUpperCase());
//						if (inv != null)
//						setCompound(cmpdService.loadCompoundById(inv.getCompound().getCid()));
//					}
//					else{
//					CompoundName compoundName = cnameService.loadByName(input);
//					if (compoundName != null)
//						setCompound(cmpdService.loadCompoundById(compoundName.getCompound().getCid()));
//					}
//					target.add(label);
//					updatePanels(getCompound());
//					target.add(tabbedPanel);
//					tabbedPanel.setSelectedTab(tabbedPanel.getSelectedTab());
					}

				@Override
				protected void onError(AjaxRequestTarget target)  {  }
				});
			// issue 39
			add(new AjaxLink <Void>("saveChanges")
				{
	            @Override
	            public void onClick(AjaxRequestTarget target)
	            	{
	            	String input = field.getValue().trim();
	            	ValidateInput(input, target, label);
	    			}
				});
			
			add(emptyPanel= new EmptyPanel("emptyPanel"));
			updatePanels(null);
			}
		
		private void runClick(AjaxRequestTarget target)
			{
        	}
		
		private void updatePanels(Compound compound)
			{
			emptyPanel.setVisible(compound==null);
			tabbedPanel.setVisible(compound!=null);
			}
		
		private boolean verifyFormat(String format, String input) 
			{
	        Pattern pattern = Pattern.compile(format);
	        Matcher matcher = pattern.matcher(input);
	        return matcher.find();
			}
		
		private void ValidateInput(String input, AjaxRequestTarget target, Label label)
			{
			String cFormat="(C)\\d{5}|(CA)\\d{4}", iFormat="(NV)\\d{5}";
			Compound c=null;
			if (verifyFormat(cFormat,input.toUpperCase()))
				{
				try { c=compoundService.loadCompoundById(input.toUpperCase()); }
				catch (EmptyResultDataAccessException e)
					{
					updatePanels(null);
					CompoundDetail.this.error("Compound not found!");
					}
				
				if(c!=null) setCompound(c);
				}
			else if (verifyFormat(iFormat,input.toUpperCase()))
				{
				Inventory inv=null;
				
				try { inv=invService.loadById(input.toUpperCase()); }
				catch (EmptyResultDataAccessException e)
					{
					updatePanels(null);
					CompoundDetail.this.error("Inventory not active!");
					}
				
				if (inv != null)
					setCompound(c=compoundService.loadCompoundById(inv.getCompound().getCid()));
				}
			else
				{
				CompoundName compoundName = compoundNameService.loadByName(input);
				if (compoundName != null)
					setCompound(c=compoundService.loadCompoundById(compoundName.getCompound().getCid()));
				}
			
			updatePanels(c);
			if(c!=null)
				{
				target.add(tabbedPanel);
				tabbedPanel.setSelectedTab(tabbedPanel.getSelectedTab());
				}
			target.add(prntPanel);
			}

		
	private Compound compound;
	
	public void setCompound(Compound compound) { this.compound=compound; }
	
	public Compound getCompound() { return compound; }
	}
}



