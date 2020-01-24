package com.mechalikh.pureedgesim.datacenter;

import java.util.ArrayList;
import java.util.List;

import com.mechalikh.pureedgesim.energy.EnergyModel;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;

import com.mechalikh.pureedgesim.mobility.Location;
import com.mechalikh.pureedgesim.mobility.Mobility;
import com.mechalikh.pureedgesim.scenariomanager.simulationParameters;
import com.mechalikh.pureedgesim.core.SimulationManager;
import com.mechalikh.pureedgesim.orchestration.VmTaskMapItem;

public abstract class EdgeDataCenter extends DatacenterSimple {

	protected static final int UPDATE_STATUS = 2000; // Avoid conflicting with CloudSim Plus Tags

	protected SimulationManager simulationManager;
	protected simulationParameters.TYPES deviceType;
	protected List<VmTaskMapItem> vmTaskMap;

	protected Mobility mobilityManager;
	protected boolean isMobile = false;  // TODO Move to mobility model

	protected EnergyModel energyModel;
	protected boolean isBatteryPowered = false;  // TODO Move to energy model
	protected double batteryCapacity;  // TODO Move to energy model
	protected boolean isDead = false;  // TODO Move to energy model
	protected double deathTime;  // TODO Move to energy model

	protected EdgeDataCenter orchestrator; // TODO ???
	protected boolean isIdle = true;
	protected int applicationType;  // This is not used anywhere but may be interesting to keep
	protected boolean isOrchestrator = false;

	// TODO All these should be removed as this is host-level information
	protected int utilizationFrequency = 0;
	protected double totalCpuUtilization = 0;  // This is only used for logging and charts
	protected double currentCpuUtilization = 0;  // This is used nowhere

	public EdgeDataCenter(SimulationManager simulationManager, List<? extends Host> hostList) {
		super(simulationManager.getSimulation(), hostList,new VmAllocationPolicySimple());
		this.simulationManager = simulationManager;
		vmTaskMap = new ArrayList<>();
	}

	protected void updateEnergyConsumption() {
		setIdle(true);
		double vmUsage = 0;
		currentCpuUtilization = 0;

		// get the cpu usage of all vms
		for (int i = 0; i < this.getVmList().size(); i++) {
			vmUsage = this.getVmList().get(i).getCloudletScheduler()
					.getRequestedCpuPercentUtilization(simulationManager.getSimulation().clock());
			currentCpuUtilization += vmUsage; // the current utilization
			totalCpuUtilization += vmUsage;
			utilizationFrequency++; // in order to get the average usage from the total usage
			if (vmUsage != 0)
				setIdle(false); // set as active (not idle) if at least one vm is used
		}

		if (this.getVmList().size() > 0)
			currentCpuUtilization = currentCpuUtilization / this.getVmList().size();

		// update the energy consumption
		this.getEnergyModel().updateCpuEnergyConsumption(currentCpuUtilization);

		if (isBattery() && this.getEnergyModel().getTotalEnergyConsumption() > batteryCapacity) {
			isDead = true;
			deathTime = simulationManager.getSimulation().clock();
		}
	}

	public EnergyModel getEnergyModel() {
		return energyModel;
	}

	public simulationParameters.TYPES getType() {
		return deviceType;
	}

	public void setType(simulationParameters.TYPES type) {
		this.deviceType = type;
	}

	public Location getLocation() {
		return getMobilityManager().getCurrentLocation();
	}

	public boolean isMobile() {
		return isMobile;
	}

	public void setMobile(boolean mobile) {
		isMobile = mobile;
	}

	public boolean isBattery() {
		return isBatteryPowered;
	}

	public void setBattery(boolean battery) {
		this.isBatteryPowered = battery;
	}

	public double getBatteryCapacity() {
		return batteryCapacity;
	}

	public void setBatteryCapacity(double batteryCapacity) {
		this.batteryCapacity = batteryCapacity;
	}

	public double getBatteryLevel() {
		if (!isBattery())
			return 0;
		if (batteryCapacity < this.getEnergyModel().getTotalEnergyConsumption())
			return 0;
		return batteryCapacity - this.getEnergyModel().getTotalEnergyConsumption();
	}

	public double getBatteryLevelPercentage() {
		return getBatteryLevel() * 100 / batteryCapacity;
	}

	public boolean isDead() {
		return isDead;
	}

	public double getDeathTime() {
		return deathTime;
	}

	public List<VmTaskMapItem> getVmTaskMap() {
		return vmTaskMap;
	}

	public void setApplication(int app) {
		this.applicationType = app;
	}

	public int getApplication() {
		return applicationType;
	}

	public boolean isOrchestrator() {
		return isOrchestrator;
	}

	public void setOrchestrator(boolean isOrchestrator) {
		this.isOrchestrator = isOrchestrator;
	}

	public double getTotalCpuUtilization() {
		if (utilizationFrequency == 0)
			utilizationFrequency = 1;
		return totalCpuUtilization * 100 / utilizationFrequency;
	}

	public double getCurrentCpuUtilization() {
		return currentCpuUtilization * 100;
	}

	public boolean isIdle() {
		return isIdle;
	}

	public void setIdle(boolean isIdle) {
		this.isIdle = isIdle;
	}

	public Mobility getMobilityManager() {
		return mobilityManager;
	}

	public void setMobilityManager(Object mobilityManager) {
		this.mobilityManager = (Mobility) mobilityManager;
	}

	public void setEnergyModel(Object energyModel) {
		this.energyModel = (EnergyModel) energyModel;

	}

	public EdgeDataCenter getOrchestrator() {
		return this.orchestrator;
	}
}
