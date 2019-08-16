// DrccMsInfo.java
// Written by Jan Wigginton July 2015

package edu.umich.brcf.metabolomics.panels.lipidshome.drcc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DrccMsInfo implements Serializable
	{
	ArrayList <DrccInfoField> infoFields = new ArrayList<DrccInfoField>();
	
	String selectedMode;
	Date analysisDate;
	
	DrccMsInfo()
		{
		}
	
	DrccMsInfo(String selectedExperiment, String selectedMode, Date analysisDate)
		{
		this.analysisDate = analysisDate;
		this.selectedMode = selectedMode;
		
		infoFields.add(new DrccInfoField("Ion Mode*", "ionMode", "Negative", "Positive"));
		this.infoFields.add(new DrccInfoField("MS Instrument Type*", "msInstrumentType", "Triple TOF", 2));
		this.infoFields.add(new DrccInfoField("MS Ionization Type*", "msIonizationType", "ESI", 2));
		this.infoFields.add(new DrccInfoField("Capillary Temp", "capillaryTemp", "", 2));
		this.infoFields.add(new DrccInfoField("Capillary Voltage", "capillaryVoltage", "", 2));
		this.infoFields.add(new DrccInfoField("Collision Energy", "collisionEnergy", "MS1 10 and MS2 30", 2));
		this.infoFields.add(new DrccInfoField("Collision Gas", "collisionGas", "N2",2 ));
		this.infoFields.add(new DrccInfoField("Dry Gas Flow", "dryGasFlow", "40psi", 2));
		this.infoFields.add(new DrccInfoField("Dry Gas Temp", "dryGasTemp", "", 2));
		this.infoFields.add(new DrccInfoField("Fragment Voltage", "fragmentVoltage", "", 2));
		this.infoFields.add(new DrccInfoField("Fragmentation Method", "fragmentationMethod", "CID", 2)); 
		this.infoFields.add(new DrccInfoField("Gas Pressure", "gasPressure", "", 2));
		this.infoFields.add(new DrccInfoField("Helium Flow", "heliumFlow", "", 2));
		this.infoFields.add(new DrccInfoField("Ion Source Temperature", "ionSourceTemperature", "450", 2));
		this.infoFields.add(new DrccInfoField("Ion Spray Voltage", "ionSprayVoltage", "", 2));
		this.infoFields.add(new DrccInfoField("Ionization", "ionization", "", 2));
		this.infoFields.add(new DrccInfoField("Ionization Energy", "ionizationEnergy", "", 2));
		this.infoFields.add(new DrccInfoField("Ionization Potential", "ionizationPotential", "", 2));
		this.infoFields.add(new DrccInfoField("Mass Accuracy", "massAccuracy", "", 2));
		this.infoFields.add(new DrccInfoField("Precursor Type", "precursorType", "", 2));
		this.infoFields.add(new DrccInfoField("Reagant Gas", "reagantGas", "", 2));
		this.infoFields.add(new DrccInfoField("Source Temperature", "sourceTemperature", "", 2));
		this.infoFields.add(new DrccInfoField("Spray Voltage", "sprayVoltage", "", 2));
		this.infoFields.add(new DrccInfoField("Activation Parameter", "activationParameter", "", 2));
		this.infoFields.add(new DrccInfoField("Activation Time", "activationTime", "", 2));
		this.infoFields.add(new DrccInfoField("Atom Gun Current", "atomGunCurrent", "", 2));
		this.infoFields.add(new DrccInfoField("Automatic Gain Control", "automaticGainControl", "", 2));
		this.infoFields.add(new DrccInfoField("Bombardment", "bombardment", "", 2));
		this.infoFields.add(new DrccInfoField("CDL Side Oct Bias Voltage", "cdlSideOctBiasVoltage", "", 2));
		this.infoFields.add(new DrccInfoField("CDL Temperature", "cdlTemperature", "", 2));
		this.infoFields.add(new DrccInfoField("Data Format", "dataFormat", "", 2));
		this.infoFields.add(new DrccInfoField("Desolvation Gas Flow", "desolvationGasFlow", "", 2));
		this.infoFields.add(new DrccInfoField("Desolvation Temp", "desolvationTemp", "", 2));
		this.infoFields.add(new DrccInfoField("Interface Voltage", "interfaceVoltage", "", 2));
		this.infoFields.add(new DrccInfoField("IT Side Oct Bias Voltage", "itSideOctBiasVolt", "", 2));
		this.infoFields.add(new DrccInfoField("Laser", "laser", "", 2));
		this.infoFields.add(new DrccInfoField("Matrix", "matrix", "", 2));
		this.infoFields.add(new DrccInfoField("Nebulizer", "nebulizer", "", 2));
		this.infoFields.add(new DrccInfoField("Octopole Voltage", "octopoleVoltage", "", 2));
		this.infoFields.add(new DrccInfoField("Probe Tip", "probeTip", "", 2));
		this.infoFields.add(new DrccInfoField("Resolution Setting", "resolutionSetting", "", 2));
		this.infoFields.add(new DrccInfoField("Sample Dripping", "sampleDripping", "", 2));
		this.infoFields.add(new DrccInfoField("Scan Range Mz", "scanRangeMz", "50-1200", 2));
		this.infoFields.add(new DrccInfoField("Scanning", "scanning", "", 2));
		this.infoFields.add(new DrccInfoField("Scanning Cycle", "scanningCycle", "", 2));
		this.infoFields.add(new DrccInfoField("Scanning Range", "scanningRange", "", 2));
		this.infoFields.add(new DrccInfoField("Skimmer Voltage", "skimmerVoltage", "", 2));
		this.infoFields.add(new DrccInfoField("Tube Lens Voltage", "tubeLensVoltage", "", 2));
		
		setViewNegativeMode(!("Positive".equals(selectedMode)));
    	setViewPositiveMode(!("Negative".equals(selectedMode)));
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
	
	
	public void setViewNegativeMode(boolean printNegative)
		{
		for (int i = 0; i < infoFields.size(); i++)
			infoFields.get(i).setColPrinted(0, printNegative);
		}

	public void setViewPositiveMode(boolean printPositive)
		{
		for (int i = 0; i < infoFields.size(); i++)
			infoFields.get(i).setColPrinted(1, printPositive);
		}
	}
	
