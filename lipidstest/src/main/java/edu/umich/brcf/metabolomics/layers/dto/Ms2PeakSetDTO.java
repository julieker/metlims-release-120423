///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//MS2PeakSetDTO.java
//Written by Jan Wigginton 04/10/15
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.layers.dto;


import java.io.Serializable;

import edu.umich.brcf.metabolomics.layers.domain.Ms2PeakSet;


public class Ms2PeakSetDTO implements Serializable
	{
	private static final long serialVersionUID = -9007111343989723215L;

	public static Ms2PeakSetDTO instance(String psid, String ln, Double stMass, Double endMass, 
	 Double expectedRt, String lipidClass, String ks, String dsid)
		{
		return new Ms2PeakSetDTO(psid, ln, stMass, endMass, expectedRt, lipidClass, ks, dsid);
		}

	
	public static Ms2PeakSetDTO instance(Ms2PeakSet peakSet)
		{
		return new Ms2PeakSetDTO(peakSet.getPeakSetId(), peakSet.getLipidName(), peakSet.getStartMass(),
		 peakSet.getEndMass(), peakSet.getExpectedRt(), peakSet.getLipidClass(), peakSet.getKnownStatus(), peakSet.getDataSet().getDataSetId());
		}

	
	private String peakSetId;
	private String lipidName;
	private Double startMass, endMass, expectedRt;
	private String lipidClass;
	private String knownStatus;
	private String dataSetId;

	
	public Ms2PeakSetDTO() {}

	
	private Ms2PeakSetDTO(String psid, String ln, Double stMass, Double endMass, Double expectedRt, String lipidClass, String ks, String dsid)
		{
		this.peakSetId = psid;
		this.lipidName = ln;
		this.startMass = stMass;
		this.endMass = endMass;
		this.expectedRt = expectedRt;
		this.lipidClass = lipidClass;
		this.knownStatus = ks;
		this.dataSetId = dsid;
		}

	
	public String getKnownStatus()
		{
		return knownStatus;
		}

	public void setKnownStatus(String ks)
		{
		knownStatus = ks;
		}

	public String getPeakSetId()
		{
		return peakSetId;
		}

	public void setPeakSetId(String peakSetId)
		{
		this.peakSetId = peakSetId;
		}

	public String getLipidName()
		{
		return lipidName;
		}

	public void setLipidName(String lipidName)
		{
		this.lipidName = lipidName;
		}

	public Double getStartMass()
		{
		return startMass;
		}

	public void setStartMass(Double startMass)
		{
		this.startMass = startMass;
		}

	public Double getEndMass()
		{
		return endMass;
		}

	public void setEndMass(Double endMass)
		{
		this.endMass = endMass;
		}

	public Double getExpectedRt()
		{
		return expectedRt;
		}

	public void setExpectedRt(Double expectedRt)
		{
		this.expectedRt = expectedRt;
		}

	public String getLipidClass()
		{
		return lipidClass;
		}

	public void setLipidClass(String lipidClass)
		{
		this.lipidClass = lipidClass;
		}

	public String getDataSetId()
		{
		return this.dataSetId;
		}

	public void setDataSetId(String dsid)
		{
		this.dataSetId = dsid;
		}
	}
