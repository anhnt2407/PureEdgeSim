package com.mechalikh.pureedgesim;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.*;

import com.mechalikh.pureedgesim.configs.DatacenterConfig;
import com.mechalikh.pureedgesim.configs.EdgeDatacenterConfig;
import com.mechalikh.pureedgesim.logging.ScenarioLog;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudsimplus.util.Log;
import com.mechalikh.pureedgesim.energy.EnergyModel;
import com.mechalikh.pureedgesim.core.ServersManager;
import com.mechalikh.pureedgesim.mobility.Mobility;
import com.mechalikh.pureedgesim.mobility.DefaultMobilityModel;
import com.mechalikh.pureedgesim.network.DefaultNetworkModel;
import com.mechalikh.pureedgesim.network.NetworkModel;
import com.mechalikh.pureedgesim.scenariomanager.FilesParser;
import com.mechalikh.pureedgesim.scenariomanager.Scenario;
import com.mechalikh.pureedgesim.scenariomanager.simulationParameters;
import com.mechalikh.pureedgesim.logging.ChartsGenerator;
import com.mechalikh.pureedgesim.logging.SimLog;
import com.mechalikh.pureedgesim.core.SimulationManager;
import com.mechalikh.pureedgesim.tasksgenerator.DefaultTasksGenerator;
import com.mechalikh.pureedgesim.tasksgenerator.Task;
import com.mechalikh.pureedgesim.tasksgenerator.TasksGenerator;
import com.mechalikh.pureedgesim.orchestration.TradeOffOrchestrator;
import com.mechalikh.pureedgesim.orchestration.Orchestrator;
import com.mechalikh.pureedgesim.energy.DefaultEnergyModel;

import ch.qos.logback.classic.Level;

public class MainApplication {

	// TODO Temporary solution: All random numbers are generated with this object to assure reproducibility
	public static Random random = new Random(0);

	protected static String outputFolder = "PureEdgeSim/output/";

	protected static ScenarioLog scenarioLog = new ScenarioLog();
	protected static List<Scenario> scenarios = new ArrayList<>();

	protected static Class<? extends Mobility> mobilityManagerClass = DefaultMobilityModel.class;
	protected static Class<? extends TasksGenerator> tasksGeneratorClass = DefaultTasksGenerator.class;
	protected static Class<? extends Orchestrator> orchestratorClass = TradeOffOrchestrator.class;
	protected static Class<? extends EnergyModel> energyModelClass = DefaultEnergyModel.class;
	protected static Class<? extends NetworkModel> networkModelClass = DefaultNetworkModel.class;

	public static void main(String[] args) {
		launchSimulation();
	}

	public static void launchSimulation() {
		launchSimulation("config/default");
	}

