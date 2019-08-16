///////////////////////////////////////////
// Writtten by Anu Janga
///////////////////////////////////////////

package edu.umich.brcf.shared.layers.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import edu.umich.brcf.shared.layers.domain.BarcodePrinters;
import edu.umich.brcf.shared.layers.domain.SystemConfiguration;
import edu.umich.brcf.shared.util.utilpackages.BarcodePrinterUtil;



@Repository
public class SystemConfigDAO extends BaseDAO 
	{
	private String defaultAliquotVolume;
	private String defaultAliquotVolumeUnits;
	private String defaultAliquotLocation;
	private String fileUploadFolder;
	private String processedFilesFolder;
	private List<BarcodePrinters> barcodePrinterList;
	private BarcodePrinterUtil plateBarcodePrinter;
	private String serverQueue;
	private String clientMessageQueue;
	private String jmsBrokerUrl;
	private String productionServerName;
	private String productionDatabaseInstance;

	private String standardMatrixSampleId;
	private Integer numberStandardMatrixSamplePerRun;
	private Integer numberBlankSamplePerRun;
	private String grobMixId;

	private String blankSolventSampleId;
	private String blankBlankSampleId;
	private String blankProcessSampleId;

	private String instrumentIdGC1;
	private String instrumentIdLC1;
	private String instrumentIdLC2;
	private String dataProcessingInstrumentId;

	private String grobPrepID;
	private String warmUpPrepID;
	private String defaultSampleRunMode_LC;
	private String defaultSampleRunMode_GC;

	// Use this for testing purposes only!!!!!!!!!!!!!!
	private Map<String, Object> systemConfigMap;

	public Map<String, Object> getSystemConfigurationMap() 
		{
		if (systemConfigMap != null) return systemConfigMap;

		Map<String, Object> map = new HashMap<String, Object>();
		
		List<SystemConfiguration> list = getEntityManager().createQuery("from SystemConfiguration").getResultList();
		
		for (SystemConfiguration config : list) 
			{
			if (config.isUnique())
				map.put(config.getParameter(), config.getValue());
			else {
				if (!map.containsKey(config.getParameter()))
					map.put(config.getParameter(), new ArrayList<Object>());

			((List<Object>) map.get(config.getParameter())).add(config.getValue());
			}
		}
		return map;
	}
	/**
	 * FOR TEST PURPOSES ONLY!!!!!!
	 * 
	 * @param map
	 */
	public void setSystemConfigurationMap(Map<String, Object> map) {
		this.systemConfigMap = map;
	}

	public String getBlankBlankSampleId() {
		return blankBlankSampleId;
	}

	public void setBlankBlankSampleId(String blankBlankSampleId) {
		this.blankBlankSampleId = blankBlankSampleId;
	}

	public String getBlankProcessSampleId() {
		return blankProcessSampleId;
	}

	public String getBlankSolventSampleId() {
		return blankSolventSampleId;
	}

	public void setBlankSolventSampleId(String blankSolventSampleId) {
		this.blankSolventSampleId = blankSolventSampleId;
	}

	public String getProductionServerName() {
		return productionServerName;
	}

	public void setProductionServerName(String productionServerName) {
		this.productionServerName = productionServerName;
	}

	public String getDefaultAliquotVolume() {
		return defaultAliquotVolume;
	}

	public void setDefaultAliquotVolume(String defaultAliquotVolume) {
		this.defaultAliquotVolume = defaultAliquotVolume;
	}

	public String getDefaultAliquotVolumeUnits() {
		return defaultAliquotVolumeUnits;
	}

	public void setDefaultAliquotVolumeUnits(String defaultAliquotVolumeUnits) {
		this.defaultAliquotVolumeUnits = defaultAliquotVolumeUnits;
	}

	public String getDefaultAliquotLocation() {
		return defaultAliquotLocation;
	}

	public void setDefaultAliquotLocation(String defaultAliquotLocation) {
		this.defaultAliquotLocation = defaultAliquotLocation;
	}

	public String getJmsBrokerUrl() {
		return jmsBrokerUrl;
	}

	public void setJmsBrokerUrl(String url) {
		this.jmsBrokerUrl = url;
	}

	public String getGrobMixId() {
		return grobMixId;
	}

	public void setGrobMixId(String grobMixId) {
		this.grobMixId = grobMixId;
	}

	public String getBlankSampleId() {
		return blankBlankSampleId;
	}

	public String getServerQueue() {
		return serverQueue;
	}

	public void setServerQueue(String serverQueue) {
		this.serverQueue = serverQueue;
	}

	public String getClientMessageQueue() {
		return clientMessageQueue;
	}

	public void setClientMessageQueue(String clientMessageQueue) {
		this.clientMessageQueue = clientMessageQueue;
	}

	public void setBlankProcessSampleId(String blankSampleId) {
		this.blankProcessSampleId = blankSampleId;
	}

	public Integer getNumberBlankSamplePerRun() {
		return numberBlankSamplePerRun;
	}

	public void setNumberBlankSamplePerRun(Integer numberBlankSamplePerRun) {
		this.numberBlankSamplePerRun = numberBlankSamplePerRun;
	}

	public String getStandardMatrixSampleId() {
		return standardMatrixSampleId;
	}

	public void setStandardMatrixSampleId(String standardMatrixSampleId) {
		this.standardMatrixSampleId = standardMatrixSampleId;
	}

	public Integer getNumberStandardMatrixSamplePerRun() {
		return numberStandardMatrixSamplePerRun;
	}

	public void setNumberStandardMatrixSamplePerRun(Integer numberStandardMatrixSamplePerRun) {
		this.numberStandardMatrixSamplePerRun = numberStandardMatrixSamplePerRun;
	}

	public List<BarcodePrinters> getBarcodePrinterList() {
		return barcodePrinterList;
	}

	public void setBarcodePrinterList(List<BarcodePrinters> barcodePrinterList) {
		this.barcodePrinterList = barcodePrinterList;
	}

	public String getProcessedFilesFolder() {
		return processedFilesFolder;
	}

	public void setProcessedFilesFolder(String processedFileFolder) {
		this.processedFilesFolder = processedFileFolder;
	}

	public String getUploadFolder() {
		return fileUploadFolder;
	}

	public void setFileUploadFolder(String fileUploadFolder) {
		this.fileUploadFolder = fileUploadFolder;
	}

	public BarcodePrinterUtil getPlateBarcodePrinter() {
		return plateBarcodePrinter;
	}

	public void setPlateBarcodePrinter(BarcodePrinterUtil plateBarcodePrinter) {
		this.plateBarcodePrinter = plateBarcodePrinter;
	}

	public String getProductionDatabaseInstance() {
		return productionDatabaseInstance;
	}

	public void setProductionDatabaseInstance(String productionDatabaseInstance) {
		this.productionDatabaseInstance = productionDatabaseInstance;
	}

	public String getInstrumentIdGC1() {
		return instrumentIdGC1;
	}

	public void setInstrumentIdGC1(String instrumentIdGC1) {
		this.instrumentIdGC1 = instrumentIdGC1;
	}

	public String getInstrumentIdLC1() {
		return instrumentIdLC1;
	}

	public void setInstrumentIdLC1(String instrumentIdLC1) {
		this.instrumentIdLC1 = instrumentIdLC1;
	}

	public String getInstrumentIdLC2() {
		return instrumentIdLC2;
	}

	public void setInstrumentIdLC2(String instrumentIdLC2) {
		this.instrumentIdLC2 = instrumentIdLC2;
	}

	public String getDataProcessingInstrumentId() {
		return dataProcessingInstrumentId;
	}

	public void setDataProcessingInstrumentId(String dataProcessingInstrumentId) {
		this.dataProcessingInstrumentId = dataProcessingInstrumentId;
	}

	public String getGrobPrepID() {
		return grobPrepID;
	}

	public void setGrobPrepID(String grobPrepID) {
		this.grobPrepID = grobPrepID;
	}

	public String getWarmUpPrepID() {
		return warmUpPrepID;
	}

	public void setWarmUpPrepID(String warmUpPrepID) {
		this.warmUpPrepID = warmUpPrepID;
	}

	public String getDefaultSampleRunMode_LC() {
		return defaultSampleRunMode_LC;
	}

	public void setDefaultSampleRunMode_LC(String defaultSampleRunMode_LC) {
		this.defaultSampleRunMode_LC = defaultSampleRunMode_LC;
	}

	public String getDefaultSampleRunMode_GC() {
		return defaultSampleRunMode_GC;
	}

	public void setDefaultSampleRunMode_GC(String defaultSampleRunMode_GC) {
		this.defaultSampleRunMode_GC = defaultSampleRunMode_GC;
	}
}
