//ControlDAO.java
//Written by Jan Wigginton
//March 2015

package edu.umich.brcf.shared.layers.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.springframework.dao.support.DataAccessUtils;
//import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Repository;

import edu.umich.brcf.shared.layers.domain.Control;
import edu.umich.brcf.shared.util.StringParser;




@Repository
public class ControlDAO extends BaseDAO
	{
	public void createControl(Control control) 	
		{
		getEntityManager().persist(control);
		}
	
	
	public void deleteControl(Control control) 
		{
		getEntityManager().remove(control);
		}
	
	
	public List<Control> allControls() 
		{
		List<Control> controlList = getEntityManager()
				.createQuery("from Controls").getResultList();
		for (Control control : controlList) 
			{
			initializeTheKids(control, new String[] { "exp", "assay", "controlType"});
			Hibernate.initialize(control.getExp().getProject());
			}
		return controlList;
		}

	
	public Control loadById(String controlId) 
		{
		return (getEntityManager().find(Control.class, controlId));
		}
		
	
	public Control loadInitializedById(String controlId) 
		{
		Control control = getEntityManager().find(Control.class, controlId);

		initializeTheKids(control, new String[] { "exp", "assay", "controlType"});
		Hibernate.initialize(control.getExp().getProject());
		Hibernate.initialize(control.getExp().getPriority());
	
		return control;
		}
	
	
	public String controlIdForExpIdAssayIdAndControlTypeId(String eid, String aid, String ctid)
		{
		Query query = getEntityManager().createNativeQuery("select cast(c.control_id as CHAR(9)) "
				+ "from Controls c where c.exp_id = ?1 and "
				+ "c.assay_id = ?2 "
				+ "c.control_type_id = ?3  order by  c.control_id asc")
				.setParameter(1,eid).setParameter(2, aid ).setParameter(3 ,ctid);
		
		List<String> controlList = query.getResultList();
		String controlId = (String) DataAccessUtils.requiredSingleResult(controlList);
		
		return controlId;
		}
	
	
	public List<String> allControlNamesAndIdsForExpId(String eid)
		{
		if (true)
			return this.allControlNamesAndIdsForExpIdAndAbsciex(eid);
		 
		Query query = getEntityManager().createNativeQuery("select cast(c.control_id as CHAR(9)), cast(ct.control_type_name as "
				+ "VARCHAR2(50)) "
				+ "from Controls c inner join Control_Type ct on c.control_type_id = ct.control_type_id where c.exp_id = ?1 "
				+ " order by  c.control_id asc")
				.setParameter(1, eid);
		
		
		ArrayList<String> labelledAssays = new ArrayList<String>();
		List<Object[]> assayList =  query.getResultList();
		
		for(Object [] assayResult : assayList)
			{
			String assayId = (String) assayResult[0];
			String assayName = (String) assayResult[1];
			labelledAssays.add(assayName + " (" + assayId + ")");
			}
		
		return labelledAssays;		
		}
	

	public List<String> allControlNamesAndIdsForExpIdAndPlatformId(String eid, String plat_id)
		{
		if (plat_id.equals("PL001"))
		     return this.allControlNamesAndIdsForExpIdAndAgilent(eid);
		
		return this.allControlNamesAndIdsForExpIdAndAbsciex(eid);
		}
	
	
	public List<String> allControlNamesAndIdsForExpIdAndAssayId(String eid, String aid, String platformId)
		{
		Query query = getEntityManager().createNativeQuery("select cast(ct.control_type_name as VARCHAR2(50)), "
				+ " cast(c.control_id as CHAR(9)) from ControlType ct "
				+ " left join Control c on c.control_type_id = ct.control_type_id "
				+ " where c.exp_id = ?1 and c.assay_id = ?2 "
				+ " order by ct.control_type_id").setParameter(1, eid).setParameter(2,  aid);

		List<Object[]> controlList =  query.getResultList();

		if (controlList.size() == 0)
			return this.allControlNamesAndIdsForExpIdAndPlatformId(eid, platformId);
		
		ArrayList<String> labelledControls= new ArrayList<String>();
		for(Object [] controlResult : controlList)
			{
			String controlId = (String) controlResult[0];
			String controlName = (String) controlResult[1];
			String.format("%-20s %12s", controlName, "(" + controlId + ")");
			labelledControls.add(String.format("%-18s %15s", controlName, "(" + controlId + ")"));
			}
		return labelledControls;
		}
		
	
	public List<String> allControlNamesAndIdsForExpIdAndAbsciex(String eid)
		{
		ArrayList<String> tempArray = new ArrayList<String>();
		tempArray.add("Blank         (CS00000002)");
		tempArray.add("Standard Mix  (CS00000003)");
		tempArray.add("Pooled Plasma (CS00000004)");
		tempArray.add("Test Pool     (CS00000001)");
		
		tempArray.add("Pool.0        (CS0000009)");
		tempArray.add("Pool.1        (CS0000091)");
		tempArray.add("Pool.2        (CS0000092)");
		tempArray.add("Pool.3        (CS0000093)");
		tempArray.add("Pool.4        (CS0000094)");
		tempArray.add("Pool.5        (CS0000095)");
		tempArray.add("Pool.6        (CS0000096)");
		tempArray.add("Pool.7        (CS0000097)");
		tempArray.add("Pool.8        (CS0000098)");
		tempArray.add("Pool.9        (CS0000099)");
		
		tempArray.add("Reference 1 - urine  (R00000091)");
		tempArray.add("Reference 2 - urine  (R00000092)");
		tempArray.add("Reference 1 - plasma (R00000098)");
		tempArray.add("Reference 2 - plasma (R00000099)");
	
		return tempArray;
		}
	

	// issue 394
	public List<String> allControlNamesAndIdsForExpIdAndAgilent(String eid)
	{
	ArrayList<String> tempArray = new ArrayList<String>();
		
	tempArray.add("Process Blank (CS00000PB)");
	tempArray.add("Solvent Blank (CS00000SB)");
	tempArray.add("Red Cross     (CS00000RC)");
	tempArray.add("Neat Blank    (CS00000NB)");

	tempArray.add("Master Pool   (CS00000MP)");
	tempArray.add("Master Pool.QCMP (CS000QCMP)"); // issue 450 // issue 17
	tempArray.add("Batch Pool.M1 (CS000BPM1)");// issue 17
	tempArray.add("Batch Pool.M2 (CS000BPM2)"); 
	tempArray.add("Batch Pool.M3 (CS000BPM3)"); 
	tempArray.add("Batch Pool.M4 (CS000BPM4)"); 
	tempArray.add("Batch Pool.M5 (CS000BPM5)"); 
	tempArray.add("Other Pool.0  (CS0000OP0)");
	tempArray.add("Other Pool.1  (CS0000OP1)");
	tempArray.add("Other Pool.2  (CS0000OP2)");
	tempArray.add("Other Pool.3  (CS0000OP3)");
	tempArray.add("Other Pool.4  (CS0000OP4)");
	tempArray.add("Other Pool.5  (CS0000OP5)");

	// issue 126
	
	tempArray.add("MoTrPAC -   Muscle-Human : Female  (CSMR81020)");
	tempArray.add("MoTrPAC -   Muscle-Human : Male  (CSMR81010)");
	
    // issue 22
	tempArray.add("MoTrPAC -   Hippocampus, Sedentary  (CSMR80025)");
	tempArray.add("MoTrPAC -   Hippocampus, Exercise  (CSMR80024)");
	
	// issue 33
	tempArray.add("MoTrPAC -   Heart, Sedentary  (CSMR80023)");
	tempArray.add("MoTrPAC -   Heart, Exercise (CSMR80022)");
	tempArray.add("MoTrPAC -   Brown Adipose, Sedentary  (CSMR80021)");
	tempArray.add("MoTrPAC -   Brown Adipose, Exercise  (CSMR80020)");
	tempArray.add("MoTrPAC -   Kidney, Sedentary (CSMR80019)");
	tempArray.add("MoTrPAC -   Kidney, Exercise (CSMR80018)");
	tempArray.add("MoTrPAC -   Lung, Sedentary (CSMR80017)");
	tempArray.add("MoTrPAC -   Lung, Exercise (CSMR80016)");
	
	// Issue 422
	tempArray.add("MoTrPAC -   Plasma, Sedentary  (CSMR80015)");
	tempArray.add("MoTrPAC -   Plasma, Exercise (CSMR80014)");
	tempArray.add("MoTrPAC -   Adipose, Sedentary  (CSMR80013)");
	tempArray.add("MoTrPAC -   Adipose, Exercise (CSMR80012)");
	tempArray.add("MoTrPAC -   Liver, Sedentary  (CSMR80011)");
	tempArray.add("MoTrPAC -   Liver, Exercise (CSMR80010)");
	tempArray.add("MoTrPAC -   Gastrocnemius, Sedentary  (CSMR80009)");
	tempArray.add("MoTrPAC -   Gastrocnemius, Exercise (CSMR80008)");
	tempArray.add("UM rat   plasma control (CS0UMRP01)");
	
	// issue 126
	tempArray.add("UM rat   gastrocnemius control (CS0UMRG01)");
	// Issue 427
	tempArray.add("UM rat   liver control (CS0UMRL01)");
	tempArray.add("UM rat   adipose control (CS0UMRA01)");
	// issue 126
	tempArray.add("UM Human muscle control  (CSOUMHM03)");
	
	tempArray.add("Standard.0    (CS000STD0)");
	tempArray.add("Standard.1    (CS000STD1)");
	tempArray.add("Standard.2    (CS000STD2)");
	tempArray.add("Standard.3    (CS000STD3)");
	tempArray.add("Standard.4    (CS000STD4)");
	tempArray.add("Standard.5    (CS000STD5)");
	tempArray.add("Standard.6    (CS000STD6)");
	tempArray.add("Standard.7    (CS000STD7)");
	tempArray.add("Standard.8    (CS000STD8)");
	tempArray.add("Standard.9    (CS000STD9)");
	tempArray.add("Standard.10   (CS00STD10)");
	tempArray.add("Standard.11   (CS00STD11)");
	tempArray.add("Standard.12   (CS00STD12)");

	tempArray.add("Agilent Standard (CS00000QC)");

	tempArray.add("Reference 1 - urine  (R00CHRUR1)");
	tempArray.add("Reference 2 - urine  (R00CHRUR2)");
	tempArray.add("Reference 1 - plasma (R00CHRPL1)");
	tempArray.add("Reference 2 - plasma (R00CHRPL2)");

	return tempArray;
	}
	
	
	// Issue 302
	public String controlIdForNameAndAgilent(String name)
	    {
	    List <String> allIds = this.allControlNamesAndIdsForExpIdAndAgilent(null);
	    for (String id : allIds)	
	        if (id.startsWith(name))  
	            {   
	            if ("Pool.1".equals(name) && id.startsWith("Pool.1b"))
	                continue;
	            else
	                return StringParser.parseId(id);
	            }
	     return "Unknown";
	     }

	
	public String dropStringForIdAndAgilent(String id)
		{
		List<String> allEntries = this.allControlNamesAndIdsForExpIdAndAgilent(null);
		
		int i = 0; 
		for (String entry : allEntries)
			{
			String idFound = StringParser.parseId(entry);
			if (idFound != null && idFound.equals(id))
				return  allEntries.get(i);
			
			i++;
			}
		
		return "Unknown";
		}
	}

	
