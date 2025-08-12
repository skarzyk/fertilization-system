package com.app.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class FertilizationPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variety_id", nullable = false)
    private Variety variety;

    @OneToMany
    @JoinColumn(name = "fertilization_plan_id")
    private List<Fertilizer> fertilizerTypes;

    @Column(name = "required_moisture", nullable = false)
    private double requiredMoisture;

    @Column(name = "required_ph", nullable = false)
    private double requiredPh;

    @Column(name = "required_nitrogen", nullable = false)
    private double requiredNitrogen;
    @Column(name = "required_phosphorus", nullable = false)
    private double requiredPhosphorus;
    @Column(name = "required_potassium", nullable = false)
    private double requiredPotassium;


    @ManyToMany(mappedBy = "fertilizationPlans")
    private List<Dispenser> dispensers = new ArrayList<>();

    public FertilizationPlan() {}

    public void setId(Long id) {
        this.id = id;
    }

    public void setFertilizerTypes(List<Fertilizer> fertilizerTypes) {
        this.fertilizerTypes = fertilizerTypes;
    }

    public void setRequiredNitrogen(double requiredNitrogen) {
        this.requiredNitrogen = requiredNitrogen;
    }

    public void setRequiredPhosphorus(double requiredPhosphorus) {
        this.requiredPhosphorus = requiredPhosphorus;
    }

    public void setRequiredPotassium(double requiredPotassium) {
        this.requiredPotassium = requiredPotassium;
    }

    public void setRequiredMoisture(double requiredMoisture) {
        this.requiredMoisture = requiredMoisture;
    }

    public double getRequiredNitrogen() {
        return requiredNitrogen;
    }

    public double getRequiredPhosphorus() {
        return requiredPhosphorus;
    }

    public double getRequiredPotassium() {
        return requiredPotassium;
    }

    public double getRequiredMoisture() {
        return requiredMoisture;
    }

    public void setVariety(Variety variety) {
        this.variety = variety;
    }

    public double getRequiredPh() {
        return requiredPh;
    }

    public void setRequiredPh(double requiredPh) {
        this.requiredPh = requiredPh;
    }
}