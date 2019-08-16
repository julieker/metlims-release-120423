////////////////////////////////////
// RandomizedSample.java
// Written by Jan Wigginton
///////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.io.Serializable;


public class RandomizedSample implements Serializable, Comparable<RandomizedSample>
	{
	private static final long serialVersionUID = 925962566197648713L;
	
	private String sampleName;
	private Double randomValue;
	private Integer randomOrder;
	private Integer replicate = 0;
	
	public RandomizedSample(String name)
		{
		this(name, Double.NaN);
		}
	
	// onComponentTag
	public RandomizedSample(String name, Double value)
		{
		this(name, value, -1);
		}
	
	public RandomizedSample(String name, Integer order)
		{
		this(name, Double.NaN, order);
		}
	
	
	public RandomizedSample(String name, Double value, Integer order)
		{
		this(name, value, order, 0);
		}
	
	public RandomizedSample(String name, Double value, Integer order, Integer replicate)
		{
		sampleName = name;
		randomValue = value;
		randomOrder = order;
		this.replicate = replicate;
		}
	
	// issue 340
	public Integer getReplicate() {
		return replicate;
	}

	public void setReplicate(Integer replicate) {
		this.replicate = replicate;
	}

	public String getSampleName() 
		{
		return sampleName;
		}


	public void setSampleName(String sampleName) 
		{
		this.sampleName = sampleName;
		}


	public Double getRandomValue() 
		{
		return randomValue;
		}


	public void setRandomValue(Double randomValue) 
		{
		this.randomValue = randomValue;
		}


	public Integer getRandomOrder() 
		{
		return randomOrder;
		}


	public void setRandomOrder(Integer randomOrder) 
		{
		this.randomOrder = randomOrder;
		}

	
 	public boolean equals(Object o) 
 		{
        if (!(o instanceof RandomizedSample))
            return false;
        RandomizedSample n = (RandomizedSample) o;
        return sampleName.equals(n.sampleName) && randomOrder.equals(n.randomOrder) && randomValue.equals(n.randomValue) && replicate.equals(n.getReplicate());
 		}

    public int hashCode() 
    	{
        return 31*sampleName.hashCode() + randomValue.hashCode() + 4 * randomOrder.hashCode() + 19 * replicate.hashCode();
    	}

    
    public String toString() 
    	{
    	return sampleName + " " + randomValue;
    	}

    
    public int compareTo(RandomizedSample n) 
    	{
        int nextCmp = randomValue.compareTo(n.randomValue);	
        int orderCmp = randomOrder.compareTo(n.randomOrder);
        
        if (nextCmp != 0)
        	return nextCmp;
        
        if (orderCmp != 0) 
        	return orderCmp;
        			
        return ( replicate.compareTo(n.replicate));
    	}
    
    }
	
