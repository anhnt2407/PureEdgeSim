package com.mechalikh.pureedgesim.core;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.mechalikh.pureedgesim.MainApplication;
import com.mechalikh.pureedgesim.datacenter.EdgeDataCenter;
import com.mechalikh.pureedgesim.energy.EnergyModel;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.power.models.PowerModelLinear;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mechalikh.pureedgesim.mobility.Location;
import com.mechalikh.pureedgesim.mobility.Mobility;
import com.mechalikh.pureedgesim.scenariomanager.simulationParameters;

public class ServersManager {
	private List<EdgeDataCenter> datacentersList;
	private List<EdgeDataCenter> orchestratorsList;
	private List<Vm> vmList;
	private SimulationManager simulationManager;
	private Class<? extends Mobility> mobilityManagerClass;
	private Class<? extends EnergyModel> energyModelClass;

	public ServersManager(SimulationManager simulationManager,
						  Class<? extends Mobility> mobilityManagerClass,
						  Class<? extends EnergyModel> energyModelClass) {
		datacentersList = new ArrayList<>();
		orchestratorsList = new ArrayList<>();
		vmList = new ArrayList<>();
		this.simulationManager = simulationManager;
		this.mobilityManagerClass = mobilityManagerClass;
		this.energyModelClass = energyModelClass;
	}

	public void generateDatacentersAndDevices() throws Exception {
		generateCloudDataCenters();
		generateFogDataCenters();
		generateEdgeDev();
		// Select where the orchestrators are deployed
		if (simulationParameters.ENABLE_ORCHESTRATORS)
			selectOrchestrators();
		getSimulationManager().getSimulationLogger().print("ServersManager- Datacenters and devices were generated");

	}

