//////////////////////////////////////
//PeakSetPage.java
//Written by Jan Wigginton 06/02/15
//////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.lipidshome.browse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.Ms2Peak;
import edu.umich.brcf.metabolomics.layers.domain.Ms2PeakSet;
import edu.umich.brcf.metabolomics.layers.domain.Ms2SampleMap;
import edu.umich.brcf.metabolomics.layers.service.Ms2PeakSetService;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.interfaces.IWriteableTextData;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;
import edu.umich.brcf.shared.util.widgets.TextDownloadLink;



public class PeakSetPage extends WebPage 
	{
	@SpringBean
	Ms2PeakSetService ms2PeakSetService;
	
	@SpringBean
	SampleService sampleService;
	
	
	public PeakSetPage() 
		{
		super();
		}
	
	
	public PeakSetPage(String id, String peakSetId, ModalWindow modal1, Map <String, Ms2SampleMap> map) 
		{	
		modal1.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
		PeakSetForm rlf = new PeakSetForm("peakSetForm",peakSetId, modal1, map);
		add(rlf);
		}
	
	
	public final class PeakSetForm extends Form 
		{
		private Ms2PeakSet peakSet;
		private List<Ms2Peak> peaksList;
		private ListView <Ms2Peak>peaksListView;
		String boxTitle = "Peaks Areas by Sample", carbonCount  = "", doubleBondCount = "", ionName = "";
		String wholeLabel;
		
		public PeakSetForm(final String id, String peakSetId, final ModalWindow modal1, Map <String, Ms2SampleMap> map) 
			{
			super(id);
			setModalDimensions(modal1, 0.5, 0.8);
			
			add(new FeedbackPanel("feedback"));
			this.setOutputMarkupId(true);
			setMultiPart(true);
     
			peakSet = ms2PeakSetService.loadById(peakSetId);
			peaksList = peakSet.getSamplePeaks();
			String peakName = peakSet.getLipidName();
			
			initializeClientLabels(peaksList, map);
			
			boxTitle = "Peak Areas by Sample for  " + peakSet.getLipidName();
			carbonCount     = LipidStringParser.parseCarbonsFromLipidName(peakSet.getLipidName());
			doubleBondCount = LipidStringParser.parseDoubleBondsFromLipidName(peakSet.getLipidName());
			ionName = LipidStringParser.getLipidBlastAdductFromLongName(peakSet.getLipidName());
		
			add(new Label("boxTitle", new PropertyModel<String>(this, "boxTitle")));
			add(new Label("carbonCount", new PropertyModel<String>(this, "carbonCount")));
			add(new Label("doubleBondCount", new PropertyModel<String>(this, "doubleBondCount")));
			add(new Label("ionName", new PropertyModel<String>(this, "ionName")));

			List<String> colTitles = Arrays.asList(new String [] {"MRC2 Sample Id", "Column Label", "Client Sample Label", "Peak Area"});
		
			IWriteableTextData writer = new PeakSetWriter("PeakAreas_" + peakSet.getLipidName() + ".tsv", peaksList);
			add(new TextDownloadLink("downloadData", writer));
		
			add(peaksListView = buildPeaksListView("peaksListView", modal1)); 
			add(new AjaxCancelLink("cancelButton", modal1));
			}

		
		public String getCarbonCount() 
			{
			return carbonCount;
			}
		
		
		public void setCarbonCount(String carbonCount) 
			{
			this.carbonCount = carbonCount;
			}

		
		public String getDoubleBondCount() 
			{
			return doubleBondCount;
			}

		
		public void setDoubleBondCount(String doubleBondCount) 
			{
			this.doubleBondCount = doubleBondCount;
			}

		
		public String getIonName() 
			{
			return ionName;
			}

		public void setIonName(String ionName) 
			{
			this.ionName = ionName;
			}
		
		
		private void setModalDimensions(ModalWindow modal1, double pctWidth, double pctHeight)
			{
			int pageHeight = 800;//((MedWorksSession) getSession()).getClientProperties().getBrowserHeight();
			modal1.setInitialHeight((int) Math.round(pageHeight * pctHeight)) ;
			
			int pageWidth = 1000; //((MedWorksSession) getSession()).getClientProperties().getBrowserWidth();
			modal1.setInitialWidth((int) Math.round(pageWidth * pctWidth));
			}
		
		
		 private void initializeClientLabels(List <Ms2Peak> peaks, Map <String, Ms2SampleMap> map)
		 	{
			 for (int i = 0; i < peaks.size(); i++)
			 	{
				 String clientLabel = "";
				 Ms2Peak pk = peaks.get(i);
				 String sampleMapId = pk.getSampleMapId();
				 Ms2SampleMap info = map.get(sampleMapId);
				 
				 String sampleId = (info != null ? info.getSampleId() : "");
				
				 if (FormatVerifier.verifyFormat(Sample.idFormat, sampleId))
					 clientLabel = sampleService.sampleNameForId(sampleId);
				 
				 Ms2SampleMap entry = map.get(sampleMapId);
				 
				 pk.setTempSampleClientTag(clientLabel);
				 pk.setTempSampleTag(entry.getSampleTag());
				 pk.setTempSampleId(sampleId);
			 	}
		 	}
		 
		 
		 private ListView buildPeaksListView(String id, final ModalWindow modal)
		  	{
		  	return new ListView(id, new PropertyModel<Ms2Peak>(this, "peaksList"))
		  		{	
				public void populateItem(ListItem listItem) 
					{
					Ms2Peak peak =   (Ms2Peak) listItem.getModelObject();
		
					listItem.add(new Label("sampleId", new PropertyModel<String>(peak, "tempSampleId") ));
					listItem.add(new Label("sampleLabel", new PropertyModel<String>(peak, "tempSampleTag") ));
					listItem.add(new Label ("clientSampleLabel", new PropertyModel<String>(peak, "tempSampleClientTag")));
					listItem.add(new Label("peakArea", new PropertyModel<String>(peak, "peakArea") ));
					
					listItem.add(OddEvenAttributeModifier.create(listItem));
					}
				};
		  	}
	  	   
		@Override
		protected void onSubmit()  {  }
		
		public String getBoxTitle()
			{
			return boxTitle;
			}


		public void setBoxTitle(String boxTitle)
			{
			this.boxTitle = boxTitle;
			}
		
		public List<Ms2Peak> getPeaksList()
			{
			return peaksList;
			}
		
		public void setPeaksList(List <Ms2Peak> peaks)
			{
			this.peaksList = peaks;
			}
		}
	
	}


