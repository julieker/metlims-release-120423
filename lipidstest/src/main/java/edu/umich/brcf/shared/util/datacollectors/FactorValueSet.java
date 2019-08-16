// FactorValueSet.java
// Written by Jan Wigginton, October 2015

package edu.umich.brcf.shared.util.datacollectors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.util.Assert;

import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.structures.Pair;
import edu.umich.brcf.shared.util.io.StringUtils;




public class FactorValueSet implements Serializable
	{
	@SpringBean
	SampleService sampleService;
	
	@SpringBean
	ExperimentService experimentService;
	
	private String expId, factorName;
	private List<Pair> idsAndValues;
	

	public FactorValueSet(String eid)
		{
		Injector.get().inject(this);
		
		idsAndValues = new ArrayList<Pair>();

		expId = eid;
		factorName = "New Factor Name Here";
		
		// issue 180 allow for ascending order of samples
		List<String> sampleIds = sampleService.orderedSampleIdsForExpId(expId,false);
		
		for (int i =0; i < sampleIds.size(); i++)
			idsAndValues.add(new Pair(sampleIds.get(i), " "));
		}
	
	public int getIndexForSampleId(String sampleId)
		{
		Assert.notNull(sampleId);
		int i = 0;
		for (Pair pr : idsAndValues)
			{
			if (sampleId.equals(pr.getId()))
				return i;
			i++;
			}
		
		return -1;
		}
	
	public String getSampleId(int i)
		{
		return idsAndValues.get(i).getId();
		}
	
	public String getSampleValue(int i)
		{
		return idsAndValues.get(i).getValue();
		}
	
	
	public void setSampleId(int i, String id)
		{
		try { idsAndValues.get(i).setId(id); }
		catch (IndexOutOfBoundsException e) { }
		}
	
	public void setSampleValue(int i, String value)
		{
		try { idsAndValues.get(i).setValue(value); }
		catch (IndexOutOfBoundsException e) { }
		}

	public String getExpId() 
		{
		return expId;
		}

	public void setExpId(String expId) 
		{
		this.expId = expId;
		}

	public List<Pair> getIdsAndValues() 
		{
		return idsAndValues;
		}

	public void setIdsAndValues(List<Pair> idsAndValues) 
		{
		this.idsAndValues = idsAndValues;
		}

	public String getFactorName() 
		{
		return factorName;
		}

	public void setFactorName(String factorName) 
		{
		this.factorName = factorName;
		}
	
	
	public String toString()
		{
		StringBuilder sb = new StringBuilder();
		
		sb.append("Experiment : " + expId);
		sb.append("Factor Name : " + factorName);
		
		for (int i = 0; i < idsAndValues.size(); i++)
			sb.append(idsAndValues.get(i).getId() + " " + idsAndValues.get(i).getValue());
		
		return sb.toString();
		}
	
	public boolean valuesInitialized()
		{
		for (int i =0;i < idsAndValues.size(); i++)
			if (StringUtils.isEmpty(idsAndValues.get(i).getValue()))
					return false;
			
		return true;
		}
	
	public boolean nameIsNewToExperiment()
		{
		List<String> existingNames = experimentService.getFactorNamesForExpId(expId);
		return  (existingNames == null || !existingNames.contains(factorName));
		}
	}
