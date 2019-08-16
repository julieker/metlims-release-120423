package edu.umich.brcf.metabolomics.layers.dto;

import java.io.Serializable;

public class AnalyzerProTargetComponentDTO implements Serializable {
	String name;
	String expectPosition;
	String forwardThreshold;
	String reverseThreshold;
	String area;
	String height;
	String position;
	String delta;
	String forward;
	String reverse;
	String ionRationStatus;
	String status;
	String confidence;
	String componentIndex;
	String ratios;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExpectPosition() {
		return expectPosition;
	}

	public void setExpectPosition(String expectPosition) {
		this.expectPosition = expectPosition;
	}

	public String getForwardThreshold() {
		return forwardThreshold;
	}

	public void setForwardThreshold(String forwardThreshold) {
		this.forwardThreshold = forwardThreshold;
	}

	public String getReverseThreshold() {
		return reverseThreshold;
	}

	public void setReverseThreshold(String reverseThreshold) {
		this.reverseThreshold = reverseThreshold;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getDelta() {
		return delta;
	}

	public void setDelta(String delta) {
		this.delta = delta;
	}

	public String getForward() {
		return forward;
	}

	public void setForward(String forward) {
		this.forward = forward;
	}

	public String getReverse() {
		return reverse;
	}

	public void setReverse(String reverse) {
		this.reverse = reverse;
	}

	public String getIonRationStatus() {
		return ionRationStatus;
	}

	public void setIonRationStatus(String ionRationStatus) {
		this.ionRationStatus = ionRationStatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getConfidence() {
		return confidence;
	}

	public void setConfidence(String confidence) {
		this.confidence = confidence;
	}

	public String getComponentIndex() {
		return componentIndex;
	}

	public void setComponentIndex(String componentIndex) {
		this.componentIndex = componentIndex;
	}

	public String getRatios() {
		return ratios;
	}

	public void setRatios(String ratios) {
		this.ratios = ratios;
	}
}
