package com.app.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Variety {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @OneToOne(mappedBy = "variety", cascade = CascadeType.ALL)
    private Fruit fruit;

    @ManyToOne
    @JoinColumn(name = "plot_id", nullable = false)
    private Plot plot;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "fertilization_plan_id", unique = true)
    private FertilizationPlan fertilizationPlan;

    @OneToMany(mappedBy = "variety", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seedling> seedlings;

    public Variety() {}

    public String getName() {
        return name;
    }

    public FertilizationPlan getFertilizationPlan() {
        return fertilizationPlan;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFertilizationPlan(FertilizationPlan fertilizationPlan) {
        this.fertilizationPlan = fertilizationPlan;
    }

    public void setFruit(Fruit fruit) {
        this.fruit = fruit;
    }

    public void setSeedlings(List<Seedling> seedlings) {
        this.seedlings = seedlings;
    }

    public void setPlot(Plot plot) {
        this.plot = plot;
    }
}