package com.mechalikh.pureedgesim.configs;

import java.util.List;

public class Config {
    private List<DatacenterConfig> datacenters;

    public List<DatacenterConfig> getDatacenters() {
        return datacenters;
    }

    public void setDatacenters(List<DatacenterConfig> datacenters) {
        this.datacenters = datacenters;
    }
}
