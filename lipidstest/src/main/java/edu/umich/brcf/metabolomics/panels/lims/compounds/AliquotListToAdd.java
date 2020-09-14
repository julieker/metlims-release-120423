package edu.umich.brcf.metabolomics.panels.lims.compounds;

/***************
 * 
 * Created by:   Julie Keros
 * Date:  Aug 2020 
 * For relating an aliquot to an experiment on the experiment detail panel
 ********************/

import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.metabolomics.layers.service.InventoryService;
import edu.umich.brcf.shared.layers.domain.Aliquot;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.dto.AliquotDTO;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;

public class AliquotListToAdd extends WebPage
	{
	@SpringBean
	CompoundService compoundService;
	
	// issue 61
	@SpringBean 
	AliquotService aliquotService;
	@SpringBean 
	InventoryService inventoryService;
	FeedbackPanel aFeedback;
	ListView listView;
	WebMarkupContainer container;
	WebMarkupContainer containerAliquot;
	List<Compound> parentageList;
	ListView listViewAliquots; // issue 61
	private List<Aliquot> aliquots; // issue 61
	private List<Aliquot> deletedAliquots;
	AliquotListToAdd aliquotListToAdd = this;
	DropDownChoice<String> aliquotsDD;
	String aliquotId;
	AjaxLink saveChangesButton;
	// itemList
	AliquotDTO aliquotDto = new AliquotDTO();
	public AliquotDTO getAliquotDto() { return aliquotDto; }
	public void setAliquotDto(AliquotDTO aliquotDto)  { this.aliquotDto = aliquotDto; }
	public AliquotListToAdd(final String id, final Experiment exp) 
		{
		aFeedback = new FeedbackPanel("feedback");
		aFeedback.setEscapeModelStrings(false);		
		add(aFeedback);	
		add(new AliquotListToAddForm("aliquotListToAddForm", exp));
		}
	
	public final class AliquotListToAddForm extends Form 
		{		
		public AliquotListToAddForm (String id, final Experiment exp)
		    {
			super(id, new CompoundPropertyModel(aliquotDto));
	        add(new Label("titleLabel", "Choose Aliquot to Add"));  
			final List<String> aliquotChoices = new ArrayList<String>();
			aliquotsDD= new DropDownChoice("aliquotId",  aliquotChoices);			
			aliquotsDD.setOutputMarkupId(true);
			add(aliquotsDD);	
			aliquotChoices.addAll(aliquotService.loadAllAliquotsNotChosen(exp.getExpID()));
			aliquotsDD.setChoices(aliquotChoices);
			aliquotsDD.setOutputMarkupId(true);
		    aliquotsDD.add(buildStandardFormComponentUpdateBehavior("change", "updateAliquot"));
			saveChangesButton = new AjaxLink <Void> ("addAliquot")
				{
				@Override
				public void onClick(AjaxRequestTarget target) 
					{		
					if (StringUtils.isNullOrEmpty(aliquotDto.getAliquotId()))
						{
						String msg = "<span style=\"color:red;\">" +   "Please choose an Aliquot."  + "</span>";	;
						AliquotListToAdd.this.info(msg);
						setResponsePage(getPage());
						return;
						}
					aliquotService.saveExperimentAliquot(aliquotDto.getAliquotId(), exp);
					aliquotChoices.clear();
					aliquotChoices.addAll(aliquotService.loadAllAliquotsNotChosen(exp.getExpID()));
					aliquotsDD.setChoices(aliquotChoices);
					target.add(aliquotListToAdd.aliquotsDD);
					String msg = "<span style=\"color:blue;\">" +   "Aliquot :" + aliquotDto.getAliquotId() +  " has been added to Experiment:" + exp.getExpID() + "</span>";	;
					AliquotListToAdd.this.info(msg);
					}
				};
			add(saveChangesButton);	
		    }		
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
	                    case "updateAliquot" :
	                            break;  
	                    default : break;
	                    }
	            }
	        };
	    }

	// issue 61 2020
	public String getAliquotId()
		{
		return aliquotId;
		}

	public void setAliquotId(String aliquotId)
		{
		this.aliquotId = aliquotId;
		}
	} // extends form
	
	
