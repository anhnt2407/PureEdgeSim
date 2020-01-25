package com.mechalikh.pureedgesim.datacenter;

import java.util.ArrayList;
import java.util.List;

import com.mechalikh.pureedgesim.energy.EnergyModel;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;

import com.mechalikh.pureedgesim.mobility.Location;
import com.mechalikh.pureedgesim.mobility.Mobility;
import com.mechalikh.pureedgesim.scenariomanager.simulationParameters;
import com.mechalikh.pureedgesim.core.SimulationManager;
import com.mechalikh.pureedgesim.orchestration.VmTaskMapItem;
import org.cloudbus.cloudsim.vms.Vm;

public class EdgeDataCenter extends DatacenterSimple {

	protected static final int UPDATE_STATUS = 2000; // Avoid conflicting with CloudSim Plus Tags

	protected SimulationManager simulationManager;
	protected simulationParameters.TYPES deviceType;
	protected List<VmTaskMapItem> vmTaskMap;

	// TODO This should only exist on edge datacenters
	protected Mobility mobilityManager;
	protected boolean isMobile = false;  // TODO Should be determined via mobilityManager != Mobility.NULL

	protected EnergyModel energyModel;
	protected boolean isBatteryPowered = false;  // TODO Move to energy model
	protected double batteryCapacity;  // TODO Move to energy model
	protected double deathTime;  // TODO Move to energy model

	protected boolean isOrchestrator = false;
	protected int applicationType;  // This is not used anywhere but may be interesting to keep

	// TODO These are only used for logging and charts and should be implemented via host.getUtilizationHistory()
	protected double totalCpuUtilization = 0;
	protected int utilizationFrequency = 0;

	public EdgeDataCenter(SimulationManager simulationManager, List<? extends Host> hostList) {
		super(simulationManager.getSimulation(), hostList,new VmAllocationPolicySimple());
		this.simulationManager = simulationManager;
		vmTaskMap = new ArrayList<>();
	}

	@Override
	public void startEntity() {
		super.startEntity();
		schedule(this, simulationParameters.INITIALIZATION_TIME, UPDATE_STATUS);
	}

	@Override
	public void processEvent(final SimEvent ev) {
		if (ev.getTag() == UPDATE_STATUS) {// Update energy consumption
			updateEnergyConsumption();

			// Update location
			if (isMobile()) {
				getMobilityManager().getNextLocation();
			}

			if (!isDead()) {
				schedule(this, simulationParameters.UPDATE_INTERVAL, UPDATE_STATUS);
			}
		} else {
			super.processEvent(ev);
		}
	}

	protected void updateEnergyConsumption() {  // TODO Move to energy model
		double currentCpuUtilization = 0;

		// get the cpu usage of all vms
		List<Vm> vms = new ArrayList<>();
		for (Host host : getHostList()) {
			vms.addAll(host.getVmList());
		}

		for (Vm vm : vms) {
			double vmUsage = vm.getCloudletScheduler()
					.getRequestedCpuPercentUtilization(simulationManager.getSimulation().clock());
			currentCpuUtilization += vmUsage; // the current utilization
			totalCpuUtilization += vmUsage;
			utilizationFrequency++; // in order to get the average usage from the total usage
		}

		if (vms.size() > 0) {
			currentCpuUtilization = currentCpuUtilization / vms.size();
		}

		// update the energy consumption
		this.getEnergyModel().updateCpuEnergyConsumption(currentCpuUtilization);

		if (isDead()) {
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
		return isBattery() && this.getEnergyModel().getTotalEnergyConsumption() > batteryCapacity;
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

	public Mobility getMobilityManager() {
		return mobilityManager;
	}

	public void setMobilityManager(Mobility mobilityManager) {
		this.mobilityManager = mobilityManager;
	}

	public void setEnergyModel(EnergyModel energyModel) {
		this.energyModel = energyModel;
	}
}
