package com.app.service;

import com.app.model.FertilizationHistory;
import com.app.model.SensorNutrients;
import com.app.model.SensorReading;
import com.app.model.Seedling;
import com.app.repository.FertilizationHistoryRepository;
import com.app.repository.FertilizationPlanRepository;
import com.app.repository.SensorReadingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FertilizationService {
    private static final Logger log = LoggerFactory.getLogger(FertilizationService.class);

    private final FertilizationPlanRepository planRepo;
    private final SensorReadingRepository readingRepo;
    private final DispenserService dispenserService;
    private final FertilizationHistoryRepository historyRepo;

    public FertilizationService(FertilizationPlanRepository planRepo,
                                SensorReadingRepository readingRepo,
                                DispenserService dispenserService,
                                FertilizationHistoryRepository historyRepo) {
        this.planRepo = planRepo;
        this.readingRepo = readingRepo;
        this.dispenserService = dispenserService;
        this.historyRepo = historyRepo;
    }

    @Transactional
    public List<String> checkAndFertilize(Seedling seed) {
        List<String> logs = new ArrayList<>();
        planRepo.findByVariety(seed.getVariety()).ifPresent(plan -> {
            Map<String, Double> requiredMap = Map.of(
                    "N", plan.getRequiredNitrogen(),
                    "P", plan.getRequiredPhosphorus(),
                    "K", plan.getRequiredPotassium()
            );
            requiredMap.forEach((type, required) -> {
                logs.addAll(applyIfNeeded(seed, type, required));
            });
        });
        return logs;
    }

    private List<String> applyIfNeeded(Seedling seed, String nutrientType, double required) {
        Optional<Double> lastVal = readingRepo
                .findFirstBySensor_Seedling_IdAndNutrientTypeOrderByTimestampDesc(seed.getId(), nutrientType)
                .map(SensorReading::getValue);
        double actual = lastVal.orElse(0.0);
        List<String> out = new ArrayList<>();

        if (actual < required) {
            double toApply = required - actual;
            dispenserService.dispense(
                    seed,
                    "N".equals(nutrientType),
                    "P".equals(nutrientType),
                    "K".equals(nutrientType),
                    "N".equals(nutrientType) ? toApply : 0,
                    "P".equals(nutrientType) ? toApply : 0,
                    "K".equals(nutrientType) ? toApply : 0
            );
            seed.getDispenser().getFertilizers().stream()
                    .filter(f -> nutrientType.equals(f.getIngredient()))
                    .filter(f -> f.getAvailableAmount() <= f.getMinThreshold())
                    .forEach(f -> out.add("Brakuje " + f.getName() + " nawozu"));

            historyRepo.save(new FertilizationHistory(
                    LocalDateTime.now(),
                    nutrientType,
                    toApply,
                    seed,
                    seed.getPlot().getName()
            ));
        } else {
            historyRepo.save(new FertilizationHistory(
                    LocalDateTime.now(), nutrientType, 0.0, seed, seed.getPlot().getName()
            ));

            seed.getSensors().stream()
                    .filter(SensorNutrients.class::isInstance)
                    .map(SensorNutrients.class::cast)
                    .findFirst()
                    .ifPresent(sn -> {
                        double newVal = switch (nutrientType) {
                            case "N" -> sn.getNitrogenLevel();
                            case "P" -> sn.getPhosphorusLevel();
                            default  -> sn.getPotassiumLevel();
                        };
                        LocalDateTime now = LocalDateTime.now();

                        SensorReading newReading = new SensorReading(now, nutrientType, newVal, sn);
                        readingRepo.save(newReading);
                    });
        }
        return out;
    }
}