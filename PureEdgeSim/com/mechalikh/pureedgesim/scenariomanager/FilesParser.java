package com.mechalikh.pureedgesim.scenariomanager;

import com.mechalikh.pureedgesim.configs.DatacenterConfig;
import com.mechalikh.pureedgesim.configs.EdgeDatacenterConfig;
import com.mechalikh.pureedgesim.configs.SimulationConfig;
import com.mechalikh.pureedgesim.logging.SimLog;
import com.mechalikh.pureedgesim.tasksgenerator.Application;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.util.ArrayList;

public class FilesParser {

	public static SimulationConfig getSimulationConfig(String simConfigFile) throws FileNotFoundException {
		SimLog.println("FilesParser- Checking simulation properties file");

		if (simulationParameters.MIN_NUM_OF_EDGE_DEVICES > simulationParameters.MAX_NUM_OF_EDGE_DEVICES) {
			SimLog.println("Error: The entered min number of edge devices is higher than the max number.");
			System.exit(0);
		}

		return new Yaml(new Constructor(SimulationConfig.class))
				.load(new FileInputStream(new File(simConfigFile)));
	}

	public static ArrayList<DatacenterConfig> getDatacenterConfig(String appFile) throws FileNotFoundException {
		return new Yaml().load(new FileInputStream(new File(appFile)));
	}

	public static ArrayList<EdgeDatacenterConfig> getEdgeDeviceConfig(String appFile) throws FileNotFoundException {
		return new Yaml().load(new FileInputStream(new File(appFile)));
	}

	public static ArrayList<Application> getApplications(String appFile) throws FileNotFoundException {
		return new Yaml().load(new FileInputStream(new File(appFile)));
	}
	 
}
