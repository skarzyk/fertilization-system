package com.app.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fertilizer")
public class Fertilizer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "ingredient", nullable = false)
    private String ingredient;

    @Column(name = "availableAmount", nullable = false)
    private double availableAmount;

    @Column(name = "minThreshold", nullable = false)
    private double minThreshold;

    @ManyToOne
    @JoinColumn(name = "fertilization_plan_id")
    private FertilizationPlan fertilizationPlan;

    @ManyToMany(mappedBy = "fertilizers")
    private List<Dispenser> dispensers = new ArrayList<>();

    public Fertilizer() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getAvailableAmount() {
        return availableAmount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public void setAvailableAmount(double availableAmount) {
        this.availableAmount = availableAmount;
    }

    public void setMinThreshold(double minThreshold) {
        this.minThreshold = minThreshold;
    }

    public double getMinThreshold() {
        return minThreshold;
    }

    public String getIngredient() {
        return ingredient;
    }

    @Override
    public String toString() {
        return name != null ? name : super.toString();
    }
}