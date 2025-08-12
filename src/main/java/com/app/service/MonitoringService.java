package com.app.service;

import com.app.model.*;
import com.app.repository.*;
import com.app.scheduler.MonitoringScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MonitoringService {

    private final PlotRepository plotRepo;
    private final SensorReadingRepository readingRepo;
    private final SensorNutrientsRepository nutrientsRepo;
    private final MonitoringScheduler scheduler;
    private final FertilizationService fertilizationService;
    private static final Logger log = LoggerFactory.getLogger(MonitoringService.class);
    private final TransactionTemplate txTemplate;

    private final Queue<String> logs = new ConcurrentLinkedQueue<>();
    private Long currentPlotId;

    public MonitoringService(PlotRepository plotRepo,
                             SensorReadingRepository readingRepo,
                             SensorNutrientsRepository nutrientsRepo,
                             MonitoringScheduler scheduler, FertilizationService fertilizationService,
                             PlatformTransactionManager txManager) {
        this.plotRepo = plotRepo;
        this.readingRepo = readingRepo;
        this.nutrientsRepo = nutrientsRepo;
        this.scheduler = scheduler;
        this.fertilizationService = fertilizationService;
        this.txTemplate = new TransactionTemplate(txManager);

    }

    public void startContinuousMonitoring(Plot plot, Runnable uiRefresh) {
        currentPlotId = plot.getId();
        scheduler.start(currentPlotId, () -> {
            try {
                txTemplate.executeWithoutResult(status -> poll(plot, uiRefresh));
            } catch (Exception ex) {
                log.error("Błąd podczas monitoringu dla kwatery {}: ", plot.getName(), ex);
            }
        });
    }

    public void stopContinuousMonitoring() {
        scheduler.stop(currentPlotId);
        currentPlotId = null;
    }

    @Transactional
    public void poll(Plot plot, Runnable uiRefresh) {
        Plot fresh = plotRepo.findById(plot.getId())
                .orElseThrow(() -> new IllegalArgumentException("Plot not found"));

        fresh.getSeedlings().forEach(seed -> {
            FertilizationPlan plan = seed.getVariety().getFertilizationPlan();

            seed.getSensors().forEach(sensor -> {
                LocalDateTime now = LocalDateTime.now();

                if (sensor instanceof SensorNutrients sn) {
                    double delta = 1.0;
                    double baseDelta = 1.0;
                    double deltaN = baseDelta * ThreadLocalRandom.current().nextDouble(0.7, 1.3);
                    double deltaP = baseDelta * ThreadLocalRandom.current().nextDouble(0.7, 1.3);
                    double deltaK = baseDelta * ThreadLocalRandom.current().nextDouble(0.7, 1.3);
                    sn.setNitrogenLevel(Math.max(0, sn.getNitrogenLevel() - deltaN));
                    sn.setPhosphorusLevel(Math.max(0, sn.getPhosphorusLevel() - deltaP));
                    sn.setPotassiumLevel(Math.max(0, sn.getPotassiumLevel() - deltaK));
                    nutrientsRepo.save(sn);

                    SensorReading rN = new SensorReading(now, "N", sn.getNitrogenLevel(), sn);
                    SensorReading rP = new SensorReading(now, "P", sn.getPhosphorusLevel(), sn);
                    SensorReading rK = new SensorReading(now, "K", sn.getPotassiumLevel(), sn);
                    readingRepo.saveAll(List.of(rN, rP, rK));

                } else if (sensor instanceof SensorHumidity sh) {
                    double hum = sh.sensorValue();
                    readingRepo.save(new SensorReading(now, "H", hum, sh));
                    if (hum < plan.getRequiredMoisture()) {
                        logs.add("Wilgotność spadła poniżej " + plan.getRequiredMoisture());
                    }

                } else if (sensor instanceof SensorPH sp) {
                    double ph = sp.sensorValue();
                    readingRepo.save(new SensorReading(now, "pH", ph, sp));
                    if (ph < plan.getRequiredPh()) {
                        logs.add("Poziom pH spadł poniżej " + plan.getRequiredPh());
                    }

                } else {
                    double val = sensor.sensorValue();
                    String code = sensor.getClass().getSimpleName().substring(0, 1);
                    readingRepo.save(new SensorReading(now, code, val, sensor));
                }
            });

            fertilizationService.checkAndFertilize(seed)
                    .forEach(logs::add);
        });

        uiRefresh.run();
    }

    public List<String> getLastLogsAndClear() {
        List<String> out = new ArrayList<>(logs);
        logs.clear();
        return out;
    }
}