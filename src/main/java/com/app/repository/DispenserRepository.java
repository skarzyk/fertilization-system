package com.app.repository;

import com.app.model.Dispenser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DispenserRepository extends JpaRepository<Dispenser, Long> {
    List<Dispenser> findByFertilizers_Id(Long fertilizerId);
    List<Dispenser> findBySeedling_Plot_Id(Long plotId);

}
