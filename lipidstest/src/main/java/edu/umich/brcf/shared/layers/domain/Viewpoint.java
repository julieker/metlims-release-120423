package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.wicket.util.io.IClusterable;




@Entity()
@Table(name="VIEWPOINT")

public class Viewpoint implements Serializable, IClusterable
	{
	public static Viewpoint instance(String name, Long level)
		{
		return new Viewpoint(null, name, level);
		}
	
	public static Viewpoint instance(Long id, String name, Long level)
		{
		return new Viewpoint(id, name, level);
		}

	@Id()
	@Column(name="VIEWPOINT_ID", unique=true, nullable=false, precision=3, columnDefinition="NUMBER(3,0)")
	private Long id;
	
	@Basic()
	@Column(name="NAME", unique=true, nullable=false, length=36, columnDefinition="VARCHAR(36)")
	private String name;
	
	@Basic()
	@Column(name="USER_LEVEL", unique=true, nullable=false, precision=2, columnDefinition="NUMBER(2,0)")
	private Long level;

	public Viewpoint(){  }
	
	private Viewpoint(Long id, String name, Long level) 
		{
	//	super();
		this.id = id;
		this.name = name;
		this.level = level;
		}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Long getLevel() {
		return level;
	}
}


/*

@Entity()
@Table(name = "VIEWPOINT")
public class Viewpoint implements Serializable, IClusterable
	{
	public static Viewpoint instance(String name, Long level)
		{
		return new Viewpoint(null, name, level);
		}

	@Id()
	@Column(name = "VIEWPOINT_ID", unique = true, nullable = false, precision = 3, columnDefinition = "NUMBER(3,0)")
	private Long id;

	@Basic()
	@Column(name = "NAME", unique = true, nullable = false, length = 36, columnDefinition = "VARCHAR2(36)")
	private String name;

	@Basic()
	@Column(name = "USER_LEVEL", unique = true, nullable = false, precision = 2, columnDefinition = "NUMBER(2,0)")
	private Long level;

	
	public Viewpoint()  {   } 

	
	private Viewpoint(Long id, String name, Long level)
		{
		super();
		this.id = id;
		this.name = name;
		this.level = level;
		}

	public Long getId()
		{
		return id;
		}

	public String getName()
		{
		return name;
		}

	public Long getLevel()
		{
		return level;
		}
	} */
