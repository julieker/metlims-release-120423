package edu.umich.brcf.metabolomics.layers.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.metabolomics.layers.dao.CompoundDAO;
import edu.umich.brcf.metabolomics.layers.dao.CompoundNameDAO;
import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.domain.CompoundName;
import edu.umich.brcf.shared.layers.domain.Inventory;
import edu.umich.brcf.shared.layers.dto.CompoundDTO;
import edu.umich.brcf.shared.util.comparator.InventoryListCompoundComparator;
import edu.umich.brcf.shared.util.io.StringUtils;


@Service()
@Transactional
public class CompoundService 
	{
	CompoundDAO compoundDao;
	CompoundNameDAO compoundNameDao;

	public List<String> getMatchingCids(String input)
		{
		return compoundDao.getMatchingCids(input);
		}
	
	public List<Compound> getChildren(Compound compound)
		{
		List<Compound> children=compoundDao.getChildren(compound);
		return children;
		}

	public List<CompoundName> searchName(String name) 
		{
		return compoundDao.searchName(name);
		}

	public Compound loadCompoundById(String cid) 
		{
		return compoundDao.loadCompoundById(cid);
		}
	
	public Compound loadCompoundByCan(String can) 
		{
		return compoundDao.loadCompoundByCan(can);
		}

	public Compound save(CompoundDTO dto) 
		{
		Assert.notNull(dto);		
		Compound compound = null;
		CompoundName cname;
		String msg = "";
		if (dto.getCid() != null && !"to be assigned".equals(dto.getCid()))
			try 
				{
				compound = compoundDao.loadCompoundById(dto.getCid());
				if (!StringUtils.isEmptyOrNull(dto.getParentCid()))
			        compound.update(dto, getParent(compound, dto));
				//issue 15
				else 
					compound.update(dto,null);
				// issue 8 
				if (!StringUtils.isEmptyOrNull(compound.getSmiles()))
				    compound.updateSolvent(compoundDao.getSolventForLogPValue(compound.getLogP()));
				else
					{
					compound.updateSolvent(null);
					compound.setMolecular_formula(null);
					compound.setMolecular_weight(null);
					}
				} 
			catch (Exception e) {e.printStackTrace(); }
		else
			try
				{
				compound = Compound.instance(dto.getCid(),dto.getChem_abs_number(), dto.getSmiles(), dto.getHuman_rel().charAt(0), null);
				//msg = "Error while updating parent compound";
				if (!StringUtils.isEmptyOrNull(dto.getParentCid()))
					compound.updateParent(getParent(compound, dto));
				//msg = "Error while updating solvent";
				// issue 8
				if (!StringUtils.isEmptyOrNull(compound.getSmiles()))
					compound.updateSolvent(compoundDao.getSolventForLogPValue(compound.getLogP()));				    
				//msg = "Error while creating compound";
				compoundDao.createCompound(compound);
				}		
			catch (Exception e) { e.printStackTrace(); compound = null; throw new RuntimeException(msg); }		
		if (compound != null)
			if (!StringUtils.isEmptyOrNull(dto.getName()))
				{
				cname = CompoundName.instance(dto.getHtml(), compound, dto.getName(), dto.getType());
				compoundNameDao.save(cname);
				compound.addName(cname);
				}
		return compound;
		}
	
	
	/* Original logic
	 * public Compound save(CompoundDTO dto) {
		Compound compound;
//		Compound parent;
		CompoundName cname;
		try {
			compound = compoundDao.loadCompoundById(dto.getCid());
			compound.update(dto, getParent(compound, dto));
			compound.updateSolvent(compoundDao.getSolventForLogPValue(compound.getLogP()));
		} catch (Exception e) {
			compound = Compound.instance(dto.getCid(),dto.getChem_abs_number(), dto.getSmiles(), dto.getHuman_rel().charAt(0), null);
			compound.updateParent(getParent(compound, dto));
			compound.updateSolvent(compoundDao.getSolventForLogPValue(compound.getLogP()));
			compoundDao.createCompound(compound);
		}
		
		if ((dto.getName()!=null)&&(dto.getName().trim().length()>0))
		{
			cname = CompoundName.instance(dto.getHtml(), compound, dto.getName(), dto.getType());
			cnameDao.save(cname);
			compound.addName(cname);
		}
		return compound;
	}
	
	public Compound getParent(Compound compound, CompoundDTO dto) {
		Compound parent;
		try {
			parent = compoundDao.loadCompoundById(dto.getParentCid());
			}catch(Exception ex){
				parent = compound;
			}
			return parent;
	}

	 * 
	 * 
	 * 
	 * 
	 * */
	
	public Compound getParent(Compound compound, CompoundDTO dto) 
		{
		Compound parent;
		try { parent = compoundDao.loadCompoundById(dto.getParentCid()); }
		catch(Exception ex){ parent = compound; }
		
		return parent;
		}

	
	public void clearCompound(String id) 
		{
		Compound compound = compoundDao.loadCompoundById(id);
		compound.clear();
		}

	/**
	 * get inventory list for multiplexing and return in sorted order of
	 * location and compound's primary name.
	 * 
	 * @return
	 */
	public List<Inventory> getCompoundsForMultiplexing() 
		{
		List<Inventory> invList = compoundDao.grabCompoundsForMultiplexing();
		Collections.sort(invList, new InventoryListCompoundComparator());
		return invList;
		}

	
	public List<Compound> getCompoundsForMassAndPPM(BigDecimal mass, BigDecimal ppm) 
		{
		double upperLimit=(mass.doubleValue())+((mass.doubleValue()*ppm.doubleValue())/1000000);
		double lowerLimit=(mass.doubleValue())-((mass.doubleValue()*ppm.doubleValue())/1000000);
		return compoundDao.getCompoundsWithinMass(upperLimit,lowerLimit);
		}
	
	public List<Compound> getCompoundsWithinMass(double lowerLimit, double upperLimit) 
		{
		//	double upperLimit=(mass.doubleValue())+((mass.doubleValue()*ppm.doubleValue())/1000000);
		//	double lowerLimit=(mass.doubleValue())-((mass.doubleValue()*ppm.doubleValue())/1000000);
		return compoundDao.getCompoundsWithinMass(upperLimit,lowerLimit);
		}


	public List<Compound> getCompoundsAllInfoWithinMass(double lowerLimit, double upperLimit) 
		{
		//	double upperLimit=(mass.doubleValue())+((mass.doubleValue()*ppm.doubleValue())/1000000);
		//	double lowerLimit=(mass.doubleValue())-((mass.doubleValue()*ppm.doubleValue())/1000000);
		return compoundDao.getCompoundsAllInfoWithinMass(lowerLimit,upperLimit);
		}


	public void setCompoundDao(CompoundDAO compoundDao) 
		{
		this.compoundDao = compoundDao;
		}


	public CompoundNameDAO getCompoundNameDao()
		{
		return compoundNameDao;
		}

	public void setCompoundNameDao(CompoundNameDAO compoundNameDao)
		{
		this.compoundNameDao = compoundNameDao;
		}

	public CompoundDAO getCompoundDao()
		{
		return compoundDao;
		}

	public Compound loadByCatnum(String catnum) 
		{
		return compoundDao.loadByCatnum(catnum);
		}
	}


/*
public Compound save(CompoundDTO dto) 
	{
	Compound compound = null;
	CompoundName cname;

	try {
		compound = compoundDao.loadCompoundById(dto.getCid());
		compound.update(dto, getParent(compound, dto));
		compound.updateSolvent(compoundDao.getSolventForLogPValue(compound.getLogP()));
	} catch (Exception e) {
		compound = Compound.instance(dto.getCid(),dto.getChem_abs_number(), dto.getSmiles(), dto.getHuman_rel().charAt(0), null);
		compound.updateParent(getParent(compound, dto));
		compound.updateSolvent(compoundDao.getSolventForLogPValue(compound.getLogP()));
		compoundDao.createCompound(compound);
	}
	
	if ((dto.getName()!=null)&&(dto.getName().trim().length()>0))
	{
		cname = CompoundName.instance(dto.getHtml(), compound, dto.getName(), dto.getType());
		compoundNameDao.save(cname);
		compound.addName(cname);
	}
	return compound;
} */
