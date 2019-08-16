/////////////////////////////////////////
// Ms2SampleMapService.java
// Written by Jan Wigginton June 2015
/////////////////////////////////////////

package edu.umich.brcf.metabolomics.layers.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.metabolomics.layers.dao.Ms2DataSetDAO;
import edu.umich.brcf.metabolomics.layers.dao.Ms2SampleMapDAO;
import edu.umich.brcf.metabolomics.layers.domain.Ms2DataSet;
import edu.umich.brcf.metabolomics.layers.domain.Ms2SampleMap;
import edu.umich.brcf.metabolomics.layers.dto.Ms2SampleMapDTO;
import edu.umich.brcf.metabolomics.panels.lipidshome.browse.SampleInfoUploader;
import edu.umich.brcf.shared.layers.dao.IdGeneratorDAO;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.METWorksException;


@Transactional
public class Ms2SampleMapService
	{
	Ms2SampleMapDAO ms2SampleMapDao;
	Ms2DataSetDAO ms2DataSetDao;
	IdGeneratorDAO idGeneratorDao;
	
	public Ms2SampleMap loadById(String id)
		{
		Assert.notNull(id);
		return ms2SampleMapDao.loadById(id);
		}

	
	public Ms2SampleMap save(Ms2SampleMapDTO dto)
		{
		Assert.notNull(dto);

		Ms2SampleMap sampleMap = null;
		if (dto.getSampleMapId() != null)
			try
				{
				sampleMap = ms2SampleMapDao.loadById(dto.getSampleMapId());
				sampleMap.update(dto);
				}
			catch(Exception e) { e.printStackTrace(); sampleMap = null; }
		
		else
			try
				{
				sampleMap = Ms2SampleMap.instance(dto.getSampleMapId(), dto.getSampleId(), dto.getSampleTag(), 
					dto.getDataSetId(), dto.getRunOrderIdx(), dto.getOtherId(), dto.getComment());
			
				ms2SampleMapDao.createMs2SampleMap(sampleMap);
				}
			catch(Exception e) { e.printStackTrace(); sampleMap = null; }
			
		
		return sampleMap;
		}
	

	
	public void updateRandomizationFromSampleInfoUpload(SampleInfoUploader upload) throws METWorksException
		{
		List<Ms2SampleMapDTO> dtos = new ArrayList<Ms2SampleMapDTO>();
		
		try
			{
			Map<String, Integer> map = upload.getSampleRandomization();
			String dsid = upload.getAssociatedId();
			String eid = upload.getExpId();
				
			Assert.isTrue(FormatVerifier.verifyFormat(Ms2DataSet.idFormat, dsid));
			Assert.isTrue(FormatVerifier.verifyFormat(Experiment.fullIdFormat, eid));
			
			
			for (String key :  map.keySet())
				{
				String sampleId = key;
				Integer randomIdx = map.get(key);
				Ms2SampleMapDTO dto = Ms2SampleMapDTO.instance(sampleId, "", dsid, randomIdx, "", "");
				dtos.add(dto);
				}
			}
		catch (Exception e) { throw new METWorksException("Database error while updating randomization"); }
		
		// if record exists, updates the info random
		for (int i  = 0; i < dtos.size(); i++)
			updateMissingInfo(dtos.get(i));
		}

	
	public void updateMissingInfo(Ms2SampleMapDTO dto)
		{
		Assert.notNull(dto);

		Ms2SampleMap sampleMap;
		try
			{
			sampleMap = ms2SampleMapDao.loadForDataSetIdAndSampleId(dto.getDataSetId(), dto.getSampleId());
			sampleMap.updateMissing(dto);
			}
		catch (Exception e) { }
		}
	
	
	public Ms2DataSetDAO getMs2DataSetDao()
		{
		return ms2DataSetDao;
		}

	
	public List<String> loadSampleTagsForDataSetId(String dataSetId)
		{
		return ms2SampleMapDao.loadSampleTagsForDataSetId(dataSetId);
		}
	
	
	public String getNextSampleMapId()
		{
		return ((String) idGeneratorDao.getNextValue("Ms2SampleMap"));
		}

	public void setMs2DataSetDao(Ms2DataSetDAO ms2DataSetDao)
		{
		this.ms2DataSetDao = ms2DataSetDao;
		}


	public void update(String id, Ms2SampleMapDTO dto)
		{
		Assert.notNull(id);
		Assert.notNull(dto);
		
		Ms2SampleMap map = ms2SampleMapDao.loadById(id);
		Ms2DataSet dSet = ms2DataSetDao.loadById(dto.getDataSetId());
		
		map.update(dto);
		}
	
	public Map <String, Ms2SampleMap> loadMapForDataSetId(String dataSetId)
		{
		List <Ms2SampleMap> lst  = ms2SampleMapDao.loadForDataSetId(dataSetId);
		Map<String,Ms2SampleMap> map = new HashMap<String, Ms2SampleMap>();
		for (int i = 0; i < lst.size(); i++)
			map.put(lst.get(i).getSampleMapId(), lst.get(i));
		
		return map;
		}
	
	public Ms2SampleMapDAO getMs2SampleMapDao()
		{
		return ms2SampleMapDao;
		}

	public void setMs2SampleMapDao(Ms2SampleMapDAO ms2SampleMapDao)
		{
		this.ms2SampleMapDao = ms2SampleMapDao;
		}

	public IdGeneratorDAO getIdGeneratorDao() 
		{
		return idGeneratorDao;
		}

	public void setIdGeneratorDao(IdGeneratorDAO idGeneratorDao) 
		{
		this.idGeneratorDao = idGeneratorDao;
		}
	}
