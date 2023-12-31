// Revisited : October 2016 (JW)
// Updated Jun 3rd 2020 (JK)
package edu.umich.brcf.metabolomics.panels.lims.compounds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import edu.umich.brcf.shared.layers.domain.Aliquot;
import edu.umich.brcf.shared.layers.domain.Inventory;
import edu.umich.brcf.shared.layers.service.AliquotService;

public class CompoundDetail  extends Panel{
	
	@SpringBean
	CompoundNameService compoundNameService;
	
	@SpringBean
	CompoundService compoundService;
	
	@SpringBean
	InventoryService invService;
	
	@SpringBean
	AliquotService alqService;
	
	CompoundDetail prntPanel = this;
	TabbedPanel tabbedPanel;                        
	EmptyPanel emptyPanel;
	Link invSrch;
	int doRender = 1;
	CompoundDetailPanel compoundDetailPanel;
	public Form<?> formCompoundDetail;
	boolean atCompound = false;
	
	
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
            		if (compoundDetailPanel != null)
            	    // issue 113
            		    {
            			compoundDetailPanel.setCmpId(getCompound().getCid());
            			compoundDetailPanel.updateCompound(getCompound().getCid());
            			compoundDetailPanel.smilesInchiKeyMultipleSmilesList = compoundDetailPanel.getSmilesFromCompoundIdandSetTag();	
            			compoundDetailPanel.getStructure();
            			return compoundDetailPanel;
            		    }
            		compoundDetailPanel = new CompoundDetailPanel(panelId, getCompound().getCid(), true) ;
            		return compoundDetailPanel;            		
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
			 // Issue 84
			 final String cidFormat =   "(C)\\d{1}|(C)\\d{2}|(C)\\d{3}|(C)\\d{4}|(C)\\d{5}|(CA)\\d{1}|(CA)\\d{2}|(CA)\\d{3}|(CA)\\d{4}|(D)\\d{1}|(D)\\d{2}|(D)\\d{3}|(D)\\d{4}|(D)\\d{5}";
			 final String nvIdFormat =  "(NV)\\d{1}|(NV)\\d {2}|(NV)\\d{3}|(NV)\\d{4}|(NV)\\d{5}";
			 final String aIdFormat = "(A)\\d{1}| (A)\\d{2}| (A)\\d{3}| (A)\\d{4}| (A)\\d{5}| (A)\\d{6}| (A)\\d{7}| (A)\\d{8}";
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
							{
							for (String cid : compoundService.getMatchingCids(input.toUpperCase())) 
								choices.add(cid);
							// issue 165
							for (String strName : compoundNameService.getMatchingNamesCompoundId(input)) 
							   {
							   if (strName.toUpperCase().replaceAll("'", "''").contains(input.toUpperCase()))
							       choices.add(strName);
							   } 
							
							}
						else if (verifyFormat(nvIdFormat,input.toUpperCase()))
							for (String invId : invService.getMatchingInvIds(input.toUpperCase())) 
								choices.add(invId);
						// issue 48 for CAS numbers
						// issue 61 for Aliquots
						else if  (verifyFormat(aIdFormat,input.toUpperCase()))
							{
							for (String aId : alqService.getMatchingAliquotIds(input.toUpperCase()))
								choices.add(aId);
							}
						else
							{							   
							// issue 48
						    for (String strName : compoundNameService.getMatchingNamesCompoundId(input)) 
							   {
							   if (strName.toUpperCase().replaceAll("'", "''").contains(input.toUpperCase()))
							       choices.add(strName);
							   } 
							for (String strName : compoundService.getMatchingCASIds(input)) 
							   {
							   if (strName.toUpperCase().contains(input.toUpperCase()))
							       choices.add(strName);
							   } 
							// issue 158
							input=input.replaceAll("''", "'");
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
					// issue 84 String cFormat="(C)\\d{5}|(CA)\\d{4}", iFormat="(NV)\\d{5}", aIdFormat="(A)\\d{8}";
					String cFormat="(C)\\d{5}|(CA)\\d{4}|(D)\\d{5}", iFormat="(NV)\\d{5}", aIdFormat="(A)\\d{8}";
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
	            	atCompound = true;
	            	compoundDetailPanel.updateCompound(getCompound().getCid());	            	
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
			// issue 84
			String cFormat="(C)\\d{5}|(CA)\\d{4}|(D)\\d{5}", iFormat="(NV)\\d{5}", aIdFormat="(A)\\d{8}";
			Compound c=null;
			// issue 158
			input=input.replaceAll("'", "''"); 
			if (verifyFormat(cFormat,input.toUpperCase()) && !input.contains("CID:"))
				{
				try 
				    { 
					c=compoundService.loadCompoundById(input.toUpperCase()); 
					}
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
			//// issue 61 for aliquot
			else if (verifyFormat(aIdFormat,input.toUpperCase()))
				{
				Aliquot alq=null;
				try 
				    { 
					alq=alqService.loadById(input.toUpperCase());
				    }				
				catch (EmptyResultDataAccessException e)
					{
					updatePanels(null);
					CompoundDetail.this.error("Aliquot not found!");
					}				
				if (alq != null)
					setCompound(c=c=compoundService.loadCompoundById(alq.getCompound().getCid()));
				}
			//// issue 48 for CAS			 
			else
				// issue 48
				{
				// cas format
				if (input.contains("CAS:" ))
					{
					String vCid = input.substring(input.lastIndexOf("CID:")+4);
					setCompound(c=compoundService.loadCompoundById(vCid));
					}
				// issue 61 aliquot name
				else if (input.contains("ALIQUOT NAME:"))
					{
					String vCid = input.substring(input.lastIndexOf("CID:")+4);
					setCompound(c=compoundService.loadCompoundById(vCid));
					}
				else   
				    {
					// issue 158
					CompoundName compoundName = compoundNameService.loadByNameCompoundId(input);
					if (compoundName != null)
						setCompound(c=compoundService.loadCompoundById(compoundName.getCompound().getCid()));
				    }
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



