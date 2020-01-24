package examples;

import com.mechalikh.pureedgesim.MainApplication;
import com.mechalikh.pureedgesim.core.SimulationManager;
import com.mechalikh.pureedgesim.tasksgenerator.DefaultTasksGenerator;
import com.mechalikh.pureedgesim.orchestration.IncreaseLifetimeOrchestrator;
import com.mechalikh.pureedgesim.orchestration.Orchestrator;

public class Example1 extends MainApplication {
	/**
	 * This is a simple example showing how to launch simulation using custom
	 * mobility model, energy model, custom edge orchestrator, custom tasks
	 * generator, and custom edge devices. By removing them, you will use the
	 * default models provided by PureEdgeSim. As you can see, this class extends
	 * the Main class provided by PureEdgeSim, which is required for this example to
	 * work.
	 */

	public static void main(String[] args) {
		// To change the mobility model
		setMobilityModel(CustomMobilityManager.class);

		// To change the tasks generator
		setTasksGenerator(DefaultTasksGenerator.class);

		// To use a custom energy model
		setEnergyModel(CustomEnergyModel.class);

		/* to use the default one you can simply delete or comment those lines */
		setOrchestrator(IncreaseLifetimeOrchestrator.class);

		// Finally,you can launch the simulation
		launchSimulation();
	}

}
