package com.mechalikh.pureedgesim.logging;

import com.mechalikh.pureedgesim.scenariomanager.Scenario;

import java.util.ArrayList;
import java.util.List;

public class ScenarioLog {
    private final List<String> resultsList = new ArrayList<>();

    public ScenarioLog() {
        addToResultsList("Orchestration architecture,Orchestration algorithm,Edge devices count,"
                + "Tasks execution delay (s),Average execution delay (s),Tasks waiting time (s),"
                + "Average wainting time (s),Generated tasks,Tasks successfully executed,"
                + "Task not executed (No resources available or long waiting time),Tasks failed (delay),Tasks failed (device dead),"
                + "Tasks failed (mobility),Tasks not generated due to the death of devices,Total tasks executed (Cloud),"
                + "Tasks successfully executed (Cloud),Total tasks executed (Fog),Tasks successfully executed (Fog),"
                + "Total tasks executed (Edge),Tasks successfully executed (Edge),"
                + "Network usage (s),Wan usage (s),Lan usage (s), Total network traffic (MBytes), Containers wan usage (s), Containers lan usage (s),Average bandwidth per task (Mbps),Average VM CPU usage (%),"
                + "Average VM CPU usage (Cloud) (%),Average VM CPU usage (Fog) (%),Average VM CPU usage (Edge) (%),"
                + "Energy consumption (Wh),Average energy consumption (Wh/Data center),Cloud energy consumption (Wh),"
                + "Average Cloud energy consumption (Wh/Data center),Fog energy consumption (Wh),Average Fog energy consumption (Wh/Data center),"
                + "Edge energy consumption (Wh),Average Edge energy consumption (Wh/Device),Dead devices count,"
                + "Average remaining power (Wh),Average remaining power (%), First edge device death time (s),"
                + "List of remaining power (%) (only battery powered devices / 0 = dead),List of the time when each device died (s)");
    }

    public List<String> getResultsList() {
        return resultsList;
    }

    public void addToResultsList(String line) {
        resultsList.add(line);
    }

    public void appendToLastResult(String s) {
        resultsList.set(resultsList.size() - 1, resultsList.get(resultsList.size() - 1) + s);
    }
}
