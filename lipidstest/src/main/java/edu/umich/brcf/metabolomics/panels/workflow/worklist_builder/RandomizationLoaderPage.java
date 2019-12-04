//RandomizationLoaderPage.java
//Written by Jan Wigginton, November 2015
/////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.GCDerivatizationMethod;
import edu.umich.brcf.metabolomics.panels.lims.prep.EditGCPrep;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.panels.utilitypanels.ConfirmBox;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.utilpackages.ListUtils;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;
import net.sf.cglib.core.CollectionUtils;


public abstract class RandomizationLoaderPage extends WebPage 
	{
	AjaxLink  cancelLink;
	Page backPage; 
	
	//private FileUploadField fileUploadField;
	private String UPLOAD_FOLDER = "./";
	
	final ModalWindow modalRandom = ModalCreator.createModalWindow("modalRandom", 500, 300); // issue 464
	
	@SpringBean 
	SampleService sampleService;
        
	ExperimentRandomization er = new ExperimentRandomization();  
        
	public RandomizationLoaderPage(Page backPage, final String eid, final ModalWindow modal1, final WorklistSimple originalWorklist)
		{	
		this.backPage = backPage;
		modal1.setCssClassName(ModalWindow.CSS_CLASS_BLUE);		
		RandomizationLoaderForm rlf = new  RandomizationLoaderForm("randomizationForm", eid, modal1, originalWorklist);
		rlf.add(new UploadProgressBar("progress", rlf));
		add(rlf);
		setOutputMarkupId(true);		
		}
	
	public final class RandomizationLoaderForm extends Form
		{
		private static final long serialVersionUID = -6032695592374473021L;

		private FileUploadField fileUploadField;
		private ArrayList<FileUpload> filesUploaded;
		String expId;
		WorklistSampleGroup sampleGroup; 
			
	    DropDownChoice<String> assayType; 
	    String  selectedAssay; 
	    ModalWindow modal;
		
		public RandomizationLoaderForm(String id, String eid, final ModalWindow modal1, final WorklistSimple originalWorklist)
			{
			super(id);
		    modal = modal1;
			expId = eid;
			add(new FeedbackPanel("feedback"));
			this.setOutputMarkupId(true);
			add (modalRandom);
			setMultiPart(true);				
		 	add(assayType = buildDropdownAssayType("randomizationType"));
		 	setSelectedAssay("Custom");
            
		 	add(fileUploadField = new FileUploadField("fileContents", new Model(filesUploaded))
	        	{
		 		public boolean isEnabled()
					{
					return fileUploadField != null;
					}
		 		
	        	protected boolean wantOnSelectionChangedNotifications() 
					{
	                return true;
					}
	        	});
			 			 	
		    add(new AjaxLink <Void>("cancelButton")
        	    {
			    public void onClick(AjaxRequestTarget target)
				    {
				    if (modal1 != null)
					    modal1.close(target);
				    }
			    });
                     
            add(new AjaxButton("submitButton")
	            {
	        	public boolean isVisible()
	        		{
	        		return true;
	        		}
	        	
	        	public boolean isEnabled()
	        		{	        		
	        		return fileUploadField != null; //(fileUploadField.getFileUpload().getClientFileName() != null); // getSelected().equals("Client Report");
	        		}	
	        	
	        	// Issue 268 move onsubmit to form
	        	@Override
	    		public void onSubmit(AjaxRequestTarget target)// issue 464
	    		    {	
	    		    try
	    	            {
	    		    	List<String> sampleInvalidIds= loadExperimentRandomization( originalWorklist);    		    		    		        	
	    		    	// issue 298
	    		    	if (!er.hasAllSamplesForExpAssay(originalWorklist.getSampleGroup(0).extractAssayIdFromType()))
	    		    	    {
	    		    		List <String> sampleMissingList = buildSamplesMissingListEfficiently(er.getSamplesArray()) ; 		    		 		    		
	    		    		target.appendJavaScript(StringUtils.makeAlertMessage("Warning:  Not all samples for experiment:" + er.getExpId() + " and assay:" + originalWorklist.getSampleGroup(0).getAssayType() + " were listed in the file. Missing samples are:" + sampleMissingList.toString()));
	    		    	    }
	    		    	List <String> sampleOtherExp = buildSamplesOtherExperiment(er.getSamplesArray());	    		    	
	    		    	if (sampleOtherExp.size() > 0)
	    		    		throw (new Exception  ("The following samples are not in experiment " + expId + " " +  sampleOtherExp.toString()));
	    		    	if (sampleInvalidIds.size()> 0 )
				            { 
	    		            showConfirmBoxWarning("Are you sure that you want to do this randomization?.  The following samples are not in the correct format:" + System.getProperty("line.separator") + ListUtils.prettyPrint(sampleInvalidIds), sampleInvalidIds.size());	    		        	
	    		            modalRandom.show(target); 
				            } 
	    		        else
	    		            {
	    		            RandomizationLoaderPage.this.saveRandomization(er);							
		        	        target.appendJavaScript(StringUtils.makeAlertMessage("Randomization for experiment " + expId + " loaded successfully. "));
	    		            modal.show(target);
	    		            }	    		        	
				        }
	    			catch (Exception e) 
					    {	    			
					    modal.setInitialHeight(195);
					    if (sampleGroup !=null)
						    sampleGroup.setRandomizationType("None");
					    if (e != null && e.getMessage() != null && e.getMessage() != "")
					        {					
					    	String sampleFormatErrMsg = e.getMessage().toString().replace("'", "\\'");
					    	target.appendJavaScript(StringUtils.makeAlertMessage(sampleFormatErrMsg));
					    	modal.show(target);
					        }
					    else 
					        {
					        target.appendJavaScript(StringUtils.makeAlertMessage("Error while loading randomization for experiment " + expId + "."));
					        modal.show(target);
					        }
					    }
				     }
	            });         
 	     	}
   
		
		//issue 298
		public List<String> buildSamplesMissingListEfficiently (List<RandomizedSample> listSamples)
		    {
			List<String> samplesMissing = sampleService.sampleIdsForExpId(expId);
			List<String> samplesInCsv = new ArrayList<String> ();
			if (listSamples != null)
			    for (RandomizedSample id : listSamples)	
			         samplesInCsv.add(id.toString().replace("NaN", "").trim())	;
			samplesMissing.removeAll(samplesInCsv);
			return samplesMissing;
		    }
		
		// issue 299
		public List<String> buildSamplesOtherExperiment (List<RandomizedSample> listSamples)
		    {
			List<String> erArrayInvalidFormats = new ArrayList<String> ();
			List<String> validSamplesInCsv = new ArrayList<String> ();
			erArrayInvalidFormats = grabInvalidSampleFormats(er.getSamplesArray());
			for (RandomizedSample id : listSamples)	
		         validSamplesInCsv.add(id.toString().replace("NaN", "").trim())	;
		 	validSamplesInCsv.removeAll(erArrayInvalidFormats);
		 	validSamplesInCsv.removeAll(sampleService.sampleIdsForExpId(expId));
		 	return validSamplesInCsv;
		    }
		
		public int fnScaledHeight(int numberInvalidSamples)
		    {
			 // 220 = 0-5
			 // 280 = 6-10 
			 // 365 = 11-15
			 // 460 = 16-20
			 // 500 >= 21
		    if (numberInvalidSamples <= 5)
				return 235;
			else if (numberInvalidSamples <= 10)
				return 280;
			else if (numberInvalidSamples <= 15)
				return 365;
			else if (numberInvalidSamples <= 20)
				return 460;
			else
				return 500;					
		    }
		
		// Issue 268
		public void showConfirmBoxWarning(final String msg, int numberInvalidSamples)
		    {			
	    	//////////// Issue 268	
			 modalRandom.setInitialWidth(800);			 
             modalRandom.setInitialHeight(fnScaledHeight(numberInvalidSamples));	            
             modalRandom.setPageCreator(new ModalWindow.PageCreator()
                 {
                 public Page createPage()
 	                { 	        	        			
	                return new ConfirmBox("confirmBox", msg , modal)
		                {
			            @Override
			            protected void doAction(AjaxRequestTarget target) 
				            { 						            	
			                RandomizationLoaderPage.this.saveRandomization(er);							
			        	    target.appendJavaScript(StringUtils.makeAlertMessage("Randomization for experiment " + expId + " loaded successfully. "));
				            }
		                };
	                 }
	             }
                );	          	            
		    }
		
		private DropDownChoice<String> buildDropdownAssayType(String id)
			{
			return new DropDownChoice<String>(id,  new PropertyModel(this, "selectedAssay"), 
				new LoadableDetachableModel<List<String>>() 
					{
                	@Override
                	protected List<String> load() 
                		{ 
                		return Arrays.asList(new String [] {"Custom", "Other"});
                		}
					})
				{
				
				private static final long serialVersionUID = 8374367750179219455L;

				public boolean isEnabled()
					{
					return false;
					}
				
				protected boolean wantOnSelectionChangedNotifications() 
					{
		            return false;
					}
				};		
			}		

		
		// issue 268
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

		public void setSelectedAssay(String s)
			{
			selectedAssay = s;
			}
	
		public String getSelectedAssay()
			{
			return selectedAssay;
			}
		
		///issue 268
		private List<String> loadExperimentRandomization ( WorklistSimple originalWorklist) throws Exception
	        {			 
			try
			   {
			   final FileUpload uploadedFile = fileUploadField.getFileUpload();			
			   // issue 268
			   if (uploadedFile == null)
			       throw (new Exception ("Please choose a file name"));
			   if (uploadedFile != null) 
				   {
				   String uploadedFileName = uploadFile(uploadedFile);						 
				   if (uploadedFileName != null)
				       {
					   RandomizationLoader loader = new RandomizationLoader(expId);	
					   // issue 268 include assay
					   er = loader.loadRandomization(uploadedFileName, originalWorklist, originalWorklist.getSampleGroup(0).extractAssayIdFromType());				         
					   if (er == null)
					       return null;					     
				       }
			        }
			    return grabInvalidSampleFormats(er.getSamplesArray());
		        }
			catch (METWorksException me)
			    {
				throw me;				
			    }
		    catch (Exception e) 
			    {				
			    throw e;			    
			    }
	         }
		
		 // Issue 268
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
		/////////////////////////////		
    	}
	
	protected abstract void saveRandomization(ExperimentRandomization gEr);
	}


//////////////////////////////

//if (er != null && er.hasAllSamplesForExp())
//	{
//	System.out.println("Experiment was " + er.getExpId());
	//info("Finished loading randomization for experiment" + er.getExpId());
//	}
//else
//	info("Didn't load randomization values for samples in experiment " + expId);
