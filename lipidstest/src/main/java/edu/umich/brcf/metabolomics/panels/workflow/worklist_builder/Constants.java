// for issue 29
package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;
public final class Constants
    {
	public static final int BEFORE = 0;
	public static final int AFTER = 1;
	private Constants(){
		    //this prevents even the native class from 
		    //calling this ctor as well :
		    throw new AssertionError();
		  }
    }