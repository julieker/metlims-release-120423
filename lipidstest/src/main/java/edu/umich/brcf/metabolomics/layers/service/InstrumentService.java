package edu.umich.brcf.metabolomics.layers.service;

import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.metabolomics.layers.dao.InstrumentDAO;
import edu.umich.brcf.metabolomics.layers.domain.Instrument;
import edu.umich.brcf.metabolomics.layers.domain.InstrumentRegistry;
import edu.umich.brcf.metabolomics.layers.dto.InstrumentDTO;
import edu.umich.brcf.shared.layers.dao.UserDAO;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.util.io.StringUtils;

@Transactional
public class InstrumentService
	{
	InstrumentDAO instrumentDao;
	UserDAO userDao;

	public Instrument save(InstrumentDTO dto)
		{
		Assert.notNull(dto);

		Instrument instrument = null;
		
		if (!StringUtils.isEmptyOrNull(dto.getInstrumentID()))
			try {
				instrument = instrumentDao.loadById(dto.getInstrumentID());
				instrument.update(dto);
				}
			catch(Exception e) { e.printStackTrace(); instrument = null; }
		else
			try
				{
				Instrument inst = Instrument.instance(null, dto.getName(),dto.getDescription(), dto.getType(), dto.getRoom(),
					dto.getManufacturer(), dto.getModel(), dto.getSerialNumber(), dto.getInstrumentClass());
	
				instrumentDao.createInstrument(inst);
				}
			catch(Exception e) { e.printStackTrace(); instrument = null; }
		
		return instrument;
		}

	
	public Instrument loadById(String org_id)
		{
		return instrumentDao.loadById(org_id);
		}

	public List<String>getListOfAllInstrumentTypes()
		{
		return instrumentDao.getListOfAllInstrumentTypes();
		}
	
	public List<String> getListOfAnalyticalInstrumentType()
		{
		return instrumentDao.getListOfAnalyticalInstrumentType();
		}

	public List<String> getListOfAnalyticalForAbsciex()
		{
		return instrumentDao.getListOfAnalyticalForAbsciex();
		}

	public List<String> getListOfAnalyticalForAgilent()
		{
		return instrumentDao.getListOfAnalyticalForAgilent();
		}

	public List<String> getLabelledListOfAnalyticalForAbsciex()
		{
		return instrumentDao.getLabelledListOfAnalyticalForAbsciex();
		}

	public List<String> getLabelledListOfAnalyticalForAgilent()
		{
		return instrumentDao.getLabelledListOfAnalyticalForAgilent();
		}

	public Instrument loadInstrumentById(String id)
		{
		return instrumentDao.getInstrumentById(id);
		}

	public Instrument loadInstrumentWithUserNotifySetByInstrumentId(String id)
		{
		return instrumentDao.loadInstrumentWithUserNotifySetByInstrumentId(id);
		}

	public List<InstrumentRegistry> loadInstrumentRegistry()
		{
		return instrumentDao.loadInstrumentRegistry();
		}

	public Set<User> getUserNotifySetForInstrument(Instrument instrument)
		{
		Instrument myInstrument = instrumentDao.loadInstrumentWithUserNotifySet(instrument);
		return myInstrument.getUserNotifySet();
		}

	
	public void addUserAssociationToInstrument(Instrument selectedInstrument, User selectedUser)
		{
		Assert.notNull(selectedInstrument, "Null instrument received in removeInstrumentToUserAssociation()");
		Assert.notNull(selectedUser, "Null user received in removeInstrumentToUserAssociation()");
		Instrument instrument = instrumentDao.loadInstrumentWithUserNotifySet(selectedInstrument);
		User user = userDao.loadById(selectedUser.getId());
		instrument.addUserNotifyAssociation(user);
		}

	
	public Set<User> removeInstrumentToUserAssociation(Instrument instrument, User user)
		{
		Assert.notNull(instrument,"Null instrument received in removeInstrumentToUserAssociation()");
		Assert.notNull(user, "Null user received in removeInstrumentToUserAssociation()");
		Instrument myInstrument = instrumentDao.getInstrumentById(instrument .getInstrumentID());
		User myUser = userDao.loadById(user.getId());

		myInstrument.getUserNotifySet().remove(myUser);

		return myInstrument.getUserNotifySet();
		}

	
	public InstrumentRegistry getInstrumentRegistryEntryForIPAddress(String ip)
		{
		return instrumentDao.getInstrumentRegistryEntryForIPAddress(ip);
		}

	
	public InstrumentRegistry getInstrumentRegistryEntryForInstrumentId(String id)
		{
		return instrumentDao.getInstrumentRegistryEntryForInstrumentId(id);
		}


	public List<Instrument> allInstruments()
		{
		return instrumentDao.allInstruments();
		}

	public void setInstrumentDao(InstrumentDAO instrumentDao)
		{
		this.instrumentDao = instrumentDao;
		}

	public void setUserDao(UserDAO userDao)
		{
		this.userDao = userDao;
		}
	}
