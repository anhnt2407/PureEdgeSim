package com.mechalikh.pureedgesim.configs;

import java.util.ArrayList;
import java.util.List;

public class HostConfig {
    private int count = 1;
    private int core;
    private int mips;
    private int ram;
    private int storage;
    private float idleConsumption;
    private float maxConsumption;
    private List<VmConfig> vms = new ArrayList<>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCore() {
        return core;
    }

    public void setCore(int core) {
        this.core = core;
    }

    public int getMips() {
        return mips;
    }

    public void setMips(int mips) {
        this.mips = mips;
    }

    public int getRam() {
        return ram;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    public int getStorage() {
        return storage;
    }

    public void setStorage(int storage) {
        this.storage = storage;
    }

    public float getIdleConsumption() {
        return idleConsumption;
    }

    public void setIdleConsumption(float idleConsumption) {
        this.idleConsumption = idleConsumption;
    }

    public float getMaxConsumption() {
        return maxConsumption;
    }

    public void setMaxConsumption(float maxConsumption) {
        this.maxConsumption = maxConsumption;
    }

    public List<VmConfig> getVms() {
        return vms;
    }

    public void setVms(List<VmConfig> vms) {
        this.vms = vms;
    }
}
