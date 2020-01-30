package com.mechalikh.pureedgesim.core;

public class Events {
    public static final int Base = 1000; // avoid conflict with CloudSim Plus tags
    public static final int UPDATE_REAL_TIME_CHARTS = Base + 7;
    public static final int SEND_TO_ORCH = Base + 6;
    public static final int RESULT_RETURN_FINISHED = Base + 5;
    public static final int TRANSFER_RESULTS_TO_ORCH = Base + 4;
    public static final int EXECUTE_TASK = Base + 3;
    public static final int SHOW_PROGRESS = Base + 2;
    public static final int PRINT_LOG = Base + 1;
    public static final int SEND_TASK_FROM_ORCH_TO_DESTINATION = Base + 8;
}
