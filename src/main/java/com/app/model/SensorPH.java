package com.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class SensorPH extends Sensor {
    @Column(name = "ph", nullable = false)
    private double ph;

    public SensorPH() {}

    public SensorPH(String unit, double ph) {
        super(unit);
        this.ph = ph;
    }

    @Override
    public double sensorValue() {
        return ph;
    }

}