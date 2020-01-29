package com.mechalikh.pureedgesim.scenariomanager;

import com.mechalikh.pureedgesim.configs.DatacenterConfig;
import com.mechalikh.pureedgesim.configs.EdgeDatacenterConfig;
import com.mechalikh.pureedgesim.configs.SimulationConfig;
import com.mechalikh.pureedgesim.logging.SimLog;
import com.mechalikh.pureedgesim.tasksgenerator.Application;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.util.ArrayList;

public class FilesParser {

	public static void setSimulationProperties(String simConfigFile) {
		SimLog.println("FilesParser- Checking simulation properties file");

		if (simulationParameters.MIN_NUM_OF_EDGE_DEVICES > simulationParameters.MAX_NUM_OF_EDGE_DEVICES) {
			SimLog.println("Error: The entered min number of edge devices is higher than the max number.");
			System.exit(0);
		}

		Yaml yaml = new Yaml(new Constructor(SimulationConfig.class));
		try (InputStream input = new FileInputStream(new File(simConfigFile))) {
			SimulationConfig config = yaml.load(input);

			simulationParameters.INITIALIZATION_TIME = config.getInitializationTime(); // seconds
			simulationParameters.PARALLEL = config.isParallelSimulation();
			simulationParameters.SIMULATION_TIME = simulationParameters.INITIALIZATION_TIME + config.getSimulationTime(); // seconds
			simulationParameters.UPDATE_INTERVAL = config.getUpdateInterval(); // seconds

			simulationParameters.DISPLAY_REAL_TIME_CHARTS = config.isDisplayRealTimeCharts();
			simulationParameters.AUTO_CLOSE_REAL_TIME_CHARTS = config.isAutoCloseRealTimeCharts();
			simulationParameters.CHARTS_UPDATE_INTERVAL = config.getChartsUpdateInterval();
			simulationParameters.SAVE_CHARTS = config.isSaveCharts();

			simulationParameters.AREA_HEIGHT = config.getHeight(); // meters
			simulationParameters.AREA_WIDTH = config.getWidth(); // meters

			// Edge devices, server,datacenters..
			simulationParameters.MIN_NUM_OF_EDGE_DEVICES = config.getMinNumberOfEdgeDevices();
			simulationParameters.MAX_NUM_OF_EDGE_DEVICES = config.getMaxNumberOfEdgeDevices();
			simulationParameters.EDGE_DEVICE_COUNTER_STEP = config.getEdgeDeviceCounterSize();
			simulationParameters.SPEED = config.getSpeed(); // meters per second m/s

			// Simulation logger parameters
			simulationParameters.DEEP_LOGGING = config.isDeepLogEnabled();
			simulationParameters.SAVE_LOG = config.isSaveLogFile();

			// Network parameters
			simulationParameters.BANDWIDTH_WLAN = 1000 * config.getWlanBandwidth(); // Mbits/s
			simulationParameters.WAN_BANDWIDTH = 1000 * config.getWanBandwidth();// Mbits/s
			simulationParameters.EDGE_RANGE = config.getEdgeRange(); // meters
			simulationParameters.FOG_RANGE = config.getFogCoverage(); // meters
			simulationParameters.NETWORK_UPDATE_INTERVAL = config.getNetworkUpdateInterval(); // seconds
			simulationParameters.WAN_PROPAGATION_DELAY = config.getWanPropogationDelay(); // seconds

			// Energy model parameters
			simulationParameters.AMPLIFIER_DISSIPATION_FREE_SPACE = config.getAmplifierDissipationFreeSpace(); // J/bit/m^2
			simulationParameters.AMPLIFIER_DISSIPATION_MULTIPATH = config.getAmplifierDissipationMultipath(); // J/bit/m^4
			simulationParameters.CONSUMED_ENERGY_PER_BIT = config.getConsumedEnergyPerBit(); // J/bit

			// Tasks orchestration parameters
			simulationParameters.ENABLE_ORCHESTRATORS = config.isEnableOrchestrators();
			simulationParameters.TASKS_PER_EDGE_DEVICE_PER_MINUTES = config.getTasksGenerationRate();
			simulationParameters.ORCHESTRATION_ALGORITHMS = config.getOrchestrationAlgorithms().split(",");
			simulationParameters.ORCHESTRATION_ARCHITECTURES = config.getOrchestrationArchitectures().split(",");
			simulationParameters.ENABLE_REGISTRY = config.isEnableRegistry();
			simulationParameters.REGISTRY_MODE = config.getRegistryMode();
			simulationParameters.CPU_ALLOCATION_POLICY = config.getApplicationCpuAllocationPolicy();
			simulationParameters.DEPLOY_ORCHESTRATOR = config.getDeployOrchestrator();
			simulationParameters.WAIT_FOR_TASKS = config.isWaitForAllTasks();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static ArrayList<DatacenterConfig> getDatacenterConfig(String appFile) {
		Yaml yaml = new Yaml();
		InputStream input = null;
		try {
			input = new FileInputStream(new File(appFile));
		} catch (FileNotFoundException e) {
			SimLog.println("Error: Could not parse datacenter file.");
			System.exit(0);
		}
		return yaml.load(input);
	}

	public static ArrayList<EdgeDatacenterConfig> getEdgeDeviceConfig(String appFile) {
		Yaml yaml = new Yaml();
		try (InputStream input = new FileInputStream(new File(appFile))) {
			return yaml.load(input);
		} catch (IOException e) {
			SimLog.println("Error: Could not parse edge device file.");
			return new ArrayList<>();
		}
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
	 
}
