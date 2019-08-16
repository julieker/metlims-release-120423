package edu.umich.brcf.metabolomics.panels.lipidshome.browse;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//MS2DataSetHandler.java
//Written by Jan Wigginton 02/08/15
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.Ms2DataSet;
import edu.umich.brcf.metabolomics.layers.domain.Ms2Peak;
import edu.umich.brcf.metabolomics.layers.domain.Ms2PeakSet;
import edu.umich.brcf.metabolomics.layers.domain.Ms2SampleMap;
import edu.umich.brcf.metabolomics.layers.dto.Ms2DataSetDTO;
import edu.umich.brcf.metabolomics.layers.dto.Ms2PeakDTO;
import edu.umich.brcf.metabolomics.layers.dto.Ms2PeakSetDTO;
import edu.umich.brcf.metabolomics.layers.dto.Ms2SampleMapDTO;
import edu.umich.brcf.metabolomics.layers.service.Ms2DataSetService;
import edu.umich.brcf.metabolomics.layers.service.Ms2PeakService;
import edu.umich.brcf.metabolomics.layers.service.Ms2PeakSetService;
import edu.umich.brcf.metabolomics.layers.service.Ms2SampleMapService;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;



public class Ms2DataSetHandler implements Serializable
	{
	private static final long serialVersionUID = 1202540547523597545L;

	@SpringBean
	Ms2PeakSetService ms2PeakSetService;

	@SpringBean
	Ms2PeakService ms2PeakService;

	@SpringBean
	Ms2DataSetService ms2DataSetService;

	@SpringBean
	Ms2SampleMapService ms2SampleMapService;

	private Ms2DataSet dataSet;

	List<String> compoundLabels = new ArrayList<String>();
	List<Ms2SampleMapDTO> sampleMappings = new ArrayList<Ms2SampleMapDTO>();
	public Map<String, Ms2SampleMap> sampleHashMap = new HashMap<String, Ms2SampleMap>();

	// retain in case we want to go back to meaningful organized labels...
	public List<String> tableColumnLabels = new ArrayList<String>();

	static final int maxPeakSetsToPull = 1000000000;
	public Boolean hasStartMass = false, hasEndMass = false, hasRt = false;

	
	
	public Ms2DataSetHandler(String eid, String inputFileName, Calendar cal,
		String ionMode, String dataNotation, ArrayList<Integer> colIndices, String user)
		{
		Injector.get().inject(this);

		dataSet = new Ms2DataSet("", eid, cal, DateUtils.todaysDateAsCalendar(), 1, user, ionMode, dataNotation);

		Ms2DataSetReader reader = new Ms2DataSetReader(inputFileName,colIndices);
		Ms2RawData rawData = reader.parseRawData();

		initializeFromData(rawData, eid, cal, user, ionMode, dataNotation);
		}

	
	public Ms2DataSetHandler(String dataSetId)
		{
		Injector.get().inject(this);

		dataSet = ms2DataSetService.loadById(dataSetId);
		dataSet.setPeakSets(ms2PeakSetService
				.loadInitializedForDataSetId(dataSetId));

		sampleHashMap = ms2SampleMapService.loadMapForDataSetId(dataSetId);

		List<Ms2PeakSet> smallSet = new ArrayList<Ms2PeakSet>();
		for (int i = 0; i < Math.min(maxPeakSetsToPull, dataSet.getPeakSets().size()); i++)
			smallSet.add(dataSet.getPeakSets().get(i));

		dataSet.setPeakSets(smallSet);

		compoundLabels.clear();

		for (int i = 0; i < Math.min(maxPeakSetsToPull, dataSet.getPeakSets().size()); i++)
			{
			Ms2PeakSet pkSet = dataSet.getPeakSets().get(i);
			pkSet.setSamplePeaks(ms2PeakService.loadForPeakSetId(pkSet.getPeakSetId()));

			compoundLabels.add(pkSet.getLipidName());
			if (i == 0)
				for (int j = 0; j < pkSet.getSamplePeaks().size(); j++)
					{
					Ms2Peak pk = pkSet.getSamplePeaks().get(j);
					String sampleMapId = pk.getSampleMapId();
					Ms2SampleMap sampleInfo = this.sampleHashMap.get(sampleMapId);
					tableColumnLabels.add(sampleInfo.getSampleTag() == null ? "" : sampleInfo.getSampleTag());
					}
			}

		checkForDoubleEntries();
		}

	
	private void checkForDoubleEntries()
		{
		this.hasEndMass = true;
		this.hasRt = true;
		this.hasStartMass = true;
		}
	

	public void persistToDatabase()
		{
		dataSet.setDataSetId("");
		Ms2DataSetDTO fDto = Ms2DataSetDTO.instance(dataSet);

		List<Ms2PeakSetDTO> psDtos = new ArrayList<Ms2PeakSetDTO>();
		List<List<Ms2PeakDTO>> pDtos = new ArrayList<List<Ms2PeakDTO>>();

		for (int j = 0; j < dataSet.getPeakSets().size(); j++)
			{
			Ms2PeakSet currSet = dataSet.getPeakSets().get(j);
			psDtos.add(Ms2PeakSetDTO.instance(currSet));

			List<Ms2PeakDTO> sDtos = new ArrayList<Ms2PeakDTO>();
			for (int i = 0; i < currSet.getSamplePeaks().size(); i++)
				sDtos.add(Ms2PeakDTO.instance(currSet.getSamplePeaks().get(i)));

			pDtos.add(sDtos);
			}

		ms2DataSetService.saveDataSet(fDto, psDtos, pDtos, sampleMappings);
		}

	
	private void initializeFromData(Ms2RawData rawData, String eid, Calendar cal, String user, String ionMode, String dataNotation)
		{
		if (rawData == null) 
			{ System.out.println("Initializing peak sets with null rawData -- returning");
			return;
			}

		dataSet = new Ms2DataSet("MD99999", eid, cal, DateUtils.todaysDateAsCalendar(), 1, user, ionMode, dataNotation);
		List<Ms2PeakSet> peakSets = initializePeakSetsFromData(rawData, dataSet);
		dataSet.setPeakSets(peakSets);

		compoundLabels = new ArrayList<String>();
		for (int i = 0; i < rawData.compoundLabels.size(); i++)
			compoundLabels.add(rawData.compoundLabels.get(i));

		sampleMappings = createSampleMap(rawData.sampleLabels,
				dataSet.getDataSetId());

		for (int i = 0; i < rawData.compoundLabels.size(); i++)
			{
			ArrayList<Double> peaksAreasForCompound = rawData.peakAreas.get(i);
			Ms2PeakSet peakSet = dataSet.getPeakSets().get(i);
			for (int j = 0; j < peaksAreasForCompound.size(); j++)
				{
				Double peakArea = peaksAreasForCompound.get(j);

				String sampleMapId = sampleMappings.get(j).getSampleMapId();
				Ms2Peak peak = new Ms2Peak(null, peakSet, sampleMapId, peakArea);
				peakSet.getSamplePeaks().add(peak);
				}
			}

		initializeTableColumnLabels(rawData.sampleLabels);
		}

	
	private List<Ms2PeakSet> initializePeakSetsFromData(Ms2RawData rawData, Ms2DataSet dataSet)
		{
		List<Ms2PeakSet> sets = new ArrayList<Ms2PeakSet>();

		for (int i = 0; i < rawData.compoundLabels.size(); i++)
			{
			if (!this.hasEndMass)
				hasEndMass |= (rawData.endMasses.get(i) != null);

			if (!this.hasStartMass)
				hasStartMass |= (rawData.startMasses.get(i) != null);

			if (!this.hasRt)
				hasRt |= (rawData.retentionTimes.get(i) != null);

			String lipidLabel = rawData.compoundLabels.get(i);
			Double endMass = rawData.endMasses.get(i);
			Double startMass = rawData.startMasses.get(i);
			Double retentionTime = rawData.retentionTimes.get(i);
			String lipidClass = rawData.lipidClasses.get(i);
			String knownStatus = rawData.knownStatuses.get(i).toUpperCase()
					.substring(0, 1);

			Ms2PeakSet set = Ms2PeakSet.instance(lipidLabel, retentionTime,
					startMass, endMass, lipidClass, knownStatus, dataSet);
			sets.add(set);
			}

		return sets;
		}

	
	List<Ms2SampleMapDTO> createSampleMap(List<String> rawSampleLabels, String dataSetId)
		{
		List<Ms2SampleMapDTO> maps = new ArrayList<Ms2SampleMapDTO>();

		for (int i = 0; i < rawSampleLabels.size(); i++)
			{
			String sampleTag = rawSampleLabels.get(i);
			String sampleId = LipidStringParser.getSampleIdFromWorklistLabel(sampleTag);
			Ms2SampleMapDTO map = Ms2SampleMapDTO.instance(sampleId, sampleTag, dataSetId, 1, "", "Comment for item" + i);
			maps.add(map);
			}
		
		return maps;
		}

	
	public void initializeTableColumnLabels(ArrayList<String> rawLabels)
		{
		tableColumnLabels.clear();
		for (int i = 0; i < rawLabels.size(); i++)
			this.tableColumnLabels.add(rawLabels.get(i));
		}

	
	public List<String> getCompoundLabels()
		{
		return compoundLabels;
		}

	public String getCompoundName(int i)
		{
		ArrayList<String> labels = (ArrayList<String>) getCompoundLabels();

		return (i > 0 && i < labels.size() ? labels.get(i) : "");
		}

	public List<Ms2PeakSet> getResults()
		{
		return dataSet.getPeakSets();
		}

	public Ms2PeakSet getPeakSets(int i)
		{
		return dataSet.getPeakSets().get(i);
		}

	public Ms2DataSet getDataSet()
		{
		return dataSet;
		}

	public void setDataSet(Ms2DataSet dataSet)
		{
		this.dataSet = dataSet;
		}
	}
