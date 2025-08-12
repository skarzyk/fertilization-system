package com.app.service;

import com.app.model.*;
import com.app.repository.DispenserRepository;
import com.app.repository.FertilizerRepository;
import com.app.repository.SensorNutrientsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;


@Service
public class DispenserService {
    private static final Logger log = LoggerFactory.getLogger(DispenserService.class);

    private final DispenserRepository dispenserRepo;
    private final FertilizerRepository fertilizerRepo;
    private final SensorNutrientsRepository sensorRepo;

    private static final Map<String, BiConsumer<SensorNutrients, Double>> SENSOR_UPDATERS =
            Map.of(
                    "N", (s, d) -> s.setNitrogenLevel(s.getNitrogenLevel() + d),
                    "P", (s, d) -> s.setPhosphorusLevel(s.getPhosphorusLevel() + d),
                    "K", (s, d) -> s.setPotassiumLevel(s.getPotassiumLevel() + d)
            );

    public DispenserService(DispenserRepository dispenserRepo,
                            FertilizerRepository fertilizerRepo,
                            SensorNutrientsRepository sensorRepo) {
        this.dispenserRepo = dispenserRepo;
        this.fertilizerRepo = fertilizerRepo;
        this.sensorRepo = sensorRepo;
    }

    @Transactional
    public void dispense(Seedling seedling,
                         boolean needN, boolean needP, boolean needK,
                         double doseN, double doseP, double doseK) {
        Dispenser d = seedling.getDispenser();
        if (d == null) {
            log.warn("Brak dozownika dla sadzonki {}", seedling.getId());
            return;
        }

        Optional<SensorNutrients> sOpt = seedling.getSensors().stream()
                .filter(sensor -> sensor instanceof SensorNutrients)
                .map(SensorNutrients.class::cast)
                .findFirst();
        if (sOpt.isEmpty()) {
            log.warn("Brak SensorNutrients dla sadzonki {}", seedling.getId());
            return;
        }
        SensorNutrients s = sOpt.get();

        if (needN) apply(d, s, "N", doseN);
        if (needP) apply(d, s, "P", doseP);
        if (needK) apply(d, s, "K", doseK);
        dispenserRepo.save(d);
    }

    private void apply(Dispenser d, SensorNutrients s, String ing, double dose) {
        if (dose <= 0) {
            return;
        }

        var fertOpt = d.getFertilizers().stream()
                .filter(f -> ing.equals(f.getIngredient()))
                .findFirst();
        if (fertOpt.isEmpty()) {
            return;
        }

        var fert = fertOpt.get();
        SENSOR_UPDATERS.getOrDefault(ing, (x, y) -> {}).accept(s, dose);
        fert.setAvailableAmount(fert.getAvailableAmount() - dose);
        d.setStatus(DispenserStatus.ACTIVE);

        sensorRepo.save(s);
        fertilizerRepo.save(fert);
    }

    @Transactional
    public void setStatusByPlot(Long plotId, DispenserStatus status) {
        List<Dispenser> list = dispenserRepo.findBySeedling_Plot_Id(plotId);
        list.forEach(d -> d.setStatus(status));
        dispenserRepo.saveAll(list);
    }

    @Transactional(readOnly = true)
    public List<Dispenser> findByFertilizer(Long fertilizerId) {
        return dispenserRepo.findByFertilizers_Id(fertilizerId);
    }

}