package com.mechalikh.pureedgesim.orchestration;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.vms.Vm;

import com.mechalikh.pureedgesim.datacenter.EdgeDataCenter;
import com.mechalikh.pureedgesim.scenariomanager.simulationParameters;
import com.mechalikh.pureedgesim.scenariomanager.simulationParameters.TYPES;
import com.mechalikh.pureedgesim.logging.SimLog;
import com.mechalikh.pureedgesim.core.SimulationManager;
import com.mechalikh.pureedgesim.tasksgenerator.Task;

public abstract class Orchestrator {
	protected List<List<Integer>> orchestrationHistory;
	protected List<Vm> vmList;
	protected SimulationManager simulationManager;
	protected SimLog simLog;
	protected String architecture;

	public Orchestrator(SimulationManager simulationManager) {
		this.simulationManager = simulationManager;
		simLog = simulationManager.getSimulationLogger();
		orchestrationHistory = new ArrayList<>();
		vmList = simulationManager.getServersManager().getVmList();
		architecture = simulationManager.getScenario().getOrchArchitecture();
		initHistoryList(vmList.size());
	}

	private void initHistoryList(int size) {
		for (int vm = 0; vm < size; vm++) {
			// Creating a list to store the orchestration history for each VM (virtual machine)
			orchestrationHistory.add(new ArrayList<>());
		}
	}

	public void initialize(Task task) {
		if ("CLOUD_ONLY".equals(architecture)) {
			cloudOnly(task);
		} else if ("EDGE_ONLY".equals(architecture)) {
			edgeOnly(task);
		} else if ("FOG_AND_CLOUD".equals(architecture)) {
			fogAndCloud(task);
		} else if ("ALL".equals(architecture)) {
			all(task);
		} else if ("FOG_ONLY".equals(architecture)) {
			fogOnly(task);
		} else if ("EDGE_AND_CLOUD".equals(architecture)) {
			edgeAndCloud(task);
		}
	}

	// If the orchestration scenario is EDGE_ONLY send Tasks only to edge virtual machines (vms)
	private void edgeOnly(Task task) {
		String[] Architecture = { "Edge" };
		sendTask(task, findVM(Architecture, task));
	}

	// If the orchestration scenario is ClOUD_ONLY send Tasks (cloudlets) only to cloud virtual machines (vms)
	private void cloudOnly(Task task) {
		String[] Architecture = { "Cloud" };
		sendTask(task, findVM(Architecture, task));
	}

	// If the orchestration scenario is FOG_AND_CLOUD send Tasks only to fog or cloud virtual machines (vms)
	private void fogAndCloud(Task task) {
		String[] Architecture = { "Cloud", "Fog" };
		sendTask(task, findVM(Architecture, task));
	}

	// If the orchestration scenario is FOG_AND_CLOUD send Tasks only to fog or cloud virtual machines (vms)
	private void edgeAndCloud(Task task) {
		String[] Architecture = { "Cloud", "Edge" };
		sendTask(task, findVM(Architecture, task));
	}

	// If the orchestration scenario is FOG_AND_CLOUD send Tasks only to fog or cloud virtual machines (vms)
	private void fogOnly(Task task) {
		String[] Architecture = { "Fog" };
		sendTask(task, findVM(Architecture, task));
	}

	// If the orchestration scenario is ALL send Tasks (cloudlets) any virtual machine (vm)
	private void all(Task task) {
		String[] Architecture = { "Cloud", "Fog", "Edge" };
		sendTask(task, findVM(Architecture, task));
	}

	protected abstract Vm findVM(String[] architecture, Task task);

	protected void sendTask(Task task, Vm vm) {
		// assign the tasks to the vm found
		assignTaskToVm(vm, task);

		// Offload it only if resources are available (i.e. the offloading destination is available)
		if (task.getVm() != Vm.NULL) // Send the task to execute it
			task.getEdgeDevice().getVmTaskMap().add(new VmTaskMapItem(task.getVm(), task));
	}

	protected void assignTaskToVm(Vm vm, Task task) {
		if (vm == Vm.NULL) {
			simLog.incrementTasksFailedLackOfRessources(task);
		} else {
			task.setVm(vm); // send this task to this vm
			simLog.deepLog(simulationManager.getSimulation().clock() + " : EdgeOrchestrator, Task: " + task.getId()
					+ " assigned to " + ((EdgeDataCenter) vm.getHost().getDatacenter()).getType() + " vm: " + vm.getId());

			// update history
			orchestrationHistory.get(vm.getId()).add(task.getId());
		}
	}

	protected boolean sameLocation(EdgeDataCenter device1, EdgeDataCenter device2, int RANGE) {
		if (device2.getType() == TYPES.CLOUD)
			return true;
		double distance = Math
				.abs(Math.sqrt(Math.pow((device1.getLocation().getXPos() - device2.getLocation().getXPos()), 2)
						+ Math.pow((device1.getLocation().getYPos() - device2.getLocation().getYPos()), 2)));

		return (distance < RANGE);
	}

	protected boolean arrayContains(String[] Architecture, String value) {
		for (String s : Architecture) {
			if (s.equals(value))
				return true;
		}
		return false;
	}

	protected boolean offloadingIsPossible(Task task, Vm edgeVM, String[] architecture) {
		simulationParameters.TYPES vmType = ((EdgeDataCenter)edgeVM.getHost().getDatacenter()).getType();
		return ((arrayContains(architecture, "Cloud") && vmType == simulationParameters.TYPES.CLOUD) // cloud
				|| (arrayContains(architecture, "Fog") && vmType == simulationParameters.TYPES.FOG // fog
				// compare destination (fog host) location and origin (edge) location, if they
				// are in same area offload to his device
						&& (sameLocation(((EdgeDataCenter) edgeVM.getHost().getDatacenter()), task.getEdgeDevice(),
								simulationParameters.FOG_RANGE)
								// or compare the location of their orchestrators
								|| (simulationParameters.ENABLE_ORCHESTRATORS
										&& sameLocation(((EdgeDataCenter) edgeVM.getHost().getDatacenter()),
												task.getOrchestrator(), simulationParameters.FOG_RANGE))))

				|| (arrayContains(architecture, "Edge") && vmType == simulationParameters.TYPES.EDGE // edge
				// compare destination (edge device) location and origin (edge) location, if
				// they are in same area offload to his device
						&& (sameLocation(((EdgeDataCenter) edgeVM.getHost().getDatacenter()), task.getEdgeDevice(),
								simulationParameters.EDGE_RANGE)
								// or compare the location of their orchestrators
								|| (simulationParameters.ENABLE_ORCHESTRATORS
										&& sameLocation(((EdgeDataCenter) edgeVM.getHost().getDatacenter()),
												task.getOrchestrator(), simulationParameters.EDGE_RANGE))
										&& ((EdgeDataCenter) edgeVM.getHost().getDatacenter()).isDead())));
	}

	// TODO This is implemented nowhere
	public abstract void resultsReturned(Task task);

}
