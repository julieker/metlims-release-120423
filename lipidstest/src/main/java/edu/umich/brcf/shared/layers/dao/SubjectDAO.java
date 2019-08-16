///////////////////////////////////////////
// Writtten by Anu Janga
///////////////////////////////////////////

package edu.umich.brcf.shared.layers.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Subject;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.io.StringUtils;


@Repository
public class SubjectDAO extends BaseDAO
	{
	public void createSubject(Subject subject) 
		{
		getEntityManager().persist(subject);
		}

	
	public List<Subject> allSubjects()
	    {
	    List<Subject> subjectList = getEntityManager().createQuery("from Subject").getResultList();
	
	    return subjectList;
	    }
	
	
	public Subject loadSubjectById(String subjectId) 
		{
		if (subjectId == null) return null;
		
		Subject subject = getEntityManager().find(Subject.class, subjectId);
		return subject;
		}
	
	

	public Subject loadSubjectByNameAndExp(String subjectName, Experiment exp) 
		{
		Query query = getEntityManager().createQuery("select su.subjectId from Subject su, Sample s" +
				" where su.userSubjectId='"+subjectName+"' and su.subjectId=s.subject.subjectId and s.exp.expID ='"+exp.getExpID()+"'");
		
		List<String> subjectId = (List<String>) query.getResultList();
		return loadSubjectById(subjectId.get(0));
		}
	
	
	public boolean subjectExistsForExperimentAndName(String subjectName, Experiment exp)
		{
		// issue 462
		subjectName = subjectName.replaceAll("'","''");
		Query query = getEntityManager().createQuery("select su.subjectId from Subject su, Sample s" +
				" where su.userSubjectId='"+subjectName+"' and su.subjectId=s.subject.subjectId and s.exp.expID ='"+exp.getExpID()+"'");
		
		
		List<String> subjectIdList = (List<String>) query.getResultList();
		return (subjectIdList.size() > 0);
		}

	
	public int getSubjectCountForExperiment(String expId)
		{
		List<String> orgList = getSubjectIdsForExperimentId(expId); //query.getResultList();
		
		return (orgList != null ? orgList.size()  : 0);
		}
	
	
	public List<String> getSubjectIdsForExperimentId(String expId)
		{
		List<String> subjectIds = new ArrayList<String>();
		
		if (StringUtils.isEmptyOrNull(expId))
			return subjectIds;
	
		if (!(FormatVerifier.verifyFormat(Experiment.idFormat, expId)))
			return subjectIds;
		
		Query query = getEntityManager().createNativeQuery("select cast(su.subject_id as VARCHAR2(9)) from Subject su left join "
				+ " Sample s on su.subject_id = s.subject_id where s.exp_id = ?1 group by su.subject_id order by su.subject_id ")
				.setParameter(1, expId);
	
		subjectIds = query.getResultList();
		
		return subjectIds;
		}
	
	
	 public List<String> getExpIdforSub(String subId)
	     {
	     List<String> eIds= new ArrayList<String>();
	
	     Query query = getEntityManager().createNativeQuery("select cast(s.exp_id as VARCHAR2(7)) from Subject su left join "
	                     + " Sample s on su.subject_id = s.subject_id where trim(su.subject_id) = trim('" + subId + "') and rownum = 1 ");
	
	     eIds = query.getResultList();
	
	     return eIds;
	     }
	}

