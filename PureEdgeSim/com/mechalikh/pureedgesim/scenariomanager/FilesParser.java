package com.mechalikh.pureedgesim.scenariomanager;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.mechalikh.pureedgesim.configs.DatacenterConfig;
import com.mechalikh.pureedgesim.tasksgenerator.Application;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
 
import com.mechalikh.pureedgesim.scenariomanager.simulationParameters.TYPES;
import com.mechalikh.pureedgesim.logging.SimLog;
import org.yaml.snakeyaml.Yaml;

public class FilesParser {

	// Scan files
	public boolean checkFiles(String simProp, String edgeFile) {
		simulationParameters.EDGE_DEVICES_FILE = edgeFile;
		return (checkSimulationProperties(simProp) && checkXmlFiles(edgeFile, TYPES.EDGE));
	}

	private boolean checkSimulationProperties(String simProp) {
		SimLog.println("FilesParser- Checking simulation properties file");
		boolean result = false;
		InputStream input = null;
		try {
			input = new FileInputStream(simProp);

			// loading properties file
			Properties prop = new Properties();
			prop.load(input);
			simulationParameters.PARALLEL = Boolean.parseBoolean(prop.getProperty("parallel_simulation").trim());

			simulationParameters.INITIALIZATION_TIME = Double
					.parseDouble(prop.getProperty("initialization_time").trim()); // seconds
			simulationParameters.SIMULATION_TIME = simulationParameters.INITIALIZATION_TIME
					+ Double.parseDouble(prop.getProperty("simulation_time").trim()); // seconds

			simulationParameters.DISPLAY_REAL_TIME_CHARTS = Boolean
					.parseBoolean(prop.getProperty("display_real_time_charts").trim());
			simulationParameters.AUTO_CLOSE_REAL_TIME_CHARTS = Boolean
					.parseBoolean(prop.getProperty("auto_close_real_time_charts").trim());
			simulationParameters.CHARTS_UPDATE_INTERVAL = Double
					.parseDouble(prop.getProperty("charts_update_interval").trim());
			simulationParameters.SAVE_CHARTS = Boolean.parseBoolean(prop.getProperty("save_charts").trim());

			simulationParameters.AREA_HEIGHT = Integer.parseInt(prop.getProperty("height").trim()); // meters
			simulationParameters.AREA_WIDTH = Integer.parseInt(prop.getProperty("width").trim()); // meters
			simulationParameters.UPDATE_INTERVAL = Double.parseDouble(prop.getProperty("update_interval").trim()); // seconds
			simulationParameters.DEEP_LOGGING = Boolean.parseBoolean(prop.getProperty("deep_log_enabled").trim());
			simulationParameters.SAVE_LOG = Boolean.parseBoolean(prop.getProperty("save_log_file").trim());
			simulationParameters.WAIT_FOR_TASKS = Boolean.parseBoolean(prop.getProperty("wait_for_all_tasks").trim());
			simulationParameters.ENABLE_REGISTRY = Boolean.parseBoolean(prop.getProperty("enable_registry").trim()); 
			simulationParameters.registry_mode = prop.getProperty("registry_mode").trim(); 
			simulationParameters.ENABLE_ORCHESTRATORS = Boolean
					.parseBoolean(prop.getProperty("enable_orchestrators").trim());

			simulationParameters.EDGE_RANGE = Integer.parseInt(prop.getProperty("edge_range").trim()); // meters
			simulationParameters.FOG_RANGE = Integer.parseInt(prop.getProperty("fog_coverage").trim()); // meters
			simulationParameters.PAUSE_LENGTH = Integer.parseInt(prop.getProperty("pause_length").trim());// seconds
			simulationParameters.MIN_NUM_OF_EDGE_DEVICES = Integer
					.parseInt(prop.getProperty("min_number_of_edge_devices").trim());
			simulationParameters.MAX_NUM_OF_EDGE_DEVICES = Integer
					.parseInt(prop.getProperty("max_number_of_edge_devices").trim());
			if (simulationParameters.MIN_NUM_OF_EDGE_DEVICES > simulationParameters.MAX_NUM_OF_EDGE_DEVICES) {
				SimLog.println(
						"FilelParser, Error,  the entered min number of edge devices is superior than the max number, check the 'simulation.properties' file.");
				System.exit(0);
			}
			simulationParameters.EDGE_DEVICE_COUNTER_STEP = Integer
					.parseInt(prop.getProperty("edge_device_counter_size").trim());
			simulationParameters.SPEED = Double.parseDouble(prop.getProperty("speed").trim()); // meters per second m/s
			simulationParameters.BANDWIDTH_WLAN = 1000 * Integer.parseInt(prop.getProperty("wlan_bandwidth").trim()); // Mbits/s
																														// to
																														// Kbits/s
			simulationParameters.WAN_BANDWIDTH = 1000 * Integer.parseInt(prop.getProperty("wan_bandwidth").trim());// Mbits/s
																													// to
																													// Kbits/s
			simulationParameters.WAN_PROPAGATION_DELAY = Double
					.parseDouble(prop.getProperty("wan_propogation_delay").trim()); // seconds
			simulationParameters.NETWORK_UPDATE_INTERVAL = Double
					.parseDouble(prop.getProperty("network_update_interval").trim()); // seconds
			simulationParameters.CPU_ALLOCATION_POLICY = prop.getProperty("Applications_CPU_allocation_policy").trim();
			simulationParameters.TASKS_PER_EDGE_DEVICE_PER_MINUTES = Integer
					.parseInt(prop.getProperty("tasks_generation_rate").trim());
			simulationParameters.ORCHESTRATION_ARCHITECTURES = prop.getProperty("orchestration_architectures")
					.split(",");
			simulationParameters.ORCHESTRATION_ALGORITHMS = prop.getProperty("orchestration_algorithms").split(",");
			simulationParameters.DEPLOY_ORCHESTRATOR = prop.getProperty("deploy_orchestrator").trim();

			simulationParameters.CONSUMED_ENERGY_PER_BIT = Double
					.parseDouble(prop.getProperty("consumed_energy_per_bit").trim()); // J/bit
			simulationParameters.AMPLIFIER_DISSIPATION_FREE_SPACE = Double
					.parseDouble(prop.getProperty("amplifier_dissipation_free_space").trim()); // J/bit/m^2
			simulationParameters.AMPLIFIER_DISSIPATION_MULTIPATH = Double
					.parseDouble(prop.getProperty("amplifier_dissipation_multipath").trim()); // J/bit/m^4

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
					result = true;
					SimLog.println("FilesParser- Properties file successfully Loaded propoerties file!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				SimLog.println("FilesParser- Failed to load properties file!");
				result = false;
			}
		}

		return result;

	}

