package edu.umich.brcf.shared.layers.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.shared.layers.dao.SampleTypeDAO;
import edu.umich.brcf.shared.layers.domain.SampleType;
import edu.umich.brcf.shared.util.io.StringUtils;


@Transactional
public class SampleTypeService
	{
	SampleTypeDAO sampleTypeDao;

	public SampleType loadById(String stId)
		{
		return sampleTypeDao.loadById(stId);
		}

	public String getNameForId(String stId)
		{
		SampleType st = loadById(stId);

		return (st == null ? "" : st.getDescription());
		}

	public List<SampleType> allSampleTypes()
		{
		return sampleTypeDao.allSampleTypes();
		}

	public SampleType loadByDescriptionAndUsage(String stName, String usage)
		{
		Assert.notNull(stName);
		return sampleTypeDao.loadByDescriptionAndUsage(stName, usage);
		}

	public List<SampleType> getMatchingTypes(String str)
		{
		return sampleTypeDao.getMatchingTypes(str);
		}

	public SampleType loadByDescription(String desc)
		{
		return sampleTypeDao.loadByDescription(desc);
		}

	public void setSampleTypeDao(SampleTypeDAO sampleTypeDao)
		{
		Assert.notNull(sampleTypeDao);
		this.sampleTypeDao = sampleTypeDao;
		}

	public String lookupCommonSampleTypeId(String selection)
		{
		if (StringUtils.isEmptyOrNull(selection))
			return "";

		String toCompare = StringUtils.cleanAndTrim(selection).trim().toLowerCase();

		switch (toCompare)
			{
			case "plasma": return "D010949";
			case "serum": return "D044967";
			case "blank": return "D000000";
			case "culturemedia": return "D003470";
			case "feces": return "D005243";
			case "tissues": return "D014024";
			case "urine":return "D014556";
			case "saliva":return "D012463";
			case "csf":
			case "cerebrospinalfluid": return "D002555";
			case "bronchoalveolarlavagefluid": return "D001992";
			case "fluidssecretionsother": return "D005441";
			case "adiposetissue":return "D000273";
			case "liver": return "D008099";
			case "muscle": return "D009132";
			case "kidney":return "D007668";
			case "nerve": return "D009417";
			case "ileum":return "D007082";
			case "cecum":return "D002432";
			case "tissuesother":return "D014024";
			case "bacteria": return "D001419";
			case "algae":return "D000456";
			case "yeasts":return "D015003";
			case "monocytes": return "D009000";
			case "erythrocytes":return "D004912";
			case "cellline": return "D002460";
			case "cellextracts": return "D002457";
			case "3T3l1cells": return "D04172";
			case "hct116cells":return "D045325";
			case "cells": return "D002477";
			case "cellscultured":return "D002478";
			case "cellsother":return "D002477";
			default: return "";
			}
		}
	}
