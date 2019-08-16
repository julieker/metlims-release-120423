////////////////////////////////////////////
//METWorksPctSizableModal.java
//Written by Jan Wigginton September 2015
/////////////////////////////////////////////

package edu.umich.brcf.shared.util.widgets;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

import edu.umich.brcf.shared.panels.login.MedWorksSession;


public class METWorksPctSizableModal extends ModalWindow
	{
	double pageHeightPct = 0.3, pageWidthPct = 0.3;

	public METWorksPctSizableModal(String id)
		{
		this(id, 0.3, 0.2);
		}
	

	public METWorksPctSizableModal(String id, double widthPct, double heightPct)
		{
		super(id);
		setPageDimensions(widthPct, heightPct);
		}

	
	public void setPageDimensions(double widthPct, double heightPct)
		{
		setPageWidthPct(widthPct);
		setPageHeightPct(heightPct);

		int pageHeight = 1200;
		if (((MedWorksSession) getSession()).getClientProperties() != null)
			pageHeight = ((MedWorksSession) getSession()).getClientProperties().getBrowserHeight();
		
		setInitialHeight(((int) Math.round(pageHeight * pageHeightPct)));

		int pageWidth = 1500;
		if (((MedWorksSession) getSession()).getClientProperties() != null)
			pageWidth = ((MedWorksSession) getSession()).getClientProperties().getBrowserWidth();

		setInitialWidth(((int) Math.round(pageWidth * pageWidthPct)));
		}
	

	public double getPageHeightPct()
		{
		return pageHeightPct;
		}

	
	public void setPageHeightPct(double pageHeightPct)
		{
		this.pageHeightPct = pageHeightPct;
		}
	

	public double getPageWidthPct()
		{
		return pageWidthPct;
		}
	

	public void setPageWidthPct(double pageWidthPct)
		{
		this.pageWidthPct = pageWidthPct;
		}
	}