	private boolean checkXmlFiles(String xmlFile, TYPES type) {
		SimLog.println("FilesParser- Checking file: " + xmlFile);

		try {
			File devicesFile = new File(xmlFile);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document xmlDoc = dBuilder.parse(devicesFile);
			xmlDoc.getDocumentElement().normalize();

			NodeList datacenterList = xmlDoc.getElementsByTagName("datacenter");
			for (int i = 0; i < datacenterList.getLength(); i++) {

				Node datacenterNode = datacenterList.item(i);

				Element datacenterElement = (Element) datacenterNode;
				isAttribtuePresent(datacenterElement, "arch");
				isAttribtuePresent(datacenterElement, "os");
				isAttribtuePresent(datacenterElement, "vmm");
				isElementPresent(datacenterElement, "idleConsumption");
				isElementPresent(datacenterElement, "maxConsumption");
				if (type == TYPES.EDGE) {
					isElementPresent(datacenterElement, "mobility");
					isElementPresent(datacenterElement, "battery");
					isElementPresent(datacenterElement, "percentage");
					isElementPresent(datacenterElement, "batterycapacity");
				} else {
					simulationParameters.NUM_OF_FOG_DATACENTERS++;
					Element location = (Element) datacenterElement.getElementsByTagName("location").item(0);
					isElementPresent(location, "x_pos");
					isElementPresent(location, "y_pos");
				}

				NodeList hostList = datacenterElement.getElementsByTagName("host");
				for (int j = 0; j < hostList.getLength(); j++) {
					Node hostNode = hostList.item(j);

					Element hostElement = (Element) hostNode;
					isElementPresent(hostElement, "core");
					isElementPresent(hostElement, "mips");
					isElementPresent(hostElement, "ram");
					isElementPresent(hostElement, "storage");

					NodeList vmList = hostElement.getElementsByTagName("VM");
					for (int k = 0; k < vmList.getLength(); k++) {
						Node vmNode = vmList.item(k);

						Element vmElement = (Element) vmNode;
						isAttribtuePresent(vmElement, "vmm");
						isElementPresent(vmElement, "core");
						isElementPresent(vmElement, "mips");
						isElementPresent(vmElement, "ram");
						isElementPresent(vmElement, "storage");
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			SimLog.println("FilesParser- Failed to load edge devices file!");
			return false;
		}
		SimLog.println("FilesParser- Edge devices XML file successfully Loaded!");
		return true;
	}

	public static ArrayList<DatacenterConfig> getCloudConfig(String appFile) {
		Yaml yaml = new Yaml();
		InputStream input = null;
		try {
			input = new FileInputStream(new File(appFile));
		} catch (FileNotFoundException e) {
			SimLog.println("Error: Could not parse applications file.");
			System.exit(0);
		}
		return yaml.load(input);
	}

	public static ArrayList<Application> getApplications(String appFile) {
		Yaml yaml = new Yaml();
		InputStream input = null;
		try {
			input = new FileInputStream(new File(appFile));
		} catch (FileNotFoundException e) {
			SimLog.println("Error: Could not parse applications file.");
			System.exit(0);
		}
		return yaml.load(input);
	}

	private void isElementPresent(Element element, String key) {
		try {
			String value = element.getElementsByTagName(key).item(0).getTextContent();
			if (value == null || value.isEmpty()) {
				throw new IllegalArgumentException(
						"Element '" + key + "' is not found in '" + element.getNodeName() + "'");
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Element '" + key + "' is not found in '" + element.getNodeName() + "'");
		}
	}

	private void isAttribtuePresent(Element element, String key) {
		String value = element.getAttribute(key);
		if (value == null || value.isEmpty()) {
			throw new IllegalArgumentException(
					"Attribure '" + key + "' is not found in '" + element.getNodeName() + "'");
		}
	} 
	 
}
