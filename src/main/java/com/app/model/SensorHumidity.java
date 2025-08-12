package com.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class SensorHumidity extends Sensor {

    @Column(name = "humidity", nullable = false)
    private double humidity;

    public SensorHumidity() {}

    public SensorHumidity(String unit, double humidity) {
        super(unit);
        this.humidity = humidity;
    }

    @Override
    public double sensorValue() {
        return humidity;
    }

}