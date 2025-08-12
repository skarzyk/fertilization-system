package com.app.model;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seedling_id")
    private Seedling seedling;

    protected Sensor() {}

    public Sensor(String unit) {
        this.unit = unit;
    }

    public Long getId() {
        return id;
    }

    public void setSeedling(Seedling seedling) {
        this.seedling = seedling;
    }

    public abstract double sensorValue();
}