///////////////////////////////////////
//ButtonColumn3.java
//Written by Jan Wigginton August 2015
///////////////////////////////////////

package edu.umich.brcf.shared.panels.utilitypanels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;




public abstract class ButtonColumn3<T, S> extends AbstractColumn<T, S>
	{
	private final String label;
	
	public ButtonColumn3(IModel<String> colDisplayModel, String label)
		{
		this(colDisplayModel, label, null);
		}
	
	public ButtonColumn3(IModel<String> displayModel, String property, String sort)
		{
		super(displayModel);
		this.label = property;
		}
	
	@Override
	public void populateItem(Item<ICellPopulator<T>> item, String componentId,
	IModel<T> rowModel)
		{
		item.add(new ButtonLinkPanel(componentId, rowModel));
		}
	
	protected abstract void onClick(IModel<T> clicked, AjaxRequestTarget arg0);
	
	private class ButtonLinkPanel extends Panel
		{
		public ButtonLinkPanel(String id, IModel<T> rowModel)
			{
			super(id);
			IndicatingAjaxLink <T> link = new IndicatingAjaxLink<T>("link", rowModel)
				{
				@Override
				public void onClick(AjaxRequestTarget arg0)
					{
					ButtonColumn3.this.onClick(getModel(), arg0);
					}
			
				protected void onComponentTag(final ComponentTag tag)
					{
					super.onComponentTag(tag);
					tag.put("class", label.trim().startsWith("Edit") ? "buttonEdit" : "button");
					}
				};
	
			add(link);
			link.add(new Label("label", label));
			}
		}
	}
