package com.mechalikh.pureedgesim.orchestration;

import com.mechalikh.pureedgesim.datacenter.EdgeDataCenter;
import com.mechalikh.pureedgesim.core.SimulationManager;
import com.mechalikh.pureedgesim.tasksgenerator.Task;
import org.cloudbus.cloudsim.vms.Vm;

public class IncreaseLifetimeOrchestrator extends Orchestrator {
	public IncreaseLifetimeOrchestrator(SimulationManager simulationManager) {
		super(simulationManager);
	}

	protected Vm findVM(String[] architecture, Task task) {
		Vm vm = Vm.NULL;
		double minTasksCount = -1; // vm with minimum assigned tasks;
		double vmMips = 0;
		double weight = 0;
		double minWeight = 20;
		// get best vm for this task
		for (int i = 0; i < orchestrationHistory.size(); i++) {
			if (offloadingIsPossible(task, vmList.get(i), architecture)) {
				if (((EdgeDataCenter) vmList.get(i).getHost().getDatacenter()).isBattery()) {
					if (task.getEdgeDevice()
							.getBatteryLevel() > ((EdgeDataCenter) vmList.get(i).getHost().getDatacenter())
									.getBatteryLevel())
						weight = 20; // the destination device has lower remaining power than the task offloading
										// device,in this case it is better not to offload
										// that's why the weight is high (20)
					else
						weight = 15; // in this case the destination has higher remaining power, so it is okey to
										// offload tasks for it, if the cloud and the fog are absent.
				} else {
					weight = 1;  // if it is not battery powered
				}

				if (minTasksCount == 0) {
					minTasksCount = 1; // avoid devision by 0
				}

				if (minTasksCount == -1) { // if it is the first iteration
					minTasksCount = orchestrationHistory.get(i).size()
							- vmList.get(i).getCloudletScheduler().getCloudletFinishedList().size() + 1;
					// if this is the first time, set the first vm as the
					vm = vmList.get(i); // best one
					vmMips = vmList.get(i).getMips();
					minWeight = weight;
				} else if (vmMips / (minTasksCount * minWeight) < vmList.get(i).getMips()
						/ ((orchestrationHistory.get(i).size()
								- vmList.get(i).getCloudletScheduler().getCloudletFinishedList().size() + 1)
								* weight)) {
					// if this vm has more cpu mips and less waiting tasks
					minWeight = weight;
					vmMips = vmList.get(i).getMips();
					minTasksCount = orchestrationHistory.get(i).size()
							- vmList.get(i).getCloudletScheduler().getCloudletFinishedList().size() + 1;
					vm = vmList.get(i);
				}
			}
		}
		// assign the tasks to the vm found
		return vm;
	}

	@Override
	public void resultsReturned(Task task) {
		// TODO Auto-generated method stub
	}

}
