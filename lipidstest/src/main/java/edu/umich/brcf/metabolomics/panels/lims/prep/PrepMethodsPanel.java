package edu.umich.brcf.metabolomics.panels.lims.prep;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.file.Folder;
import org.apache.wicket.util.string.Strings;

import edu.umich.brcf.metabolomics.layers.domain.FractionPreparation;
import edu.umich.brcf.metabolomics.layers.domain.GCDerivatizationMethod;
import edu.umich.brcf.metabolomics.layers.domain.GCPlate;
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
import edu.umich.brcf.shared.layers.domain.PrepPlate;
import edu.umich.brcf.shared.layers.domain.Preparation;
import edu.umich.brcf.shared.layers.domain.PreppedFraction;
import edu.umich.brcf.shared.layers.domain.PreppedItem;
import edu.umich.brcf.shared.layers.dto.BufferBean;
import edu.umich.brcf.shared.layers.dto.DilutionBean;
import edu.umich.brcf.shared.layers.dto.PreppedFractionDTO;
import edu.umich.brcf.shared.layers.dto.PreppedSampleDTO;
import edu.umich.brcf.shared.layers.service.SamplePrepService;
import edu.umich.brcf.shared.panels.utilitypanels.AddNotesPage;
import edu.umich.brcf.shared.util.structures.SelectableObject;
import edu.umich.brcf.shared.util.widgets.CustomCheckbox;


