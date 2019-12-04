// NewNewPrepMethodPanel.java
// Disentangled by PrepMethodsPanel.java/Rewritten by Jan Wigginton

package edu.umich.brcf.metabolomics.panels.lims.prep;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import edu.umich.brcf.metabolomics.layers.domain.FractionPreparation;
import edu.umich.brcf.metabolomics.layers.service.FractionationService;
import edu.umich.brcf.shared.layers.domain.PrepPlate;
import edu.umich.brcf.shared.layers.domain.Preparation;
import edu.umich.brcf.shared.layers.domain.PreppedFraction;
import edu.umich.brcf.shared.layers.domain.PreppedItem;
import edu.umich.brcf.shared.layers.dto.PreppedSampleDTO;
import edu.umich.brcf.shared.layers.service.SamplePrepService;
import edu.umich.brcf.shared.util.structures.SelectableObject;
import edu.umich.brcf.shared.util.widgets.CustomCheckbox;
import edu.umich.brcf.shared.util.widgets.METWorksPctSizableModal;


public class SelectablePlateMapPanel extends Panel
	{
	@SpringBean
	SamplePrepService samplePrepService;
	
	@SpringBean
	FractionationService fractionationService;
	
	int plateCols = 0, plateRows = 0;
	String preparation = null; 
	boolean isSamplePrep = true, isProteinPlate = false;

	public static String[] rowIdentity ={"A","B","C","D","E","F","G","H"};
	
	CustomCheckbox[] colCheck;
	CustomCheckbox selectAll = new CustomCheckbox();
	SelectableObject[][] samples;
	
	
	public SelectablePlateMapPanel(String id, String preparation) 
		{
		this(id, preparation, false);
		}
	
	public SelectablePlateMapPanel(String id, String preparation, boolean isProt) 
		{
		super(id);
		
		isProteinPlate = isProt;
		setPreparation(preparation);
		setOutputMarkupId(true);
		SelectablePlateMapPanel pmp = this;
		add(new SelectablePlateMapForm("prepDetailForm", pmp));
		}

	
	public final class SelectablePlateMapForm extends Form 
		{
		public SelectablePlateMapForm(final String id, final SelectablePlateMapPanel pmp)
			{
			super(id);
			
			final METWorksPctSizableModal modal1= new METWorksPctSizableModal("modal1", 0.3, 0.2);
			modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
	        	{
	            public void onClose(AjaxRequestTarget target) { target.add(pmp); }
	         	});
	         
	        add(modal1);

	        for (int k=0;k<plateCols;k++)
				colCheck[k]=new CustomCheckbox();
	        
		    add(new Loop("rows", plateRows) 
	     		{
				protected void populateItem(LoopItem item) 
					{
					final int row = item.getIndex();
					item.add(new Label("rowIdentity", new Model(rowIdentity[row])));
			    	item.add(buildColsLoop(plateCols, row, modal1,pmp));
			    	}
				});
	     
		    add(buildColHeaderLoop(plateCols, pmp));
			add(grabSelectAllBox(pmp)); 
			  
		    setMultiPart(true);
			}
		}
	

	private AjaxCheckBox grabSelectAllBox(final SelectablePlateMapPanel pmp)
		{
		return new AjaxCheckBox("selectAll",new PropertyModel(selectAll, "selected")) 
		 	{
			public void onUpdate(AjaxRequestTarget target) 	
				{
				final String value = getValue();
				if (value != null)
					{
					for(int col=0;col<plateCols;col++)
						updateColSelection(col, Strings.isTrue(value));
				
					target.add(pmp);
					}
				}
		 	};
		}
	
	
	public AjaxLink buildLinkToModal(final String linkID, final METWorksPctSizableModal modal1, final String value,
			final SelectablePlateMapPanel pmp) 
		{
		// issue 39
		return new AjaxLink <Void>(linkID)
        	{
			@Override
            public void onClick(AjaxRequestTarget target)
            	{
				modal1.setPageDimensions(0.4, 0.5);
            	modal1.setPageCreator(new ModalWindow.PageCreator()
            		{
                    public Page createPage()
                    	{
                    	return grabAliquotOrFractionPage(value);
                    	}
                    });
            	
            	target.add(this);
            	target.add(pmp.getParent().getParent().getParent());
            	modal1.show(target);
            	}
        	};
		}


	 public boolean checkForFullPlate(AjaxRequestTarget target, SelectablePlateMapPanel pmp)
		{
		List<PreppedFraction> smpList=fractionationService.loadPreppedFractions(preparation);
		
    	if(!(smpList.size() < plateCols * plateRows))
    	 	{
    		SelectablePlateMapPanel.this.error("Plate is full -- please create another worklist");
    		target.add(pmp.getParent().getParent().getParent());
    		return true;
    	 	}
    	 
    	return false;
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

	
	public void setSamples(SelectableObject[][] samples) 
		{
		this.samples=samples;
		}

	public SelectableObject[][] getSamples() 
		{
		return samples;
		}

	public SelectableObject[][] getSelectableSamples(PreppedItem[] values)
		{
		SelectableObject[][] samples = new SelectableObject[plateRows][plateCols];
		int index = 0;
		for (int i = 0; i < plateRows; i++)
			for (int j = 0; j < plateCols; j++)
				{
				if((index < values.length)&&(values[index]!=null))
					samples[i][j] = new SelectableObject(new PreppedSampleDTO(values[index++]));//new SelectableSample(values[index++].getItemID());//new PreppedSampleDTO(values[i][j],"100","ul", null, null));
				
				else
					samples[i][j] = new SelectableObject();
				}
		
		return samples;
		}
	// annotate
	protected void updateColSelection(int col, boolean value) 
		{
		SelectableObject[][] samples = getSamples();
		
		for (int i=0;i<plateRows;i++)
			if(samples[i][col].getSelectionObject()!=null)
					samples[i][col].setSelected(value);
		
		setSamples(samples);
		}
	

	public int getSelectedSamplesCount()
		{
		SelectableObject[][] samples = getSamples();
		
		int count=0;
		for (int i = 0; i < plateRows; i++)
			for (int j=0;j<plateCols;j++)
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
			
		//	for (PrepPlate plate: plates)
		//		plateStr += plate.getInstrument().getName()+",";
		///	plateStr = plateStr.substring(0,plateStr.lastIndexOf(","));
			
			
			plateCols = isProteinPlate || plates.get(0).getPlateFormat().equals("PF01") ? 12 : 9;
			plateRows = isProteinPlate || plates.get(0).getPlateFormat().equals("PF01") ? 8 : 6;
			
			colCheck = new CustomCheckbox[plateCols];
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
			if(items.size() > 0)
				for (Iterator it = items.iterator(); it.hasNext();)
					{
					PreppedItem item=(PreppedItem) it.next();
					if (item.getWell()  != null  && item.getWell().getIndex() > 0)
						values[item.getWell().getIndex()-1]=item;
					}
			
			items = null;
			setSamples(getSelectableSamples(values));
			values = null;
			}
		}
	
	public String getPreparation()
		{
		return preparation;
		}
	
	    private Loop buildColsLoop(int cols, final int row, final METWorksPctSizableModal modal1, 
	    		final SelectablePlateMapPanel pmp)
    	{
    	return	new Loop("cols", cols) 
     		{
    		protected void populateItem(LoopItem item) 
    	 		{
    			final int col=item.getIndex();
    			
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
    			String item_id = (dto==null ? "" : dto.getId());
	               
    			AjaxLink sampleLink;
    			item.add(sampleLink = buildLinkToModal("sampleLink", modal1, item_id, pmp));
    			sampleLink.add(new Label("sample.sampleid", new PropertyModel(model, "selectionObject.sampleid")));
    			sampleLink.setOutputMarkupId(true);
    	 		}
     		};
    	}
	    
	    
	    private Loop buildColHeaderLoop(int cols, final SelectablePlateMapPanel pmp)
	    	{
	    	return new Loop("colNumbers", cols) 
		 		{
	    		protected void populateItem(LoopItem item) 
					{
					final int col=item.getIndex();
					 
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
					
					item.add(new Label("colNum", new Model(item.getIndex()+1)));
					}
		 		};
			}
	    
	    
		public int getPlateCols() {
			return plateCols;
		}


		public void setPlateCols(int plateCols) {
			this.plateCols = plateCols;
		}


		public int getPlateRows() {
			return plateRows;
		}


		public void setPlateRows(int plateRows) {
			this.plateRows = plateRows;
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


//@Override
// protected void onComponentTag(final ComponentTag tag)
//   	{
//   	super.onComponentTag(tag);
	
//    	if (field.equals("randomIdx") && item.getRepresentsControl() == true)
//    		tag.put("title", "Delete control " + item.getSampleName());
  	
//   	tag.put("style", style);