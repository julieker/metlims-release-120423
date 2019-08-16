package edu.umich.brcf.metabolomics.panels.lipidshome.browse;

//MS2RawData.java
//Written by Jan Wigginton 04/10/15

import java.io.Serializable;
import java.util.ArrayList;

public class Ms2RawData implements Serializable
	{
	private static final long serialVersionUID = 4762640379785082305L;
	public ArrayList<ArrayList<Double>> peakAreas;
	public ArrayList<String> compoundLabels, sampleLabels, sideColumnLabels;

	public ArrayList<Double> retentionTimes, startMasses, endMasses;
	public ArrayList<String> lipidClasses, knownStatuses;

	public Ms2RawData()
		{
		initializeArrays();
		}

	public void initializeArrays()
		{
		peakAreas = new ArrayList<ArrayList<Double>>();

		compoundLabels = new ArrayList<String>();
		sampleLabels = new ArrayList<String>();
		sideColumnLabels = new ArrayList<String>();

		retentionTimes = new ArrayList<Double>();
		startMasses = new ArrayList<Double>();
		endMasses = new ArrayList<Double>();

		lipidClasses = new ArrayList<String>();
		knownStatuses = new ArrayList<String>();
		}

	public void addStartMass(Double startMass)
		{
		startMasses.add(startMass);
		}

	public void addCompoundLabel(String label)
		{
		compoundLabels.add(label);
		}

	public void addRetentionTime(Double rt)
		{
		retentionTimes.add(rt);
		}

	public void addEndMass(Double em)
		{
		endMasses.add(em);
		}

	public void addSampleLabel(String label)
		{
		sampleLabels.add(label);
		}

	public void addLipidClass(String lc)
		{
		lipidClasses.add(lc);
		}

	public void addKnownStatus(String status)
		{
		knownStatuses.add(status);
		}

	public void addPeakArea(int i, Double pa)
		{
		peakAreas.get(i).add(pa);
		}
	}