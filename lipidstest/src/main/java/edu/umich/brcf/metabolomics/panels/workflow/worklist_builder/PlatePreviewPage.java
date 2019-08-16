////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  PlatePreviewPage.java
//  Written by Jan Wigginton
//  July 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;

import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;


public class PlatePreviewPage extends WebPage
	{
	final int nItemsPerRow, nItemsPerCol;
	
	public PlatePreviewPage(Page backPage, List<WorklistItemSimple> items, ModalWindow modal1, boolean useCarousel)
		{
		super();

		modal1.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
		List<WorklistItemSimple> listCopy = new ArrayList<WorklistItemSimple>();

		for (int i = 0; i < items.size(); i++)
			listCopy.add(items.get(i).makeCopy());

		nItemsPerRow = useCarousel ? 10 : 9;
		nItemsPerCol = useCarousel ? 10 : 6;

		PlatePreviewForm rlf = new PlatePreviewForm("platePreviewForm",
				listCopy, modal1, useCarousel);
		add(rlf);
		}

	
	public class PlatePreviewForm extends Form
		{
		boolean useCarousel = false;

		public PlatePreviewForm(final String id, List<WorklistItemSimple> items, final ModalWindow modal1, boolean useCarousel)
			{
			super(id);

			add(new FeedbackPanel("feedback"));
			this.setOutputMarkupId(true);

			setMultiPart(true);

			this.useCarousel = useCarousel;
			PlateListHandler handler = new PlateListHandler(nItemsPerCol, nItemsPerRow, useCarousel);
			List<WorklistItemSimple> sortedSpacedItems = handler.condenseSortAndSpace(items);
			PageableListView plateListView;
			add(plateListView = buildPlateListView(nItemsPerRow, nItemsPerCol, sortedSpacedItems));
			add(new AjaxPagingNavigator("navigator", plateListView));// Issue 283
			
			// issue 464
			add(new AjaxCancelLink("cancelButton", modal1));	   
			}
		
		public PageableListView buildPlateListView(final int nItemsPerRow, final int nItemsPerCol, List<WorklistItemSimple> items)
			{
			int nRowsNeeded = (int) Math.ceil(items.size() / (nItemsPerRow * 1.0));
			int nPlatesNeeded = (int) Math.ceil(nRowsNeeded / (nItemsPerCol * 1.0));
			int nRowsToCreate = nPlatesNeeded * nItemsPerCol;
			
			WorklistItemSimpleMatrix itemMatrix = new WorklistItemSimpleMatrix(nRowsToCreate, nItemsPerRow, items);

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
					for (int i = 0; i < item.nItemsPerRow; i++)
						{
						String property = "name." + i;
						String label = "position" + (i + 1);
						listItem.add(WorklistFieldBuilder.buildPlateLabelWorklistField(label, item.getItem(i), "sampleName"));
						}
					}
				};
			}

		@Override
		protected void onSubmit() {  }
		}
	}