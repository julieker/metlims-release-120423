package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

public class CountPair {
	
	private String tag;
	private Integer count = null;
	
	public CountPair() { 
		tag = "";
		count = null;
	}
	
	public CountPair(String tag, Integer count) {
		this.tag = tag;
		this.count = count;
	}
	
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	
	

}