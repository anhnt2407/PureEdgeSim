package examples;
 
import com.mechalikh.pureedgesim.MainApplication; 

public class Example2 extends MainApplication {
 /**
  * This is a simple example showing how to launch simulation using a custom mobility model.
  * by removing it, pureEdgeSim will use the default model.
  * As you can see, this class extends the Main class provided by PureEdgeSim, which is required  for this example to work
  */

	public static void main(String[] args) { 
		/* To use your custom mobility model, do this:
		The custom mobility manager class can be found in the examples folder as well.
		 by removing this line, pureEdgeSim will use the default mobility model.  
		*/
		setMobilityModel(CustomMobilityManager.class);

		// To use the PureEdgeSim default Mobility Manager you can also uncomment this: 
		// setCustomMobilityModel(MobilityManager.class);  
		
		// Start the simulation
		launchSimulation();
	} 
 
}
