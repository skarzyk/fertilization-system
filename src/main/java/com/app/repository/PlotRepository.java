package com.app.repository;
import com.app.model.Plot;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlotRepository extends JpaRepository<Plot, Long> {

    @EntityGraph(attributePaths = {"seedlings", "seedlings.sensors"})
    Optional<Plot> findById(Long id);

}