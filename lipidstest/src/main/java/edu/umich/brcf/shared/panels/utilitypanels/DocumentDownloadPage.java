////////////////////////////////////////////////////
// DocumentDownloadPage.java
// Written by Jan Wigginton, April 2018
////////////////////////////////////////////////////

// issue 441
package edu.umich.brcf.shared.panels.utilitypanels;

import java.math.BigDecimal;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.ClientReport;
import edu.umich.brcf.shared.layers.domain.ExperimentDocument;
import edu.umich.brcf.shared.layers.domain.ProtocolReport; // issue 441
import edu.umich.brcf.shared.layers.service.ClientReportService;
import edu.umich.brcf.shared.layers.service.DocumentService;
import edu.umich.brcf.shared.layers.service.ProtocolReportService;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;
import edu.umich.brcf.shared.util.widgets.MyFileLink;
import edu.umich.brcf.shared.util.widgets.MyProtocolLink;
import edu.umich.brcf.shared.util.widgets.MyReportLink;

public class DocumentDownloadPage extends WebPage
	{
	@SpringBean
	DocumentService documentService;
	
	@SpringBean
	ClientReportService clientReportService;
	
	@SpringBean 
	ProtocolReportService protocolReportService;
	
	public DocumentDownloadPage(String identifier, ModalWindow modal, String linkID)
		{
		//fileLink
		// issue 441
		if (linkID.equals("fileLink"))
			{
			ExperimentDocument doc = (ExperimentDocument) documentService.loadById(identifier);
			MyFileLink link = new MyFileLink("fileLink", new Model<ExperimentDocument>(doc));
			link.add(new Label("fileName", doc.getFileName()));
			add(link);
			}
		else if (linkID.equals("repLink"))
		    {
			ClientReport doc = (ClientReport) clientReportService.loadById(identifier);
			MyReportLink link = new MyReportLink("fileLink", new Model<ClientReport>(doc));
			link.add(new Label("fileName", doc.getFileName()));
			add(link);
			}
		else if (linkID.equals("protocolLink"))
		    {
			ProtocolReport doc = (ProtocolReport) protocolReportService.loadByReportId(identifier);
			MyProtocolLink link = new MyProtocolLink("fileLink", new Model<ProtocolReport>(doc));
			link.add(new Label("fileName", doc.getFileName()));
			add(link);
			}
		
		add(new AjaxCancelLink("cancelButton", modal));
		}
	
	}
