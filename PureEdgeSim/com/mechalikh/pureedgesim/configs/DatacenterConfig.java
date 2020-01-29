package com.mechalikh.pureedgesim.configs;

import java.util.List;

public class DatacenterConfig {
    private String arch = "x86";
    private String os = "Linux";
    private String vmm = "Xen";
    private boolean isOrchestrator = false;
    private List<HostConfig> hosts;

    public String getArch() {
        return arch;
    }

    public void setArch(String arch) {
        this.arch = arch;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getVmm() {
        return vmm;
    }

    public void setVmm(String vmm) {
        this.vmm = vmm;
    }

    public boolean isOrchestrator() {
        return isOrchestrator;
    }

    public void setOrchestrator(boolean orchestrator) {
        isOrchestrator = orchestrator;
    }

    public List<HostConfig> getHosts() {
        return hosts;
    }

    public void setHosts(List<HostConfig> hosts) {
        this.hosts = hosts;
    }
}
