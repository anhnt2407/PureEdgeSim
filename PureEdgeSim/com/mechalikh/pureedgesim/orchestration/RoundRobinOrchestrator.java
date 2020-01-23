package com.mechalikh.pureedgesim.orchestration;

import com.mechalikh.pureedgesim.core.SimulationManager;
import com.mechalikh.pureedgesim.datacenter.EdgeDataCenter;
import com.mechalikh.pureedgesim.loadgenerator.Task;
import com.mechalikh.pureedgesim.logging.SimLog;
import com.mechalikh.pureedgesim.scenariomanager.simulationParameters;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

public class RoundRobinOrchestrator extends Orchestrator {
	public RoundRobinOrchestrator(SimulationManager simulationManager) {
		super(simulationManager);
	}

	protected Vm findVM(String[] architecture, Task task) {
		// TODO THis seems buggy
		List<Vm> vmList = simulationManager.getServersManager().getVmList();
		Vm vm = Vm.NULL;
		int minTasksCount = -1; // vm with minimum assigned tasks;
		// get best vm for this task
		for (int i = 0; i < orchestrationHistory.size(); i++) {
			if (offloadingIsPossible(task, vmList.get(i), architecture)) {
				if (minTasksCount == -1) {
					minTasksCount = orchestrationHistory.get(i).size();
					// if this is the first time, set the first vm as the best one
					vm = vmList.get(i);
				} else if (minTasksCount > orchestrationHistory.get(i).size()) {
					minTasksCount = orchestrationHistory.get(i).size();
					// new min found, so we choose it as the best VM
					return vmList.get(i);
					// TODO This break seems to be a bug?
					//  break;
				}
			}
		}
		return vm;
	}

	@Override
	public void resultsReturned(Task task) {

	}

}
