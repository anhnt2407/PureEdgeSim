package com.mechalikh.pureedgesim.core;

import com.mechalikh.pureedgesim.MainApplication;
import com.mechalikh.pureedgesim.configs.*;
import com.mechalikh.pureedgesim.datacenter.EdgeDataCenter;
import com.mechalikh.pureedgesim.energy.EnergyModel;
import com.mechalikh.pureedgesim.logging.SimLog;
import com.mechalikh.pureedgesim.mobility.Location;
import com.mechalikh.pureedgesim.mobility.Mobility;
import com.mechalikh.pureedgesim.scenariomanager.simulationParameters;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.power.models.PowerModelLinear;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServersManager {
	private List<EdgeDataCenter> datacenters;
	private List<EdgeDataCenter> orchestratorsList;
	private List<Vm> vmList;
	private SimulationManager simulationManager;
	private Class<? extends Mobility> mobilityManagerClass;
	private Class<? extends EnergyModel> energyModelClass;

	public ServersManager(SimulationManager simulationManager,
						  Class<? extends Mobility> mobilityManagerClass,
						  Class<? extends EnergyModel> energyModelClass) {
		datacenters = new ArrayList<>();
		orchestratorsList = new ArrayList<>();
		vmList = new ArrayList<>();
		this.simulationManager = simulationManager;
		this.mobilityManagerClass = mobilityManagerClass;
		this.energyModelClass = energyModelClass;
	}

	public void generateDatacentersAndDevices(ArrayList<DatacenterConfig> datacenterConfigs,
											  ArrayList<EdgeDatacenterConfig> edgeDatacenterConfigs) throws Exception {
		generateDatacenters(datacenterConfigs);
		generateEdgeDevices(edgeDatacenterConfigs);
		if (simulationParameters.ENABLE_ORCHESTRATORS) {
			selectOrchestrators();
		}
		SimLog.println("ServersManager- Datacenters and devices were generated");
	}

	private void selectOrchestrators() {
		for (EdgeDataCenter edgeDataCenter : datacenters) {
			if ("".equals(simulationParameters.DEPLOY_ORCHESTRATOR)
					|| ("CLOUD".equals(simulationParameters.DEPLOY_ORCHESTRATOR)
					&& edgeDataCenter.getType() == simulationParameters.TYPES.CLOUD)) {
				edgeDataCenter.setOrchestrator(true);
				orchestratorsList.add(edgeDataCenter);
			} else if ("FOG".equals(simulationParameters.DEPLOY_ORCHESTRATOR)
					&& edgeDataCenter.getType() == simulationParameters.TYPES.FOG) {
				edgeDataCenter.setOrchestrator(true);
				orchestratorsList.add(edgeDataCenter);
			} else if ("EDGE".equals(simulationParameters.DEPLOY_ORCHESTRATOR)
					&& edgeDataCenter.getType() == simulationParameters.TYPES.EDGE) {
				edgeDataCenter.setOrchestrator(true);
				orchestratorsList.add(edgeDataCenter);
			}
		}

	}

	private void generateDatacenters(ArrayList<DatacenterConfig> cloudConfig) throws Exception {
		for (DatacenterConfig datacenterConfig : cloudConfig) {
			datacenters.add(createDatacenter(datacenterConfig));
		}
	}

	public void generateEdgeDevices(ArrayList<EdgeDatacenterConfig> edgeDatacenterConfigs) throws Exception {
		int totalPercentage = 0;
		for (EdgeDatacenterConfig edgeDatacenterConfig : edgeDatacenterConfigs) {
			int instancesPercentage = edgeDatacenterConfig.getPercentage();
			totalPercentage += instancesPercentage;
			int devicesInstances = getSimulationManager().getScenario().getDevicesCount() * instancesPercentage / 100;
			for (int j = 0; j < devicesInstances; j++) {
				datacenters.add(createDatacenter(edgeDatacenterConfig));
			}
		}

		if (totalPercentage != 100) {
			SimLog.println("Error: Total percentage of edge devices is " + totalPercentage);
			System.exit(0);
		}
	}

	private EdgeDataCenter createDatacenter(DatacenterConfig datacenterConfig) throws Exception {
		simulationParameters.TYPES type;
		if (datacenterConfig.getTag().equals("CLOUD")) {
			type = simulationParameters.TYPES.CLOUD;
		} else if (datacenterConfig.getTag().equals("FOG")) {
			type = simulationParameters.TYPES.FOG;
		} else {
			type = simulationParameters.TYPES.EDGE;
		}
		List<Host> hostList = createHosts(datacenterConfig.getHosts(), type);
		EdgeDataCenter datacenter = new EdgeDataCenter(getSimulationManager(), hostList);
		datacenter.setOrchestrator(datacenterConfig.isOrchestrator());
		datacenter.setType(type);
		datacenter.setEnergyModel(energyModelClass.getConstructor().newInstance());

		if (type == simulationParameters.TYPES.FOG || type == simulationParameters.TYPES.EDGE) {
			int x_position;
			int y_position;
			if (type == simulationParameters.TYPES.FOG) {
				FogDatacenterConfig fogDatacenterConfig = (FogDatacenterConfig) datacenterConfig;
				x_position = fogDatacenterConfig.getLocationX();
				y_position = fogDatacenterConfig.getLocationY();
			} else {
				EdgeDatacenterConfig edgeDatacenterConfig = (EdgeDatacenterConfig) datacenterConfig;
				datacenter.setMobile(edgeDatacenterConfig.isMobility());
				datacenter.setBattery(edgeDatacenterConfig.isBattery());
				datacenter.setBatteryCapacity(edgeDatacenterConfig.getBatteryCapacity());
				x_position = MainApplication.random.nextInt(simulationParameters.AREA_WIDTH);
				y_position = MainApplication.random.nextInt(simulationParameters.AREA_HEIGHT);
			}
			Location datacenterLocation = new Location(x_position, y_position);
			Constructor<? extends Mobility> mobilityConstructor = mobilityManagerClass.getConstructor(Location.class);
			datacenter.setMobilityManager(mobilityConstructor.newInstance(datacenterLocation));
		}

		return datacenter;
	}

	private List<Host> createHosts(List<HostConfig> hostConfigs, simulationParameters.TYPES type) {
		List<Host> hosts = new ArrayList<>();

		for (HostConfig hostConfig : hostConfigs) {
			long bandwidth;
			if (type == simulationParameters.TYPES.CLOUD) {
				bandwidth = simulationParameters.WAN_BANDWIDTH / hostConfigs.size();
			} else {
				bandwidth = simulationParameters.BANDWIDTH_WLAN / hostConfigs.size();
			}

			List<Pe> peList = new ArrayList<>();
			for (int i = 0; i < hostConfig.getCore(); i++) {
				peList.add(new PeSimple(hostConfig.getMips(), new PeProvisionerSimple()));
			}

			Host host = new HostSimple(hostConfig.getRam(), bandwidth, hostConfig.getStorage(), peList);

			for (VmConfig vmConfig : hostConfig.getVms()) {
				CloudletScheduler tasksScheduler;
				if ("SPACE_SHARED".equals(simulationParameters.CPU_ALLOCATION_POLICY))
					tasksScheduler = new CloudletSchedulerSpaceShared();
				else
					tasksScheduler = new CloudletSchedulerTimeShared();
				long vmBandwidth = bandwidth / hostConfig.getVms().size();
				Vm vm = new VmSimple(vmList.size(), vmConfig.getMips(), vmConfig.getCore());
				vm.setRam(vmConfig.getRam()).setBw(vmBandwidth).setSize(vmConfig.getStorage()).setCloudletScheduler(tasksScheduler);
				vm.getUtilizationHistory().enable();
				vm.setHost(host);  // TODO Does it make sense to statically assign VMs to Hosts?
				vmList.add(vm);
			}
			double maxPower = hostConfig.getMaxConsumption();
			double staticPowerPercent = hostConfig.getIdleConsumption() / maxPower;
			host.setPowerModel(new PowerModelLinear(maxPower, staticPowerPercent));

			hosts.add(host);
		}

		return hosts;
	}

	public List<Vm> getVmList() {
		return vmList;

	}

	public List<EdgeDataCenter> getDatacenters() {
		return datacenters;
	}

	public List<EdgeDataCenter> getOrchestratorsList() {
		return orchestratorsList;
	}

	public SimulationManager getSimulationManager() {
		return simulationManager;
	}

	public List<EdgeDataCenter> getCloudDatacenters() {
		return filterDatacentersByType(simulationParameters.TYPES.CLOUD);
	}

	public List<EdgeDataCenter> getFogDatacenters() {
		return filterDatacentersByType(simulationParameters.TYPES.FOG);
	}

	public List<EdgeDataCenter> getEdgeDatacenters() {
		return filterDatacentersByType(simulationParameters.TYPES.EDGE);
	}

	private List<EdgeDataCenter> filterDatacentersByType(simulationParameters.TYPES type) {
		return datacenters.stream().filter(dc -> dc.getType() == type).collect(Collectors.toList());
	}

}
