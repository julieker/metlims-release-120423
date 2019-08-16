//SubjectService.java
//Written by Jan Wigginton August 2015

package edu.umich.brcf.shared.layers.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import edu.umich.brcf.shared.layers.dao.SubjectDAO;
import edu.umich.brcf.shared.layers.domain.Subject;
import edu.umich.brcf.shared.util.io.StringUtils;



@Transactional
public class SubjectService
	{
	SubjectDAO subjectDao;
	
	// TO DO : Why is there no Subject.save??

	public Subject loadSubjectById(String subjectId)
		{
		return subjectDao.loadSubjectById(subjectId);
		}

	public void setSubjectDao(SubjectDAO subjectDao)
		{
		this.subjectDao = subjectDao;
		}

	public int getSubjectCountForExperiment(String expId)
		{
		return subjectDao.getSubjectCountForExperiment(expId);
		}

	public List<String> getSubjectIdsForExperimentId(String expId)
		{
		return subjectDao.getSubjectIdsForExperimentId(expId);
		}

	public Boolean areSubjectsForExpId(String expId, List<String> candidateIds)
		{
		List<String> existingSubjects = getSubjectIdsForExperimentId(expId);

		for (String id : candidateIds)
			{
			if (StringUtils.isEmpty(id))
				continue;

			if (!existingSubjects.contains(id))
				return false;
			}

		return true;
		}
	}