package com.app.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "fertilizationhistory")
public class FertilizationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fertilizername", nullable = false)
    private String fertilizerName;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "applicationdate", nullable = false)
    private LocalDateTime applicationDate;

    @Column(name = "plotname", nullable = false)
    private String plotName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "seedling_id", nullable = false)
    private Seedling seedling;

    public FertilizationHistory() {}

    public FertilizationHistory(LocalDateTime applicationDate, String fertilizerName, double amount, Seedling seedling, String plotName) {
        this.applicationDate = applicationDate;
        this.fertilizerName = fertilizerName;
        this.amount = amount;
        this.seedling = seedling;
        this.plotName = plotName;
    }

    public String getPlotName() {
        return plotName;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFertilizerName() {
        return fertilizerName;
    }

    public double getAmount() {
        return amount;
    }

}