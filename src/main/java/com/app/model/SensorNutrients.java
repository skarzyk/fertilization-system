package com.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class SensorNutrients extends Sensor {
    @Column(nullable = false, name = "nitrogen_level")
    private double nitrogenLevel;

    @Column(nullable = false, name = "phosphorus_level")
    private double phosphorusLevel;

    @Column(nullable = false, name = "potassium_level")
    private double potassiumLevel;

    public SensorNutrients() {
        super("unit");
    }

    public SensorNutrients(String unit,
                           double nitrogenLevel,
                           double phosphorusLevel,
                           double potassiumLevel) {
        super(unit);
        this.nitrogenLevel = nitrogenLevel;
        this.phosphorusLevel = phosphorusLevel;
        this.potassiumLevel = potassiumLevel;
    }

    @Override
    public double sensorValue() {
        return (nitrogenLevel + phosphorusLevel + potassiumLevel) / 3.0;
    }

    public double getNitrogenLevel() {
        return nitrogenLevel;
    }
    public void setNitrogenLevel(double nitrogenLevel) {
        this.nitrogenLevel = nitrogenLevel;
    }

    public double getPhosphorusLevel() {
        return phosphorusLevel;
    }
    public void setPhosphorusLevel(double phosphorusLevel) {
        this.phosphorusLevel = phosphorusLevel;
    }

    public double getPotassiumLevel() {
        return potassiumLevel;
    }
    public void setPotassiumLevel(double potassiumLevel) {
        this.potassiumLevel = potassiumLevel;
    }
}