	private void selectOrchestrators() {
		for (EdgeDataCenter edgeDataCenter : datacentersList) {
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

	public void generateEdgeDev() throws Exception {
		// Generate edge devices instances from edge devices types in xml file
		File devicesFile = new File(simulationParameters.EDGE_DEVICES_FILE);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(devicesFile);
		NodeList edgeDevicesList = doc.getElementsByTagName("device");
		int instancesPercentage = 0;
		Element edgeElement = null;

		// Load all devices types in edgedevices.xml file
		for (int i = 0; i < edgeDevicesList.getLength(); i++) {
			Node edgeNode = edgeDevicesList.item(i);
			edgeElement = (Element) edgeNode;
			instancesPercentage = Integer
					.parseInt(edgeElement.getElementsByTagName("percentage").item(0).getTextContent());

			// Find the number of instances of this type of devices
			float devicesInstances = getSimulationManager().getScenario().getDevicesCount() * instancesPercentage / 100;

			for (int j = 0; j < devicesInstances; j++) {
				if (datacentersList.size() > getSimulationManager().getScenario().getDevicesCount() + simulationParameters.NUM_OF_FOG_DATACENTERS) {
					getSimulationManager().getSimulationLogger().print(
							"ServersManager- Wrong percentages values (the sum is superior than 100%), check edge_devices.xml file !");
					break;
				}

				datacentersList.add(createDatacenter(edgeElement, simulationParameters.TYPES.EDGE));

			}
		}
		if (datacentersList.size() < getSimulationManager().getScenario().getDevicesCount()) // if percentage of
																								// generated devices is
																								// < 100%
			getSimulationManager().getSimulationLogger().print(
					"ServersManager- Wrong percentages values (the sum is inferior than 100%), check edge_devices.xml file !");
		// Add more devices
		int missingInstances = getSimulationManager().getScenario().getDevicesCount() - datacentersList.size();
		for (int k = 0; k < missingInstances; k++) {
			datacentersList.add(createDatacenter(edgeElement, simulationParameters.TYPES.EDGE));
		}

	}

	private void generateFogDataCenters() throws Exception {
		// Fill list with fog data centers
		File serversFile = new File(simulationParameters.FOG_SERVERS_FILE);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(serversFile);
		NodeList datacenterList = doc.getElementsByTagName("datacenter");
		for (int i = 0; i < datacenterList.getLength(); i++) {
			Node datacenterNode = datacenterList.item(i);
			Element datacenterElement = (Element) datacenterNode;
			datacentersList.add(createDatacenter(datacenterElement, simulationParameters.TYPES.FOG));
		}
	}

	private void generateCloudDataCenters() throws Exception {
		// Fill the list with cloud datacenters
		File datacentersFile = new File(simulationParameters.CLOUD_DATACENTERS_FILE);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(datacentersFile);
		NodeList datacenterList = doc.getElementsByTagName("datacenter");
		for (int i = 0; i < datacenterList.getLength(); i++) {
			Node datacenterNode = datacenterList.item(i);
			Element datacenterElement = (Element) datacenterNode;
			datacentersList.add(createDatacenter(datacenterElement, simulationParameters.TYPES.CLOUD));
		}
	}

	private EdgeDataCenter createDatacenter(Element datacenterElement, simulationParameters.TYPES level) throws Exception {
		List<Host> hostList = createHosts(datacenterElement, level);
		EdgeDataCenter datacenter = new EdgeDataCenter(getSimulationManager(), hostList);

		if (level == simulationParameters.TYPES.FOG || level == simulationParameters.TYPES.EDGE) {
			int x_position;
			int y_position;
			if (level == simulationParameters.TYPES.FOG) {
				Element location = (Element) datacenterElement.getElementsByTagName("location").item(0);
				x_position = Integer.parseInt(location.getElementsByTagName("x_pos").item(0).getTextContent());
				y_position = Integer.parseInt(location.getElementsByTagName("y_pos").item(0).getTextContent());
			} else {
				datacenter.setMobile(Boolean.parseBoolean(datacenterElement.getElementsByTagName("mobility").item(0).getTextContent()));
				datacenter.setBattery(Boolean.parseBoolean(datacenterElement.getElementsByTagName("battery").item(0).getTextContent()));
				datacenter.setBatteryCapacity(Double.parseDouble(datacenterElement.getElementsByTagName("batterycapacity").item(0).getTextContent()));
				x_position = MainApplication.random.nextInt(simulationParameters.AREA_WIDTH);
				y_position = MainApplication.random.nextInt(simulationParameters.AREA_HEIGHT);
			}
			Location datacenterLocation = new Location(x_position, y_position);
			Constructor<? extends Mobility> mobilityConstructor = mobilityManagerClass.getConstructor(Location.class);
			datacenter.setMobilityManager(mobilityConstructor.newInstance(datacenterLocation));
		}

		datacenter.setOrchestrator(Boolean.parseBoolean(datacenterElement.getElementsByTagName("isOrchestrator").item(0).getTextContent()));
		datacenter.setType(level);

		Constructor<? extends EnergyModel> energyConstructor = energyModelClass.getConstructor();
		datacenter.setEnergyModel(energyConstructor.newInstance());

		return datacenter;
	}

	private List<Host> createHosts(Element datacenterElement, simulationParameters.TYPES type) {

		// Here are the steps needed to create a hosts and vms for that datacenter.
		List<Host> hostList = new ArrayList<>();

		NodeList hostNodeList = datacenterElement.getElementsByTagName("host");
		for (int j = 0; j < hostNodeList.getLength(); j++) {

			Node hostNode = hostNodeList.item(j);
			Element hostElement = (Element) hostNode;
			int numOfCores = Integer.parseInt(hostElement.getElementsByTagName("core").item(0).getTextContent());
			double mips = Double.parseDouble(hostElement.getElementsByTagName("mips").item(0).getTextContent());
			long storage = Long.parseLong(hostElement.getElementsByTagName("storage").item(0).getTextContent());
			long bandwidth;
			long ram;

			if (type == simulationParameters.TYPES.CLOUD) {
				bandwidth = simulationParameters.WAN_BANDWIDTH / hostNodeList.getLength();
			} else {
				bandwidth = simulationParameters.BANDWIDTH_WLAN / hostNodeList.getLength();
			}
			ram = Integer.parseInt(hostElement.getElementsByTagName("ram").item(0).getTextContent());

			// A Machine contains one or more PEs or CPUs/Cores. Therefore, should
			// create a list to store these PEs before creating
			// a Machine.
			List<Pe> peList = new ArrayList<>();

			// Create PEs and add these into the list.
			// for a quad-core machine, a list of 4 PEs is required:
			for (int i = 0; i < numOfCores; i++) {
				peList.add(new PeSimple(mips, new PeProvisionerSimple())); // need to store Pe id and MIPS Rating
			}

			ResourceProvisioner ramProvisioner = new ResourceProvisionerSimple();
			ResourceProvisioner bwProvisioner = new ResourceProvisionerSimple();
			VmScheduler vmScheduler = new VmSchedulerSpaceShared();

			// Create Hosts with its id and list of PEs and add them to the list of machines
			Host host = new HostSimple(ram, bandwidth, storage, peList);
			host.setRamProvisioner(ramProvisioner).setBwProvisioner(bwProvisioner).setVmScheduler(vmScheduler);

			NodeList vmNodeList = hostElement.getElementsByTagName("VM");
			for (int k = 0; k < vmNodeList.getLength(); k++) {
				Node vmNode = vmNodeList.item(k);
				Element vmElement = (Element) vmNode;
				// VM Parameters
				long vmNumOfCores = Long.parseLong(vmElement.getElementsByTagName("core").item(0).getTextContent());
				double vmMips = Double.parseDouble(vmElement.getElementsByTagName("mips").item(0).getTextContent());
				long vmStorage = Long.parseLong(vmElement.getElementsByTagName("storage").item(0).getTextContent());
				long vmBandwidth;
				int vmRam;

				vmBandwidth = bandwidth / vmNodeList.getLength();
				vmRam = Integer.parseInt(vmElement.getElementsByTagName("ram").item(0).getTextContent());

				CloudletScheduler tasksScheduler;

				if ("SPACE_SHARED".equals(simulationParameters.CPU_ALLOCATION_POLICY))
					tasksScheduler = new CloudletSchedulerSpaceShared();
				else
					tasksScheduler = new CloudletSchedulerTimeShared();

				Vm vm = new VmSimple(vmList.size(), vmMips, vmNumOfCores);
				vm.setRam(vmRam).setBw(vmBandwidth).setSize(vmStorage).setCloudletScheduler(tasksScheduler);
				vm.getUtilizationHistory().enable();
				vm.setHost(host);
				vmList.add(vm);
			}

			// TODO Temporary solution to provide PowerModel
			double dcIdleConsumption = Double.parseDouble(datacenterElement.getElementsByTagName("idleConsumption").item(0).getTextContent());
			double dcMaxConsumption = Double.parseDouble(datacenterElement.getElementsByTagName("maxConsumption").item(0).getTextContent());
			double maxPower = dcMaxConsumption / hostNodeList.getLength();
			double staticPowerPercent = dcIdleConsumption / hostNodeList.getLength() / maxPower;
			host.setPowerModel(new PowerModelLinear(maxPower, staticPowerPercent));

			hostList.add(host);
		}

		return hostList;
	}

	public List<Vm> getVmList() {
		return vmList;

	}

	public List<EdgeDataCenter> getDatacenterList() {
		return datacentersList;
	}

	public List<EdgeDataCenter> getOrchestratorsList() {
		return orchestratorsList;
	}

	public SimulationManager getSimulationManager() {
		return simulationManager;
	}
}
