//ClickablePropertyColumn.java
//Translated from  WicketRecipes by ??

package edu.umich.brcf.shared.panels.utilitypanels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;


public abstract class ClickablePropertyColumn<T, S> extends AbstractColumn<T, S>
	{
	private final String property;

	public ClickablePropertyColumn(IModel<String> displayModel, String property)
		{
		this(displayModel, property, null);
		}

	
	public ClickablePropertyColumn(IModel<String> displayModel,
			String property, String sort)
		{
		super(displayModel);
		this.property = property;
		}
	

	@Override
	public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel)
		{
		item.add(new LinkPanel(componentId, rowModel, new PropertyModel<Object>(rowModel, property)));
		}
	

	protected abstract void onClick(IModel<T> clicked, AjaxRequestTarget arg0);
	
	
	private class LinkPanel extends Panel
		{
		public LinkPanel(String id, IModel<T> rowModel, IModel<?> labelModel)
			{
			super(id);
			IndicatingAjaxLink<T> link = new IndicatingAjaxLink<T>("link", rowModel)
				{
				public void onClick(AjaxRequestTarget arg0)
					{
					ClickablePropertyColumn.this.onClick(getModel(), arg0);
					}
				};

			add(link);
			link.add(new Label("label", labelModel));
			}
		}
	}
