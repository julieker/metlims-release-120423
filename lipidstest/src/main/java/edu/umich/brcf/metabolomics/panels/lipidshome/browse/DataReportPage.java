// DataReportPage.java
// Written by Jan Wigginton 05/09/15

package edu.umich.brcf.metabolomics.panels.lipidshome.browse;

//MS2DataSetHandler
import java.util.List;

import org.apache.wicket.markup.html.WebPage;

import edu.umich.brcf.MedWorksSecurePage;
import edu.umich.brcf.metabolomics.layers.domain.Ms2PeakSet;


public class DataReportPage extends MedWorksSecurePage 
	{
	public DataReportPage(String id, WebPage backPage, String dataSetId, List <Ms2PeakSet> selectedPeaks)
		{
		add(new DataReportPanel("reportPanel", backPage, dataSetId, selectedPeaks));
		}
	}