public class PrepMethodsPanel extends Panel
	{
	int cols=0;
	int rows=0;
	public static String[] rowIdentity ={"A","B","C","D","E","F","G","H"};
	CustomCheckbox[] colCheck;
	CustomCheckbox selectAll = new CustomCheckbox();
	SelectableObject[][] samples;
	AjaxLink link,lcNotes,gcNotes, derivatization, reconstitution;
	private FileUploadField fileUploadField;
	String preparation=null;
	boolean isSamplePrep=true;
	String plateStr="";
	
	@SpringBean
	SamplePrepService samplePrepService;
	
	@SpringBean
	FractionationService fractionationService;
	
	public void setSamplePrepService(SamplePrepService samplePrepService) {
		this.samplePrepService = samplePrepService;
	}
	
	public void setFractionationService(FractionationService fractionationService) {
		this.fractionationService = fractionationService;
	}


	public PrepMethodsPanel(String id, String preparation) {
		super(id);
		setPreparation(preparation);
		setOutputMarkupId(true);
		PrepMethodsPanel pmp = this;
		add(new PrepDetailForm("prepDetailForm", pmp));
	}

	public final class PrepDetailForm extends Form {
		public PrepDetailForm(final String id, final PrepMethodsPanel pmp){
			super(id);
			
			final ModalWindow modal1= new ModalWindow("modal1");
			 modal1.setInitialWidth(620);
	         modal1.setInitialHeight(320);
	         modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
	         {
	             public void onClose(AjaxRequestTarget target)
	             {
	             	target.add(pmp);
	             }
	         });
	         add(modal1);
	         final ModalWindow modal2= new ModalWindow("modal2");
	         add(modal2);
//	         add(new Label("sp", new Model(preparation)));
		     for (int k=0;k<cols;k++)
				colCheck[k]=new CustomCheckbox();
			 add(new Loop("rows", rows) {
					protected void populateItem(LoopItem item) {
				     final int row=item.getIndex();
				     item.add(new Label("rowIdentity", new Model(rowIdentity[row])));
				     item.add(new Loop("cols", cols) {
				    	 protected void populateItem(LoopItem item) {
				               final int col=item.getIndex();
				               IModel model=new IModel(){
									public Object getObject() {
										return samples[row][col];
									}

									public void setObject(Object arg0) {
										samples[row][col]=(SelectableObject) arg0;
									}

									public void detach() {
									}
				               };
				               item.add(new AjaxCheckBox("selected", new PropertyModel(model, "selected")){//
									public void onUpdate(AjaxRequestTarget target) {
											
									}
								}.setEnabled(samples[row][col].getSelectionObject()!=null));
//				               if(isSamplePrep){
				               PreppedSampleDTO dto = (PreppedSampleDTO) samples[row][col].getSelectionObject();
				               String item_id=(dto==null)?null:dto.getId();
					               AjaxLink sampleLink;
					               item.add(sampleLink=buildLinkToModal("sampleLink", modal1, item_id, pmp));
						            sampleLink.add(new Label("sample.sampleid", new PropertyModel(model, "selectionObject.sampleid")));
						            sampleLink.setOutputMarkupId(true);
//				               }
//				               else
//				            	   item.add(new Link("sampleLink", new Model()){
//				           			public void onClick() {
////				        				setResponsePage(new EditSample(getPage(), getModel()));
//				        			}
//				        		}.add(new Label("sample.sampleid", new PropertyModel(model, "sample"))));
				            	  
				    	 	}
						});
				   }
				});
			 add(new Loop("colNumbers", cols) {
				protected void populateItem(LoopItem item) {
					final int col=item.getIndex();
					 IModel model=new IModel() {
							public Object getObject() {
								return colCheck[col];
							}

							public void setObject(Object arg0) {
								colCheck[col]=(CustomCheckbox) arg0;
							}

							public void detach() {
							}
		               };
					item.add(new AjaxCheckBox("colCheck",new PropertyModel(colCheck[col], "selected")) {
						public void onUpdate(AjaxRequestTarget target) {
							final String value = getValue();
							  if (value != null)
							  {
								  updateColSelection(col, Strings.isTrue(value));
								  target.add(pmp);
							  }
						}
					});
					item.add(new Label("colNum", new Model(item.getIndex()+1)));
				}});
			 add(new AjaxCheckBox("selectAll",new PropertyModel(selectAll, "selected")) {
					public void onUpdate(AjaxRequestTarget target) {
						final String value = getValue();
						  if (value != null)
						  {
							  for(int col=0;col<cols;col++)
								  updateColSelection(col, Strings.isTrue(value));
							  target.add(pmp);
						  }
					}
				});
			 add(buildLinkToModal("buffer", modal1, null, pmp).setVisible(isSamplePrep));
			 add(buildLinkToModal("homogenization", modal1, null, pmp).setVisible(isSamplePrep));
			 add(buildLinkToModal("dilution", modal1, null, pmp));
			 add(buildLinkToModal("protienDetermination", modal1,null, pmp).setVisible(isSamplePrep));
			 add(buildLinkToModal("generalPrep", modal1, null, pmp).setVisible(isSamplePrep));
			 add(buildLinkToModal("absorbance", modal1, null, pmp).setVisible(isSamplePrep));
		     // issue 39
			 add(new AjaxLink <Void>("addNotes")
		        {
		            @Override
		            public void onClick(AjaxRequestTarget target)
		            {
		            	modal1.setInitialHeight(320);
		            	if(getSelectedSamplesCount()==0){
		            		PrepMethodsPanel.this.error("Please select the sample(s) for which you want to note Observations!");}
		            	else{
		            		modal1.setPageCreator(new ModalWindow.PageCreator(){
		                     public Page createPage(){
		                         return (new AddNotesPage(getPage()){
									@Override
									protected void onSave(String notes) {
										samplePrepService.attachNotes(getSamples(), notes, cols, rows);
										PrepMethodsPanel.this.info("Observations noted for "+getSelectedSamplesCount()+" samples!");
									}});
		                     }});
		            	 modal1.show(target);
		            	 target.add(modal1);
		            	}
		            	target.add(pmp.getParent().getParent().getParent());
	            	}
				});
		     setMultiPart(true);
//	         add(fileUploadField = new FileUploadField("fileInput"));
//	         fileUploadField.setVisible(isSamplePrep);
//	         add(new Label("fileUploadText", "Upload Protien Readings").setVisible(isSamplePrep));
//	         add(new Label("fileText", "File").setVisible(isSamplePrep));
//	         add(new Button("upload"){
//	        	 @Override
//				public void onSubmit()
//	             {
//	        		 uploadFile();
//	             }
//	         }.setVisible(isSamplePrep));
			 add(new Label("plates", new Model(plateStr)));

			 add(derivatization=buildLinkToModal("derivatization", modal1, null, pmp));
			 add(reconstitution=buildLinkToModal("reconstitution", modal1, null, pmp));
			 derivatization.setVisible(isSamplePrep);
			 reconstitution.setVisible(isSamplePrep);
			 add(buildLinkToModal("createNewPlate", modal1, null, pmp));
			 // issue 39
			 add(gcNotes=new AjaxLink<Void>("gcNotes")
		        {
		            @Override
		            public void onClick(AjaxRequestTarget target)
		            {
		            	createNotesModal(modal1, target,"GC");
					}
		        });
			 // issue 39
			 add(lcNotes=new AjaxLink <Void>("lcNotes")
		        {
		            @Override
		            public void onClick(AjaxRequestTarget target)
		            {
		            	createNotesModal(modal1, target,"LC");
					}
		        });
			 add(buildLinkToModal("addSample", modal1, null, pmp).setVisible(!isSamplePrep));
			 checkComponentVisibility();
			 
	    }
	}
	public void checkComponentVisibility(){
		if(preparation!=null){
			gcNotes.setVisible(false);
			lcNotes.setVisible(false);
			derivatization.setVisible(false);
			reconstitution.setVisible(false);
			List<PrepPlate> plates=samplePrepService.loadPlatesByPreparation(preparation);
			for (PrepPlate plate: plates){
				if (plate instanceof GCPlate){
					gcNotes.setVisible(true);
					if(isSamplePrep)
						derivatization.setVisible(true);
				}
				else{
					lcNotes.setVisible(true);
					if(isSamplePrep)
						reconstitution.setVisible(true);
				}
			}
		}
	}
	
	public AjaxLink buildLinkToModal(final String linkID, final ModalWindow modal1, final String value, final PrepMethodsPanel pmp) {
		// issue 39
		return new AjaxLink <Void>(linkID)
        	{
			@Override 
			public boolean isEnabled()
				{
				//if(!linkID.startsWith("s")&&!linkID.startsWith("der")&&!linkID.startsWith("r")&&!linkID.startsWith("c")&&!linkID.startsWith("ab")&&!linkID.startsWith("ad"))
	            //		return (getSelectedSamplesCount()!=0);
	            
	            return true;
				}

			@Override
            public void onClick(AjaxRequestTarget target)
            	{
            	if(!linkID.startsWith("s")&&!linkID.startsWith("der")&&!linkID.startsWith("r")&&!linkID.startsWith("c")&&!linkID.startsWith("ab")&&!linkID.startsWith("ad"))
            		{
            		if(getSelectedSamplesCount()==0)
            			{
            			String msg = "Please select at least one sample to assign methods.";
	            		//PrepMethodsPanel.this.error(msg);
	            		//target.add(pmp.getParent().getParent().getParent());
	            		target.appendJavaScript("alert('" + msg + "')");
	            		return;
	            		}
            		}
            	
            	if(linkID.startsWith("addS")){
            		List<PreppedFraction> smpList=fractionationService.loadPreppedFractions(preparation);
            		int noCells=cols*rows;
	            	 if(!(smpList.size()<noCells)){
	            		 PrepMethodsPanel.this.error("Plate is full!!! Please create another worklist! ");
	            		 target.add(pmp.getParent().getParent().getParent());
	            		 return;
	            	 }
            	}
            	if(linkID.startsWith("s")||linkID.startsWith("g")||linkID.startsWith("h")||linkID.startsWith("c"))modal1.setInitialHeight(430); 
            	else modal1.setInitialHeight(200);
            	modal1.setPageCreator(new ModalWindow.PageCreator(){
                     public Page createPage(){
                    	if(linkID.startsWith("s")){
                    		if (isSamplePrep){
                    	
                    			return (new PrepAliquotDetail(getPage(), 
                        		 new PreppedSampleDTO(samplePrepService.loadPreppedSampleByID(value))){

							@Override
							protected void onSave(PreppedSampleDTO prepSample) {
								samplePrepService.updateAliquotVolume(prepSample);
							}
                         });} 
                    		else{
                    			return (new PreppedFractionDetail(getPage(), 
                               		 new PreppedSampleDTO(samplePrepService.loadPreppedItemByID(value))){

       							@Override
       							protected void onSave(PreppedSampleDTO prepSample) {
       								samplePrepService.updateFractionVolume(prepSample);
       							}
                                });}
                    	}
                    	 else if(linkID.startsWith("g")){
                    		return (new EditGeneralPrep(getPage(), GeneralPrepDTO.instance(samplePrepService.loadGeneralPrepSOPByID(GeneralPrepSOP.DEFAULT_SOP))){

								@Override
								protected void onSave(GeneralPrepSOP sop) {
									samplePrepService.assignGeneralPrep(getSamples(), sop, cols, rows);
								}

								
	                         });
	                    	 }
//                    		 return (new EditCrashPage(getPreparation()){
//
// 								@Override
// 								protected void onSave(GeneralPrepSOP sop, List<SelectableSample> platesSelected, List<SelectableSample> itemsSelected) {
// 									for(SelectableSample ss : platesSelected)
//	 									System.out.println(((PrepPlate)ss.getSample()).getPlateID()+" "+ss.isSelected());
//	 								for(SelectableSample ss : itemsSelected)
//	 									System.out.println(((PreppedSampleDTO)ss.getSample()).getId()+" "+ss.isSelected());
// 								}
// 	                         });
// 	                    	 }
                    	 else if(linkID.startsWith("h")){
                    		return (new EditHomogenization(getPage(), 
                    				 HomogenizationDTO.instance(samplePrepService.loadHomogenizationByID
                    						 (HomogenizationSOP.DEFAULT_SOP)))
                    			{
                    			@Override
 								protected void onSave(HomogenizationSOP sop) {
	 									samplePrepService.assignHomogenization(getSamples(), sop, cols, rows);
 								}
 	                         });
//                    		 return (new EditHomogenizationPage(getPreparation()){
//
// 								@Override
// 								protected void onSave(HomogenizationSOP sop, List<SelectableSample> platesSelected, List<SelectableSample> itemsSelected) {
//	 								for(SelectableSample ss : platesSelected)
//	 									System.out.println(((PrepPlate)ss.getSample()).getPlateID()+" "+ss.isSelected());
//	 								for(SelectableSample ss : itemsSelected)
//	 									System.out.println(((PreppedSampleDTO)ss.getSample()).getId()+" "+ss.isSelected());
// 								}
// 	                         });
                    	 }
                    	 else if(linkID.startsWith("p")){
                    		return (new EditProtienDetermination(getPage(), 
                    				ProteinDeterminationDTO.instance(samplePrepService.loadProtienDeterminationSOPByID(ProtienDeterminationSOP.DEFAULT_SOP))){

 								@Override
 								protected void onSave(ProtienDeterminationSOP sop) {
	 									samplePrepService.assignProtienDetermination(getSamples(), sop, cols, rows);
 								}
 	                         });}
//                    		 return (new EditProtienDeterminationPage(getPreparation()){
//
// 								@Override
// 								protected void onSave(ProtienDeterminationSOP sop, List<SelectableSample> platesSelected, List<SelectableSample> itemsSelected) {
// 									for(SelectableSample ss : platesSelected)
//	 									System.out.println(((PrepPlate)ss.getSample()).getPlateID()+" "+ss.isSelected());
//	 								for(SelectableSample ss : itemsSelected)
//	 									System.out.println(((PreppedSampleDTO)ss.getSample()).getId()+" "+ss.isSelected());
// 								}
// 	                         });}
                    	 else if(linkID.startsWith("ab")){
                     		return (new EditAbsorbancePage(getPreparation(), cols, rows){

//  								@Override
//  								protected void onSave(ProtienDeterminationSOP sop) {
// 	 									samplePrepService.assignProtienDetermination(getSamples(), sop);
//  								}
  	                         });}
                    	 else if(linkID.startsWith("addS")){
                    		 return (new AddSampleToPrepPage(){

   								@Override
								protected void onSave(PreppedFractionDTO frDto) {
   									fractionationService.addFractionToPrep(frDto, getPreparation());
   									setPreparation(getPreparation());
								}
   	                         });}
                    		 
                    	 else if(linkID.startsWith("dil")){
                    	 	return (new EditDilution(getPage(), new DilutionBean(), isSamplePrep){

  								@Override
  								protected void onSave(DilutionBean bean) {
 	 									samplePrepService.assignDilution(getSamples(), bean, isSamplePrep, cols, rows);
  								}
  	                         });}
                    	 else if(linkID.startsWith("b")){
                     		return (new EditBuffer(getPage(), new BufferBean()){

  								@Override
  								protected void onSave(BufferBean bean) {
 	 									samplePrepService.assignBuffer(getSamples(), bean, cols, rows);
  								}
								
  	                         });}
                    	 else if(linkID.startsWith("c")){
                    		 return (new CreateNewPlatePage(getPage()){

   								@Override
   								protected void onSave(List<String> instrumentName) {
  	 									samplePrepService.createPlates(getPreparation(),instrumentName);
   								}
   	                         });
                    	 }
                    	 else if(linkID.startsWith("der")){
                      		return (new EditGCPrep(getPage(), GCDerivatizationDTO.instance(samplePrepService.loadGCDerivatizationByID(GCDerivatizationMethod.DEFAULT_SOP)))
                      			{

   								@Override
   								protected void onSave(GCDerivatizationMethod bean) 
   									{
   									GCDerivatizationMethod m;
  	 								samplePrepService.assignDerivatization(getPreparation(), bean);
   									}
                      			});}
                    	 else {
                      		return (new EditLCPrep(getPage(), LCReconstitutionDTO.instance(samplePrepService.loadLCReconstitutionByID(LCReconstitutionMethod.DEFAULT_SOP))){

   								@Override
   								protected void onSave(LCReconstitutionMethod bean) {
  	 									samplePrepService.assignReconstitution(getPreparation(), bean);
   								}

								
   	                         });}
                     }
                 });
            	target.add(this);
            	target.add(pmp.getParent().getParent().getParent());
            	modal1.show(target);
            }
        };
	}
	
	public void createNotesModal(ModalWindow modal1, AjaxRequestTarget target, final String plateType){
		modal1.setInitialHeight(320);
		modal1.setPageCreator(new ModalWindow.PageCreator(){
             public Page createPage(){
                 return (new AddNotesPage(getPage()){
					@Override
					protected void onSave(String notes) {
						samplePrepService.attachNotes(preparation,plateType, notes);
						PrepMethodsPanel.this.info("Observations noted for "+plateType+" Plate!");
					}});
             }});
    	 modal1.show(target);
    	 target.add(modal1);
	}
	
	public void setSamples(SelectableObject[][] samples) {
		this.samples=samples;
	}

	public SelectableObject[][] getSamples() {
		return samples;
	}

	public SelectableObject[][] getSelectableSamples(PreppedItem[] values){
		SelectableObject[][] samples = new SelectableObject[rows][cols];
		int index=0;
		for (int j=0;j<cols;j++){
			for (int i=0;i<rows;i++){
				if((index<values.length)&&(values[index]!=null)){
//					if(values[index] instanceof PreppedSample)
//						samples[i][j] = new SelectableSample(((PreppedSample)values[index++]).getSample().getSampleID());//getItemID()//new PreppedSampleDTO(values[i][j],"100","ul", null, null));
//					else
//						samples[i][j] = new SelectableSample(((PreppedFraction)values[index++]).getFraction().getSampleID());
					samples[i][j] = new SelectableObject(new PreppedSampleDTO(values[index++]));//new SelectableSample(values[index++].getItemID());//new PreppedSampleDTO(values[i][j],"100","ul", null, null));
				}
				else
					samples[i][j] = new SelectableObject();
			}
		}
		return samples;
	}
	
	protected void updateColSelection(int col, boolean value) {
		SelectableObject[][] samples = getSamples();
		for (int i=0;i<rows;i++){
			if(samples[i][col].getSelectionObject()!=null)
					samples[i][col].setSelected(value);
		}
		setSamples(samples);
	}
	
