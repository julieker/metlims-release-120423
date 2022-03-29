////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  PlatePreviewPage.java
//  Written by Jan Wigginton
//  July 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;


public class PlatePreviewPage extends WebPage
	{
	@SpringBean
	private SampleService sampleService;
	
	final int nItemsPerRow, nItemsPerCol;
	boolean gBothQCMPandMP;
	WorklistBuilderPanel workListBuilderPanel;
	Map<String, String> idsVsReasearcherNameMap = new HashMap<String, String> ();
	
	public PlatePreviewPage(boolean bothQCMPandMP, Page backPage, List<WorklistItemSimple> items, ModalWindow modal1, boolean useCarousel, WorklistBuilderPanel wp, WorklistSimple ws)
		{
		super();
		workListBuilderPanel = wp;
		modal1.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
		List<WorklistItemSimple> listCopy = new ArrayList<WorklistItemSimple>();
		for (int i = 0; i < items.size(); i++)
			listCopy.add(items.get(i).makeCopy());
		if (listCopy.get(0).getGroup().getParent().getIs96Well())
			{
			nItemsPerRow = useCarousel ? 10 : 12;
			nItemsPerCol = useCarousel ? 10 : 8;
			}
		else
			{
			nItemsPerRow = useCarousel ? 10 : 9;
			nItemsPerCol = useCarousel ? 10 : 6;
			}
        
		PlatePreviewForm rlf = new PlatePreviewForm("platePreviewForm",
				listCopy, modal1, useCarousel, ws);
		add(rlf);
		gBothQCMPandMP=bothQCMPandMP;
		}

	public class PlatePreviewForm extends Form
		{
		boolean useCarousel = false;
		List<WorklistItemSimple> sortedSpacedItems;
		public PlatePreviewForm(final String id, List<WorklistItemSimple> items, final ModalWindow modal1, boolean useCarousel, WorklistSimple ws)
			{
			super(id);

			add(new FeedbackPanel("feedback"));
			this.setOutputMarkupId(true);

			setMultiPart(true);

			this.useCarousel = useCarousel;
			PlateListHandler handler = new PlateListHandler(nItemsPerCol, nItemsPerRow, useCarousel);
			// issue 212
			if (items.get(0).getGroup().getParent().getIs96Well())
				sortedSpacedItems = handler.condenseSortAndSpace(items);
			else 
				sortedSpacedItems = handler.condenseSortAndSpaceOriginal(items);
			PageableListView plateListView;
			add(plateListView = buildPlateListView(nItemsPerRow, nItemsPerCol, sortedSpacedItems, ws));
			add(new AjaxPagingNavigator("navigator", plateListView));// Issue 283
			
			// issue 464
			add(new AjaxCancelLink("cancelButton", modal1));
			
			}
		
		public PageableListView buildPlateListView(final int nItemsPerRow, final int nItemsPerCol, List<WorklistItemSimple> items, WorklistSimple ws)
			{
			int nRowsNeeded = (int) Math.ceil(items.size() / (nItemsPerRow * 1.0));
			int nPlatesNeeded = (int) Math.ceil(nRowsNeeded / (nItemsPerCol * 1.0));
			int nRowsToCreate = nPlatesNeeded * nItemsPerCol;
			idsVsReasearcherNameMap =
				     sampleService.sampleIdToResearcherNameMapForExpId(ws.getSampleGroup(0).getExperimentId());								
			ws.populateSampleName(ws,idsVsReasearcherNameMap ); 
			WorklistItemSimpleMatrix itemMatrix = new WorklistItemSimpleMatrix(nRowsToCreate, nItemsPerRow, items);
			Label plateHead10, plateHead11, plateHead12;
			add(plateHead10 = new Label("plateHead10", "10"));
			add(plateHead11 = new Label("plateHead11", "11"));
			add(plateHead12 = new Label("plateHead12", "12"));
			if (workListBuilderPanel.change96WellBox.getDefaultModelObjectAsString().equals("false"))
				{
				plateHead10.add(AttributeAppender.replace("style", "display:none"));
				plateHead11.add(AttributeAppender.replace("style", "display:none"));
				plateHead12.add(AttributeAppender.replace("style", "display:none"));
				}
			return new PageableListView("plateRows", new PropertyModel<WorklistItemSimpleRow>(itemMatrix, "rows"), nItemsPerCol)
				{
				final int nPerCol = nItemsPerCol;

				public void populateItem(ListItem listItem)
					{		
					WorklistItemSimpleRow item = (WorklistItemSimpleRow) listItem.getModelObject();
					char c = ((char) ('A' + (item.getRowIndex() % nPerCol)));
					Character cs = c;
					String rowLabel = cs.toString();
					listItem.add(new Label("rowTitle", rowLabel));
					String commentString = "";
					// issue 215
					for (int i = 0; i < item.nItemsPerRow; i++)
						{						
						String property = "name." + i;
						String label = "position" + (i + 1);
						if (!item.getItem(i).getRepresentsControl())
							item.getItem(i).setResearcherName(idsVsReasearcherNameMap.get(item.getItem(i).getSampleName()));
					    if (! item.getItem(i).getSampleName().contains("SB"))
					    	{
					   
					    	// issue 212
					    	listItem.add(WorklistFieldBuilder.buildPlateLabelWorklistField(gBothQCMPandMP, label, item.getItem(i), "sampleName", ws));
					    	}
						else // issue 215
							{
							if (workListBuilderPanel.change96WellBox.getDefaultModelObjectAsString().equals("true"))								
								listItem.add(WorklistFieldBuilder.buildPlateLabelWorklistField(gBothQCMPandMP, label, new WorklistItemSimple(), "sampleName", ws));	
							else 
								listItem.add(WorklistFieldBuilder.buildPlateLabelWorklistField(gBothQCMPandMP, label, item.getItem(i), "sampleName", ws));
							}
						}
					    if (workListBuilderPanel.change96WellBox.getDefaultModelObjectAsString().equals("false"))
					    	{
					    	Label lpos10, lpos11, lpos12;
					    	listItem.add(lpos10 = WorklistFieldBuilder.buildPlateLabelWorklistField(gBothQCMPandMP, "position10", new WorklistItemSimple(), "sampleName", ws));
							listItem.add(lpos11 =WorklistFieldBuilder.buildPlateLabelWorklistField(gBothQCMPandMP, "position11", new WorklistItemSimple(), "sampleName", ws));
							listItem.add(lpos12 = WorklistFieldBuilder.buildPlateLabelWorklistField(gBothQCMPandMP, "position12", new WorklistItemSimple(), "sampleName", ws));
							lpos10.add(AttributeAppender.replace("style", "display:none"));
							lpos11.add(AttributeAppender.replace("style", "display:none"));
							lpos12.add(AttributeAppender.replace("style", "display:none"));
					    	}	
				    }
				};
			}

		@Override
		protected void onSubmit() {  }
		}
	}