package com.app.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Dispenser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DispenserStatus status;

    @ManyToMany
    @JoinTable(
            name = "dispenser_plan",
            joinColumns = @JoinColumn(name = "dispenser_id"),
            inverseJoinColumns = @JoinColumn(name = "fertilization_plan_id")
    )
    private Set<FertilizationPlan> fertilizationPlans = new HashSet<>();


    @OneToOne(mappedBy = "dispenser", cascade = CascadeType.ALL)
    private Seedling seedling;

    @ManyToMany
    @JoinTable(
            name = "dispenser_fertilizer",
            joinColumns = @JoinColumn(name = "dispenser_id"),
            inverseJoinColumns = @JoinColumn(name = "fertilizer_id")
    )
    private Set<Fertilizer> fertilizers = new HashSet<>();

    public Dispenser() {}

    public void setId(Long id) {
        this.id = id;
    }

    public void setStatus(DispenserStatus status) {
        this.status = status;
    }

    public Set<FertilizationPlan> getFertilizationPlans() {
        return fertilizationPlans;
    }


    public Set<Fertilizer> getFertilizers() {
        return fertilizers;
    }

    public Long getId() {
        return id;
    }

    public DispenserStatus getStatus() {
        return status;
    }
}