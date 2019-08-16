// Written by ??
package edu.umich.brcf.shared.layers.service;


import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.shared.layers.dao.AliquotDAO;
import edu.umich.brcf.shared.layers.dao.InventoryDAO;
import edu.umich.brcf.shared.layers.dao.SampleDAO;
import edu.umich.brcf.shared.layers.dao.UserDAO;


@Transactional
public class AliquotService {
	
	AliquotDAO aliquotDao;
	SampleDAO sampleDao;
	InventoryDAO inventoryDao;
	UserDAO userDao;
	
//	public Aliquot loadById(String id) {
//		Assert.notNull(id);
//		return aliquotDao.loadById(id);
//	}

//	public List<ValueLabelBean> saveAliquot(AliquotDTO dto, BigDecimal sampleCurVol) {
//		Assert.notNull(dto);
//		Sample sample = sampleDao.loadById(dto.getSampleid());
//		Location location;
//		List<ValueLabelBean> barcodesList = new ArrayList<ValueLabelBean>();
//		try{
//		location = inventoryDao.getLocationById(dto.getLocid());
//		} catch(Exception e){
//			location = inventoryDao.getLocationById("LC0000");
//		}
//		User creator = userDao.loadById(((METWorksSession) Session.get()).getCurrentUserId());
//		int numberOfAliquots=Integer.parseInt(dto.getNumberOfAliquots());
//		int i;
//		try{
//			i=aliquotDao.getMaxSequence(dto.getSampleid());
//			}catch(Exception e){
//				i=0;
//			}
////			int k;
//		for(int k=0;k<numberOfAliquots;k++)
//		{
//			++i;
//			Aliquot aliquot = Aliquot.instance( i,sample, creator, Calendar.getInstance(), new BigDecimal(dto.getVolume()), 
//					aliquotDao.loadVolUnitsById(dto.getVolUnits().trim()), "", location);//dto.getSampleid().replace('S', 'A'), i,
//			aliquot=aliquotDao.createAliquot(aliquot);
//			barcodesList.add(new ValueLabelBean(aliquot.getAliquotID(), sample.getId()));
//		}
//		sample.updateCurrentVolume(sampleCurVol);
//		return barcodesList;
//	}
	
	public List<String> getVolUnitsDDList(String unitsID) {
		return aliquotDao.getVolUnitsDDList(unitsID);
	}
	
	public List<String> getAllVolUnits() {
		return aliquotDao.getAllVolUnits();
	}
	

	
	public boolean wasInjected(Long wellId) {
		List<String> fileList = aliquotDao.getWellDataFile(wellId);
		return fileList.size()>0? true:false;
	}
	
	public void setAliquotDao(AliquotDAO aliquotDao) {
		this.aliquotDao = aliquotDao;
	}

	public void setSampleDao(SampleDAO sampleDao) {
		Assert.notNull(sampleDao);
		this.sampleDao = sampleDao;
	}

	public void setInventoryDao(InventoryDAO inventoryDao) {
		this.inventoryDao = inventoryDao;
	}

	public void setUserDao(UserDAO userDao) {
		this.userDao = userDao;
	}
}
