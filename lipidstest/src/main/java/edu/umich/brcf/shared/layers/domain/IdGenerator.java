package edu.umich.brcf.shared.layers.domain;

import java.util.Formatter;
import java.util.Locale;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.util.Assert;

/**
 * All in the necessity to generate String sequence value for our objects so as not 
 * to change current DB structures
 * 
 * yes sequences can be used, but we run into problems when we hit the max sequence
 * width and need to change the letter value and reset the sequence.  i.e. what happens
 * when we have 4 digits and our current sequence is XX9999?
 */
@Entity()
@Table(name="ID_CONTROL")
public class IdGenerator {
	public static IdGenerator instance(String idClass, int width, Character first, Character second, Long sequence){
		return new IdGenerator(idClass, width, first, second, sequence);
	}
	@Id()
	@Column(name="ID_CLASS", nullable=false, unique=true)
	private String idClass;

	@Basic()
	@Column(name="WIDTH", nullable=false)
	private Integer width;
	
	@Basic()
	@Column(name="FIRST_LETTER", nullable=false)
	private Character firstLetter;
	
	@Basic()
	@Column(name="SECOND_LETTER", length=1)
	private Character secondLetter;
	
	@Basic()
	@Column(name="SEQUENCE", nullable=false)
	private Long sequence;
	
	public IdGenerator(){  } 

	
	private IdGenerator(String idClass, int width, Character first, Character second, Long sequence)
		{
		Assert.notNull(idClass);
		Assert.notNull(sequence);
		Assert.notNull(first);
		Assert.isTrue(width>3);
		this.idClass = idClass;
		this.width = width;
		this.firstLetter = first;
		this.secondLetter = second;
		this.sequence = sequence;
		}
	
	protected Long getMaxNumericValue()
		{
		StringBuilder sb = new StringBuilder();
		for( int i=0 ; i<numericWidth() ; i++)
			sb.append("9");  
		
		return Long.decode(sb.toString());
		}
	
	protected int numericWidth()
		{
		return width - 1 - (secondLetter==null? 0 : 1);
		}	
	
	protected String makeFormatString()
		{
		if( secondLetter!=null)
			return "%c%c%0"+numericWidth()+"d";
			
		return "%c%0"+numericWidth()+"d"; 
		}
	
	// Issue 205
	public String getNextIdValue()
	    {
		return getNextIdValue(true,1);		
	    }  
	
	// Issue 205
	public String getNextIdValue(boolean increment, Integer incrementNumber){
		Formatter formatter = new Formatter(new StringBuilder(), Locale.US);
		Long sequenceTmp = sequence;
	
		sequenceTmp = sequence+ incrementNumber;
		if( sequenceTmp > getMaxNumericValue() )
			{
			sequenceTmp = 0L;
			if( secondLetter == null )
				firstLetter = new Character((char)(firstLetter.charValue()+1));
			else 
				secondLetter = new Character((char)(secondLetter.charValue()+1));
			}
		if (increment)
			sequence = sequenceTmp;
		if( secondLetter!=null)
			return String.format(makeFormatString(), firstLetter, secondLetter, sequenceTmp);
		
		return String.format(makeFormatString(), firstLetter, sequenceTmp);
		}
	
	
	public Long getSequence(){
		return sequence;
	}
}
