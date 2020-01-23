package com.mechalikh.pureedgesim.orchestration;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.events.SimEvent;

import com.mechalikh.pureedgesim.core.SimulationManager;
import com.mechalikh.pureedgesim.loadgenerator.Task;

public class EdgeBroker extends DatacenterBrokerSimple {

	private SimulationManager simulationManager;

	public EdgeBroker(CloudSim simulation) {
		super(simulation);
	}

	@Override
	public void processEvent(final SimEvent ev) {
		super.processEvent(ev);
		switch (ev.getTag()) {
			case CloudSimTags.CLOUDLET_RETURN: // the task execution finished
				final Task task = (Task) ev.getData();
				scheduleNow(simulationManager, SimulationManager.TRANSFER_RESULTS_TO_ORCH, task);
				break;
			default:
				break;
		}
	}

	public void setSimulationManager(SimulationManager simulationManager) {
		this.simulationManager = simulationManager;

	}

}
