package com.app.service;

import com.app.model.*;
import com.app.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class VarietyService {
    private final PlotRepository plotRepo;
    private final VarietyRepository varietyRepo;
    private final FertilizerRepository fertilizerRepo;

    public VarietyService(PlotRepository plotRepo,
                          VarietyRepository varietyRepo,
                          FertilizerRepository fertilizerRepo) {
        this.plotRepo = plotRepo;
        this.varietyRepo = varietyRepo;
        this.fertilizerRepo = fertilizerRepo;
    }

    @Transactional
    public void addFullVarietyToPlot(Long plotId,
                                     Variety variety,
                                     int seedlingsCount,
                                     double requiredN, double requiredP, double requiredK,
                                     double requiredMoisture, double requiredPh,
                                     double initN, double initP, double initK,
                                     double initHumidity, double initPH) {
        Plot plot = plotRepo.findById(plotId)
                .orElseThrow(() -> new IllegalArgumentException("Plot not found"));
        variety.setPlot(plot);

        List<Fertilizer> fertilizers = fertilizerRepo.findAll();

        FertilizationPlan plan = new FertilizationPlan();
        plan.setVariety(variety);
        plan.setRequiredNitrogen(requiredN);
        plan.setRequiredPhosphorus(requiredP);
        plan.setRequiredPotassium(requiredK);
        plan.setRequiredMoisture(requiredMoisture);
        plan.setRequiredPh(requiredPh);
        plan.setFertilizerTypes(fertilizers);
        variety.setFertilizationPlan(plan);

        List<Seedling> seedlings = new ArrayList<>();
        for (int i = 0; i < seedlingsCount; i++) {
            Seedling s = new Seedling();
            s.setPlot(plot);
            s.setVariety(variety);

            SensorNutrients sn = new SensorNutrients("mg/L", initN, initP, initK);
            sn.setSeedling(s);

            SensorHumidity sh = new SensorHumidity("%", initHumidity);
            sh.setSeedling(s);

            SensorPH sp = new SensorPH("pH", initPH);
            sp.setSeedling(s);

            s.getSensors().add(sn);
            s.getSensors().add(sh);
            s.getSensors().add(sp);

            Dispenser d = new Dispenser();
            d.setStatus(DispenserStatus.INACTIVE);
            d.getFertilizationPlans().add(plan);
            d.getFertilizers().addAll(fertilizers);

            s.setDispenser(d);
            seedlings.add(s);
        }
        variety.setSeedlings(seedlings);

        varietyRepo.save(variety);
    }
}