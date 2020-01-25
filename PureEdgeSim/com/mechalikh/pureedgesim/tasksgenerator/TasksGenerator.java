package com.mechalikh.pureedgesim.tasksgenerator;

import java.util.ArrayList;
import java.util.List;
import com.mechalikh.pureedgesim.datacenter.EdgeDataCenter;
import com.mechalikh.pureedgesim.core.SimulationManager;

public abstract class TasksGenerator {
	protected List<Task> taskList;
	protected List<? extends EdgeDataCenter> datacentersList;
	private SimulationManager simulationManager;

	public TasksGenerator(SimulationManager simulationManager) {
		taskList = new ArrayList<>();
		this.setSimulationManager(simulationManager);
		this.datacentersList = this.getSimulationManager().getServersManager().getDatacenterList();
	}

	public List<Task> getTaskList() {
		return taskList;
	}

	public SimulationManager getSimulationManager() {
		return simulationManager;
	}

	public void setSimulationManager(SimulationManager simulationManager) {
		this.simulationManager = simulationManager;
	}

	public abstract List<Task> generate();
}
