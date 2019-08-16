// DrccSamplePrepInfo.java
// Written by Jan Wigginton July 2015

package edu.umich.brcf.metabolomics.panels.lipidshome.drcc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DrccSamplePrepInfo implements Serializable
	{
	ArrayList <DrccInfoField> infoFields = new ArrayList<DrccInfoField>();
	
	public DrccSamplePrepInfo()
		{
		}
	
	public DrccSamplePrepInfo(String expId)
		{
		infoFields.add(new DrccInfoField("Sample Prep Summary", "samplePrepSummary", ""));
		infoFields.add(new DrccInfoField("Protocol Id", "protocolId", ""));
		infoFields.add(new DrccInfoField("Protocol File Name", "protocolFilename", "Lipid_Extraction_Protocol.docx"));
		infoFields.add(new DrccInfoField("Protocol Comments", "protocolComments", ""));
		infoFields.add(new DrccInfoField("Processing Method", "processingMethod", ""));
		infoFields.add(new DrccInfoField("Processing Storage Conditions (Lyophilization, "
				+ "Homogenization, Lysis, etc)", "processingStorageConditions", ""));
	
		infoFields.add( new DrccInfoField("Extraction Method", "extractionMethod", "Liquid-liquid Extraction"));
		infoFields.add(new DrccInfoField("Extraction Concentration", "extractConcentration", ""));
		infoFields.add( new DrccInfoField("Extract Enrichment (SPE, DeSalting, Etc", "extractEnrichment", ""));
		infoFields.add(new DrccInfoField("Extract Cleanup", "extractCleanup", ""));
	
		infoFields.add(new DrccInfoField("Extract Storage", "extractStorage", ""));
		infoFields.add(new DrccInfoField("Sample Resuspension", "sampleResuspension", "IPA:CAN:H20 (85:10:5) in 10mM NH4OAc"));
		infoFields.add(new DrccInfoField("Sample Derivatization", "sampleDerivatization", ""));
		infoFields.add(new DrccInfoField("Sample Spiking ((Internal Standards, Retention Standards, etc)", "sampleSpiking", ""));
		infoFields.add(new DrccInfoField("Organ", "organ", ""));
		infoFields.add(new DrccInfoField("Organ Specification", "organSpecification", ""));
		infoFields.add(new DrccInfoField("Cell Type", "cellType", ""));
		infoFields.add(new DrccInfoField("Subcellular Location", "subCellularLocation", ""));
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

	