//	private class customChekbox implements Serializable {
//		private Boolean selected = Boolean.FALSE;
//
//		public customChekbox() {
//		}
//
//		public Boolean getSelected() {
//			return selected;
//		}
//
//		public void setSelected(Boolean selected) {
//			this.selected = selected;
//		}
//	}
	
	private int getSelectedSamplesCount(){
		SelectableObject[][] samples = getSamples();
		int count=0;
		for (int i=0;i<rows;i++){
			for (int j=0;j<cols;j++){
				if(samples[i][j].isSelected()){
					++count;
				}
			}
		}
		return count;
	}

	public void setPreparation(String preparation){
		this.preparation=preparation;
		if(preparation!=null){
			Preparation prep=samplePrepService.loadPreparationByID(preparation);
			List<PrepPlate> plates=samplePrepService.loadPlatesByPreparation(preparation);
			for (PrepPlate plate: plates)
				plateStr+=plate.getInstrument().getName()+",";
			plateStr=plateStr.substring(0,plateStr.lastIndexOf(","));
			if(plates.get(0).getPlateFormat().equals("PF01"))
			{cols=12;rows=8;}
			else
			{cols=9;rows=6;}
			colCheck = new CustomCheckbox[cols];
			List items=null;
			if (prep instanceof FractionPreparation){
				isSamplePrep=false;
				items =fractionationService.loadPreppedFractions(preparation);
			}
			else{
			items=samplePrepService.loadPreppedSamples(preparation);}
			PreppedItem[] values=null;
			values=new PreppedItem[items.size()];
			if(items.size()>0)
			{
				for (Iterator it = items.iterator(); it.hasNext();){
					PreppedItem item=(PreppedItem) it.next();
					values[item.getWell().getIndex()-1]=item;
				}
			}
			items=null;
			setSamples(getSelectableSamples(values));
			values=null;
		}
	}
	
	public String getPreparation(){
		return preparation;
	}
	
	public void uploadFile(){
		BigDecimal[][] protienReadings=new BigDecimal[rows][cols];
        final FileUpload upload = fileUploadField.getFileUpload();
        if (upload != null)
        {
            if(upload.getContentType().equalsIgnoreCase("application/vnd.ms-excel"))
            {
                // Create a new file
                File newFile = new File(getUploadFolder(), upload.getClientFileName());
                checkFileExists(newFile);
                try
                {
                    // Save to new file
                    newFile.createNewFile();
                    upload.writeTo(newFile);
                }catch (Exception e)
                {
                    throw new IllegalStateException("Unable to write file");
                }
                int rowCount=0,cellCount=0,sheetNum=2;
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
                    		 while(cells.hasNext()){
                    			 ++cellCount;
                    			 cell=cells.next();
                				 if((cellCount>1)&&(cellCount<14)){
                    				 if((cell==null)||(cell.toString()==null)||(cell.toString().trim().length()==0))
         	                    		break;
                    				 protienReadings[rowCount-2][cellCount-2]= new BigDecimal(cell.toString());
                    				 System.out.println("["+(rowCount-2)+"]["+(cellCount-2)+"] "+protienReadings[rowCount-2][cellCount-2]);
                    			 }
                    		 }
                    	}
                    }
                    String errorAt=samplePrepService.assignProtienReadings(getSamples(),protienReadings, cols, rows);
                    if(errorAt.equals("none"))
                    	PrepMethodsPanel.this.info("Save Successful!");
                    else if (errorAt.equals("0"))
                    	PrepMethodsPanel.this.error("0 readings were uploaded! Please make sure that Protein Readings are in sheet 2 of the document being uploaded!");
                    else
                    	PrepMethodsPanel.this.error("Unable to upload file, error assigning value to sample in location: "+errorAt);
	            }
                catch (Exception e)
                {
                	e.printStackTrace();
                	PrepMethodsPanel.this.error("Unable to upload file, error in sheet "+sheetNum +" at line: "+rowCount+" ,cell:"+cellCount);
                }
            }
        }
    }
	
	 private void checkFileExists(File newFile)
	    {
	        if (newFile.exists())
	        {
	            // Try to delete the file
	            if (!Files.remove(newFile))
	            {
	                throw new IllegalStateException("Unable to overwrite " + newFile.getAbsolutePath());
	            }
	        }
	    }

	    private Folder getUploadFolder()
	    {
	    	Folder uploadFolder = new Folder(System.getProperty("java.io.tmpdir"), "sample-uploads");
	        // Ensure folder exists
	        uploadFolder.mkdirs();
	        return (uploadFolder);
	    }
}

















