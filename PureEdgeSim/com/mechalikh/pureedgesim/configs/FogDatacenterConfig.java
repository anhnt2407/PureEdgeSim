package com.mechalikh.pureedgesim.configs;

public class FogDatacenterConfig extends DatacenterConfig {

    // TODO Why ints????
    private int locationX;
    private int locationY;

    public int getLocationX() {
        return locationX;
    }

    public void setLocationX(int locationX) {
        this.locationX = locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public void setLocationY(int locationY) {
        this.locationY = locationY;
    }
}
