package com.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SensorReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "value", nullable = false)
    private Double value;

    @Column(nullable = false, length = 2)
    private String nutrientType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @Column(name = "date", nullable = false)
    private LocalDateTime timestamp;

    public SensorReading() {}

    public void setId(Long id) {
        this.id = id;
    }

    public SensorReading(LocalDateTime timestamp, String nutrientType, double value, Sensor sensor) {
        this.timestamp = timestamp;
        this.nutrientType = nutrientType;
        this.value = value;
        this.sensor = sensor;

    }

    public Long getId() {
        return id;
    }

    public double getValue() {
        return value;
    }

}