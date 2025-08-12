package com.app.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Seedling {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plot_id")
    private Plot plot;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "dispenser_id", unique = true)
    private Dispenser dispenser;

    @ManyToOne
    @JoinColumn(name = "variety_id")
    private Variety variety;

    @OneToMany(mappedBy = "seedling", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sensor> sensors;

    public Seedling() {
        this.sensors = new ArrayList<>();
    }

    public Seedling( Plot plot, Dispenser dispenser, Variety variety, List<Sensor> sensors ) {
        this.plot = plot;
        this.dispenser = dispenser;
        this.variety = variety;
        this.sensors = new ArrayList<>();
    }

    public void setPlot(Plot plot) {
        this.plot = plot;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public Variety getVariety() {
        return variety;
    }

    public Dispenser getDispenser() {
        return dispenser;
    }

    public void setVariety(Variety variety) {
        this.variety = variety;
    }

    public Plot getPlot() {
        return plot;
    }

    public Long getId() {
        return id;
    }

    public void setDispenser(Dispenser dispenser) {
        this.dispenser = dispenser;
    }

}