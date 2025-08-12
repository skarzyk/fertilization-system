package com.app.repository;

import com.app.model.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {

    Optional<SensorReading> findFirstBySensor_Seedling_IdAndNutrientTypeOrderByTimestampDesc(
            Long seedlingId,
            String nutrientType
    );
}