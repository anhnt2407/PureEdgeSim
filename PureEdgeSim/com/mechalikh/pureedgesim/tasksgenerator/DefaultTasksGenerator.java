package com.mechalikh.pureedgesim.tasksgenerator;

import java.util.List;

import com.mechalikh.pureedgesim.MainApplication;
import com.mechalikh.pureedgesim.datacenter.EdgeDataCenter;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

import com.mechalikh.pureedgesim.scenariomanager.simulationParameters;
import com.mechalikh.pureedgesim.core.SimulationManager;

public class DefaultTasksGenerator extends TasksGenerator {
	public DefaultTasksGenerator(SimulationManager simulationManager) {
		super(simulationManager);
	}

	public List<Task> generate() {
		double simulationTime = simulationParameters.SIMULATION_TIME / 60; // in minutes
		for (EdgeDataCenter dc : edgeDataCenters) { // for each device
			Application app = simulationParameters.APPLICATIONS.get(MainApplication.random.nextInt(simulationParameters.APPLICATIONS.size()));
			dc.setApplication(app); // assign this application to that device
			for (int st = 0; st < simulationTime; st++) { // for each minute
				// generating tasks
				int time = st * 60;
				time += MainApplication.random.nextInt(59);// pickup random second in this minute "st";

				// Shift the time by the defined value "INITIALIZATION_TIME"
				// in order to start after generating all the resources
				time += simulationParameters.INITIALIZATION_TIME;
				insert(time, app, dc);
			}
		}
		return this.getTaskList();
	}

	private void insert(int time, Application app, EdgeDataCenter dc) {
		Task[] task = new Task[simulationParameters.TASKS_PER_EDGE_DEVICE_PER_MINUTES];

		// generate tasks for every edge device
		for (int i = 0; i < simulationParameters.TASKS_PER_EDGE_DEVICE_PER_MINUTES; i++) {
			int id = taskList.size();
			UtilizationModel utilizationModel = new UtilizationModelFull();
			task[i] = new Task(id, app.getTaskLength(), app.getRequiredCore());
			task[i].setFileSize(app.getRequestSize()).setOutputSize(app.getResultsSize()).setUtilizationModel(utilizationModel);
			task[i].setTime(time);
			task[i].setContainerSize(app.getContainerSize());
			task[i].setMaxLatency(app.getMaxDelay());
			task[i].setEdgeDevice(dc); // the device that generate this task (the origin)
			taskList.add(task[i]);
			getSimulationManager().getSimulationLogger()
					.deepLog("BasicTasksGenerator, Task " + id + " with execution time " + time + " (s) generated.");
		}
	}

}
