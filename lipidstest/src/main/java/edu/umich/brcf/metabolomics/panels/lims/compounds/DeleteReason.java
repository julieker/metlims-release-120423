package edu.umich.brcf.metabolomics.panels.lims.compounds;
/***********************
 * Created By:  Julie Keros 
 * Date:  Sep 2 2020
 * Choose a delete Reason
 ********************/


import java.util.Arrays;
import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.h2.util.StringUtils;
import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.metabolomics.layers.service.InventoryService;
import edu.umich.brcf.shared.layers.domain.Aliquot;
import edu.umich.brcf.shared.layers.dto.AliquotDTO;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;

public class DeleteReason extends WebPage
	{
	@SpringBean
	CompoundService compoundService;	
	// issue 61
	@SpringBean 
	AliquotService aliquotService;
	@SpringBean 
	InventoryService inventoryService;
	ListView listView;
	WebMarkupContainer container;
	WebMarkupContainer containerAliquot;
	List<Compound> parentageList;
	ListView listViewAliquots; // issue 61
	private List<Aliquot> aliquots; // issue 61
	private List<Aliquot> deletedAliquots;
	DropDownChoice<String> deleteReasonDD;
	DeleteReason DeleteReason = this;
	Button saveReasonButton;
	// itemList	
	AliquotDTO aliquotDto = new AliquotDTO();
	DeleteReason deleteReason =  this;
	TextField otherReason;
	Label otherReasonLabel;
	
	public DeleteReason(String id,  final String aliquotId, ModalWindow modal1) 
		{
	//	super(id);
		add(new deleteReasonForm("deleteReasonForm", aliquotId, modal1));
		}
	
	// issue 75
	public final class deleteReasonForm extends Form 
		{		
		public deleteReasonForm (String id, final String aliquotId, final ModalWindow modal1)
		    {	
			super(id, new CompoundPropertyModel(aliquotDto));
			add(new Label("titleLabel", "Deletion Reason for aliquot:" + aliquotId));
			deleteReasonDD= new DropDownChoice("deleteReason", Arrays.asList(new String[] {"Used", "Missing", "Observed Degradation", "Other" }));			
			deleteReasonDD.setOutputMarkupId(true);
			add(deleteReasonDD);	
			deleteReasonDD.setOutputMarkupPlaceholderTag(true) ;		
			deleteReasonDD.add(buildStandardFormComponentUpdateBehavior("change", "updateReason"));
			add(new AjaxButton ("saveReason") 
				{
				@Override
				public void onSubmit(AjaxRequestTarget target)
					{									   
					if (!StringUtils.isNullOrEmpty(aliquotDto.getDeleteReason()))
					    {
						if (!StringUtils.isNullOrEmpty(aliquotDto.getOtherReason()))		
						    aliquotDto.setDeleteReason("OTHER:" + aliquotDto.getOtherReason());
						aliquotService.deleteAndSetReason(aliquotId, aliquotDto.getDeleteReason());
						if (modal1 != null)
						    modal1.close(target);
					    }
					else 
						{
						target.appendJavaScript("alert('Please choose a deletion reason for aliquot: " + aliquotId + "');");
						modal1.show(target);
						}
					}
				});
			
			otherReason = new TextField("otherReason")
				{
				@Override
				public boolean isVisible()
					{
					if ( aliquotDto.getDeleteReason() ==null)
						return false;	
					return aliquotDto.getDeleteReason().equals("Other");		
					}
				};	
		    add (otherReason);
		    otherReason.setOutputMarkupId(true);
		    otherReason.setOutputMarkupPlaceholderTag(true) ;
		    otherReasonLabel = new Label("otherReasonLabel", "Other Reason")
		    	{
				@Override
				public boolean isVisible()
					{
					if ( aliquotDto.getDeleteReason() ==null)
						return false;	
					return aliquotDto.getDeleteReason().equals("Other");		
					}
				};			
			add (otherReasonLabel);
			otherReasonLabel.setOutputMarkupId(true);
			otherReasonLabel.setOutputMarkupPlaceholderTag(true) ;
			add(new AjaxCancelLink("cancelReason", modal1, "Cancel"));
            }
		}
	
	// issue 79
	private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(String event, final String response)
	    {
	     return new AjaxFormComponentUpdatingBehavior(event)
	        {
	        @Override
	        protected void onUpdate(AjaxRequestTarget target)
	            {
	            switch (response)
	                    {
	                    case "updateReason" :
			        		   target.add(deleteReason.otherReason);
			        		   target.add(deleteReason.otherReasonLabel);
	                           break;  
	                    default : break;
	                    }
	            }
	        };
	    }
	
	
	// issue 61
	
	}