/*package edu.umich.metworks.web.panels.lims.preparations;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.file.Folder;
import org.apache.wicket.util.string.Strings;

import edu.umich.metworks.lims.domain.FractionPreparation;
import edu.umich.metworks.lims.domain.GCDerivatizationMethod;
import edu.umich.metworks.lims.domain.GCPlate;
import edu.umich.metworks.lims.domain.GeneralPrepSOP;
import edu.umich.metworks.lims.domain.HomogenizationSOP;
import edu.umich.metworks.lims.domain.LCReconstitutionMethod;
import edu.umich.metworks.lims.domain.PrepPlate;
import edu.umich.metworks.lims.domain.Preparation;
import edu.umich.metworks.lims.domain.PreppedFraction;
import edu.umich.metworks.lims.domain.PreppedItem;
import edu.umich.metworks.lims.domain.ProtienDeterminationSOP;
import edu.umich.metworks.lims.dto.GCDerivatizationDTO;
import edu.umich.metworks.lims.dto.GeneralPrepDTO;
import edu.umich.metworks.lims.dto.HomogenizationDTO;
import edu.umich.metworks.lims.dto.LCReconstitutionDTO;
import edu.umich.metworks.lims.dto.PreppedFractionDTO;
import edu.umich.metworks.lims.dto.PreppedSampleDTO;
import edu.umich.metworks.lims.dto.ProtienDeterminationDTO;
import edu.umich.metworks.lims.service.FractionationService;
import edu.umich.metworks.lims.service.SamplePrepService;
import edu.umich.metworks.web.panels.lims.fractionation.AddSampleToPrepPage;
import edu.umich.metworks.web.panels.lims.fractionation.PreppedFractionDetail;
import edu.umich.metworks.web.utils.AddNotesPage;
import edu.umich.metworks.web.utils.CustomCheckbox;
import edu.umich.metworks.web.utils.SelectableObject;

public class PrepMethodsPanel extends Panel
	{
	@SpringBean
	SamplePrepService samplePrepService;
	
	@SpringBean
	FractionationService fractionationService;
	
	int cols = 0, rows = 0;
	String preparation=null, plateStr = "";
	boolean isSamplePrep=true;

	public static String[] rowIdentity ={"A","B","C","D","E","F","G","H"};
	
	CustomCheckbox[] colCheck;
	CustomCheckbox selectAll = new CustomCheckbox();
	SelectableObject[][] samples;
	AjaxLink link,lcNotes,gcNotes, derivatization, reconstitution;
	
	private FileUploadField fileUploadField;
	
	
	public void setSamplePrepService(SamplePrepService samplePrepService) 
		{
		this.samplePrepService = samplePrepService;
		}
	
	public void setFractionationService(FractionationService fractionationService) 
		{
		this.fractionationService = fractionationService;
		}

	public PrepMethodsPanel(String id, String preparation) 
		{
		super(id);
		setPreparation(preparation);
		setOutputMarkupId(true);
		PrepMethodsPanel pmp = this;
		add(new PrepDetailForm("prepDetailForm", pmp));
		}

	public final class PrepDetailForm extends Form 
		{
		public PrepDetailForm(final String id, final PrepMethodsPanel pmp)
			{
			super(id);
			
			final ModalWindow modal1= new ModalWindow("modal1");
			modal1.setInitialWidth(620);
	        modal1.setInitialHeight(320);
	        
	        modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
	        	{
	            public void onClose(AjaxRequestTarget target) { target.add(pmp); }
	         	});
	         
	        add(modal1);
	        final ModalWindow modal2= new ModalWindow("modal2");
	        add(modal2);

	        for (int k=0;k<cols;k++)
				colCheck[k]=new CustomCheckbox();
			 
		    add(new Loop("rows", rows) 
	     		{
				protected void populateItem(LoopItem item) 
					{
					final int row=item.getIteration();
					item.add(new Label("rowIdentity", new Model(rowIdentity[row])));
			    	item.add(buildColsLoop(cols, row, modal1,pmp));
			     	}
	     		});
	     
			add(buildColsLoop(cols, pmp));
			add(grabSelectAllBox(pmp)); 
			  
			add(buildLinkToModal("buffer", modal1, null, pmp).setVisible(isSamplePrep));
			add(buildLinkToModal("homogenization", modal1, null, pmp).setVisible(isSamplePrep));
			add(buildLinkToModal("dilution", modal1, null, pmp));
			add(buildLinkToModal("protienDetermination", modal1,null, pmp).setVisible(isSamplePrep));
			add(buildLinkToModal("generalPrep", modal1, null, pmp).setVisible(isSamplePrep));
			add(buildLinkToModal("absorbance", modal1, null, pmp).setVisible(isSamplePrep));
		    add(buildNotesLink("addNotes", modal1, pmp));
		    setMultiPart(true);
			add(new Label("plates", new Model(plateStr)));

			add(derivatization = buildLinkToModal("derivatization", modal1, null, pmp));
			derivatization.setVisible(isSamplePrep);
			
			add(reconstitution=buildLinkToModal("reconstitution", modal1, null, pmp));
			reconstitution.setVisible(isSamplePrep);
			
			add(buildLinkToModal("createNewPlate", modal1, null, pmp));
			add(gcNotes = grabLinkToNotesModal("gcNotes", "GC", modal1));
			add(lcNotes = grabLinkToNotesModal("lcNotes", "LC", modal1));
			add(buildLinkToModal("addSample", modal1, null, pmp).setVisible(!isSamplePrep));
	
			checkComponentVisibility();
			}
		}
	

	private AjaxCheckBox grabSelectAllBox(final PrepMethodsPanel pmp)
		{
		return new AjaxCheckBox("selectAll",new PropertyModel(selectAll, "selected")) 
		 	{
			public void onUpdate(AjaxRequestTarget target) 	
				{
				final String value = getValue();
				if (value != null)
					{
					for(int col=0;col<cols;col++)
						updateColSelection(col, Strings.isTrue(value));
					target.add(pmp);
					}
				}
		 	};
		}
	
	
	private AjaxLink grabLinkToNotesModal(String id, final String tag, final ModalWindow modal1)
		{
		return new AjaxLink(id)
	        {
	        @Override
	        public void onClick(AjaxRequestTarget target) { createNotesModal(modal1, target,tag); }
	        };
		}

	public void checkComponentVisibility()
		{
		if(preparation!=null)
			{
			gcNotes.setVisible(false);
			lcNotes.setVisible(false);
			derivatization.setVisible(false);
			reconstitution.setVisible(false);
			
			List<PrepPlate> plates=samplePrepService.loadPlatesByPreparation(preparation);
			
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
				}
			}
		}
	
	
	public AjaxLink buildLinkToModal(final String linkID, final ModalWindow modal1, final String value, final PrepMethodsPanel pmp) 
		{
		return new AjaxLink(linkID)
        	{
			@Override
            public void onClick(AjaxRequestTarget target)
            	{
				if (checkForNoSelection(linkID, target))
					return;
            	
            	if(linkID.startsWith("addS") && checkForFullPlate(target, pmp))
            		return;
            	
            	boolean oneTypeLink = (linkID.startsWith("s")||linkID.startsWith("g")||linkID.startsWith("c"));
            	modal1.setInitialHeight(oneTypeLink ? 430 : 200); 
            	if (linkID.equals("homogenization")) modal1.setInitialHeight(300);
            	
            	modal1.setPageCreator(new ModalWindow.PageCreator()
            		{
                    public Page createPage()
                    	{
                    	switch (linkID)
                    	{
                    	//case "s" : return grabAliquotOrFractionPage(value);
                    	case "generalPrep" : 	return grabGeneralPrepPage();
                    	case "homogenization" :  return grabEditHomogenizationPage();
                    	case "proteinDetermination" :  return grabEditProteinDeterminationPage();
                    	case "absorbance" :	 return grabEditAbsorbancePage();
                    	case "addSample" :   return grabAddSampleToPrepPage();
                    	case "dilution" : return grabEditDilutionPage();
                    	case "buffer" :  return grabEditBufferPage();
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
        	};
		}


	private boolean checkForFullPlate(AjaxRequestTarget target, PrepMethodsPanel pmp)
		{
		List<PreppedFraction> smpList=fractionationService.loadPreppedFractions(preparation);
		
    	if(!(smpList.size() < cols * rows))
    	 	{
    		PrepMethodsPanel.this.error("Plate is full!!! Please create another worklist! ");
    		target.add(pmp.getParent().getParent().getParent());
    		return true;
    	 	}
    	 
    	return false;
		}

	private boolean checkForNoSelection(String linkID, AjaxRequestTarget target)
		{
    	if(!linkID.startsWith("s")&&!linkID.startsWith("der")&&!linkID.startsWith("r")&&!linkID.startsWith("c")&&!linkID.startsWith("ab")&&!linkID.startsWith("ad"))
			if(getSelectedSamplesCount()==0)
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
			protected void onSave(BufferBean bean) { samplePrepService.assignBuffer(getSamples(), bean, cols, rows); }
			});
		}

	
	private Page grabEditDilutionPage()
		{
 		return (new EditDilution(getPage(), new DilutionBean(), isSamplePrep)
 			{
			@Override
			protected void onSave(DilutionBean bean) { samplePrepService.assignDilution(getSamples(), bean, isSamplePrep, cols, rows); }
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
 		return (new EditAbsorbancePage(getPreparation(), cols, rows)
 			{
//			@Override
//			protected void onSave(ProtienDeterminationSOP sop) {
//					samplePrepService.assignProtienDetermination(getSamples(), sop);
//			}
           });
 		}
	
	private Page grabEditProteinDeterminationPage()
		{
		return (new EditProtienDetermination(getPage(), 
				 ProtienDeterminationDTO.instance(samplePrepService.loadProtienDeterminationSOPByID(ProtienDeterminationSOP.DEFAULT_SOP)))
			{
			@Override
			protected void onSave(ProtienDeterminationSOP sop) 
				{
				samplePrepService.assignProtienDetermination(getSamples(), sop, cols, rows);
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
			protected void onSave(HomogenizationSOP sop) {  samplePrepService.assignHomogenization(getSamples(), sop, cols, rows); }
			});
		}
	
	
	private Page grabGeneralPrepPage()
		{
		return (new EditGeneralPrep(getPage(), GeneralPrepDTO.instance(samplePrepService.loadGeneralPrepSOPByID(GeneralPrepSOP.DEFAULT_SOP))){

			@Override
			protected void onSave(GeneralPrepSOP sop) {
				samplePrepService.assignGeneralPrep(getSamples(), sop, cols, rows);
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
		modal1.setInitialWidth(650);
		
		modal1.setPageCreator(new ModalWindow.PageCreator(){
             public Page createPage(){
                 return (new AddNotesPage(getPage())
                 	{
					@Override
					protected void onSave(String notes) 
						{
						samplePrepService.attachNotes(preparation,plateType, notes);
						PrepMethodsPanel.this.info("Observations noted for "+ plateType + " plate");
						}});
             	}});
    	
		modal1.show(target);
    	target.add(modal1);
		}
	
	public void setSamples(SelectableObject[][] samples) {
		this.samples=samples;
	}

	public SelectableObject[][] getSamples() {
		return samples;
	}

	public SelectableObject[][] getSelectableSamples(PreppedItem[] values)
		{
		SelectableObject[][] samples = new SelectableObject[rows][cols];
		int index = 0;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				{
				if((index < values.length)&&(values[index]!=null))
					samples[i][j] = new SelectableObject(new PreppedSampleDTO(values[index++]));//new SelectableSample(values[index++].getItemID());//new PreppedSampleDTO(values[i][j],"100","ul", null, null));
				
				else
					samples[i][j] = new SelectableObject();
				}
		
		return samples;
		}
	
	protected void updateColSelection(int col, boolean value) 
		{
		SelectableObject[][] samples = getSamples();
		
		for (int i=0;i<rows;i++)
			if(samples[i][col].getSelectionObject()!=null)
					samples[i][col].setSelected(value);
		
		setSamples(samples);
		}
	

	private int getSelectedSamplesCount()
		{
		SelectableObject[][] samples = getSamples();
		
		int count=0;
		for (int i = 0; i < rows; i++)
			for (int j=0;j<cols;j++)
				if(samples[i][j].isSelected())
					++count;
		
		return count;
		}

	public void setPreparation(String preparation)
		{
		this.preparation=preparation;
		
		if(preparation!=null)
			{
			Preparation prep=samplePrepService.loadPreparationByID(preparation);
			List<PrepPlate> plates=samplePrepService.loadPlatesByPreparation(preparation);
			
			for (PrepPlate plate: plates)
				plateStr += plate.getInstrument().getName()+",";
			plateStr = plateStr.substring(0,plateStr.lastIndexOf(","));
			
			cols = plates.get(0).getPlateFormat().equals("PF01") ? 12 : 9;
			rows = plates.get(0).getPlateFormat().equals("PF01") ? 8 : 6;
			
			colCheck = new CustomCheckbox[cols];
			List items = null;
			if (prep instanceof FractionPreparation)
				{
				isSamplePrep = false;
				items = fractionationService.loadPreppedFractions(preparation);
				}
			else
				{
				items = samplePrepService.loadPreppedSamples(preparation);
				}
			
			PreppedItem[] values = null;
			values = new PreppedItem[items.size()];
			if(items.size()>0)
				{
				for (Iterator it = items.iterator(); it.hasNext();){
					PreppedItem item=(PreppedItem) it.next();
					values[item.getWell().getIndex()-1]=item;
					}
				}
			
			items = null;
			setSamples(getSelectableSamples(values));
			values = null;
			}
		}
	
	public String getPreparation(){
		return preparation;
	}
	
	public void uploadFile()
		{
		BigDecimal[][] protienReadings=new BigDecimal[rows][cols];
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
                    
                    String errorAt=samplePrepService.assignProtienReadings(getSamples(),protienReadings, cols, rows);
                    
                    if(errorAt.equals("none"))
                    	PrepMethodsPanel.this.info("Save Successful!");
                    else if (errorAt.equals("0"))
                    	PrepMethodsPanel.this.error("0 readings were uploaded! Please make sure that Protein Readings are in sheet 2 of the document being uploaded!");
                    else
                    	PrepMethodsPanel.this.error("Unable to upload file, error assigning value to sample in location: "+errorAt);
                	}
                catch (Exception e)
                	{
                	e.printStackTrace();
                	PrepMethodsPanel.this.error("Unable to upload file, error in sheet "+sheetNum +" at line: "+rowCount+" ,cell:"+cellCount);
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
    
	    
    private Loop buildColsLoop(int cols, final int row, final ModalWindow modal1, final PrepMethodsPanel pmp)
    	{
    	return	new Loop("cols", cols) 
     		{
    		protected void populateItem(LoopItem item) 
    	 		{
    			final int col=item.getIteration();
    			
    			IModel model=new IModel()
    				{
					public Object getObject() { return samples[row][col]; }

					public void setObject(Object arg0) {samples[row][col]=(SelectableObject) arg0; }

					public void detach() { }
    				};
               
    				
    			item.add(new AjaxCheckBox("selected", new PropertyModel(model, "selected"))
               		{
					public void onUpdate(AjaxRequestTarget target) {  }
               		}.setEnabled(samples[row][col].getSelectionObject()!=null));
               
    			PreppedSampleDTO dto = (PreppedSampleDTO) samples[row][col].getSelectionObject();
    			String item_id=(dto==null)?null:dto.getId();
	               
    			AjaxLink sampleLink;
    			item.add(sampleLink=buildLinkToModal("sampleLink", modal1, item_id, pmp));
    			sampleLink.add(new Label("sample.sampleid", new PropertyModel(model, "selectionObject.sampleid")));
    			sampleLink.setOutputMarkupId(true);
    	 		}
     		};
    	}
	    
	    
	    private Loop buildColsLoop(int cols, final PrepMethodsPanel pmp)
	    	{
	    	return new Loop("colNumbers", cols) 
		 		{
	    		protected void populateItem(LoopItem item) 
					{
					final int col=item.getIteration();
					 
					IModel model=new IModel() 
						{
						public Object getObject() { return colCheck[col]; }
	
						public void setObject(Object arg0) { colCheck[col]=(CustomCheckbox) arg0; }
	
						public void detach() { }
						};
	
					item.add(new AjaxCheckBox("colCheck",new PropertyModel(colCheck[col], "selected")) 
						{
						public void onUpdate(AjaxRequestTarget target) 
							{
							final String value = getValue();
							  if (value != null)
							  	{
								updateColSelection(col, Strings.isTrue(value));
								 target.add(pmp);
							  	}
							}
						});
					
					item.add(new Label("colNum", new Model(item.getIteration()+1)));
					}
		 		};
			}
	    
	    private AjaxLink buildNotesLink(String id, final ModalWindow modal1, final PrepMethodsPanel pmp)
	    	{
		    return new AjaxLink("addNotes")
	        	{
	            @Override
	            public void onClick(AjaxRequestTarget target)
	            	{
	            	modal1.setInitialHeight(320);
	            	
	            	if(getSelectedSamplesCount()==0)
	            		{  
	            		PrepMethodsPanel.this.error("Please select the sample(s) to annotate");
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
            						samplePrepService.attachNotes(getSamples(), notes, cols, rows);
            						PrepMethodsPanel.this.info("Observations noted for "+getSelectedSamplesCount()+" samples!");
									}
            					});
                    		}
            			});
            			
            			modal1.show(target);
            			target.add(modal1);
            			target.add(pmp.getParent().getParent().getParent());
            			};
	            	};
		    	}
	    	}
	*/	