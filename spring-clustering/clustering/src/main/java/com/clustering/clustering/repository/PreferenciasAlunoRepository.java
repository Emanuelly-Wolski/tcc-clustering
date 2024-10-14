package com.clustering.clustering.repository;

import com.clustering.clustering.model.PreferenciasAluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreferenciasAlunoRepository extends JpaRepository<PreferenciasAluno, Long> {
}
