package com.mechalikh.pureedgesim;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudsimplus.util.Log;
import com.mechalikh.pureedgesim.datacenter.DefaultEdgeDataCenter;
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
import com.mechalikh.pureedgesim.loadgenerator.DefaultTasksGenerator;
import com.mechalikh.pureedgesim.loadgenerator.Task;
import com.mechalikh.pureedgesim.loadgenerator.TasksGenerator;
import com.mechalikh.pureedgesim.orchestration.TradeOffOrchestrator;
import com.mechalikh.pureedgesim.orchestration.Orchestrator;
import com.mechalikh.pureedgesim.energy.DefaultEnergyModel;
import com.mechalikh.pureedgesim.datacenter.EdgeDataCenter;

import ch.qos.logback.classic.Level;

public class MainApplication {
	protected static String outputFolder = "PureEdgeSim/output/";

	// Parallel simulation Parameters
	protected static List<Scenario> scenarios = new ArrayList<>();
	// TODO Refactor this
	protected static Class<? extends Mobility> mobilityManager = DefaultMobilityModel.class;
	protected static Class<? extends EdgeDataCenter> edgedatacenter = DefaultEdgeDataCenter.class;
	protected static Class<? extends TasksGenerator> tasksGenerator = DefaultTasksGenerator.class;
	protected static Class<? extends Orchestrator> orchestrator = TradeOffOrchestrator.class;
	protected static Class<? extends EnergyModel> energyModel = DefaultEnergyModel.class;
	protected static Class<? extends NetworkModel> networkModel = DefaultNetworkModel.class;

	public static void main(String[] args) {
		launchSimulation();
	}

	public static void launchSimulation() {
		launchSimulation("config/default");
	}

	public static void launchSimulation(String configPath) {
		String simConfigfile = configPath + "/simulation_parameters.properties";
		String applicationsFile = configPath + "/applications.xml";
		String fogDevicesFile = configPath + "/fog_servers.xml";
		String edgeDevicesFile = configPath + "/edge_devices.xml";
		String cloudFile = configPath + "/cloud.xml";
		SimLog.println("Main- Loading simulation files...");

		// Check files
		FilesParser fp = new FilesParser();
		if (!fp.checkFiles(simConfigfile, edgeDevicesFile, fogDevicesFile, applicationsFile, cloudFile))
			Runtime.getRuntime().exit(0); // if files aren't correct stop everything.

		// Disable cloudsim plus log
		Log.setLevel(Level.OFF);

		Date startDate = Calendar.getInstance().getTime();

		// Walk through all orchestration scenarios
		for (int algorithmID = 0; algorithmID < simulationParameters.ORCHESTRATION_AlGORITHMS.length; algorithmID++) {
			// Repeat the operation of the whole set of criteria
			for (int architectureID = 0; architectureID < simulationParameters.ORCHESTRATION_ARCHITECTURES.length; architectureID++) {
				for (int devicesCount = simulationParameters.MIN_NUM_OF_EDGE_DEVICES; devicesCount <= simulationParameters.MAX_NUM_OF_EDGE_DEVICES; devicesCount += simulationParameters.EDGE_DEVICE_COUNTER_STEP) {
					scenarios.add(new Scenario(devicesCount, algorithmID, architectureID));
				}
			}
		}
		if (simulationParameters.PARALLEL) {
			// TODO Reimplement parallel execution
			// List<MainApplication> simulationList = new ArrayList<>(cpuCores);
			// simulationList.parallelStream().forEach(MainApplication::startSimulation);
		} else { // Sequential execution
			new MainApplication().startSimulation();
		}

		// Simulation Finished
		Date endDate = Calendar.getInstance().getTime();
		SimLog.println("Main- Simulation took : " + simulationTime(startDate, endDate));
		SimLog.println("Main- results were saved to the folder: " + outputFolder);
	}

