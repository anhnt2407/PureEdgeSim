package com.mechalikh.pureedgesim.tasksgenerator;

public class Application {
    // TODO Rename these properties
    private String name;
    private int maxDelay;  // TODO rename to latency; What is this?
    private int containerSize;
    private int requestSize;
    private int resultsSize;
    private int taskLength;
    private int requiredCore;

    public Application() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxDelay() {
        return maxDelay;
    }

    public void setMaxDelay(int maxDelay) {
        this.maxDelay = maxDelay;
    }

    public int getContainerSize() {
        return containerSize;
    }

    public void setContainerSize(int containerSize) {
        this.containerSize = containerSize;
    }

    public int getRequestSize() {
        return requestSize;
    }

    public void setRequestSize(int requestSize) {
        this.requestSize = requestSize;
    }

    public int getResultsSize() {
        return resultsSize;
    }

    public void setResultsSize(int resultsSize) {
        this.resultsSize = resultsSize;
    }

    public int getTaskLength() {
        return taskLength;
    }

    public void setTaskLength(int taskLength) {
        this.taskLength = taskLength;
    }

    public int getRequiredCore() {
        return requiredCore;
    }

    public void setRequiredCore(int requiredCore) {
        this.requiredCore = requiredCore;
    }
}
