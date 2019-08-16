package edu.umich.brcf.metabolomics.panels.lims.newexperiment.obsolete;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.domain.SampleLocation;
import edu.umich.brcf.shared.layers.dto.SampleLocationDTO;
import edu.umich.brcf.shared.layers.service.SampleLocationService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.panels.utilitypanels.EditSampleLocation;
import edu.umich.brcf.shared.panels.utilitypanels.EditSampleStatus;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.panels.utilitypanels.SampleLocationsPage;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.behavior.SampleStatusAttributeModifier;
import edu.umich.brcf.shared.util.structures.SelectableObject;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.panels.login.MedWorksSession;


public class ExperimentSamplesPanel extends Panel
	{
	@SpringBean
	SampleService sampleService;
	
	@SpringBean
	SampleLocationService sampleLocationService;

	
	private List<SelectableObject> samples = new ArrayList<SelectableObject>();
	Experiment experiment;
	ListView<SelectableObject> listView;
	
	public ExperimentSamplesPanel(String id, Experiment exp) {
		super(id);
		this.setOutputMarkupId(true);
	
		final ModalWindow modal1= ModalCreator.createModalWindow("modal1", 1000, 1000);
		modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
        	{
            public void onClose(AjaxRequestTarget target)
            	{
            	setSelectAll(false);
            	setExperiment(getExperiment());
				target.add(listView.getParent());
            	}
        	});
		add(modal1);
		
		
		for (int i = 0; i < samples.size(); i++)
			samples.get(i).setIndexSlot(i);
		
		setExperiment(exp);
		
		add(listView = new ListView<SelectableObject>("samples", new PropertyModel(this, "samples")) 
			{
			public void populateItem(final ListItem<SelectableObject> listItem) 
				{
				final SelectableObject so = (SelectableObject) listItem.getModelObject();
				listItem.add(new AjaxCheckBox("selected", new PropertyModel(so, "selected")) 
					{
					public void onUpdate(AjaxRequestTarget target) {
					}
				});
				
				listItem.add(new Label("index", new PropertyModel(so, "indexSlotStr")));
				listItem.add(new Label("sampleId", ((Sample)so.getSelectionObject()).getSampleID()));
				listItem.add(new Label("sampleName", ((Sample)so.getSelectionObject()).getSampleName()));
//				listItem.add(new Label("userDesc", ((MChearSample)so.getSelectionObject()).getVolumeAndUnits()));
				listItem.add(buildLinkToModal("locationHistory", modal1,((Sample)so.getSelectionObject()).getSampleID() ));
				listItem.add(new Label("location", ((Sample)so.getSelectionObject()).getLocID()));
				
				Component label = new Label("sampleStatus", ((Sample)so.getSelectionObject()).getStatus().getStatusValue());
				
				WebMarkupContainer cell = new WebMarkupContainer("cell");
				cell.add(SampleStatusAttributeModifier.create(((Sample)so.getSelectionObject()).getStatus().getId()));
				cell.add(label); 
				listItem.add(cell);
				listItem.add(OddEvenAttributeModifier.create(listItem));
				}
			});
//		listView.setOutputMarkupId(true);
		add(new AjaxCheckBox("selectAll", new PropertyModel(this, "selectAll")) 
			{
			public void onUpdate(AjaxRequestTarget target) 
				{
				final String value = getValue();
				  if (value != null)
				  		{
						updateAllSelection(Strings.isTrue(value));
						target.add(listView.getParent());
				  		}	
				}
			});
		add(buildLinkToModal("changeStatus",modal1, ""));
		add(buildLinkToModal("changeLocation", modal1, ""));
		}
	
	
	private int getSelectedSamplesCount()
		{
		List<SelectableObject> allSamples = getSamples();
		int count=0;
		for (SelectableObject so: allSamples)
			{
			if(so.isSelected())
				count++;
			}
		return count;
		}
	
	
	protected void updateAllSelection(boolean selection) 
		{
		List<SelectableObject> list = getSamples();
		for (SelectableObject sample : list)
			sample.setSelected(selection);
		}
	
	
	private AjaxLink buildLinkToModal(final String linkID, final ModalWindow modal1, final String id) 
		{
		return new AjaxLink(linkID)
        	{
            @Override
            public void onClick(final AjaxRequestTarget target)
            	{
            	doClick(linkID, target, modal1, id);
            	}
        	};
		}
	
	
	private void doClick(final String linkID, final AjaxRequestTarget target, final ModalWindow modal1, final String sampleId)
		{
		if(!"locationHistory".equals(linkID) && getSelectedSamplesCount()==0)
			{
			String msg =  "Please select at least 1 sample to apply status";
			target.appendJavaScript("alert('" + msg + "')");
			return;
			}
		
		modal1.setPageCreator(new ModalWindow.PageCreator() 
			{			
			@Override
			public Page createPage() 
				{
				if ("changeStatus".equals(linkID) )
					return newEditStatusPage(modal1);
				
				if ("locationHistory".equals(linkID))
					return newLocationHistoryPage(modal1, sampleId);
				
				return newEditLocationPage(modal1);
				}
			});
		
		modal1.setInitialHeight(grabModalHeight(linkID));
		modal1.setInitialWidth(grabModalWidth(linkID));

		modal1.show(target);
		}
	
	private int grabModalHeight(String linkID)
		{
		switch (linkID)
			{
			case "changeStatus" : return 160;
			case "changeLocation" : return 200;
			default : return 350;
			}
		}
	
	private int grabModalWidth(String linkID)
		{
		switch (linkID)
			{
			case "changeStatus" : return 650;
			case "changeLocation" : return 650;
			default : return 950;
			}
		}
	
	private Page newLocationHistoryPage(final ModalWindow modal1, final String sampleId)
		{
		return new SampleLocationsPage((WebPage) getPage(), modal1, sampleId)
			{
			protected void onSave(AjaxRequestTarget target, String sampleId) 
				{
				modal1.close(target);
				}
			};
		}
	
	
	private Page newEditStatusPage(final ModalWindow modal1)
		{
		return (new EditSampleStatus(getPage(), modal1)
			{
			@Override
			protected void onSave(String status, AjaxRequestTarget target1) 
				{
				sampleService.updateStatus(getSamples(), status);
				modal1.close(target1);
				}
			});
		}
	
	
	private Page newEditLocationPage(final ModalWindow modal1)
		{
		return new EditSampleLocation(getPage(), modal1)
			{
			@Override
			protected void onSave(String location, AjaxRequestTarget target1) 
				{
				List <SampleLocationDTO> dtos = grabSampleLocationDtos(location);
				sampleService.updateLocation(getSamples(), location);
				System.out.println("Updated the locations");
				sampleLocationService.logUpdates(dtos);
				sampleService.updateLocation(getSamples(), location);
				
				}
			};
		}
	
	private List<SampleLocationDTO> grabSampleLocationDtos(String locId)
		{
		List <SelectableObject> selectableSamples = getSamples();
		List <SampleLocationDTO> dtos = new ArrayList <SampleLocationDTO>();
		
		String userName = ((MedWorksSession) getSession()).getCurrentUserId();
		Calendar today = DateUtils.todaysDateAsCalendar();
		
		for (int i = 0; i < selectableSamples.size(); i++)
			{
			if (selectableSamples.get(i).isSelected())
				{
				SelectableObject sampleObj = selectableSamples.get(i);
				Sample sample = ((Sample) sampleObj.getSelectionObject());
				SampleLocation location = new SampleLocation("", sample.getSampleID(), sample.getLocID(), locId,
						today, userName); 
				SampleLocationDTO dto = SampleLocationDTO.instance(location);
				dtos.add(dto);
				}
			}
		
		return dtos;
		}
	
	
		
	//private MedworksErrorPage buildErrorPage(Page page, String msg, final ModalWindow modal1)
	//	{
	//	return new MedworksErrorPage(getPage(), "Please select at least 1 sample to apply status!")
	//		{
	//		@Override
	//		protected void onOk(AjaxRequestTarget target1) 
	//			{
	//			modal1.close(target1);
	//			}
	//		};
	//	}
	
	
    private boolean selectAll;
	
	public boolean getSelectAll() 
		{
		return selectAll;
		}

	public void setSelectAll(boolean selectAll) 
		{
		this.selectAll = selectAll;
		}
	
	public List<SelectableObject> getSamples() 
		{
		for (int i = 0; i < samples.size(); i++)
			samples.get(i).setIndexSlot(i + 1);

		return samples;
		}

	public void setSamples(List<Sample> smplList) 
		{
		samples = new ArrayList<SelectableObject>();
		for (Sample sample : smplList)
			samples.add(new SelectableObject(sample));
		}
	
	public Experiment getExperiment()
		{
		return experiment;
		}

	public void setExperiment(Experiment experiment) 
		{
		this.experiment=experiment;
		List<Sample> sampleList=new ArrayList<Sample>();
		if(experiment!=null){
			sampleList = sampleService.loadSampleForStatusTracking(experiment.getExpID());}
		setSamples(sampleList);
		}
	}

