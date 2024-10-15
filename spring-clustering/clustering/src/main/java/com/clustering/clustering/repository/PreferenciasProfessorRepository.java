package com.clustering.clustering.repository;

import com.clustering.clustering.model.PreferenciasProfessor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreferenciasProfessorRepository extends JpaRepository<PreferenciasProfessor, Long> {
}
