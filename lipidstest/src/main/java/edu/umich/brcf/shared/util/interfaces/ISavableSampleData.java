
//////////////////////////////////////////
//ISavableSampleDate.java
//Written by Jan Wigginton February 2016
//////////////////////////////////////////

package edu.umich.brcf.shared.util.interfaces;

import java.util.List;
import java.util.Map;

import edu.umich.brcf.shared.layers.dto.SampleDTO;

public interface ISavableSampleData
	{
	public Map<String, List<String>> pullAssayMap();
	public List<? extends ICheckinSampleItem> pullSampleDTOs();
	public Map<String, List<String>> pullFactorMap();
	public String getExpId();
	
	public int writeToDataBase();
	public int getSampleCount();
	}


