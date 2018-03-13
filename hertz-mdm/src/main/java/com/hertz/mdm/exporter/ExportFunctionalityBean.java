package com.hertz.mdm.exporter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExportFunctionalityBean {

	private List<String> fileNames;
	private String outputFilePath;
	private String configID;
	private Map <String,Integer> fileNamesAndCount= new HashMap<String, Integer>();
	
	public List<String> getFileNames() {
		return fileNames;
	}
	public Map<String, Integer> getFileNamesAndCount() {
		return fileNamesAndCount;
	}
	public void setFileNamesAndCount(Map<String, Integer> fileNamesAndCount) {
		this.fileNamesAndCount = fileNamesAndCount;
	}
	public void setFileNames(List<String> fileNames) {
		this.fileNames = fileNames;
	}
	public String getOutputFilePath() {
		return outputFilePath;
	}
	public void setOutputFilePath(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}
	
	
	public String getConfigID() {
		return configID;
	}
	public void setConfigID(String configID) {
		this.configID = configID;
	}
	@Override
	public String toString() {
	
		return "{outputFilePath = " + outputFilePath + "}, { fileNames in the export = " + fileNames + "}";
	}
}
