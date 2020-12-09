
//////////////////////////////////////////
//ISavableSampleDate.java
//Written by Julie Keros oct 2020 issue 94
//
//////////////////////////////////////////

package edu.umich.brcf.shared.util.interfaces;

import java.util.List;
import java.util.Map;

import edu.umich.brcf.shared.layers.dto.SampleDTO;

public interface ISavableMixtureData
	{
	/*public Map<String, List<String>> pullAssayMap();
	public List<? extends ICheckinSampleItem> pullSampleDTOs();
	public Map<String, List<String>> pullFactorMap();
	public String getExpId();*/
	
	public int writeToDataBase();
	public int getSampleCount();
	}