	public static void launchSimulation(String configPath) {
		// Disable cloudsim plus log
		Log.setLevel(Level.OFF);

		String simConfigfile = configPath + "/simulation.yaml";
		String applicationsFile = configPath + "/applications.yaml";
		String datacentersFile = configPath + "/datacenters.yaml";
		String edgeDevicesFile = configPath + "/edge_devices.yaml";
		SimLog.println("Main- Loading simulation files...");

		simulationParameters.APPLICATIONS = FilesParser.getApplications(applicationsFile);
		ArrayList<DatacenterConfig> datacenterConfigs = FilesParser.getDatacenterConfig(datacentersFile);
		ArrayList<EdgeDatacenterConfig> edgeDatacenterConfigs = FilesParser.getEdgeDeviceConfig(edgeDevicesFile);
		
		FilesParser.setSimulationProperties(simConfigfile);

		Date startDate = Calendar.getInstance().getTime();

		String startTime = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
		List<SimulationManager> simulationManagers = new ArrayList<>();

		int simulationId = 0;
		int iteration = 0;
		for (String orchestrationAlgorithm : simulationParameters.ORCHESTRATION_ALGORITHMS) {
			for (String orchestrationArchitecture : simulationParameters.ORCHESTRATION_ARCHITECTURES) {
				for (int devicesCount = simulationParameters.MIN_NUM_OF_EDGE_DEVICES;
					 devicesCount <= simulationParameters.MAX_NUM_OF_EDGE_DEVICES;
					 devicesCount += simulationParameters.EDGE_DEVICE_COUNTER_STEP) {

					try {
						scenarios.add(new Scenario(devicesCount, orchestrationAlgorithm, orchestrationArchitecture));

						CloudSim simulation = new CloudSim();
						SimLog simLog = new SimLog(startTime, scenarioLog);

						SimulationManager simulationManager = new SimulationManager(simLog, simulation, simulationId, iteration, scenarios.get(simulationId));
						simulationManagers.add(simulationManager);

						// Generate all data centers, servers, an devices
						ServersManager serversManager = new ServersManager(simulationManager, mobilityManagerClass, energyModelClass);
						serversManager.generateDatacentersAndDevices(datacenterConfigs, edgeDatacenterConfigs);
						simulationManager.setServersManager(serversManager);

						// Generate tasks list
						Constructor<?> TasksGeneratorConstructor = tasksGeneratorClass.getConstructor(SimulationManager.class);
						TasksGenerator tasksGenerator = (TasksGenerator) TasksGeneratorConstructor.newInstance(simulationManager);
						List<Task> tasksList = tasksGenerator.generate();
						simulationManager.setTasksList(tasksList);

						// Initialize the orchestrator
						Constructor<?> OrchestratorConstructor = orchestratorClass.getConstructor(SimulationManager.class);
						Orchestrator edgeOrchestrator = (Orchestrator) OrchestratorConstructor.newInstance(simulationManager);
						simulationManager.setOrchestrator(edgeOrchestrator);

						// Initialize the network model
						Constructor<?> networkConstructor = networkModelClass.getConstructor(SimulationManager.class);
						NetworkModel networkModel = (NetworkModel) networkConstructor.newInstance(simulationManager);
						simulationManager.setNetworkModel(networkModel);
					} catch (Exception e) {
						e.printStackTrace();
						SimLog.println("Main- The simulation has been terminated due to an unexpected error");
					}

					simulationId++;
				}
			}
		}

		try {
			if (simulationParameters.PARALLEL) {
				simulationManagers.parallelStream().forEach(SimulationManager::startSimulation);
			} else {
				simulationManagers.forEach(SimulationManager::startSimulation);
			}
		} catch (Exception e) {
			e.printStackTrace();
			SimLog.println("Main- The simulation has been terminated due to an unexpected error");
		}

		SimLog.println("Main- Simulation Finished!");

		// Generate and save charts
		if (simulationParameters.SAVE_CHARTS && !simulationParameters.PARALLEL) {
			SimLog.println("Main- Saving charts...");
			ChartsGenerator chartsGenerator = new ChartsGenerator(SimLog.getFileName(".csv", startTime, scenarios.size() - 1));
			chartsGenerator.generate();
		}

		// Simulation Finished
		Date endDate = Calendar.getInstance().getTime();
		SimLog.println("Main- Simulation took : " + simulationTime(startDate, endDate));
		SimLog.println("Main- results were saved to the folder: " + outputFolder);
	}

	private static String simulationTime(Date startDate, Date endDate) {
		long difference = endDate.getTime() - startDate.getTime();
		long seconds = difference / 1000 % 60;
		long minutes = difference / (60 * 1000) % 60;
		long hours = difference / (60 * 60 * 1000) % 24;
		long days = difference / (24 * 60 * 60 * 1000);
		String results = "";
		if (days > 0)
			results += days + " days, ";
		if (hours > 0)
			results += hours + " hours, ";
		results += minutes + " minutes, " + seconds + " seconds.";
		return results;
	}

	public static String getOutputFolder() {
		return outputFolder;
	}

	protected static void setTasksGenerator(Class<? extends TasksGenerator> tasksGeneratorClass) {
		MainApplication.tasksGeneratorClass = tasksGeneratorClass;
	}

	protected static void setOrchestrator(Class<? extends Orchestrator> orchestratorClass) {
		MainApplication.orchestratorClass = orchestratorClass;
	}

	protected static void setMobilityModel(Class<? extends Mobility> mobilityManagerClass) {
		MainApplication.mobilityManagerClass = mobilityManagerClass;
	}

	protected static void setEnergyModel(Class<? extends EnergyModel> energyModelClass) {
		MainApplication.energyModelClass = energyModelClass;
	}

}
