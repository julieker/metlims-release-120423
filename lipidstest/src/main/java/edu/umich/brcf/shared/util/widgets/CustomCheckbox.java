package edu.umich.brcf.shared.util.widgets;

import java.io.Serializable;

public class CustomCheckbox implements Serializable{
	
	private Boolean selected = Boolean.FALSE;

	public CustomCheckbox() {
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

}
