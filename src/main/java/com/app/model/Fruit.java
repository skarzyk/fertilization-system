package com.app.model;

import jakarta.persistence.*;

@Entity
public class Fruit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne
    @JoinColumn(name = "variety_id", unique = true)
    private Variety variety;

    public Fruit() {}

    public Fruit(String name, Variety variety) {
        this.name = name;
        this.variety = variety;
    }
}