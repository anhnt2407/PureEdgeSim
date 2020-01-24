package examples;

import com.mechalikh.pureedgesim.MainApplication;
import com.mechalikh.pureedgesim.orchestration.IncreaseLifetimeOrchestrator;

public class CustomExample extends MainApplication {
	/**
	 * This is a simple example showing how to launch simulation using a custom
	 * energy model. by removing it, pureEdgeSim will use the default model. As you
	 * can see, this class extends the Main class provided by PureEdgeSim, which is
	 * required for this example to work
	 */

	public static void main(String[] args) {
		/*
		 * To use your custom Energy model, do this: The custom energy model class can
		 * be found in the examples folder as well. by removing this line, pureEdgeSim
		 * will use the default energy model. *
		 */
		setEnergyModel(CustomEnergyModel.class);

		/* To use your custom mobility model, do this:
		The custom mobility manager class can be found in the examples folder as well.
		 by removing this line, pureEdgeSim will use the default mobility model.
		*/
		setMobilityModel(CustomMobilityManager.class);

		/*
		 * This custom class uses another orchestrator algorithm called
		 * Increase_Lifetime, that avoids offloading the tasks to battery-powered
		 * devices. This algorithm wotks better when you use the ALL architecture you
		 * can compare its performance to the Round-Robin and Trade-off algorithms used
		 * by the default orchestrator class, as this algorihtm relies more on the cloud
		 * and the fog. You can use your own algorithm by adding it to your custom
		 * class. After adding it to the orchestrator class,to use it you need to add it
		 * to the simulation parameters file (under the settings/ folder). To use the
		 * PureEdgeSim default edge orchestrator class you can also uncomment this:
		 */
		setOrchestrator(IncreaseLifetimeOrchestrator.class);

		// Start the simulation
		launchSimulation();
	}

}
