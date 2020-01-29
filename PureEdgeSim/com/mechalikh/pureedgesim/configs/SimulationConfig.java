package com.mechalikh.pureedgesim.configs;

public class SimulationConfig {
    private int simulationTime;
    private int initializationTime;
    private boolean parallelSimulation;
    private float updateInterval;

    private boolean displayRealTimeCharts;
    private boolean autoCloseRealTimeCharts;
    private float chartsUpdateInterval;
    private boolean saveCharts;

    private int height;
    private int width;
    private int edgeRange;
    private int fogCoverage;

    private boolean enableRegistry;
    private String registryMode;
    private boolean enableOrchestrators;
    private String deployOrchestrator;
    private boolean waitForAllTasks;

    private String applicationCpuAllocationPolicy;
    private boolean saveLogFile;
    private boolean deepLogEnabled;

    private int minNumberOfEdgeDevices;
    private int maxNumberOfEdgeDevices;
    private int edgeDeviceCounterSize;

    private int tasksGenerationRate;

    private float speed;
    private int wlanBandwidth;  // TODO Should be float
    private int wanBandwidth;  // TODO Should be float
    private float wanPropogationDelay;
    private float networkUpdateInterval;

    private float consumedEnergyPerBit;
    private float amplifierDissipationFreeSpace;
    private float amplifierDissipationMultipath;

    private String orchestrationArchitectures;
    private String orchestrationAlgorithms;

    public int getSimulationTime() {
        return simulationTime;
    }

    public void setSimulationTime(int simulationTime) {
        this.simulationTime = simulationTime;
    }

    public int getInitializationTime() {
        return initializationTime;
    }

    public void setInitializationTime(int initializationTime) {
        this.initializationTime = initializationTime;
    }

    public boolean isParallelSimulation() {
        return parallelSimulation;
    }

    public void setParallelSimulation(boolean parallelSimulation) {
        this.parallelSimulation = parallelSimulation;
    }

    public float getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(float updateInterval) {
        this.updateInterval = updateInterval;
    }

    public boolean isDisplayRealTimeCharts() {
        return displayRealTimeCharts;
    }

    public void setDisplayRealTimeCharts(boolean displayRealTimeCharts) {
        this.displayRealTimeCharts = displayRealTimeCharts;
    }

    public boolean isAutoCloseRealTimeCharts() {
        return autoCloseRealTimeCharts;
    }

    public void setAutoCloseRealTimeCharts(boolean autoCloseRealTimeCharts) {
        this.autoCloseRealTimeCharts = autoCloseRealTimeCharts;
    }

    public float getChartsUpdateInterval() {
        return chartsUpdateInterval;
    }

    public void setChartsUpdateInterval(float chartsUpdateInterval) {
        this.chartsUpdateInterval = chartsUpdateInterval;
    }

    public boolean isSaveCharts() {
        return saveCharts;
    }

