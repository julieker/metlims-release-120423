package edu.umich.brcf.metabolomics.layers.service;


import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.metabolomics.layers.dao.GenusSpeciesDAO;
import edu.umich.brcf.metabolomics.layers.domain.GenusSpecies;
import edu.umich.brcf.shared.util.io.StringUtils;



@Transactional
public class GenusSpeciesService 
	{
	GenusSpeciesDAO genusSpeciesDao;

	public GenusSpecies loadById(Long ID)
		{
		return genusSpeciesDao.loadById(ID);
		}
		
	public List<GenusSpecies> allGenusSpecies()
		{
		return genusSpeciesDao.allGenusSpecies();
		}
	
	public List<GenusSpecies> limitedGenusSpecies(String str)
		{
		return genusSpeciesDao.limitedGenusSpecies(str);
		}
	
	public List<String> getSubjectSpeciesForTaxId(String taxId)
		{
		return genusSpeciesDao.getSubjectSpeciesForTaxId(taxId);
		}
	
	public GenusSpecies loadByName(String genusName)
		{
		Assert.notNull(genusName);
		return genusSpeciesDao.loadByName(genusName);
		}
	
	//Animal
	
	public void setGenusSpeciesDao(GenusSpeciesDAO genusSpeciesDao) 
		{
		Assert.notNull(genusSpeciesDao);
		this.genusSpeciesDao = genusSpeciesDao;
		}
	
	public String lookupCommonGenusSpeciesId(String selection)
		{
		if (StringUtils.isEmpty(selection))
			return "";
	
		String trimCompare = StringUtils.trimPhrase(selection, "(please specify if known)");
		String toCompare = StringUtils.cleanAndTrim(trimCompare).toLowerCase();
		
		switch (toCompare)
			{
			case "homosapien" : 
			case "homosapiens" :	
			case "human" : return "18971";
			case "mus musculus" : return "19897"; 
			case "mice" : 	
			case "mouse" : return "19901";
			case "rat" :
			case "rats" : return "19953";
			case "rattus norvegicus" : 
			case "ratrattusnorvegicusother" : return "19962";
			case "beavercastorcanadensisother" : return "20109";
			case "squirrelsciuridaeany" :   return "19728";
			case "rabbitleporidaeany" :  return "19682";  
			case "otherrodent" :  return "19704";
			case "horseequusferusother" : return "19339";
			case "canislupus" : return "18980";
			case "dogcanuslupusother" : return "18979";
			case "cowbosany" : return "19564"; 
			case "pigsusany" :  return "19418";
			case "sheepovisariesother" : return "19619"; 
			case "llamaany" : return "19466";
			case "chickengallusdomesticusother" : return "17910"; 
			case "fruitflydrosophilamelanogasterother" : return "15205"; 
			case "otheranimal" :   return "33626";
			case "algae" :   return "8971";	
			case "yeastsaccharomycescerevisiaeother" : return "12307";
			case "syntrophusaciditrophicus" : return "47757";
			case "escherichiacoli" : return "2184";
			case "listeria" : return "5907";
			case "treponema" : return "437";
			case "otherbacteria" :return "3";
			case "nogenus" : return "33204";
			case "unknown" :  return "33205";
			default : return "";
			}
		}
	}
