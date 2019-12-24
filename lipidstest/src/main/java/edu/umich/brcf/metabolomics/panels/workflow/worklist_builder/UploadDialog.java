package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.kendo.ui.form.dropdown.DropDownList;

import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.panels.utilitypanels.ConfirmBox;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.utilpackages.ListUtils;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;
import com.googlecode.wicket.jquery.ui.widget.dialog.InputDialog;
import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
// issue 46
public abstract class UploadDialog extends AbstractFormDialog<FileUpload> 
{
	private static final long serialVersionUID = 1L;
	private static final List<String> GENRES = Arrays.asList("Black Metal", "Death Metal", "Doom Metal", "Folk Metal", "Gothic Metal", "Heavy Metal", "Power Metal", "Symphonic Metal", "Trash Metal", "Vicking Metal");
	protected final DialogButton btnUpload = new DialogButton(SUBMIT, Model.of("Upload!")) 
		{
		};
	@SpringBean 
	SampleService sampleService;
	protected final DialogButton btnCancel = new DialogButton(CANCEL, LBL_CANCEL);
    WorklistSimple gOriginalWorklist = null;
    ExperimentRandomization gEr = null;
    ExperimentRandomization erRandom = null;
	protected Form<?> form;
	private FeedbackPanel feedback;
	public FileUploadField field;
	private ArrayList<FileUpload> filesUploaded;
	
	final ModalWindow modalRandom = ModalCreator.createModalWindow("modalRandom", 500, 300); // issue 464
//	public FileUpload fUpload;
	
