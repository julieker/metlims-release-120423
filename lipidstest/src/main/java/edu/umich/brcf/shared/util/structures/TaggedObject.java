package edu.umich.brcf.shared.util.structures;

import java.io.Serializable;



public class TaggedObject  implements Serializable
	{
	private String tag;
	private Object taggedObject;
	
	public TaggedObject(String tag, Object object) 
		{
		this.tag = tag;
		this.taggedObject = object; 
		}

	
	public TaggedObject() { }

	
	public String getTag()
		{
		return tag;
		}

	
	public void setTag(String tag)
		{
		this.tag = tag;
		}

	
	public Object getTaggedObject()
		{
		return taggedObject;
		}

	
	public void setTaggedObject(Object taggedObject)
		{
		this.taggedObject = taggedObject;
		}
	}
