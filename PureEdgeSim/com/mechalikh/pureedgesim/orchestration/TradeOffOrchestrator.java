package com.mechalikh.pureedgesim.orchestration;

import java.util.List;

import org.cloudbus.cloudsim.vms.Vm;
import com.mechalikh.pureedgesim.datacenter.EdgeDataCenter;
import com.mechalikh.pureedgesim.scenariomanager.simulationParameters;
import com.mechalikh.pureedgesim.logging.SimLog;
import com.mechalikh.pureedgesim.core.SimulationManager;
import com.mechalikh.pureedgesim.loadgenerator.Task;

public class TradeOffOrchestrator extends Orchestrator {
	public TradeOffOrchestrator(SimulationManager simulationManager) {
		super(simulationManager);
	}

	protected Vm findVM(String[] architecture, Task task) {
		Vm vm = Vm.NULL;
		double min = -1;
		double new_min;// vm with minimum assigned tasks;

		// get best vm for this task
		for (int i = 0; i < orchestrationHistory.size(); i++) {
			if (offloadingIsPossible(task, vmList.get(i), architecture)) {
				double latency = 1;
				double energy = 1;
				if (((EdgeDataCenter) vmList.get(i).getHost().getDatacenter()).getType() == simulationParameters.TYPES.CLOUD) {
					latency = 1.6;
					energy = 1.1;
				} else if (((EdgeDataCenter) vmList.get(i).getHost().getDatacenter()).getType() == simulationParameters.TYPES.EDGE) {
					energy = 1.4;
				}
				new_min = (orchestrationHistory.get(i).size() + 1) * latency * energy / vmList.get(i).getMips();
				if (min == -1) { // if it is the first iteration
					min = new_min;
					// if this is the first time, set the first vm as the
					vm = vmList.get(i); // best one
				} else if (min > new_min) { // if this vm has more cpu mips and less waiting tasks
					// idle vm, no tasks are waiting
					min = new_min;
					vm = vmList.get(i);
				}
			}
		}
		// assign the tasks to the found vm
		return vm;
	}

	@Override
	public void resultsReturned(Task task) {

	}

}
