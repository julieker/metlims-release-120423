package edu.umich.brcf.metabolomics.panels.lims.preparations;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.file.Folder;

import edu.umich.brcf.metabolomics.layers.domain.GCDerivatizationMethod;
import edu.umich.brcf.metabolomics.layers.domain.GeneralPrepSOP;
import edu.umich.brcf.metabolomics.layers.domain.HomogenizationSOP;
import edu.umich.brcf.metabolomics.layers.domain.LCReconstitutionMethod;
import edu.umich.brcf.metabolomics.layers.domain.ProtienDeterminationSOP;
import edu.umich.brcf.metabolomics.layers.dto.GCDerivatizationDTO;
import edu.umich.brcf.metabolomics.layers.dto.GeneralPrepDTO;
import edu.umich.brcf.metabolomics.layers.dto.HomogenizationDTO;
import edu.umich.brcf.metabolomics.layers.dto.LCReconstitutionDTO;
import edu.umich.brcf.metabolomics.layers.dto.ProteinDeterminationDTO;
import edu.umich.brcf.metabolomics.layers.service.FractionationService;
import edu.umich.brcf.metabolomics.panels.lims.prep.AddSampleToPrepPage;
import edu.umich.brcf.metabolomics.panels.lims.prep.CreateNewPlatePage;
import edu.umich.brcf.metabolomics.panels.lims.prep.EditAbsorbancePage;
import edu.umich.brcf.metabolomics.panels.lims.prep.EditBuffer;
import edu.umich.brcf.metabolomics.panels.lims.prep.EditDilution;
import edu.umich.brcf.metabolomics.panels.lims.prep.EditGCPrep;
import edu.umich.brcf.metabolomics.panels.lims.prep.EditHomogenization;
import edu.umich.brcf.metabolomics.panels.lims.prep.EditLCPrep;
import edu.umich.brcf.metabolomics.panels.lims.prep.PrepAliquotDetail;
import edu.umich.brcf.metabolomics.panels.lims.prep.PreppedFractionDetail;
import edu.umich.brcf.metabolomics.panels.lims.prep.SelectablePlateMapPanel;
import edu.umich.brcf.shared.layers.domain.PrepPlate;
import edu.umich.brcf.shared.layers.dto.BufferBean;
import edu.umich.brcf.shared.layers.dto.DilutionBean;
import edu.umich.brcf.shared.layers.dto.PreppedFractionDTO;
import edu.umich.brcf.shared.layers.dto.PreppedSampleDTO;
import edu.umich.brcf.shared.layers.service.SamplePrepService;
import edu.umich.brcf.shared.panels.utilitypanels.AddNotesPage;
import edu.umich.brcf.shared.util.widgets.METWorksPctSizableModal;




