package edu.umich.brcf.metabolomics.layers.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.metabolomics.layers.dao.CompoundDAO;
import edu.umich.brcf.metabolomics.layers.dao.CompoundNameDAO;
import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.domain.CompoundName;
import edu.umich.brcf.shared.layers.domain.Aliquot;
import edu.umich.brcf.shared.layers.dto.CompoundNameDTO;


@Transactional
public class CompoundNameService 
	{
	CompoundNameDAO compoundNameDao;
	CompoundDAO compoundDao;

	public List<CompoundName> allCompoundNames()
		{
		return compoundNameDao.allCompoundNames();
		}

	public CompoundName loadByName(String name)
		{
		Assert.notNull(name);
		return compoundNameDao.loadByName(name);
		}
	
	
	public CompoundName loadByNameCompoundId(String name)
		{
		Assert.notNull(name);
		return compoundNameDao.loadByNameCompoundId(name);
		}
	
	public List<CompoundName> loadByCid(String cid)
		{
		Assert.notNull(cid);
		return compoundNameDao.loadByCid(cid);
		}
	
	public List<CompoundName> getMatchingNames(String str)
		{
		return compoundNameDao.getMatchingNames(str);
		}
	
	// issue 48
	public List<String> getMatchingNamesCompoundId(String str)
		{
		return compoundNameDao.getMatchingNamesCompoundId(str);
		}	
	
	public CompoundName save(CompoundNameDTO cnDto, boolean newFlag)
		{
		Assert.notNull(cnDto);
		Compound cmpd=compoundDao.loadCompoundById(cnDto.getCid());
		CompoundName cname = null;
		// issue 446
		if (!newFlag)
			try
				{
				cname = compoundNameDao.loadName(cnDto.getCid(), cnDto.getNewName());
				//issue 16
				compoundNameDao.updateName(cnDto.getCid(), cnDto.getNewName(), cnDto.getName(), cnDto.getType(), cnDto.getHtml());
				}
			catch(Exception e) { e.printStackTrace(); cname = null; }
		else
			try
				{
				cname = CompoundName.instance(cnDto.getHtml(), cmpd, cnDto.getNewName(), cnDto.getType() );
				compoundNameDao.save(cname);
				}
			catch(Exception e) { e.printStackTrace(); cname = null; }
			
		return cname;
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

	
	public void setCompoundDao(CompoundDAO compoundDao) 
		{
		this.compoundDao = compoundDao;
		}
	
	
	// 123
	public String getCompoundName(String cid)
		{
		List<CompoundName> cnList = loadByCid(cid);
		for (CompoundName cname : cnList)
			{
			if (cname.getNameType().equals("pri"))
			    return cname.getName().substring(0, (cname.getName().length() >=50 ? 50 : cname.getName().length() ) );
			}
		return "";
		}
	}
