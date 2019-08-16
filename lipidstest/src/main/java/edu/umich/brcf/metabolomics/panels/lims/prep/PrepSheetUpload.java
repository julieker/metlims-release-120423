package edu.umich.brcf.metabolomics.panels.lims.prep;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.service.FractionationService;
import edu.umich.brcf.shared.layers.domain.ClientDocument;
import edu.umich.brcf.shared.layers.service.DocumentService;
import edu.umich.brcf.shared.layers.service.SamplePrepService;
import edu.umich.brcf.shared.util.widgets.MyFileLink;

public class PrepSheetUpload extends WebPage{

	FileUploadField fileUploadField;
	@SpringBean
	SamplePrepService samplePrepService;
	
	@SpringBean
	DocumentService docService;
	
	@SpringBean
	FractionationService fractionationService;
	
	public PrepSheetUpload(Page backPage, String title, PreparationSearchPanel pp, String plateFrmt){
		add(new FeedbackPanel("feedback"));
		add(new PSUForm("psuForm", title, pp, plateFrmt));
	}
	
	public PrepSheetUpload(Page backPage, String title, FractionPrepPanel fp){
		add(new FeedbackPanel("feedback"));
		add(new PSUForm("psuForm", title, fp));
	}
	
	public final class PSUForm extends Form 
		{
		public PSUForm(final String id, final String title, final PreparationSearchPanel pp, final String plateFrmt){
			super(id);
			add(fileUploadField = new FileUploadField("fileInput"));
	        add(new Button("upload"){
	       	 	@Override
	       	 	public void onSubmit()
	            {
	       		 	String retStr=samplePrepService.uploadFile(fileUploadField.getFileUpload(), title, plateFrmt);
	       		 	if (retStr.startsWith("Save")){
	       			String[] messages = retStr.split("_");
	       			PrepSheetUpload.this.info(messages[0]);
	       			pp.setPreparation(messages[1]);
	        		}
	        		else{
	        			PrepSheetUpload.this.error(retStr);
	        			pp.setPreparation(null);
	        		}
	        	}
	        });
	        add(buildFileLink( docService.loadClientDocByName("SamplePrepUploadTemplate.xls")));
		}
		
		public PSUForm(final String id, final String title, final FractionPrepPanel fp){
			super(id);
			add(fileUploadField = new FileUploadField("fileInput"));
	        add(new Button("upload"){
	       	 	@Override
	       	 	public void onSubmit()
	            {
	       		 	String retStr=fractionationService.uploadPrepFile(fileUploadField.getFileUpload(), title);
	       		 	if (retStr.startsWith("Save")){
	       			String[] messages = retStr.split("_");
	       			PrepSheetUpload.this.info(messages[0]);
	       			fp.setPreparation(messages[1]);
	        		}
	        		else{
	        			PrepSheetUpload.this.error(retStr);
	        			fp.setPreparation(null);
	        		}
	        	}
	        });
	        add(buildFileLink( docService.loadClientDocByName("FractionPrepUploadTemplate.xls")));
		}
	}
	
	private Link buildFileLink(final ClientDocument doc) 
		{
		Link link = new MyFileLink("fileLink", new Model(doc)) ;
		link.add(new Label("fileName", doc.getFileName()));
		return link;
		}
	}
