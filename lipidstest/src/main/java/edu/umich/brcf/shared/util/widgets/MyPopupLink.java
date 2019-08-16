package edu.umich.brcf.shared.util.widgets;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.model.IModel;

public abstract class MyPopupLink extends Link{

	public MyPopupLink(String id, int height, int width) {
		super(id);
		setPopupSettings(new PopupSettings(PopupSettings.RESIZABLE | PopupSettings.STATUS_BAR
				| PopupSettings.SCROLLBARS).setHeight(height).setWidth(width));
	}
	
	public MyPopupLink(String id, IModel object, int height, int width) {
		super(id, object);
		setPopupSettings(new PopupSettings(PopupSettings.RESIZABLE | PopupSettings.STATUS_BAR
				| PopupSettings.SCROLLBARS).setHeight(height).setWidth(width));
	}
}
