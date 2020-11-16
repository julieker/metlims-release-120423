// Updated by Julie Keros June 2 2020
package edu.umich.brcf.shared.layers.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.Session;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import edu.umich.brcf.metabolomics.layers.dao.CompoundDAO;
import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.shared.layers.dao.AliquotDAO;
import edu.umich.brcf.shared.layers.dao.AssayDAO;
import edu.umich.brcf.shared.layers.dao.ExperimentDAO;
import edu.umich.brcf.shared.layers.dao.InventoryDAO;
import edu.umich.brcf.shared.layers.dao.LocationDAO;
import edu.umich.brcf.shared.layers.dao.SampleDAO;
import edu.umich.brcf.shared.layers.dao.UserDAO;
import edu.umich.brcf.shared.layers.domain.Aliquot;
import edu.umich.brcf.shared.layers.domain.Assay;
import edu.umich.brcf.shared.layers.domain.AssayAliquot;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.ExperimentAliquot;
import edu.umich.brcf.shared.layers.domain.Inventory;
import edu.umich.brcf.shared.layers.domain.Location;
import edu.umich.brcf.shared.layers.dto.AliquotDTO;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;

@Transactional
public class AliquotService 
    {	
	AliquotDAO aliquotDao;
	SampleDAO sampleDao;
	UserDAO userDao;
	CompoundDAO compoundDao;
	LocationDAO locationDao;
	InventoryDAO inventoryDao;
	ExperimentDAO experimentDao;
	AssayDAO assayDao;
	
	// issue 61
	public Aliquot loadById(String id) 
		{
		Assert.notNull(id);
		return aliquotDao.loadById(id);
		}
	
	// issue 79
	public List<String> loadAllAliquotsNotChosen(String expId)
		{
		return aliquotDao.loadAllAliquotsNotChosen(expId);
		}
	
	// issue 61
	/* public void delete(String aliquotId) 
		{
		aliquotDao.delete(aliquotId);
		}*/
	
	public void deleteAndSetReason(String aliquotId, String deleteReason) 
		{
		aliquotDao.deleteAndSetReason(aliquotId, deleteReason);
		}
	
	// issue 100
	public void deleteAssayAliquot ( String aliquotId) 
		{
		aliquotDao.deleteAssayAliquot(aliquotId);
		}
	
	// issue 79
	public List<String> getMatchingAliquotIds(String input)
		{
		return aliquotDao.getMatchingAliquotIds(input);
		}
	
	// issue 79
	public void deleteExperimentAliquot(String expId, String aliquotId)
		{
		aliquotDao.deleteExperimentAliquot(expId, aliquotId);
		}
	
	public List<Aliquot> loadByCid(String cid)
		{
		Assert.notNull(cid);
		return aliquotDao.loadByCid(cid);
		}
	
	public List<String> loadByEid(String eid)
		{
		Assert.notNull(eid);
		return aliquotDao.loadByEid(eid);
		}
	
	public List<Aliquot> loadByCidDeleted(String cid)
		{
		Assert.notNull(cid);
		return aliquotDao.loadByCidDeleted(cid);
		}
		
	// issue 61 2020
	public List <Aliquot> save (AliquotDTO dto, String aliquotAssigned)
	    {
		List <Aliquot> aliquotList = new ArrayList <Aliquot> ();
		Aliquot aliquot = null;		
		Compound cmpd=compoundDao.loadCompoundById(dto.getCid());
		Location loc = locationDao.loadById(dto.getLocation());
		Inventory inv = inventoryDao.loadById(dto.getParentId());
		if (!StringUtils.isEmptyOrNull(aliquotAssigned))
			dto.setAliquotId(aliquotAssigned);
		if (dto.getAliquotId() != null && !"to be assigned".equals(dto.getAliquotId()))
			try 
				{
				aliquot = aliquotDao.loadById(dto.getAliquotId());
				aliquot.update(dto);
				aliquotList.add(aliquot);
				saveAssays (dto.getAssayIds(), aliquot); // issue 100
				}
		    catch (Exception e)
		        {
		    	e.printStackTrace();
		        }
		else
			try
		        {	
				String aliquotFirstString = "";				
				if (dto.getReplicate() == 1)
					{					
					aliquot = Aliquot.instance( dto.getReplicate(),  loc,  inv, dto.getIsDry() ? '1' : '0', cmpd, dto.getSolvent(),  DateUtils.calendarFromDateStr(dto.getCreateDate(), Aliquot.ALIQUOT_DATE_FORMAT), dto.getAliquotLabel(), dto.getNotes(), dto.getCreatedBy(), dto.getNeatOrDilution(), dto.getNeatOrDilutionUnits(), new BigDecimal(dto.getIvol()), new BigDecimal(dto.getDcon()), new BigDecimal(dto.getIcon()), new BigDecimal(dto.getWeightedAmount()), new BigDecimal(dto.getDConc()) , (dto.getDvol()== null ? new BigDecimal(0) : new BigDecimal(dto.getDvol())), dto.getDConcentrationUnits(), dto.getWeightedAmountUnits(), new BigDecimal(dto.getMolecularWeight()));	
					if (StringUtils.isEmptyOrNull(dto.getCreatedBy()))
						dto.setCreatedBy(((MedWorksSession) Session.get()).getCurrentUserId());
					aliquotDao.createAliquot(aliquot);	
					saveAssays(dto.getAssayIds(), aliquot); // issue 100
					aliquotList = new ArrayList <Aliquot> ();
					aliquotList.add(aliquot);
 					return aliquotList;
					}
				else
					{
					for (int i = 1; i<= dto.getReplicate(); i++)
					    {
						if (i > 1)
						    dto.setAliquotLabel(aliquotFirstString + "_" + i);
						aliquot = Aliquot.instance( dto.getReplicate(),  loc,  inv, dto.getIsDry() ? '1' : '0' , cmpd, dto.getSolvent(),  DateUtils.calendarFromDateStr(dto.getCreateDate(), Aliquot.ALIQUOT_DATE_FORMAT), dto.getAliquotLabel(), dto.getNotes(), dto.getCreatedBy(), dto.getNeatOrDilution(), dto.getNeatOrDilutionUnits(), new BigDecimal(dto.getIvol()), new BigDecimal(dto.getDcon()), new BigDecimal(dto.getIcon()), new BigDecimal(dto.getWeightedAmount()), new BigDecimal(dto.getDConc()), (dto.getDvol()== null ? new BigDecimal(0) : new BigDecimal(dto.getDvol())), dto.getDConcentrationUnits(), dto.getWeightedAmountUnits(),  new BigDecimal(dto.getMolecularWeight()));	
						aliquotDao.createAliquot(aliquot);	
						saveAssays(dto.getAssayIds(), aliquot); // issue 100
					    if (i == 1)
						    {
							aliquot = aliquotDao.loadById(aliquot.getAliquotId());
							aliquotFirstString = aliquot.getAliquotId();
							dto.setAliquotLabel(aliquotFirstString + "_" + i);
							dto.setAliquotId(aliquot.getAliquotId());
							aliquot.update(dto);
						    } 
					    aliquotList.add(aliquot); 
					    }
					}
		        }
			catch (Exception e)
		        {
		    	e.printStackTrace();
		        }
		return aliquotList;
	    }
	
	public List<String> getVolUnitsDDList(String unitsID) 
	    {
		return aliquotDao.getVolUnitsDDList(unitsID);
	    }
	
	public List<String> getAllVolUnits() 
	    {
		return aliquotDao.getAllVolUnits();
	    }
	
	//issue 61
	public CompoundDAO getCompoundDao()
		{
		return compoundDao;
		}

	public void setCompoundDao(CompoundDAO compoundDao) 
		{
		this.compoundDao = compoundDao;
		}
	
	// issue 61	
	public AliquotDAO getAliquotDao()
		{
		return aliquotDao;
		}
	
	public void setAliquotDao(AliquotDAO aliquotDao) 
		{
		this.aliquotDao = aliquotDao;
		}

	public SampleDAO getSampleDao()
	    {
		return sampleDao;
		}
	
	public void setSampleDao(SampleDAO sampleDao) 
	    {
		Assert.notNull(sampleDao);
		this.sampleDao = sampleDao;
	    }
	
	public UserDAO getUserDao()
	    {
		return userDao;
		}
	
	public void setUserDao(UserDAO userDao) 
	    {
		this.userDao = userDao;
	    }
	
	public void setLocationDao(LocationDAO locationDao) 
	    {
		this.locationDao = locationDao;
	    }
	
	public LocationDAO getLocationDao()
	    {
		return locationDao;
		}
	
	public void setInventoryDao(InventoryDAO inventoryDao) 
	    {
		this.inventoryDao = inventoryDao;
	    }

	public InventoryDAO getInventoryDao()
	    {
		return inventoryDao;
		}

	public void setExperimentDao(ExperimentDAO experimentDao) 
	    {
		this.experimentDao = experimentDao;
	    }

	public ExperimentDAO getExperimentDao()
	    {
		return experimentDao;
		}
	
	public void setAssayDao(AssayDAO assayDao) 
	    {
		this.assayDao = assayDao;
	    }

	public AssayDAO getAssayDao()
	    {
		return assayDao;
		}
	public List<String> retrieveAssayNames(String aliquotId)
		{
		return aliquotDao.retrieveAssayNames(aliquotId);
		}
	
	// issue 79
	public void saveExperimentAliquot(String aliquotId, Experiment exp)
		{			
		Aliquot aliquot = aliquotDao.loadById(aliquotId);
		aliquotDao.createExperimentAliquot(ExperimentAliquot.instance(exp, aliquot));
		}
	
	// issue 100
	public void saveAssayAliquot(Aliquot aliquot, Assay assay)
		{			
		aliquotDao.createAssayAliquot(AssayAliquot.instance(assay, aliquot));
		}
	
	//issue 86
	// include inventory id and date
	public List<String> aliquotIdsForExpId(String eid)
		{
		List<String> aliquotIdList =  aliquotDao.aliquotIdsForExpId(eid);
		return  getInventoryDateList(aliquotIdList);
		}
	
	// issue 86
	public List<String> getInventoryDateList(List<String> aliquotIdList)
		{
		List<String> aliquotIdListInvDate = new ArrayList <String> ();
		for (String aliquotId: aliquotIdList )
			{
			Aliquot alq = loadById(aliquotId);
			String invDateStr = (StringUtils.isEmptyOrNull(alq.getAliquotLabel()) ? alq.getAliquotId() : alq.getAliquotLabel()) + "-" + alq.getInventory().getInventoryId() + "-" + alq.getCreateDateString();
			aliquotIdListInvDate.add(invDateStr);
			}
		return aliquotIdListInvDate;
		}
	// issue 100
	private void saveAssays (List <String> assayIds, Aliquot aliquot )
		{
		for (String assayId : assayIds) 
			{
	        Assay assay = assayDao.loadAssayByID(assayId.substring(assayId.lastIndexOf("(") + 1,assayId.lastIndexOf(")") ));
	        aliquotDao.deleteAssayAliquot(aliquot.getAliquotId());
	        aliquotDao.createAssayAliquot(AssayAliquot.instance(assay, aliquot));
			}
		}
	
	// issue 100
	public List<Aliquot> getAliquotsFromAssay (String assayId)
		{
		List<Aliquot> aliquotsForAssay = aliquotDao.getAliquotsFromAssay(assayId);
		return aliquotsForAssay;
		}
	
    }
