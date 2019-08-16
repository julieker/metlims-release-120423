package edu.umich.brcf.shared.util.utilpackages;



public class BarcodePrinterUtil extends PrinterUtil {
	private String printString;
	private String multiplePrintModifier;
	private String endString;

	public String getPrintString() {
		return printString;
	}

	public void setPrintString(String printString) {
		this.printString = printString.replaceAll("#newLine#", "\n");
	}

	public String getMultiplePrintModifier() {
		return multiplePrintModifier;
	}

	public void setMultiplePrintModifier(String multiplePrintModifier) {
		this.multiplePrintModifier = multiplePrintModifier.replaceAll("#newLine#", "\n");
	}

	public String getEndString() {
		return endString.replaceAll("#newLine#", "\n");
	}

	public void setEndString(String endString) {
		this.endString = endString;
	}

}
