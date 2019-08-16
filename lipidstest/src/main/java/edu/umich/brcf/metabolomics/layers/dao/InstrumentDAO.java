///////////////////////////////////////////
// Written by Anu Janga
// Revisited by Jan Wigginton Octobrt 2016
///////////////////////////////////////////
package edu.umich.brcf.metabolomics.layers.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import edu.umich.brcf.metabolomics.layers.domain.Instrument;
import edu.umich.brcf.metabolomics.layers.domain.InstrumentRegistry;
import edu.umich.brcf.metabolomics.layers.domain.InstrumentRegistry.InstrumentRegistryPK;
import edu.umich.brcf.shared.layers.dao.BaseDAO;


@Repository
public class InstrumentDAO extends BaseDAO 
	{
	public void createInstrument(Instrument inst) {
		getEntityManager().persist(inst);
	}
	
	public Instrument loadById(String org_id) 
		{
		Instrument instrument = getEntityManager().find(Instrument.class, org_id);
		return instrument;
		}
	
	public List<Instrument> allInstruments() 
		{
		List<Instrument> list = getEntityManager().createQuery("from Instrument i order by i.type").getResultList();
		if (list != null)
			for (Instrument instrument : list)
				if (instrument.getRegistryEntry() != null)
					Hibernate.initialize(instrument.getRegistryEntry());

		return list;
		}

	
	public List<String> getListOfAllInstrumentTypes()
		{
		return getEntityManager().createQuery("select distinct i.type from Instrument i").getResultList();
		}
	
	
	public List<String> getListOfAnalyticalInstrumentType() 
		{
		return getEntityManager().createQuery("select distinct i.type from Instrument i where i.instrumentClass = 'ANALYTICAL'")
				.getResultList();
		}
	
	
	public List<String> getListOfAnalyticalForAbsciex() 
		{
		return getEntityManager().createQuery("select i.name from Instrument i where i.instrumentClass = 'ANALYTICAL' and "
				+ "i.manufacturer = 'Absciex'").getResultList();
		}
	
	
	public List<String> getLabelledListOfAnalyticalForAgilent() 
		{
		return buildInstrumentList("Agilent");
		}
	
	
	public List<String> getLabelledListOfAnalyticalForAbsciex() 
		{
		return buildInstrumentList("Absciex");
		}
	
	
	public List<String> buildInstrumentList(String manufacturer)
		{
		Query query =  getEntityManager().createNativeQuery("select cast(i.name AS VARCHAR2(100)), "
				+ "cast(i.instrument_id AS VARCHAR2(6)) from Instrument i where i.class = ?1 and "
				+ "i.manufacturer = ?2 order by i.instrument_id asc")
				.setParameter(1, "ANALYTICAL" ).setParameter(2,  manufacturer);
	
		List<Object[]> instrumentList =  query.getResultList();
	
		ArrayList<String> labelledInstruments = new ArrayList<String>();
		for(Object [] instrument : instrumentList)
			{
			String instrumentId = (String) instrument[1];
			String instrumentName = (String) instrument[0];
			labelledInstruments.add(instrumentId + " (" + instrumentName + ")");
			}
		
		return labelledInstruments;
		}

	
	public List<String> getListOfAnalyticalForAgilent() 
		{
		return getEntityManager().createQuery("select i.name from Instrument i where i.instrumentClass = 'ANALYTICAL' and "
				+ "i.manufacturer = 'Agilent' order by i.name asc")
				.getResultList();
		}

	
	public Instrument loadInstrumentWithUserNotifySetByInstrumentId(String id) 
		{
		Instrument instrument = getInstrumentById(id);
		Assert.notNull(instrument, "Could not find instrument with ID=" + id);
		Hibernate.initialize(instrument.getUserNotifySet());
		return instrument;
		}

	
	public Instrument loadInstrumentWithUserNotifySet(Instrument instrumentToLoad) 
		{
		Instrument instrument = getInstrumentById(instrumentToLoad.getInstrumentID());
		Hibernate.initialize(instrument.getUserNotifySet());
		return instrument;
		}

	
	public Instrument getInstrumentById(String id) 
		{
		return getEntityManager().find(Instrument.class, id);
		}

	
	public List<InstrumentRegistry> loadInstrumentRegistry() 
		{
		List<InstrumentRegistry> instrumentRegistry = getEntityManager().createQuery("from InstrumentRegistry").getResultList();
		
		for (InstrumentRegistry entry : instrumentRegistry)
			initializeTheKids(entry, new String[] { "instrument", "currentStatus" });

		return instrumentRegistry;
		}
	

	public InstrumentRegistry getInstrumentRegistryEntryForIPAddress(String ip) 
		{
		List<InstrumentRegistry> list = getEntityManager().createQuery( "from InstrumentRegistry ir where ir.ipAddress = :ip").setParameter("ip", ip).getResultList();
		
		return (list.size() > 0 ? list.get(0) : null);
		}

	
	public InstrumentRegistry getInstrumentRegistryEntryForInstrumentId(String id) 
		{
		InstrumentRegistryPK key = InstrumentRegistryPK.instance(id);
		InstrumentRegistry entry = getEntityManager().find(InstrumentRegistry.class, key);
		if (entry != null)
			{
			Hibernate.initialize(entry.getCurrentStatus());
			Hibernate.initialize(entry.getInstrument());
			}
		return entry;
		}
	}
