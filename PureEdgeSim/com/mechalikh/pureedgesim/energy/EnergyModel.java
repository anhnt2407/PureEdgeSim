package com.mechalikh.pureedgesim.energy;

import com.mechalikh.pureedgesim.datacenter.EdgeDataCenter;
import com.mechalikh.pureedgesim.network.FileTransferProgress;
public abstract class EnergyModel {
	
	protected double wirelessEnergyConsumption = 0;

	public abstract void updatewirelessEnergyConsumption(FileTransferProgress file, EdgeDataCenter device1, EdgeDataCenter device2, int flag);

	public double getWirelessEnergyConsumption() {
		return wirelessEnergyConsumption;
	}

}
