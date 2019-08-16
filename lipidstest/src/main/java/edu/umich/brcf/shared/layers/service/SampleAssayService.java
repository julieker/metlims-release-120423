///////////////////////////////////////////
// Written by Anu Janga
// Revisited Jauary 2015, (JW)
///////////////////////////////////////////

package edu.umich.brcf.shared.layers.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import edu.umich.brcf.shared.layers.dao.SampleAssayDAO;
import edu.umich.brcf.shared.layers.dao.SampleDAO;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.domain.SampleAssay;
import edu.umich.brcf.shared.layers.domain.SampleAssay.SampleAssayPK;
import edu.umich.brcf.shared.util.structures.SelectableObject;


@Transactional
public class SampleAssayService
	{
	private SampleAssayDAO sampleAssayDao;
	private SampleDAO sampleDao;

	public void createSampleAssay(SampleAssay sampleAssay)
		{
		sampleAssayDao.createSampleAssay(sampleAssay);
		}

	public void deleteSampleAsay(SampleAssay sampleAssay)
		{
		sampleAssayDao.deleteSampleAssay(sampleAssay);
		}

	public SampleAssay loadById(SampleAssayPK sampleAssayId)
		{
		return sampleAssayDao.loadById(sampleAssayId);
		}

	public List<SampleAssay> loadAssaysForSample(Sample s)
		{
		return sampleAssayDao.loadAssaysForSample(s);
		}

	public List<SampleAssay> loadForAssayAndExperiment(String expId, String assayId)
		{
		List<Sample> sampleList = sampleDao.loadSampleForAssayStatusTracking(expId);

		List<SampleAssay> lst = new ArrayList<SampleAssay>();
		for (Sample s : sampleList)
			{
			List<SampleAssay> assaysForSample = loadAssaysForSample(s);
			for (SampleAssay assay : assaysForSample)
				if (assay.getAssay().getAssayId().equals(assayId))
					lst.add(assay);
			}

		return lst;
		}

	
	public void updateStatusForExpAndAssayId(String expId, String assayId, String status)
		{
		updateStatus(loadForAssayAndExperiment(expId, assayId), status);
		}

	
	// JAK fix issue 155 and 159
	public void updateStatusForExpAndAssayIdEfficiently(String expId, String assayId,  String status)
	{
	updateStatusEfficiently(expId, assayId, status);
	}
	

	public void updateStatusEfficiently (String expId, String assayId, String status)
	{
		sampleAssayDao.updateStatusNativelyEfficiently(status, assayId, expId);
	}

	public void updateStatusForSelections(List<SelectableObject> sampleAssaySelections, String status)
		{
		List<SampleAssay> sampleAssays = new ArrayList<SampleAssay>();

		for (SelectableObject so : sampleAssaySelections)
			if (so.isSelected())
				{
				SampleAssay s = ((SampleAssay) so.getSelectionObject()); // sampleAssayDao.loadById(((SampleAssay)so.getSelectionObject()).getId());
				sampleAssays.add(s);
				}
		
		updateStatus(sampleAssays, status);
		}

	
	public void updateStatus(List<SampleAssay> sampleAssays, String status)
		{
		Character temp = status.charAt(0);

		for (SampleAssay sa : sampleAssays)
			{
			switch (temp)
				{
				case 'Q': sa.setQueuedStatus(); break;
				case 'P': sa.setPrepStatus(); break;
				case 'R': sa.setStatusSamplesRun(); break;
				case 'D': sa.setStatusDataCuration(); break;
				case 'C': sa.setCompletedStatus(); break;
				case 'X': sa.setStatusExcluded(); break;
				default: throw new IllegalStateException(temp + " is not mapped!");
				}

			String assayId = sa.getAssay().getAssayId();
			String sampleId = sa.getSample().getSampleID();

			sampleAssayDao.updateStatusNatively(sampleId, assayId, temp);
			}
		}

	
	public SampleAssayDAO getSampleAssayDao()
		{
		return sampleAssayDao;
		}

	public void setSampleAssayDao(SampleAssayDAO sampleAssayDao)
		{
		this.sampleAssayDao = sampleAssayDao;
		}

	public SampleDAO getSampleDao()
		{
		return sampleDao;
		}

	public void setSampleDao(SampleDAO sampleDao)
		{
		this.sampleDao = sampleDao;
		}
	}

