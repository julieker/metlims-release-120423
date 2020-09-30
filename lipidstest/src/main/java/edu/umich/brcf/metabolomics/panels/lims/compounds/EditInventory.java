// Revisited : September 2016 (JW)
// Updated by Julie Keros May 1, 2020

package edu.umich.brcf.metabolomics.panels.lims.compounds;

import java.math.BigDecimal;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.metabolomics.layers.service.InventoryService;
import edu.umich.brcf.shared.layers.domain.Inventory;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.dto.InventoryDTO;
import edu.umich.brcf.shared.layers.service.BarcodePrintingService;
import edu.umich.brcf.shared.util.structures.PrintableBarcode;
import edu.umich.brcf.shared.util.structures.ValueLabelBean;
import edu.umich.brcf.shared.util.widgets.METWorksAjaxUpdatingDateTextField;
	

public class EditInventory extends WebPage 
	{
	@SpringBean
	InventoryService invService;
	
	@SpringBean
	CompoundService compoundService;
	
	@SpringBean
	BarcodePrintingService barcodePrintingService;
	
	String assignedInvId = "";
	public EditInventory(Page backPage, String cid, ModalWindow modal1) 
		{
		InventoryDTO invDto = new InventoryDTO();
		invDto.setCid(cid);
		invDto.setInventoryId("to be assigned");
		InventoryDetailPanel container=null;
		add(new Label("titleLabel", "Add Inventory"));		
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new EditInventoryForm("editInventoryForm", invDto, container, modal1));
		}
	
	public EditInventory(Page backPage, IModel invModel, InventoryDetailPanel container, final ModalWindow window)
		{
		Inventory inv = (Inventory) invModel.getObject();
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new Label("titleLabel", "Edit Inventory"));		
		add(new EditInventoryForm("editInventoryForm", InventoryDTO.instance(inv), container, window));
		}
		
	public final class EditInventoryForm extends Form 
		{
		public EditInventoryForm(final String id, final InventoryDTO invDto, final InventoryDetailPanel container, final ModalWindow window) 
			{
			super(id, new CompoundPropertyModel(invDto));
			assignedInvId = "";
			add(new Label("cid", invDto.getCid()));
			add(new Label("inventoryId", invDto.getInventoryId()));			
			DropDownChoice supplierDD=new DropDownChoice("supplier", invService.allSuppliers());
			supplierDD.setRequired(true);
			add(supplierDD);		
			add(newRequiredTextField("catNum", 50));
			add(newRequiredTextField("botSize", 50));
			add(newRequiredTextField("locId", 6));
			add(buildDateField("invDate", invDto));
			add(new RequiredTextField("purity").setType(BigDecimal.class));
			add(buildStatusField("active"));
			add(buildSaveButton("saveChanges", invDto, container));
			// issue 39
			add( new AjaxLink <Void>("close") 
			   { 
			   public void onClick(AjaxRequestTarget target)
			       {  window.close(target); 
			       } 
			   });
			}
				
		private IndicatingAjaxButton buildSaveButton(String id, final InventoryDTO invDto, final InventoryDetailPanel container)
			{
			return new IndicatingAjaxButton(id)
				{
				@Override
				public void onSubmit(AjaxRequestTarget target) 
					{
					try
						{
						// Bad design, but if save OK, cid is OK
						// issue 53
						Inventory inv = invService.save(invDto, assignedInvId);	
						if (inv.getInventoryId() != null && (invDto.getInventoryId()== null || invDto.getInventoryId().equals("to be assigned")))
							assignedInvId = inv.getInventoryId();
						EditInventory.this.info("Inventory item "+inv.getInventoryId()+" saved.");
						if (container!=null)
							updateDetailsForCid(container, invDto.getCid());
						else
							new PrintableBarcode(barcodePrintingService, "Compound Zebra",null).printBarcodes(inv.getInventoryId(), false);	
						setResponsePage(getPage());
						}
					catch (RuntimeException e) { doError(e.getMessage(), target); } 
					catch (Exception e) { doError("Error while saving inventory", target); }
					}
				@Override
				protected void onError(AjaxRequestTarget target) 
				    { 
					target.add(EditInventory.this.get("feedback")); 
				    }
				};
			}
		
		private void doError(String msg, AjaxRequestTarget target)
			{
			if (msg != null)
				EditInventory.this.error(msg);
			target.add(EditInventory.this.get("feedback"));
			}
				
		private void updateDetailsForCid(final InventoryDetailPanel container, final String cid)
			{
			if(!(cid.equals("to be assigned")))
				{
				try
					{
					Compound compound=compoundService.loadCompoundById(cid);
					(container).setCompound(compound);
					}
				catch (Exception e) { throw new RuntimeException("Error while updating inventory detail.  Can't locate compound"); } 
				}
			}
	
		private DropDownChoice buildStatusField(String id) // issue 464
			{
			DropDownChoice statusDD= new DropDownChoice("active", Inventory.STATUS_TYPES, new ChoiceRenderer()
				{
				public Object getDisplayValue(Object object)
					{
	                String stringrep;
	                String temp = (String) object;
	                Character value = temp.charAt(0);
	                switch (value) 
	                	{
	                    case 'A' :  stringrep = "Active"; break;
	                    case 'S' : stringrep = "Storage"; break;
	                    case 'I' : stringrep = "Inactive"; break;
	                    case 'O' : stringrep = "Owned"; break;
	                    default :
	                        throw new IllegalStateException(value + " is not mapped!");
	                	}
	                return stringrep;
					}
			
				
				public String getIdValue(Object object, int index) { return (Inventory.STATUS_TYPES.get(index)); }
				});
			
			statusDD.setRequired(true);
			return statusDD;
			}

		
		private METWorksAjaxUpdatingDateTextField buildDateField(String id, final InventoryDTO invDto)
			{
			METWorksAjaxUpdatingDateTextField dateFld =  new METWorksAjaxUpdatingDateTextField("invDate", new PropertyModel<String>(invDto, "invDate"), "change")
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target)  { }
				};
			
			dateFld.setRequired(true);
			dateFld.setDefaultStringFormat(Project.	PROJECT_DATE_FORMAT);
			return dateFld;
			}
		
		
		private RequiredTextField newRequiredTextField(String id,  int maxLength) 
			{
			RequiredTextField textField = new RequiredTextField(id);
			textField.add(StringValidator.maximumLength(maxLength));
			return textField;
			}
		}
	}
