package com.app.model;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name="plot")
public class Plot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "area", nullable = false)
    private double area;

    @OneToMany(mappedBy = "plot", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Seedling> seedlings = new HashSet<>();


    public Plot() {
    }

    public Plot(String name, double area) {
        this.name = name;
        this.area = area;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Set<Seedling> getSeedlings() {
        return seedlings;
    }

    @Override
    public String toString() {
        return name != null ? name : super.toString();
    }
}

