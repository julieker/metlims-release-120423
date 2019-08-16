
package edu.umich.brcf.shared.layers.domain;


import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.wicket.util.io.IClusterable;


@Entity()
@Table(name = "VOLUME_UNITS")
public class VolumeUnits  implements IClusterable
	{
	@Id()
	@Column(name = "UNITS", nullable = false, unique = true, length = 2, columnDefinition = "CHAR(2)")
	private String units;

	@Basic()
	@Column(name = "TYPE", nullable = false, length = 3, columnDefinition = "CHAR(3)")
	private String type;

	@Basic()
	@Column(name = "PRIORITY", nullable = false, length = 1, columnDefinition = "NUMBER(1)")
	private Integer priority;

	
	public VolumeUnits() {  } 

	public String getUnits() 
		{
		return units;
		}

	public String getType() 
		{
		return type;
		}

	public Integer getPriority() 
		{
		return priority;
		}
	}
