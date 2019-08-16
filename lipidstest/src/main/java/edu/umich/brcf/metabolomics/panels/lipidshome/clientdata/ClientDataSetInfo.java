// ClientDataSetInfo.java
// Written by Jan Wigginton 05/08/15

package edu.umich.brcf.metabolomics.panels.lipidshome.clientdata;

import java.io.Serializable;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.Ms2DataSet;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.service.ExperimentService;



public class ClientDataSetInfo implements Serializable
	{
	String dataSetId;
	String expId, expDescription, expLabel;
	String runDate, uploadDate;
	String nSamples, nControls;
	int    nCompounds; 
	String ionMode, dataNotation;
	String uploadedBy;
	
	@SpringBean 
	ExperimentService experimentService;
	
	public ClientDataSetInfo(Ms2DataSet dataSet)
		{
		Injector.get().inject(this);
		
		dataSetId = dataSet.getDataSetId();
		expId = dataSet.getExpId();
		runDate = dataSet.getRunDateAsStr();
		uploadDate  = dataSet.getUploadDateAsStr();
		nCompounds = dataSet.getNumCompounds();
		nControls = "     -     ";
		uploadedBy = dataSet.getUploadedBy();
		dataNotation = dataSet.getDataNotation();
		ionMode = dataSet.getIonMode();
		Experiment exp = experimentService.loadById(expId);
		
		expDescription = grabExpDescription(exp);
		nSamples = exp.getNumberOfSamples();
		
		expLabel = expDescription;
		}
	
	String grabExpDescription(Experiment exp)
		{
		String label = "     -      ";
		if (exp != null)
			{
			label = exp.getExpID() +  " ("+ exp.getExpName() +")";
			}
		return label;
		}

	
	public String getDataSetId() 
		{
		return dataSetId;
		}

	public void setDataSetId(String dataSetId) 
		{
		this.dataSetId = dataSetId;
		}

	public String getExpId() 
		{
		return expId;
		}

	public void setExpId(String expId) 
		{
		this.expId = expId;
		}

	public String getExpDescription() 
		{
		return expDescription;
		}

	public void setExpDescription(String expDescription) 
		{
		this.expDescription = expDescription;
		}

	public String getExpLabel() 
		{
		return expLabel;
		}

	public void setExpLabel(String expLabel) 
		{
		this.expLabel = expLabel;
		}

	public String getRunDate() 
		{
		return runDate;
		}

	public void setRunDate(String runDate) 
		{
		this.runDate = runDate;
		}
	
	public String getUploadDate() 
		{
		return uploadDate;
		}

	public void setUploadDate(String uploadDate) 
		{
		this.uploadDate = uploadDate;
		}

	public String getnSamples() 
		{
		return nSamples;
		}

	public void setnSamples(String nSamples) 
		{
		this.nSamples = nSamples;
		}

	public String getnControls() 
		{
		return nControls;
		}

	public void setnControls(String nControls) 
		{
		this.nControls = nControls;
		}

	public int getnCompounds() 
		{
		return nCompounds;
		}

	public void setnCompounds(int nCompounds) 
		{
		this.nCompounds = nCompounds;
		}

	public String getIonMode() 
		{
		return (ionMode == null || ionMode.trim().equals("") ? "    -    " : ionMode);
		}

	public void setIonMode(String ionMode) 
		{
		this.ionMode = ionMode;
		}

	public String getDataNotation() 
		{
		return (dataNotation == null || dataNotation.trim().equals("") ? "    -    " : dataNotation);
		}

	public void setDataNotation(String dataNotation) 
		{
		this.dataNotation = dataNotation;
		}
	}
	
