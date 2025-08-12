package com.app.repository;

import com.app.model.FertilizationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FertilizationHistoryRepository extends JpaRepository<FertilizationHistory, Long> {
}