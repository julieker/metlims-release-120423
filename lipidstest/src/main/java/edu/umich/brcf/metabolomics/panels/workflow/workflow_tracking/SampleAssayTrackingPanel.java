////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  SampleAssayTrackingPanel.java
//  Written by Jan Wigginton
//  July 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.workflow_tracking;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import edu.umich.brcf.shared.layers.domain.Assay;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.SampleAssay;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.SampleAssayService;
import edu.umich.brcf.shared.layers.service.SampleLocationService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.panels.utilitypanels.EditSampleAssayStatus;
import edu.umich.brcf.shared.panels.utilitypanels.MedworksErrorPage;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.panels.utilitypanels.SampleLocationsPage;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.behavior.SampleStatusAttributeModifier;
import edu.umich.brcf.shared.util.structures.SelectableObject;
import edu.umich.brcf.shared.util.widgets.AjaxBackButton;


public class SampleAssayTrackingPanel extends Panel
	{
	@SpringBean
	SampleAssayService sampleAssayService;

	@SpringBean
	SampleService sampleService;

	@SpringBean
	SampleLocationService sampleLocationService;

	@SpringBean
	AssayService assayService;

	private List<SelectableObject> sampleAssays = new ArrayList<SelectableObject>();
	Experiment experiment;
	ListView listView;

	public SampleAssayTrackingPanel(String id, Experiment exp, final WebPage backPage, final String assayId)
		{
		super(id);
		this.setOutputMarkupId(true);

		final ModalWindow modal1 = ModalCreator.createModalWindow("modal1", 1000, 1000);
		modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
			{
			public void onClose(AjaxRequestTarget target)
				{
				setSelectAll(false);
				setExperimentAndAssay(getExperiment(), assayId);
				if (listView != null)
					target.add(listView.getParent());
				}
			});

		add(modal1);

		add(new AjaxBackButton("backButton", backPage));

		for (int i = 0; i < sampleAssays.size(); i++)
			sampleAssays.get(i).setIndexSlot(i);

		setExperimentAndAssay(exp, assayId);
		setSelectAll(true);
		updateAllSelection(true);

		Assay assay = assayService.loadAssayByID(assayId);
	
		add(new Label("assayName", new PropertyModel<String>(assay, "assayName")));
		add(new Label("assayId", assayId));

		add(new Label("experimentName", new PropertyModel<String>(experiment, "expName")));
		add(new Label("experimentId", new PropertyModel<String>(experiment, "expID")));

		add(listView = buildAssayListView("samples", "sampleAssays", modal1));

		add(new AjaxCheckBox("selectAll", new PropertyModel<Boolean>(this, "selectAll"))
			{
			public void onUpdate(AjaxRequestTarget target)
				{
				final String value = getValue();
				if (value != null)
					{
					updateAllSelection(Strings.isTrue(value));
					
					if (listView != null)
						target.add(listView.getParent());
					}
				}
			});
		
		add(buildLinkToModal("changeStatus", modal1, ""));
		}

	
	private ListView buildAssayListView(String id, String tag, final ModalWindow modal1)
		{
		return new ListView(id, new PropertyModel(this, tag))
			{
			public void populateItem(final ListItem listItem)
				{
				final SelectableObject so = (SelectableObject) listItem.getModelObject();
				listItem.add(new AjaxCheckBox("selected", new PropertyModel<Boolean>(so, "selected"))
					{
					public void onUpdate(AjaxRequestTarget target) {   }
					});

				listItem.add(new Label("index", new PropertyModel(so,"indexSlotStr")));
				listItem.add(new Label("sampleId", ((SampleAssay) so.getSelectionObject()).getSample().getSampleID()));
				listItem.add(new Label("sampleName", ((SampleAssay) so.getSelectionObject()).getAssay().getAssayName()));
				listItem.add(buildLinkToModal("locationHistory", modal1, ((SampleAssay) so.getSelectionObject()).getSample().getSampleID()));
				listItem.add(new Label("location", ((SampleAssay) so.getSelectionObject()).getSample().getLocID()));
				
				SampleAssay sa = (SampleAssay) so.getSelectionObject();

				Component label = new Label("sampleAssayStatus", new PropertyModel<String>(sa, "status.statusValue"));
				WebMarkupContainer cell = new WebMarkupContainer("cell");
				Character c = (sa != null && sa.getStatus() != null ? sa.getStatus().getId() : 'U');
				
				cell.add(SampleStatusAttributeModifier.create(c)); 
				cell.add(label);
				
				listItem.add(cell);
				listItem.add(OddEvenAttributeModifier.create(listItem));
				}
			};
		}
	

	private int getSelectedSamplesCount()
		{
		List<SelectableObject> allSamples = getSampleAssays();
		int count = 0;
		for (SelectableObject so : allSamples)
			if (so.isSelected())
				count++;

		return count;
		}

	
	protected void updateAllSelection(boolean selection)
		{
		List<SelectableObject> list = getSampleAssays();
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
			@Override // issue 464
			public MarkupContainer setDefaultModel(IModel model) 
			    {
				// TODO Auto-generated method stub
				return this;
			    }
			};
		}


	private void doClick(final String linkID, final AjaxRequestTarget target, final ModalWindow modal1, final String sampleId)
		{
		if (!linkID.equals("locationHistory") && getSelectedSamplesCount() == 0)
			{
			String msg = "Please select at least 1 sample to apply status";
			target.appendJavaScript("alert('" + msg + "')");
			return;
			}

		modal1.setPageCreator(new ModalWindow.PageCreator()
			{
			@Override
			public Page createPage()
				{
				if ("changeStatus".equals(linkID))
					return newEditStatusPage(modal1);

				return newLocationHistoryPage(modal1, sampleId);
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
			case "changeStatus": return 160;
			case "changeLocation": return 200;
			default: return 350;
			}
		}

	
	private int grabModalWidth(String linkID)
		{
		switch (linkID)
			{
			case "changeStatus": return 650;
			case "changeLocation": return 650;
			default: return 950;
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


	private EditSampleAssayStatus newEditStatusPage(final ModalWindow modal1)
		{
		return (new EditSampleAssayStatus(getPage(), modal1)
			{
			@Override
			protected void onSave(String status, AjaxRequestTarget target1)
				{
				sampleAssayService.updateStatusForSelections(getSampleAssays(), status);
				modal1.close(target1);
				}
			});
		}


	private MedworksErrorPage buildErrorPage(Page page, String msg, final ModalWindow modal1)
		{
		return new MedworksErrorPage(getPage(), "Please select at least 1 sample to apply status!")
			{
			@Override
			protected void onOk(AjaxRequestTarget target1)
				{
				modal1.close(target1);
				}
			};
		}

	private boolean selectAll;

	
	public boolean getSelectAll()
		{
		return selectAll;
		}

	
	public void setSelectAll(boolean selectAll)
		{
		this.selectAll = selectAll;
		}

	
	public List<SelectableObject> getSampleAssays()
		{
		for (int i = 0; i < sampleAssays.size(); i++)
			sampleAssays.get(i).setIndexSlot(i + 1);

		return sampleAssays;
		}

	
	public void setSampleAssays(List<SampleAssay> smplList)
		{
		sampleAssays = new ArrayList<SelectableObject>();
		for (SampleAssay sampleAssay : smplList)
			sampleAssays.add(new SelectableObject(sampleAssay));
		}

	
	public Experiment getExperiment()
		{
		return experiment;
		}

	
	public void setExperimentAndAssay(Experiment experiment, String assayId)
		{
		if (experiment == null)
			return;
		this.experiment = experiment;
		setSampleAssays(sampleAssayService.loadForAssayAndExperiment(experiment.getExpID(), assayId));
		}
	}
