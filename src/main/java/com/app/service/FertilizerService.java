package com.app.service;

import com.app.model.Dispenser;
import com.app.model.Fertilizer;
import com.app.repository.DispenserRepository;
import com.app.repository.FertilizerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class FertilizerService {
    private final FertilizerRepository repo;
    private final DispenserRepository dispenserRepo;

    public FertilizerService(FertilizerRepository repo,
                             DispenserRepository dispenserRepo) {
        this.repo = repo;
        this.dispenserRepo = dispenserRepo;
    }

    @Transactional(readOnly = true)
    public List<Fertilizer> findAll() {
        return repo.findAll();
    }

    @Transactional
    public Fertilizer saveAndAssignToAllDispensers(Fertilizer fertilizer) {
        Fertilizer saved = repo.save(fertilizer);
        List<Dispenser> dispensers = dispenserRepo.findAll();
        dispensers.forEach(d -> d.getFertilizers().add(saved));
        dispenserRepo.saveAll(dispensers);
        return saved;
    }
}