package com.mechalikh.pureedgesim.loadgenerator;

import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import com.mechalikh.pureedgesim.datacenter.EdgeDataCenter;

public class Task extends CloudletSimple {
	private double offloadingTime;
	private double maxLatency;
	private EdgeDataCenter device;
	private long containerSize;
	private EdgeDataCenter orchestrator;
	private double receptionTime = -1; // the time when the task, or the corresponding container has been received by the offloading destination 
    private EdgeDataCenter registry;
	private int applicationID;
	public Task(int id, long cloudletLength, long pesNumber) {
		super(id, cloudletLength, pesNumber);
	}

	public void setTime(double time) {
		this.offloadingTime = time;
	}

	public double getTime() {
		return offloadingTime;
	}

	public double getMaxLatency() {
		return maxLatency;
	}

	public void setMaxLatency(double maxLatency) {
		this.maxLatency = maxLatency;
	}

	public EdgeDataCenter getEdgeDevice() {
		return device;
	}

	public void setEdgeDevice(EdgeDataCenter dev) {
		this.device = dev;
	}

	public void setContainerSize(long containerSize) {
		this.containerSize = containerSize;
	}

	public long getContainerSize() {
		return containerSize;
	}

	public void setOrchestrator(EdgeDataCenter orch) {
		this.orchestrator = orch;
	}

	public EdgeDataCenter getOrchestrator() {
		return orchestrator;
	}

	public double getReceptionTime() {
		return receptionTime;
	}

	public void setReceptionTime(double time) {
		receptionTime = time;
	}

	public EdgeDataCenter getRegistry() {
		return registry;
	}

	public void setRegistry(EdgeDataCenter registry) {
		this.registry = registry;
	}

	public int getApplicationID() {
		return applicationID;
	}

	public void setApplicationID(int applicationID) {
		this.applicationID = applicationID;
	}
 
}
