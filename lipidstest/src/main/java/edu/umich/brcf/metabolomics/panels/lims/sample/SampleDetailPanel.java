package edu.umich.brcf.metabolomics.panels.lims.sample;

import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Preparation;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.domain.SampleDocument;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.widgets.MyFileLink;


public abstract class SampleDetailPanel extends Panel
	{
	@SpringBean
	private SampleService sampleService;

	public SampleDetailPanel(String id, CompoundPropertyModel <Sample>sampleModel)
		{
		super(id, sampleModel);
		
		final Sample sample = (Sample) sampleModel.getObject();
		
		add(new Label("sampleID"));
		add(new Label("sampleName"));
		add(new Label("userDescription"));
		add(new Label("exp.expName"));
		
		String parentID=(sample.getParent()== null)? "":sample.getParent().getSampleID();
		add(new Label("parentID", parentID));
		
		String groupID=(sample.getGroup()== null)? "":sample.getGroup().getGroupID();
		add(new Label("groupID", groupID));
		
		add(new Label("genusOrSpecies.genusName"));
		add(new Label("locID"));
		add(new Label("sampleType.description"));
		add(new Label("volumeAndUnits"));
		add(new Label("curVolumeAndUnits"));

		add(new ListView("prepList", getPrepList(sample))
			{
			public void populateItem(ListItem item) 
				{
				Preparation sp=(Preparation) item.getModelObject();
				item.add(new Label("prep",sp.getPrepID()+": "+sp.getTitle()));
				}});

		
		add(new ListView("docList") {
			@Override
			protected void populateItem(ListItem item) {
				final SampleDocument doc = (SampleDocument) item.getModelObject();
				item.add(new MyFileLink("fileLink", new Model(doc)).add(new Label("fileName", doc.getFileName())));
			}
		});
//		final AjaxLink addSample = new AjaxLink("addSampleToWorklist", sampleModel) {
//			public void onClick(AjaxRequestTarget target) {
//				Sample sample = (Sample) getInnermostModel().getObject();
//				worklistModel.addWorklistItem(sample);
//			}
//		};
//		addSample.setVisible(worklistModel != null);
//		addSample.setOutputMarkupId(true);
//		add(addSample);
//		add(buildEditLink(sampleModel));//.setVisible(id.equals("modaldata")));
//		add(buildCreateAliquotLink(sampleModel));
	}

	private List<Preparation> getPrepList(Sample sample) {
		return sampleService.getPrepList(sample);
	}

	//private List<Injections> getValidInjectionforSample(Sample sample) {
	//	return sampleService.getInjectionforSample(sample);
	//}

	public void updateData(Sample sample) {
		setDefaultModel(new CompoundPropertyModel(sampleService.loadById(sample.getSampleID())));
	}
		
	private AjaxLink buildEditLink(final IModel sampleModel) {
		AjaxLink link = new AjaxLink("edit"){//, sampleModel, 400, 600) {
			 public void onClick(final AjaxRequestTarget target){
				 SampleDetailPanel.this.onEdit(sampleModel, target);
//				setResponsePage(new EditSample(getPage(), getModel()));
			}
				@Override // issue 464
				public MarkupContainer setDefaultModel(IModel model) 
				    {
					// TODO Auto-generated method stub
					return this;
				    }
		};
		return link;
	}

	protected abstract void onEdit(IModel sampleModel, AjaxRequestTarget target);

	/*
	public Link getPopUpLink(final String linkID, final Injections injection, final Sample sample){
		Link link = new MyPopupLink(linkID, new Model(injection), 700, 900) {
			public void onClick() {
//				ArrayList<String> toolTips1 = new ArrayList<String>();
//				ArrayList<String> cAreas = new ArrayList<String>();
//				XYDataset dataset1=sampleService.get_Mass_RT_Data(injection, toolTips1, cAreas);
				List<String> plotData = sampleService.get_Mass_RT_DataArray(injection);
				setResponsePage(new WicketAppletPage(plotData, null, injection.getDataFileName(), sample.getSampleName()));
//				setResponsePage(new XYScatterPlot("Component Distribution",
//            			"Mass", "RT", 800, 600,	dataset1, toolTips1, cAreas){
//
//							@Override
//							protected void onClickCallback(AjaxRequestTarget target, String cid) {
//								
//							}
//					
//				});
			}
		};
		return link;
	}  */
}
