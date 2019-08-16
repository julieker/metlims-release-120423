package edu.umich.brcf.metabolomics.panels.lims.sample;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.panels.utilitypanels.BlankPanel;

//String sid=parameters.get("sid").toString();


public class SampleDetailPage extends WebPage
	{
	@SpringBean
	private SampleService sampleService;
	Sample sample;
	
	public SampleDetailPage(String sid) 
		{
		super();
		boolean showModal = true;
		
		Sample s = null;
		try { s = sampleService.loadBasicsById(sid); }
		catch (Exception e) { showModal = false;	}
			
		if (showModal == false || s == null)
			add(new BlankPanel("sampleDetailPanel"));
		else
			{
			setSample(s);
			SampleDetailPanel sdp=new SampleDetailPanel("sampleDetailPanel", new CompoundPropertyModel(getSample()))
				{
				@Override
				protected void onEdit(IModel sampleModel, AjaxRequestTarget target) 
					{
					SampleDetailPage.this.setResponsePage(new EditSample(getPage(), sampleModel));
					}
				};
			add(sdp);
			}
		}
	
	public void setSampleService(SampleService sampleService) 
		{
		this.sampleService = sampleService;
		}

	public Sample getSample() 
		{
		return sample;
		}

	public void setSample(Sample sample) 
		{
		this.sample = sample;
		}

}
