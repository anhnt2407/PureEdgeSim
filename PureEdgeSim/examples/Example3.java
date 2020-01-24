package examples;

import com.mechalikh.pureedgesim.MainApplication;

public class Example3 extends MainApplication {
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

		// To use the PureEdgeSim default Energy Model you can also uncomment this:
		// setCustomEnergyModel(DefaultEnergyModel.class);

		// Start the simulation
		launchSimulation();
	}

}