	public void startSimulation() {
		// File name prefix
		String startTime = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
		int iteration = 0;
		boolean isFirstIteration = true;
		SimulationManager simulationManager;
		SimLog simLog = null;
		try { // Repeat the operation for different number of devices
			for (int simulationId = 0; simulationId < scenarios.size(); simulationId++) {
				// New simlog for each simulation (when parallelism is enabled
				simLog = new SimLog(startTime, isFirstIteration);
				if (simulationParameters.CLEAN_OUTPUT_FOLDER && isFirstIteration) {
					simLog.cleanOutputFolder(outputFolder);
				}
				isFirstIteration = false;

				// New simulation instance
				CloudSim simulation = new CloudSim();

				// Initialize the simulation manager
				simulationManager = new SimulationManager(simLog, simulation, simulationId, iteration,
						scenarios.get(simulationId));

				simLog.initialize(simulationManager, scenarios.get(simulationId).getDevicesCount(),
						scenarios.get(simulationId).getOrchAlgorithm(), scenarios.get(simulationId).getOrchArchitecture());

				// Generate all data centers, servers, an devices
				ServersManager serversManager = new ServersManager(simulationManager, mobilityManager, energyModel,
						edgedatacenter);
				serversManager.generateDatacentersAndDevices();
				simulationManager.setServersManager(serversManager);

				// Generate tasks list
				Constructor<?> TasksGeneratorConstructor = tasksGenerator.getConstructor(SimulationManager.class);
				TasksGenerator tasksGenerator = (TasksGenerator) TasksGeneratorConstructor
						.newInstance(simulationManager);
				List<Task> tasksList = tasksGenerator.generate();
				simulationManager.setTasksList(tasksList);

				// Initialize the orchestrator
				Constructor<?> OrchestratorConstructor = orchestrator.getConstructor(SimulationManager.class);
				Orchestrator edgeOrchestrator = (Orchestrator) OrchestratorConstructor.newInstance(simulationManager);
				simulationManager.setOrchestrator(edgeOrchestrator);

				// Initialize the network model
				Constructor<?> networkConstructor = networkModel.getConstructor(SimulationManager.class);
				NetworkModel networkModel = (NetworkModel) networkConstructor.newInstance(simulationManager);
				simulationManager.setNetworkModel(networkModel);

				// Finally launch the simulation
				simulationManager.startSimulation();

				if (!simulationParameters.PARALLEL) {
					// Take a few seconds pause to show the results
					simLog.print(simulationParameters.PAUSE_LENGTH + " seconds peause...");
					for (int k = 1; k <= simulationParameters.PAUSE_LENGTH; k++) {
						simLog.printSameLine(".");
						Thread.sleep(1000);
					}
					SimLog.println("");
				}
				iteration++;
				SimLog.println("");
				SimLog.println("SimLog- Iteration finished...");
				SimLog.println("");
				SimLog.println(
						"######################################################################################################################################################################");

			}
			SimLog.println("Main- Simulation Finished!");
			// Generate and save charts
			generateCharts(simLog);

		} catch (Exception e) {
			e.printStackTrace();
			SimLog.println("Main- The simulation has been terminated due to an unexpected error");
		}
	}

	protected void generateCharts(SimLog simLog) {
		if (simulationParameters.SAVE_CHARTS && !simulationParameters.PARALLEL && simLog != null) {
			SimLog.println("Main- Saving charts...");
			ChartsGenerator chartsGenerator = new ChartsGenerator(simLog.getFileName(".csv"));
			chartsGenerator.generate();
		}
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

	protected static void setCustomEdgeDataCenters(Class<? extends EdgeDataCenter> edgedatacenter2) {
		edgedatacenter = edgedatacenter2;
	}

	protected static void setCustomTasksGenerator(Class<? extends TasksGenerator> tasksGenerator2) {
		tasksGenerator = tasksGenerator2;
	}

	protected static void setCustomEdgeOrchestrator(Class<? extends Orchestrator> orchestrator2) {
		orchestrator = orchestrator2;
	}

	protected static void setCustomMobilityModel(Class<? extends Mobility> mobilityManager2) {
		mobilityManager = mobilityManager2;
	}

	protected static void setCustomEnergyModel(Class<? extends EnergyModel> energyModel2) {
		energyModel = energyModel2;
	}

}