	public UploadDialog(String id, String title, WorklistSimple originalWorklist) 
		{
		super(id, title, new Model<FileUpload>(), true);
        gOriginalWorklist = originalWorklist;
		// Form //
		this.form = new Form<Integer>("form");
		this.add(this.form);
		form.setOutputMarkupId(true);
		this.field = new FileUploadField("file");
		this.form.add(this.field);	
		// FeedbackPanel //
		this.feedback = new JQueryFeedbackPanel("feedback");
		this.form.add(this.feedback);
		this.form.add (modalRandom);
		modalRandom.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() 
		    {
			@Override
			public void onClose(AjaxRequestTarget target)  {    }
		    });
	    this.setOutputMarkupId(true);
	    }

	@Override
	protected List<DialogButton> getButtons()
		{
			return Arrays.asList(this.btnUpload, this.btnCancel);
		}

	
	protected void onOpen(IPartialPageRequestHandler handler)
	    {
		// re-attach the feedback panel to clear previously displayed error message(s)
		handler.add(this.feedback);
	    }
		
	// Issue 413
	private String uploadFile(FileUpload uploadedFile)
		{
		File newFile = new File(System.getProperty("java.io.tmpdir") ,uploadedFile.getClientFileName()); 
		if (newFile.exists()) 
			newFile.delete();		
		try 
			{
			newFile.createNewFile();
			uploadedFile.writeTo(newFile);
			} 
		catch (Exception e) 
			{
			info("Error while uploading file " + uploadedFile.getClientFileName() + " " + e.getMessage());
			return null;
			}		
		return newFile.getName();
		}

	
	public List<String> doUpload (AjaxRequestTarget target)
	    {		
		FileUpload fUpload = this.field.getFileUpload();	
		if (fUpload == null)
			{
			target.appendJavaScript("alert('Please choose a file name');");
			return null;
			}
		if (fUpload != null) 
		    { 
			String uploadedFileName = uploadFile(fUpload);	
			if (uploadedFileName != null)
			   {
			   RandomizationLoader loader = new RandomizationLoader(gOriginalWorklist.getDefaultExperimentId());	
			   try 
			       {
				   gEr = loader.loadRandomization(uploadedFileName, gOriginalWorklist, gOriginalWorklist.getSampleGroup(0).extractAssayIdFromType());
				   List<String> sampleInvalidIds= grabInvalidSampleFormats(gEr.getSamplesArray()) ;   		    		    		        	
    		    	// issue 298
    		       if (!gEr.hasAllSamplesForExpAssay(gOriginalWorklist.getSampleGroup(0).extractAssayIdFromType()))
		               {
		    	       List <String> sampleMissingList = buildSamplesMissingListEfficiently(gEr.getSamplesArray()) ; 		    		 		    		
		    	       target.appendJavaScript(StringUtils.makeAlertMessage("Warning:  Not all samples for experiment:" + gEr.getExpId() + " and assay:" + gOriginalWorklist.getSampleGroup(0).getAssayType() + " were listed in the file. Missing samples are:" + sampleMissingList.toString()));
		    	       }
    		       List <String> sampleOtherExp = buildSamplesOtherExperiment(gEr.getSamplesArray());	    		    	
    		       if (sampleOtherExp.size() > 0)
   		    		   throw (new METWorksException  ("The following samples are not in experiment " + gEr.getExpId() + " " +  sampleOtherExp.toString())); 
    		       return sampleInvalidIds;
			       } 
			   catch (METWorksException e) 
			       {
				   gEr = null;
				   ////
				   if (gOriginalWorklist.getSampleGroup(0) !=null)
					   gOriginalWorklist.getSampleGroup(0).setRandomizationType("None");
				   if (e != null && e.getMessage() != null && e.getMessage() != "")
				       {					
				       String sampleFormatErrMsg = e.getMessage().toString().replace("'", "\\'");
				       target.appendJavaScript(StringUtils.makeAlertMessage(sampleFormatErrMsg));
				       }
				   else 
				       target.appendJavaScript(StringUtils.makeAlertMessage("Error while loading randomization for experiment " + gEr.getExpId() + "."));
				   ////
					// TODO Auto-generated catch block
				   e.printStackTrace();
				   return new ArrayList <String> ();
				   } 
			   catch (Exception e) 
			       {
				// TODO Auto-generated catch block
				   e.printStackTrace();
			       }	
			   }
	        }
		return null; 
	    }
	
    private List<String> grabInvalidSampleFormats (List<RandomizedSample> rSamples )
	    {	       
	 	List<String> erArrayInvalidFormats = new ArrayList<String> ();
	 	for (RandomizedSample erSample : rSamples)
	 	    {
	 		// issue 304
	 	    if (!FormatVerifier.verifyFormat(Sample._2019Format, erSample.getSampleName().toUpperCase()))
	 	    	erArrayInvalidFormats.add(erSample.getSampleName().toString().replace("NaN", ""));  	    	   
	 	    }
	 	 return erArrayInvalidFormats;   
	     }
	
	public List<String> buildSamplesMissingListEfficiently (List<RandomizedSample> listSamples)
	    {
		List<String> samplesMissing = sampleService.sampleIdsForExpId(gEr.getExpId());
		List<String> samplesInCsv = new ArrayList<String> ();
		if (listSamples != null)
		    for (RandomizedSample id : listSamples)	
		         samplesInCsv.add(id.toString().replace("NaN", "").trim())	;
		samplesMissing.removeAll(samplesInCsv);
		return samplesMissing;
	    }
	
	public List<String> buildSamplesOtherExperiment (List<RandomizedSample> listSamples)
	    {
		List<String> erArrayInvalidFormats = new ArrayList<String> ();
		List<String> validSamplesInCsv = new ArrayList<String> ();
		erArrayInvalidFormats = grabInvalidSampleFormats(gEr.getSamplesArray());
		for (RandomizedSample id : listSamples)	
	         validSamplesInCsv.add(id.toString().replace("NaN", "").trim())	;
	 	validSamplesInCsv.removeAll(erArrayInvalidFormats);
	 	validSamplesInCsv.removeAll(sampleService.sampleIdsForExpId(gEr.getExpId()));
	 	return validSamplesInCsv;
	    }
    }