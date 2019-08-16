////////////////////////////////////////////////////
// EditProtocolSheet.java
// Written by Jan Wigginton, Oct 28, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.lims.newestprep.protocol;
//issue 358
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IFormSubmittingComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.validation.validator.RangeValidator;
import org.apache.wicket.validation.validator.StringValidator;
import edu.umich.brcf.shared.layers.domain.Assay;
import edu.umich.brcf.shared.layers.domain.ProtocolSheet;
import edu.umich.brcf.shared.layers.domain.StandardProtocol;
import edu.umich.brcf.shared.layers.dto.ProtocolSheetDTO;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.ProtocolSheetService;
import edu.umich.brcf.shared.layers.service.StandardProtocolService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;
import edu.umich.brcf.shared.util.widgets.AjaxBackButton;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;


public abstract class EditProtocolSheet extends WebPage
	{
	@SpringBean
	ProtocolSheetService protocolSheetService;
	
	@SpringBean 
	AssayService assayService;
	
	@SpringBean
	ExperimentService experimentService;
	
	@SpringBean
	StandardProtocolService standardProtocolService;
	
	FeedbackPanel feedback;
	private String workPath = null;
	  
	public EditProtocolSheet(String id, WebPage backPage)
		{
		this(id, null, backPage);
		}
	
	public EditProtocolSheet(String id, ProtocolSheetDTO dto, WebPage backPage)
		{
		this(id, dto, backPage, null);
		}
   
	
	public EditProtocolSheet(String id, ProtocolSheetDTO dto, WebPage backPage, ModalWindow modal)
		{
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);
		// JAK issue 170 get tomcat workpath
		workPath = System.getProperty("catalina.base") + "/work/" ;
		
		add(new EditProtocolForm("editProtocolForm", dto, backPage, modal));
		}
	
	
	public class EditProtocolForm extends Form
		{
		private File file = null; // JAK issue 192
		private File fileDocx = null; // JAK issue 192
		private List<String> availableAssays; //Issue 192
		
		public EditProtocolForm(String id, ProtocolSheetDTO dto, final WebPage backPage, final ModalWindow modal)
			{
			super(id);			
			Assay assay = assayService.loadAssayByID(dto.getAssayId());
			System.out.println("Loaded asssay" + assay.getAssayName() + " " + assay.getAlternateName()
					+ " " + assay.getAssayId());
			setDefaultModel(new CompoundPropertyModel<ProtocolSheetDTO>(dto));		
			dto.setRecordedDate(Calendar.getInstance());			
			add(new Label("assayName", new PropertyModel<String>(assay, "assayName"))); 
			add(new Label("sampleType")); 
			add(new Label("experimentId" , new PropertyModel<String>(dto, "experimentId"))); //, new PropertyModel<String>(dto, "experimentId")));
			add(new Label("assayId"));			
			String expName = experimentService.experimentNameForId(dto.getExperimentId());
			add(new Label("experimentName", expName));			
			IModel<Integer> model2 = new PropertyModel<Integer>(dto, "nCellPlates");
			TextField<Integer> nPlatesField = new TextField<Integer>("nCellPlates", model2);
			add(nPlatesField).add(RangeValidator.<Integer>range(0, 999));
			nPlatesField.setRequired(true);
			IModel<Integer> model = new PropertyModel<Integer>(dto, "nSamples");
			TextField<Integer> nSamplesField = new TextField<Integer>("nSamples", model);
			add(nSamplesField).add(RangeValidator.<Integer>range(0, 9999));
			nSamplesField.setRequired(true);
			
			// Issue 224
			TextField locTxt = new TextField("locationCode", new PropertyModel<String>(dto, "locationId"));
			locTxt.add(StringValidator.maximumLength(6));
			add (locTxt);		    		    
			add(new TextArea<String>("notes", new PropertyModel<String>(dto, "notes")));
			StandardProtocol protocolDoc = null;
			//String protocolDocumentId = ("A004".equals(assay.getAssayId()) ? "SD0002" : "SD0007"); //dto.getProtocolDocumentId();
			
			// issue 228
			if (StringUtils.isEmptyOrNull(dto.getProtocolDocumentId()))
				protocolDoc = standardProtocolService.loadLatestByAssayIdAndSampleTypeEFficiently(assay.getAssayId(), dto.getSampleType());
			else 
				protocolDoc = standardProtocolService.loadById(dto.getProtocolDocumentId());// Issue 228 Issue 232
				// JAK issue 170 no longer use the noncaching image
			//	add(new NonCachingImage("protocolDoc", new ByteArrayResource(protocolDoc.getFileType(), protocolDoc.getContents())));
            OutputStream str = null;
            try 
                {
            	// JAK issue 192 write to working directory
        		standardProtocolService.writeToOutputStream(  protocolDoc, workPath );
                }
            catch (Exception e)
                {
            	e.printStackTrace();
                }			
            /////
  		   //// JAK issue 192 convert to PDF if it is a word document
  		   ///
             file =  new File(workPath +  protocolDoc.getFileName());             
             // JAK 170 create resource stream for iframe
             IResourceStream resourceStreamM = new FileResourceStream(
     				new org.apache.wicket.util.file.File(file));
     		 try 
     		     {
     			 resourceStreamM.close();    				
     			 } 
     		 catch (IOException e) 
     		     {
     				// TODO Auto-generated catch block
     			 e.printStackTrace();
     			 }	
     		// JAK issue 170 add in IFrame for display of pdf files
    		 DocumentInlineFrame dInLine =new DocumentInlineFrame("test12345678910124567",  resourceStreamM) ;
    		 add(dInLine);
			 dto.setProtocolDocumentId(protocolDoc.getProtocolId());
			 add(buildSubmitButton("submitButton"));
			 
			 if (modal != null) // Issue 231
			     {
				 AjaxButton cancelButton = 
					 new AjaxButton("cancelButton")
		             {
		        	 // Issue 231
					 @Override		 
					 public void onSubmit(AjaxRequestTarget target) 
					    {
						 try
						     {
							 file.delete();						     
							 modal.close(target);
						     }
						 catch (Exception e)
						     {
						     e.printStackTrace();	
						     }							
						 };			        		 
		             };
					add (cancelButton);	
				    cancelButton.setDefaultFormProcessing(false);
			     }
			 else
			     {
				 AjaxButton cancelButton = 
				 new AjaxButton("cancelButton")
				     {
					 @Override
					 public void onSubmit (AjaxRequestTarget target)
					     {
						 try
						     {
							 file.delete();	 						 
							 setResponsePage(backPage);
						     }
						 catch (Exception e)
						     {
						     e.printStackTrace();	
						     }							
						 };						
					 };
				 add (cancelButton);
				 cancelButton.setDefaultFormProcessing(false);
				//// JAK issue 193 delete the file when the user closes the window			
			     }
			 
		 }
				
	private DropDownChoice buildExperimentDropdown(final String id, String propertyName)
		{
		DropDownChoice selectedExperimentDrop =  new DropDownChoice(id,  new PropertyModel(this, propertyName), 
			new LoadableDetachableModel<List<String>>() 
				{
		        @Override
		        protected List<String> load() 
		        	{ 
		        	return null;
		        	//	return assayService.
		        	}		        		
				})
				{
				@Override
				public boolean isEnabled() { return true; } // return drpIsSelected(getSelectedPlatform()); }
				};
			selectedExperimentDrop.setEnabled(false);		
			return selectedExperimentDrop;
			}

		private DropDownChoice buildAssayDropdown(final String id, String propertyName)
			{
			DropDownChoice drp = new DropDownChoice(id,  new PropertyModel(this, propertyName),  new LoadableDetachableModel<List<String>>() 
					{
	            	@Override
	            	protected List<String> load() 
	            		{ 
	            		if (availableAssays != null)
	            			return availableAssays;
	            		
	            		return new ArrayList<String>();
	            		}
					})
				{
				@Override
				public boolean isEnabled() { return true; }
				};		
			drp.setEnabled(false);
		//	drp.add(this.buildStandardFormComponentUpdateBehavior("change", "updateForAssayDrop"));						
			return drp;
			}
		
			
		public AjaxButton buildSubmitButton(String id) // issue 464
			{
			return new AjaxButton(id, this)
				{
				@Override
				protected void onSubmit(AjaxRequestTarget target)//issue 464 
					{
					ProtocolSheetDTO dto = (ProtocolSheetDTO) getForm().getModelObject();
					
					try
						{
						dto.setRecordedBy(((MedWorksSession) Session.get()).getCurrentUserId());
						ProtocolSheet sheet = protocolSheetService.save(dto);
						EditProtocolSheet.this.onSave(sheet, target);
						EditProtocolSheet.this.info("Protocol Sheet "+ sheet.getId() +" saved successfully");						
						target.add(EditProtocolSheet.this.get("feedback"));
						}
					catch (Exception e)
						{
						String msg = e.getMessage();
						if (msg.startsWith("Assay") || msg.startsWith("Contact"))
							EditProtocolSheet.this.error(msg);
						else
							EditProtocolSheet.this.error("Save unsuccessful. Please re-check entered values.");
						
						target.add(EditProtocolSheet.this.get("feedback"));
						} 
					}
	
				@Override
				// issue 464
				protected void onError(AjaxRequestTarget target)
					{
					EditProtocolSheet.this.error("Save unsuccessful. Please re-check values entered.");
					target.add(EditProtocolSheet.this.get("feedback"));
				//	target.add(EditProtocolSheet.this.get("feedback").getParent());
				//	getRequestCycle().detach();
					}
				};
			}
		}
		
	protected abstract void onSave(ProtocolSheet sheet, AjaxRequestTarget target);
	}