public class NewNewPrepMethodsPanel extends Panel
	{
	@SpringBean
	SamplePrepService samplePrepService;
	
	@SpringBean
	FractionationService fractionationService;
	
	
	String preparation=null, plateStr = "";
	SelectablePlateMapPanel plateMap;
	boolean isSamplePrep=true;

	//AjaxLink link,lcNotes,gcNotes;
	//AjaxLink derivatization, reconstitution, lcNotes, gcNotes;
	
	private FileUploadField fileUploadField;
	
	
	public NewNewPrepMethodsPanel(String id, String preparation) 
		{
		// Feedback
		super(id);
		setPreparation(preparation);
		setOutputMarkupId(true);
		NewNewPrepMethodsPanel pmp = this;
		add(new NewPrepDetailForm("prepDetailForm", pmp));
		}

	
	public final class NewPrepDetailForm extends Form 
		{
		public NewPrepDetailForm(final String id, final NewNewPrepMethodsPanel pmp)
			{
			super(id);
			
			final METWorksPctSizableModal modal1= new METWorksPctSizableModal("modal1", 0.3, 0.2);
			modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
	        	{
	            public void onClose(AjaxRequestTarget target) { target.add(pmp); }
	         	});
	         
	        add(modal1);
	        final METWorksPctSizableModal modal2= new METWorksPctSizableModal("modal2", 0.3, 0.2);
			add(modal2);

	        add(plateMap = new SelectablePlateMapPanel("plateMap", getPreparation())); 
			add(buildLinkToModal("proteinDetermination", modal1,null, pmp).setVisible(isSamplePrep));
			add(buildLinkToModal("generalPrep", modal1, null, pmp).setVisible(isSamplePrep));
			add(buildNotesLink("addNotes", modal1, pmp));
		    setMultiPart(true);
		//	add(new Label("plates", new Model(plateStr)));

		//	add(derivatization = buildLinkToModal("derivatization", modal1, null, pmp));
		//	derivatization.setVisible(isSamplePrep);
			
		//	add(reconstitution=buildLinkToModal("reconstitution", modal1, null, pmp));
		///	reconstitution.setVisible(isSamplePrep);
			
			add(buildLinkToModal("createNewPlate", modal1, null, pmp));
	//		add(gcNotes = grabLinkToNotesModal("gcNotes", "GC", modal1));
	//		add(lcNotes = grabLinkToNotesModal("lcNotes", "LC", modal1));
			add(buildLinkToModal("addSample", modal1, null, pmp).setVisible(!isSamplePrep));
	
			checkComponentVisibility();
			}
		}
	


	
	private AjaxLink grabLinkToNotesModal(String id, final String tag, final ModalWindow modal1)
		{
		return new AjaxLink(id)
	        {
	        @Override
	        public void onClick(AjaxRequestTarget target) { createNotesModal(modal1, target,tag); }
	    	@Override // issue 464
			public MarkupContainer setDefaultModel(IModel model) 
			    {
				// TODO Auto-generated method stub
				return this;
			    }
	        };
		}

	public void checkComponentVisibility()
		{
		if(preparation!=null)
			{
		//	gcNotes.setVisible(false);
		//	lcNotes.setVisible(false);
		//	derivatization.setVisible(false);
		//	reconstitution.setVisible(false);
			
			List<PrepPlate> plates=samplePrepService.loadPlatesByPreparation(preparation);
			
			/*
			for (PrepPlate plate: plates)	
				{
				if (plate instanceof GCPlate)
					{
					gcNotes.setVisible(true);
					if(isSamplePrep)
						derivatization.setVisible(true);
					}
				else{
					lcNotes.setVisible(true);
					if(isSamplePrep)
						reconstitution.setVisible(true);
					}
				} */
			}
		}
	
	
	public AjaxLink buildLinkToModal(final String linkID, final METWorksPctSizableModal modal1, final String value,
			final NewNewPrepMethodsPanel pmp) 
		{
		return new AjaxLink(linkID)
        	{
			@Override
            public void onClick(AjaxRequestTarget target)
            	{
				if (checkForNoSelection(linkID, target))
					return;
            		
            	if(linkID.startsWith("addS") && plateMap.checkForFullPlate(target, plateMap))
            		return;
            	
            	switch(linkID)
					{
					case "generalPrep" : 	modal1.setPageDimensions(0.6, 0.8); break;
	            	case "proteinDetermination" :  modal1.setPageDimensions(1.0, 1.2); break;
	            	case "createNewPlate" : modal1.setPageDimensions(0.3, 0.22); break;
	            	case "derivatization" : modal1.setPageDimensions(0.3, 0.25); break;
	            	}
            	
            	
            	modal1.setPageCreator(new ModalWindow.PageCreator()
            		{
                    public Page createPage()
                    	{
                    	switch (linkID)
                    	{
                    	case "generalPrep" : 	return grabGeneralPrepPage();
                    	case "proteinDetermination" :  return grabEditProteinDeterminationPage();
                    	case "createNewPlate" :  return grabCreatePlatePage();
                    	case "derivatization" :  return grabEditGcPrepPage();
                    	default :   return grabEditLcPrepPage();
                    	}
                    }
                    
                    
                    });
            	
            	target.add(this);
            	target.add(pmp.getParent().getParent().getParent());
            	modal1.show(target);
            	}
			@Override // issue 464
			public MarkupContainer setDefaultModel(IModel model) 
			    {
				// TODO Auto-generated method stub
				return this;
			    }
        	};
		}


	/*private boolean checkForFullPlate(AjaxRequestTarget target, NewNewPrepMethodsPanel pmp)
		{
		List<PreppedFraction> smpList=fractionationService.loadPreppedFractions(preparation);
		
    	if(!(smpList.size() < cols * rows))
    	 	{
    		NewNewPrepMethodsPanel.this.error("Plate is full -- please create another worklist");
    		target.add(pmp.getParent().getParent().getParent());
    		return true;
    	 	}
    	 
    	return false;
		} */

	private boolean checkForNoSelection(String linkID, AjaxRequestTarget target)
		{
    	if(!linkID.startsWith("s")&&!linkID.startsWith("der")&&!linkID.startsWith("r")&&!linkID.startsWith("c")&&!linkID.startsWith("ab")&&!linkID.startsWith("ad"))
			if(plateMap == null || plateMap.getSelectedSamplesCount()==0)
				{
				String msg = "Please select at least one sample to assign methods.";
	    		target.appendJavaScript("alert('" + msg + "')");
	    		return true;
	    		}
		
    	return false;
		}
	
	
	private Page grabEditLcPrepPage()
		{
		return (new EditLCPrep(getPage(), LCReconstitutionDTO.instance(samplePrepService.loadLCReconstitutionByID(LCReconstitutionMethod.DEFAULT_SOP)))
			{
			@Override
			protected void onSave(LCReconstitutionMethod bean) { samplePrepService.assignReconstitution(getPreparation(), bean); }
            });
		}

	
	private Page grabEditGcPrepPage()
		{
		return (new EditGCPrep(getPage(), GCDerivatizationDTO.instance(samplePrepService.loadGCDerivatizationByID(GCDerivatizationMethod.DEFAULT_SOP)))
			{
			@Override
			protected void onSave(GCDerivatizationMethod bean) {  samplePrepService.assignDerivatization(getPreparation(), bean);  }
            });
		}

	
	private Page grabCreatePlatePage()
		{
		return (new CreateNewPlatePage(getPage())
			{
			@Override
			protected void onSave(List<String> instrumentName) { samplePrepService.createPlates(getPreparation(),instrumentName);  }
			});
		}
	
	
	private Page grabEditBufferPage()
		{
		return (new EditBuffer(getPage(), new BufferBean())
			{
			@Override
			protected void onSave(BufferBean bean) { samplePrepService.assignBuffer(plateMap.getSamples(), bean, plateMap.getPlateCols(), plateMap.getPlateRows()); }
			});
		}

	
	private Page grabEditDilutionPage()
		{
 		return (new EditDilution(getPage(), new DilutionBean(), isSamplePrep)
 			{
			@Override
			protected void onSave(DilutionBean bean) { samplePrepService.assignDilution(plateMap.getSamples(), bean, isSamplePrep, plateMap.getPlateCols(), plateMap.getPlateRows()); }

			
 			});
 		}

		
	
	private Page grabAddSampleToPrepPage()
		{
		return (new AddSampleToPrepPage()
			{
			@Override
			protected void onSave(PreppedFractionDTO frDto) 
				{
				fractionationService.addFractionToPrep(frDto, getPreparation());
				setPreparation(getPreparation());
				}
			});
		}
	
	private Page grabEditAbsorbancePage()
		{
 		return new EditAbsorbancePage(getPreparation(), plateMap.getPlateCols(), plateMap.getPlateCols());
 			//{
			///@Override
			//protected void onSave(ProtienDeterminationSOP sop) {
			//		samplePrepService.assignProtienDetermination(getSamples(), sop);
			//}
         //  });
 		}
	
	private Page grabEditProteinDeterminationPage()
		{
		return (new NewEditProteinDetermination(getPage(),  plateMap,
				 ProteinDeterminationDTO.instance(samplePrepService.loadProtienDeterminationSOPByID(ProtienDeterminationSOP.DEFAULT_SOP)))
			{
			@Override
			protected void onSave(ProtienDeterminationSOP sop) 
				{
				samplePrepService.assignProtienDetermination(plateMap.getSamples(), sop,  plateMap.getPlateCols(), plateMap.getPlateCols());
				}
			});
		}
	
	
	private Page grabEditHomogenizationPage()
		{
		return (new EditHomogenization(getPage(), 
				 HomogenizationDTO.instance(samplePrepService.loadHomogenizationByID
						 (HomogenizationSOP.DEFAULT_SOP)))
			{
			@Override
			protected void onSave(HomogenizationSOP sop) {  samplePrepService.assignHomogenization(plateMap.getSamples(), sop, plateMap.getPlateCols(), plateMap.getPlateRows()); }
			});
		}
	
	
	private Page grabGeneralPrepPage()
		{ 
		return (new NewEditGeneralPrep(getPage(), GeneralPrepDTO.instance(samplePrepService.loadGeneralPrepSOPByID(GeneralPrepSOP.DEFAULT_SOP))){

			@Override
			protected void onSave(GeneralPrepSOP sop) {
				samplePrepService.assignGeneralPrep(plateMap.getSamples(), sop, plateMap.getPlateCols(), plateMap.getPlateRows());
			}
         });
		}


	private Page grabAliquotOrFractionPage(final String value)
		{
		if (isSamplePrep)
			return (new PrepAliquotDetail(getPage(),  new PreppedSampleDTO(samplePrepService.loadPreppedSampleByID(value)))
				{
				@Override
				protected void onSave(PreppedSampleDTO prepSample) { samplePrepService.updateAliquotVolume(prepSample);  }
				});
		
		return (new PreppedFractionDetail(getPage(), new PreppedSampleDTO(samplePrepService.loadPreppedItemByID(value)))
			{
				@Override
				protected void onSave(PreppedSampleDTO prepSample) { samplePrepService.updateFractionVolume(prepSample); }
            });
		}

	public void createNotesModal(ModalWindow modal1, AjaxRequestTarget target, final String plateType)
		{
		modal1.setInitialHeight(320);
		modal1.setInitialWidth(800);
		
		modal1.setPageCreator(new ModalWindow.PageCreator(){
             public Page createPage(){
                 return (new AddNotesPage(getPage())
                 	{
					@Override
					protected void onSave(String notes) 
						{
						samplePrepService.attachNotes(preparation,plateType, notes);
						NewNewPrepMethodsPanel.this.info("Observations noted for "+ plateType + " plate");
						}});
             	}});
    	
		modal1.show(target);
    	target.add(modal1);
		}
	
	
	
	public void setPreparation(String preparation)
		{
		this.preparation=preparation;
		
		if(preparation!=null)
			{
			//Preparation prep=samplePrepService.loadPreparationByID(preparation);
			List<PrepPlate> plates=samplePrepService.loadPlatesByPreparation(preparation);
			
			for (PrepPlate plate: plates)
				plateStr += plate.getInstrument().getName()+",";
			plateStr = plateStr.substring(0,plateStr.lastIndexOf(","));
			
			//cols = plates.get(0).getPlateFormat().equals("PF01") ? 12 : 9;
			//rows = plates.get(0).getPlateFormat().equals("PF01") ? 8 : 6;
			
			//colCheck = new CustomCheckbox[cols];
			//List items = null;
			//if (prep instanceof FractionPreparation)
			//	{
			//	isSamplePrep = false;
			//	items = fractionationService.loadPreppedFractions(preparation);
			//	}
			//else
			//	{
			//	items = samplePrepService.loadPreppedSamples(preparation);
			//	}
			
			/*PreppedItem[] values = null;
			values = new PreppedItem[items.size()];
			if(items.size()>0)
				{
				for (Iterator it = items.iterator(); it.hasNext();){
					PreppedItem item=(PreppedItem) it.next();
					values[item.getWell().getIndex()-1]=item;
					}
				}
			
			items = null; */
			//setSamples(getSelectableSamples(values));
			//values = null;
			}
		}
	
	public String getPreparation(){
		return preparation;
	}
	
	public void uploadFile()
		{
		BigDecimal[][] protienReadings=new BigDecimal[plateMap.getPlateRows()][plateMap.getPlateCols()];
        final FileUpload upload = fileUploadField.getFileUpload();
        
        if (upload != null)
        	{
            if(upload.getContentType().equalsIgnoreCase("application/vnd.ms-excel"))
            	{
                File newFile = new File(getUploadFolder(), upload.getClientFileName());
                checkFileExists(newFile);
                
                try
                	{
                    newFile.createNewFile();
                    upload.writeTo(newFile);
                	}
                catch (Exception e)
                	{
                    throw new IllegalStateException("Unable to write file");
                	}
                
                int	 rowCount = 0, cellCount = 0, sheetNum = 2;
                try
                	{
                	HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(newFile));
                    HSSFSheet sheet = workbook.getSheetAt(1);
                    Row row;
                    Cell cell;
                    Iterator<Row> sheet_rows = sheet.rowIterator ();
                    while(sheet_rows.hasNext())//((expGroupRow.getCell((short) 0).toString()!=null)&&(expGroupRow.getCell((short) 0).toString().trim().length()>0))
                    	{
                    	++rowCount;
                    	cellCount=0;
                    	row=sheet_rows.next();//sheet.getRow(rowCount++);
                    	if ((rowCount>1)&&(rowCount<10))
	                    	{
                			if((row.getCell((short) 1)==null)||(row.getCell((short) 1).toString()==null)||(row.getCell((short) 1).toString().trim().length()==0))
	                    		break;
                    		
                			Iterator<Cell> cells=row.cellIterator();
                    		while(cells.hasNext())
                    			{
                    			++cellCount;
                    			cell=cells.next();
                				if((cellCount>1)&&(cellCount<14))
                					{
                    				if((cell==null)||(cell.toString()==null)||(cell.toString().trim().length()==0))
                    					break;
                    				protienReadings[rowCount-2][cellCount-2]= new BigDecimal(cell.toString());
                    				System.out.println("["+(rowCount-2)+"]["+(cellCount-2)+"] "+protienReadings[rowCount-2][cellCount-2]);
                					}
                    			}
	                    	}
                    	}
                    
                    String errorAt=samplePrepService.assignProtienReadings(plateMap.getSamples(),protienReadings, plateMap.getPlateCols(), plateMap.getPlateRows());
                    
                    if(errorAt.equals("none"))
                    	NewNewPrepMethodsPanel.this.info("Save Successful!");
                    else if (errorAt.equals("0"))
                    	NewNewPrepMethodsPanel.this.error("No readings were uploaded. Please make sure that Protein Readings are in sheet 2 of the document being uploaded.");
                    else
                    	NewNewPrepMethodsPanel.this.error("Unable to upload file, error assigning value to sample in location: "+errorAt);
                	}
                catch (Exception e)
                	{
                	e.printStackTrace();
                	NewNewPrepMethodsPanel.this.error("Unable to upload file, error in sheet "+sheetNum +" at line: "+rowCount+" ,cell:"+cellCount);
                	}
            	}
        	}
		}
	
	 private void checkFileExists(File newFile)
	    {
	    if (newFile.exists())
	        {
	        if (!Files.remove(newFile))
	        	{
	            throw new IllegalStateException("Unable to overwrite " + newFile.getAbsolutePath());
	            }
	        }
	    }

    private Folder getUploadFolder()
    	{
    	Folder uploadFolder = new Folder(System.getProperty("java.io.tmpdir"), "sample-uploads");
        uploadFolder.mkdirs();
        return (uploadFolder);
    	}
    
	    
   	    private AjaxLink buildNotesLink(String id, final ModalWindow modal1, final NewNewPrepMethodsPanel pmp)
	    	{
		    return new AjaxLink("addNotes")
	        	{
	            @Override
	            public void onClick(AjaxRequestTarget target)
	            	{
	            	modal1.setInitialHeight(320);
	            	
	            	if(plateMap.getSelectedSamplesCount()==0)
	            		{  
	            		NewNewPrepMethodsPanel.this.error("Please select the sample(s) to annotate");
	            		target.add(pmp.getParent().getParent().getParent());
	            		return;
	            		}
	            	
	            	modal1.setPageCreator(new ModalWindow.PageCreator()
            			{
            			public Page createPage()
                    		{
            				return (new AddNotesPage(getPage())
                        		{
            					@Override
            					protected void onSave(String notes) 
									{
            						samplePrepService.attachNotes(plateMap.getSamples(), notes, plateMap.getPlateCols(), plateMap.getPlateRows());
            						int nSamples = plateMap.getSelectedSamplesCount();
            						NewNewPrepMethodsPanel.this.info("Observations noted for " + nSamples + " sample" + (nSamples == 1 ? "" : "s"));
									}
            					});
                    		}
            			});
            			
            			modal1.show(target);
            			target.add(modal1);
            			target.add(pmp.getParent().getParent().getParent());
            			}
	        	@Override // issue 464
				public MarkupContainer setDefaultModel(IModel model) 
				    {
					// TODO Auto-generated method stub
					return this;
				    }
	            	};
		    	}
	    
	    public void setSamplePrepService(SamplePrepService samplePrepService) 
		{
		this.samplePrepService = samplePrepService;
		}
	
	public void setFractionationService(FractionationService fractionationService) 
		{
		this.fractionationService = fractionationService;
		}
	    	}
		