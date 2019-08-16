//RandomizationLoader.java
//Written by Jan Wigginton, November 2015


package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

// issue 339 
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.io.StringUtils;


public class RandomizationLoader implements Serializable
	{
	private static final long serialVersionUID = -7157587000676603593L;

	private String fileName;
	private ExperimentRandomization expRandom;
    private String msg;
    
	public enum RandomizationFileType
		{
		FILE_TYPE_SAMPLE_ONLY, FILE_TYPE_SAMPLE_ORDER, FILE_TYPE_SAMPLE_VALUE, FILE_TYPE_UNEXPECTED, FILE_TYPE_UNKNOWN
		}

	public RandomizationLoader()
		{
		}

	// issue 385
	public RandomizationLoader(String eid)
		{
		}

	// issue 268
	public ExperimentRandomization loadRandomization(String filename, WorklistSimple originalWorklist, String assayID) throws METWorksException
		{
		//return loadRandomization(new File(System.getProperty("java.io.tmpdir") + filename), originalWorklist);
		return loadRandomization(new File(System.getProperty("java.io.tmpdir"), filename), originalWorklist, assayID);
		}

	
	public ExperimentRandomization loadRandomization(File file, WorklistSimple originalWorklist, String assayID) throws METWorksException
		{		
		try
		    {
			// issue 385
			expRandom = new ExperimentRandomization(originalWorklist.getDefaultExperimentId());
			// Issue 277
	        int numExcludedSamples = originalWorklist.countExcludedSamples();
	        if (numExcludedSamples > 0)
	            {
	        	msg = "There are " + numExcludedSamples + " samples that have an excluded status.  Please update the status of these samples"; 
				throw new METWorksException(msg);
	            }
			expRandom = readFile(file, originalWorklist);				
			// Issue 298 comment out abort for subset
			} 
		catch (IOException e)
			{
			e.printStackTrace(); // issue 268
			throw new METWorksException("Input Error while loading randomization file  " + expRandom.getExpId());
			}	
		return expRandom;
		}

	// issue 339

	public void initializeArrays() { } 
	
	// issue 268
	// issue 339
	// issue 439
	private ExperimentRandomization readFile(File file, WorklistSimple originalWorklist) throws METWorksException, FileNotFoundException, IOException
	    {
	    try
            {	
		    List<String> lines = Files.readAllLines(Paths.get(file.getPath()), StandardCharsets.UTF_8);                       
		    Map<String, Integer> sampleNameToReplicatesMap = buildReplicateMap(lines);
		    Map<String, Integer> foundReplicates = new HashMap<String, Integer>();       
		    for (String line : lines) 
                {
        	    if (line.length() < 1) continue;        	
        	        String[] cleanTokens = StringUtils.splitAndTrim(line);
        	    if (cleanTokens.length < 1) continue; 
        	    // issue 439
        	    readSampleLine(cleanTokens, originalWorklist, sampleNameToReplicatesMap, foundReplicates);      	        
                }
		     // issue 346 if (expRandom.hasDuplicateOrders()) throw new METWorksException("Duplicate indices in randomization file");		
		    return this.expRandom;       
            }
        catch (Exception e)
            {
    	    e.printStackTrace();
    	    throw new METWorksException(msg);
            }        
	    }

	// issue 339
	private Map<String, Integer> buildReplicateMap(List<String> lines) 
	    {
	    Map<String, Integer> replicatesMap = new HashMap<String, Integer>();	
	    Integer nExistingReps = 0;
	    for (String line : lines) 
		    {
		    if (line.length() < 1) continue;
    	    String[] cleanTokens = StringUtils.splitAndTrim(line);
    	    if (cleanTokens.length < 1) continue;    	
		    String sampleName = cleanTokens[0];	
    	    nExistingReps = 0;
		    if (replicatesMap.containsKey(sampleName))
		        nExistingReps = replicatesMap.get(sampleName);	
		    replicatesMap.put(sampleName, ++nExistingReps);
		    }
	return replicatesMap;
	}
	
	
	// Error while loading randomization file
	private boolean isSampleLine(String[] tokens, RandomizationFileType fileType)
		{
		switch (fileType)
			{
			case FILE_TYPE_SAMPLE_ONLY:
			return (tokens.length == 1);
			case FILE_TYPE_SAMPLE_VALUE:
			case FILE_TYPE_SAMPLE_ORDER:
			return (tokens.length == 2);
			default:
			return false;
			}
		}

	// issue 339
	private void readSampleLine(String[] tokens, WorklistSimple originalWorklist, 
			Map<String, Integer> sampleNameToReplicatesMap, Map<String, Integer> foundReplicates) throws METWorksException, NullPointerException, NumberFormatException
		{
		if (tokens[0] == null)
			return;		
		String sampleName = tokens[0];
		String tagSampleName = sampleName;		
		Boolean isStandardSample = FormatVerifier.verifyFormat(Sample._2019Format, sampleName.toUpperCase()); 
		if (!sampleNameToReplicatesMap.containsKey(sampleName))
			throw new METWorksException("Cant' find replicate count");	
		int nExistingReplicates = sampleNameToReplicatesMap.get(sampleName);		
		int nSuffixChars = 0;
		Integer nFoundReps = 0;	
		if  (nExistingReplicates > 1)
		    {
			if (isStandardSample) {
				msg = "The token " + sampleName + " is a duplicate sample in the csv file.  Please correct.";
				System.out.println(msg);
				throw new METWorksException(msg);
				}			
			Double pow10 = (Double) Math.log10(nExistingReplicates*1.0);
			nSuffixChars = 2 + ((Double) Math.floor(pow10)).intValue();			
			nFoundReps = foundReplicates.containsKey(sampleName) ? foundReplicates.get(sampleName) : 0;
			tagSampleName = sampleName + "-" + (++nFoundReps) ;
			foundReplicates.put(sampleName, nFoundReps);	
			}
            else if (!isStandardSample) 
			nFoundReps = 1;		
		// issue 268
		if (!isStandardSample && originalWorklist.isPlatformChosenAs("absciex"))
	        {
			msg = "The token " + sampleName + " isn't a sample name"; 
			System.out.println(msg);
			throw new METWorksException(msg);
			}		
		   Boolean nameExceedsMaxLength = sampleName.length() > (50 - nSuffixChars);
		// issue 268
		if (nameExceedsMaxLength && originalWorklist.isPlatformChosenAs("agilent"))
            {
		    msg = "The token " + sampleName + " must be no more than " + (50 - nSuffixChars) + " characters in length."; 
		    throw new METWorksException(msg);
		    }
		
		RandomizedSample sample = new RandomizedSample(tagSampleName, Double.NaN);
		sample.setReplicate(nFoundReps);
		expRandom.addSample(tagSampleName, sample);
		}
		
	public String getFileName()
		{
		return fileName;
		}

	
	public void setFileName(String fileName)
		{
		this.fileName = fileName;
		}
	}