// Written 01/26/15 by Jan Wigginton

package edu.umich.brcf.shared.layers.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.shared.layers.dao.AssayDAO;
import edu.umich.brcf.shared.layers.domain.Assay;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.SampleAssay;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.io.StringUtils;


@Transactional
public class AssayService
	{
	AssayDAO assayDao;

	public Assay loadAssayByName(String assayName)
		{
		return assayDao.loadAssayByName(assayName);
		}
	
	public void createSampleAssay(SampleAssay sampleAssay)
		{
		assayDao.createSampleAssay(sampleAssay);
		}
	
	// issue 187
	public List<String> allAssayNamesAndIdsMatching()
		{
		return assayDao.allAssayNamesAndIdsMatching();
		}
	
	// issue 187
	public List<String> allAssayNamesAndIdsMatching(boolean forAssaySearch)
		{
		return assayDao.allAssayNamesAndIdsMatching(forAssaySearch);
		}
	
	// Issue 249
	public List<String> samplesAssociatedWithAssay(String expId, String assayID)
        {
	    return assayDao.samplesAssociatedWithAssay(expId, assayID);		
        }

	// Issue 249
	public List<String> samplesTooManyAssaysForSample(String sampleTuple, String assayId)
	    {
		return assayDao.samplesTooManyAssaysForSample(sampleTuple, assayId);		
	    }
	
	public Assay loadAssayByID(String assayId)
		{
		return assayDao.loadAssayByID(assayId);
		}

	
	public List<String> allAssayNames()
		{
		return assayDao.allAssayNames();
		}
	

	public List<String> allAssayNamesAndIds()
		{
		return assayDao.allAssayNamesAndIds();
		}

	
	public String getStandardNameForAssayName(String name)
		{
		return assayDao.getStandardNameForAssayName(name);
		}

	
	public List<String> pullShortNamesAndIdsFor(List<String> longNames, int nChars)
		{
		List<String> shortNames = new ArrayList<String>();

		for (int i = 0; i < longNames.size(); i++)
			{
			if (longNames.get(i) == null || "".equals(longNames.get(i)))
				continue;

			String assayName = StringParser.parseName(longNames.get(i));
			String assayId = StringParser.parseId(longNames.get(i));
			String assayNameToUse = FormatVerifier.verifyFormat( Assay.fullIdFormat, assayId) ? assayName : longNames.get(i);
			shortNames.add(assayShortNameAndIdByName(assayNameToUse, nChars)); 
			}

		return shortNames;
		}
	

	public List<String> allAssayNamesAndIdsShortened(int nChars)
		{
		List<String> longAssayNames = allAssayNames();
		return pullShortNamesAndIdsFor(longAssayNames, nChars);
		}

	
	public String assayShortNameAndIdByName(String assayName, int nChars)
		{
		Assay assay = assayDao.loadAssayByName(assayName);
		String assayId = assay.getAssayId();
		String shortName = assayName.substring(0, Math.min(nChars, assayName.length()));

		return shortName + " (" + assayId + ")";
		}

	
	public List<String> allAssayNamesForPlatform(String platform)
		{
		System.out.println("here is platform:" + platform);
		return assayDao.allAssayNamesForPlatform(platform);
		}

	
	public List<String> allAssayNamesForPlatformAndExpId(String platform, String eid)
		{
		if (StringUtils.isEmptyOrNull(platform) || StringUtils.isEmptyOrNull(eid))
			return new ArrayList<String>();
		
		return assayDao.allAssayNamesForPlatformAndExpId(platform, eid);
		}
	

	public List<String> allAssayIdsForPlatformId(String platId)
		{
		return assayDao.allAssayIdsForPlatformId(platId);
		}

	
	public List<String> allAssayNamesForExpId(String expId)
		{
		return assayDao.allAssayNamesForExpId(expId, true);
		}

	
	public List<String> allAssayNamesForExpId(String expId, boolean skipAbsciex)
		{
		return assayDao.allAssayNamesForExpId(expId, skipAbsciex);
		}

	public List<String> allAssayIdsForExpId(String expId, boolean skipAbsciex)
		{
		return assayDao.allAssayIdsForExpId(expId, skipAbsciex);
		}

	public String getIdForAssayName(String assayName)
		{
		return assayDao.getIdForAssayName(assayName);
		}
	
	public String getNameForAssayId(String assayId)
		{
		return assayDao.getNameForAssayId(assayId);
		}

	public void setAssayDao(AssayDAO assayDao)
		{
		Assert.notNull(assayDao);
		this.assayDao = assayDao;
		}
	
	// issue 123
	public List<String> loadByAssayWithAliquots()
		{
		return assayDao.loadByAssayWithAliquots();
		}
	
	// issue 187
	public String getAssayIdForSearchString(String str, String label)
		{
		if (str == null) 
			throw new RuntimeException("Assay string string can't be null");
		
		String assayId = str;
		if(!FormatVerifier.verifyFormat(Assay.fullIdFormat,str.toUpperCase()))
			assayId = StringParser.parseId(str);
		//if(!FormatVerifier.verifyFormat(Experiment.fullIdFormat,assayId.toUpperCase()))
		//	{
			try  
				{
				Assay assay = assayDao.loadById(str);
				assayId = assay.getAssayId();
				}
			catch (Exception e) { throw new RuntimeException("Assay load error : cannot find assay  " + label + " "  + str);  }
			//}
		
		return assayId;
		} 
	
	}
