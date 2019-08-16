// DrccCollectionInfo.java
// Written by Jan Wigginton July 2015

package edu.umich.brcf.metabolomics.panels.lipidshome.drcc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SampleService;


public class DrccCollectionInfo implements Serializable
	{
	@SpringBean
	ExperimentService experimentService;
	
	@SpringBean
	SampleService sampleService;

	ArrayList <DrccInfoField> infoFields = new ArrayList<DrccInfoField>();
	

	public DrccCollectionInfo()
		{
		}
	
	public DrccCollectionInfo(String expId)
		{
		Injector.get().inject(this);
		
		String sampleType = guessSampleType(expId);
		infoFields.add(new DrccInfoField("Collection Summary", "collectionSummary", ""));
		infoFields.add(new DrccInfoField("Collection Protocol Id", "collectionProtocolId", ""));
		infoFields.add(new DrccInfoField("Collection Protocol Filename", "collectionProtocolFilename", ""));
		infoFields.add(new DrccInfoField("Collection Protocol Comments", "collectionProtocolComments", ""));
		infoFields.add(new DrccInfoField("Sample Type*", "sampleType", sampleType));
		infoFields.add(new DrccInfoField("Collection Method", "collectionMethod", ""));
		infoFields.add(new DrccInfoField("Collection Location", "collectionLocation", ""));
		infoFields.add(new DrccInfoField("Collection Frequency", "collectionFrequency", ""));
		infoFields.add(new DrccInfoField("Collection Duration", "collectionDuration", ""));
		infoFields.add(new DrccInfoField("Time of Collection (Relative to dose)", "collectionTime", ""));
		infoFields.add(new DrccInfoField("Volume/Amount Collected", "collectionVolume", ""));
		infoFields.add(new DrccInfoField("Storage Conditions", "storageConditions", ""));
		infoFields.add(new DrccInfoField("Collection Vials", "collectionVials", ""));
		infoFields.add(new DrccInfoField("Storage Vials", "storageVials", ""));
		infoFields.add(new DrccInfoField("Collection Tube Temp", "collectionTubeTemp", ""));
		infoFields.add(new DrccInfoField("Additives", "additives", ""));
		infoFields.add(new DrccInfoField("Blood, Serum or Plasma", "bloodSerumPlasma", ""));
		infoFields.add(new DrccInfoField("Tissue/Cell Id", "tissueCellId", ""));
		infoFields.add(new DrccInfoField("Tissue/Cell Quantity Taken", "tissueCellQuantity", ""));
		}
	
	
	String guessSampleType(String expId)
		{
		Experiment exp = experimentService.loadById(expId);
		
		List<Sample> sampleList = exp.getSampleList();
		
		
		String sampleId = sampleList.get(0).getSampleID();
		Sample sample = sampleService.loadById(sampleId);
		String sampleTypeId = sample.getSampleType().getSampleTypeId();
		
		switch (sampleTypeId)
			{
			case "D010949" :
			case "D044967" : 
				return "Blood";
				
			case "D002478" :
			case "D041721" :
			case "D002477" :
			case "D054885" :
			case "D045325" :
			case "D002457" :
			case "D002448" :
			case "D042783" :
			case "D019169" :
			case "D002460" :
			case "D006367" :
				return "Cell";
				
			case "D002001" :
			case "D014024" :
			case "D052436" :
			case "D000273" :
				return "Tissue";
						
			case "D012463" :
				return "Saliva";
				
			case "D014556" :
				return "Urine";
			
			default : 
				return "Other";
			}
		}
	
	public List <DrccInfoField> getInfoFields()
		{
		return infoFields;
		}

	public List <DrccInfoField> getInfoValue()
		{
		return infoFields;
		}
	
	public String getInfoValue(int i)
		{
		return infoFields.get(i).getFieldValues(0);
		}
	
	public void setInfoValue(int i, String value)
		{
		infoFields.get(i).setFieldValues(0, value);
		}
	}

	