    public void setSaveCharts(boolean saveCharts) {
        this.saveCharts = saveCharts;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getEdgeRange() {
        return edgeRange;
    }

    public void setEdgeRange(int edgeRange) {
        this.edgeRange = edgeRange;
    }

    public int getFogCoverage() {
        return fogCoverage;
    }

    public void setFogCoverage(int fogCoverage) {
        this.fogCoverage = fogCoverage;
    }

    public boolean isEnableRegistry() {
        return enableRegistry;
    }

    public void setEnableRegistry(boolean enableRegistry) {
        this.enableRegistry = enableRegistry;
    }

    public String getRegistryMode() {
        return registryMode;
    }

    public void setRegistryMode(String registryMode) {
        this.registryMode = registryMode;
    }

    public boolean isEnableOrchestrators() {
        return enableOrchestrators;
    }

    public void setEnableOrchestrators(boolean enableOrchestrators) {
        this.enableOrchestrators = enableOrchestrators;
    }

    public String getDeployOrchestrator() {
        return deployOrchestrator;
    }

    public void setDeployOrchestrator(String deployOrchestrator) {
        this.deployOrchestrator = deployOrchestrator;
    }

    public boolean isWaitForAllTasks() {
        return waitForAllTasks;
    }

    public void setWaitForAllTasks(boolean waitForAllTasks) {
        this.waitForAllTasks = waitForAllTasks;
    }

    public String getApplicationCpuAllocationPolicy() {
        return applicationCpuAllocationPolicy;
    }

    public void setApplicationCpuAllocationPolicy(String applicationCpuAllocationPolicy) {
        this.applicationCpuAllocationPolicy = applicationCpuAllocationPolicy;
    }

    public boolean isSaveLogFile() {
        return saveLogFile;
    }

    public void setSaveLogFile(boolean saveLogFile) {
        this.saveLogFile = saveLogFile;
    }

    public boolean isDeepLogEnabled() {
        return deepLogEnabled;
    }

    public void setDeepLogEnabled(boolean deepLogEnabled) {
        this.deepLogEnabled = deepLogEnabled;
    }

    public int getMinNumberOfEdgeDevices() {
        return minNumberOfEdgeDevices;
    }

    public void setMinNumberOfEdgeDevices(int minNumberOfEdgeDevices) {
        this.minNumberOfEdgeDevices = minNumberOfEdgeDevices;
    }

    public int getMaxNumberOfEdgeDevices() {
        return maxNumberOfEdgeDevices;
    }

    public void setMaxNumberOfEdgeDevices(int maxNumberOfEdgeDevices) {
        this.maxNumberOfEdgeDevices = maxNumberOfEdgeDevices;
    }

    public int getEdgeDeviceCounterSize() {
        return edgeDeviceCounterSize;
    }

    public void setEdgeDeviceCounterSize(int edgeDeviceCounterSize) {
        this.edgeDeviceCounterSize = edgeDeviceCounterSize;
    }

    public int getTasksGenerationRate() {
        return tasksGenerationRate;
    }

    public void setTasksGenerationRate(int tasksGenerationRate) {
        this.tasksGenerationRate = tasksGenerationRate;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getWlanBandwidth() {
        return wlanBandwidth;
    }

    public void setWlanBandwidth(int wlanBandwidth) {
        this.wlanBandwidth = wlanBandwidth;
    }

    public int getWanBandwidth() {
        return wanBandwidth;
    }

    public void setWanBandwidth(int wanBandwidth) {
        this.wanBandwidth = wanBandwidth;
    }

    public float getWanPropogationDelay() {
        return wanPropogationDelay;
    }

    public void setWanPropogationDelay(float wanPropogationDelay) {
        this.wanPropogationDelay = wanPropogationDelay;
    }

    public float getNetworkUpdateInterval() {
        return networkUpdateInterval;
    }

    public void setNetworkUpdateInterval(float networkUpdateInterval) {
        this.networkUpdateInterval = networkUpdateInterval;
    }

    public float getConsumedEnergyPerBit() {
        return consumedEnergyPerBit;
    }

    public void setConsumedEnergyPerBit(float consumedEnergyPerBit) {
        this.consumedEnergyPerBit = consumedEnergyPerBit;
    }

    public float getAmplifierDissipationFreeSpace() {
        return amplifierDissipationFreeSpace;
    }

    public void setAmplifierDissipationFreeSpace(float amplifierDissipationFreeSpace) {
        this.amplifierDissipationFreeSpace = amplifierDissipationFreeSpace;
    }

    public float getAmplifierDissipationMultipath() {
        return amplifierDissipationMultipath;
    }

    public void setAmplifierDissipationMultipath(float amplifierDissipationMultipath) {
        this.amplifierDissipationMultipath = amplifierDissipationMultipath;
    }

    public String getOrchestrationArchitectures() {
        return orchestrationArchitectures;
    }

    public void setOrchestrationArchitectures(String orchestrationArchitectures) {
        this.orchestrationArchitectures = orchestrationArchitectures;
    }

    public String getOrchestrationAlgorithms() {
        return orchestrationAlgorithms;
    }

    public void setOrchestrationAlgorithms(String orchestrationAlgorithms) {
        this.orchestrationAlgorithms = orchestrationAlgorithms;
    }
}
