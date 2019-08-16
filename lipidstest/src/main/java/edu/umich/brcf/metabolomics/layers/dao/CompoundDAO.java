///////////////////////////////////////////
// Writtten by Anu Janga
// Revisited by Jan Wigginton November 2016
///////////////////////////////////////////

package edu.umich.brcf.metabolomics.layers.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.domain.CompoundName;
import edu.umich.brcf.shared.layers.dao.BaseDAO;
import edu.umich.brcf.shared.layers.dao.IdGeneratorDAO;
import edu.umich.brcf.shared.layers.domain.Inventory;
import edu.umich.brcf.shared.layers.domain.Solvent;


@Repository
public class CompoundDAO extends BaseDAO 
	{
	public static String BEAN_NAME = "compoundDAO";
	private IdGeneratorDAO idGeneratorDao;

	
	public Compound loadCompoundByCan(String can) 
		{
		Map<String, Object> parms = new HashMap<String, Object>();
		parms.put("can", can.toUpperCase());
		String qry = "from Compound c where c.chem_abs_number = :can";
		List<Compound> lst = getEntityManager().createQuery(qry).setParameter("can", can.toUpperCase()).getResultList();
		
		Compound cmpd = (Compound) DataAccessUtils.requiredSingleResult(lst);
		return cmpd;
		}
	
	
	public List<Compound> getCompoundsAllInfoWithinMass(double lowerLimit, double upperLimit) 
		{
		List<Compound> lst = getEntityManager().createQuery("from Compound c where c.cid like 'C%' and c.cid not like 'CE%' and c.nominalMass between :lowerLimit and :upperLimit")
		.setParameter("upperLimit", new BigDecimal(upperLimit)).setParameter("lowerLimit", new BigDecimal(lowerLimit))
		.getResultList();
		
		for (Compound c : lst)
			{
			Hibernate.initialize(c.getParent());
			initializeTheKids(c, new String[] { "names", "inventory", "solvent"});
			}
		return lst;
		}

	
	public List<CompoundName> searchName(String str) 
		{
		String qry = "from CompoundName cn where cn.name like :name";
		return getEntityManager().createQuery(qry).setParameter("nane", str.toUpperCase()).getResultList();
		}

	
	public Compound loadCompoundById(String id) {
		Compound c = getEntityManager().find(Compound.class, id);
		if(c!=null)
			{
			initializeTheKids(c, new String[] { "names", "inventory", "solvent" }); //, "docList"
			Hibernate.initialize(c.getParent());
			for (Inventory inventory : c.getInventory())
				Hibernate.initialize(inventory.getLocation());
			}
		return c;
		}

	
	public List<String> getMatchingCids(String input)
		{
		Query query = getEntityManager().createQuery("select cid from Compound where cid like '"+input+"%'");
		return query.getResultList();
		}
	
	
	public List<Compound> getChildren(Compound compound)
		{
		String qry = "from Compound c where c.cid <> '"+ compound.getCid()+"' and c.parent= :compound";
		List<Compound> lst = getEntityManager().createQuery(qry).setParameter("compound", compound).getResultList();
		
		for (Compound c : lst)
			{
			initializeTheKids(c, new String[] { "names", "inventory", "solvent" });
			Hibernate.initialize(c.getParent());
			for (Inventory inventory : c.getInventory())
				Hibernate.initialize(inventory.getLocation());
			}
		
		return lst;
		}
	
	
	public Solvent getSolventForLogPValue(BigDecimal value) 
		{
		Solvent solvent = (Solvent) DataAccessUtils.requiredSingleResult(getEntityManager().createQuery("from Solvent s "
				+ "where :myLogP between s.startLogP and s.endLogP and rownum<=1").setParameter("myLogP", value).getResultList()
				);
		return solvent;
		}

	
	public void createCompound(Compound cmpd)
		{
		getEntityManager().persist(cmpd);
		}


	public List<Inventory> grabCompoundsForMultiplexing() 
		{
		List<Inventory> onHand = getEntityManager().createQuery("from Inventory").getResultList();
		List<Inventory> returnList = getRandomList(onHand); // this is a random
		
		for (Inventory inventory : returnList) 
			{
			Hibernate.initialize(inventory.getCompound());
			Hibernate.initialize(inventory.getCompound().getNames());
			Hibernate.initialize(inventory.getCompound().getSolvent());
			Hibernate.initialize(inventory.getLocation());
			}
		return returnList;
		}
	
	
	private List<Inventory> getRandomList(List<Inventory> list) 
		{
		List<Inventory> returnList = new ArrayList<Inventory>();
		List<Inventory> workingList = new ArrayList<Inventory>();
		workingList.addAll(list);
		Random myRand = new Random(Calendar.getInstance().getTimeInMillis());

		for (int i = 0; i < 20; i++)
			returnList.add(workingList.remove(myRand.nextInt(workingList.size())));

		return returnList;
		}

	
	public List<Compound> getCompoundsWithinMass(double upperLimit, double lowerLimit) 
		{
		List<Compound> lst = getEntityManager().createQuery("from Compound c where c.cid like 'C%' and c.cid not like 'CE%' and c.nominalMass between :lowerLimit and :upperLimit")
				.setParameter("upperLimit", new BigDecimal(upperLimit)).setParameter("lowerLimit", new BigDecimal(lowerLimit)).getResultList();
		
		for (Compound c : lst)
			initializeTheKids(c, new String[] { "names"});
			
		return lst;
		}

	
	public Compound loadByCatnum(String catnum) 
		{
		Query query = getEntityManager().createQuery("select c.cid from Compound c, Inventory i where c.cid=i.compound.cid and i.catalogueNumber like '"+catnum+"%'");
		String cid = (String) query.getSingleResult();
		return loadCompoundById(cid);
		}
	}
