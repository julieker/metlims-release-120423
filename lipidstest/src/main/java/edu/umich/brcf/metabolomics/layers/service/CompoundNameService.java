package edu.umich.brcf.metabolomics.layers.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.metabolomics.layers.dao.CompoundDAO;
import edu.umich.brcf.metabolomics.layers.dao.CompoundNameDAO;
import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.domain.CompoundName;
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
	
	public List<CompoundName> loadByCid(String cid)
		{
		Assert.notNull(cid);
		return compoundNameDao.loadByCid(cid);
		}
	
	public List<CompoundName> getMatchingNames(String str)
		{
		return compoundNameDao.getMatchingNames(str);
		}
	
	
	public CompoundName save(CompoundNameDTO cnDto)
		{
		Assert.notNull(cnDto);
		Compound cmpd=compoundDao.loadCompoundById(cnDto.getCid());
		CompoundName cname = null;
		
		if (cnDto.getCid() != null)
			try
				{
				cname = compoundNameDao.loadName(cnDto.getCid(), cnDto.getNewName());
				cname.update(cnDto, cmpd);
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
	}
