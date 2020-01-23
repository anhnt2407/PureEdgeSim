package com.mechalikh.pureedgesim.scenariomanager;

public class Scenario {
	private int devicesCount; // The number of edge devices in this scenario
	private String orchAlgorithm; // The tasks orchestration algorithm that will be used in this scenario
	private String orchArchitecture; // The used architecture/ computing paradigms

	public Scenario(int devicesCount, String orchAlgorithm, String orchArchitecture) {
		this.orchAlgorithm = orchAlgorithm;
		this.devicesCount = devicesCount;
		this.orchArchitecture = orchArchitecture; 
	}

	public int getDevicesCount() {
		return devicesCount;
	}

	public String getOrchAlgorithm() {
		return orchAlgorithm;
	}

	public String getOrchArchitecture() {
		return orchArchitecture;
	}
	
	public String toString() {
		return getOrchAlgorithm()+"-"+getOrchArchitecture()+ "-"+getDevicesCount();
	}

}
