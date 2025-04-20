// ProfessorPreferencesRepository.java
package com.clustering.clustering.repository;

import com.clustering.clustering.model.ProfessorPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessorPreferencesRepository extends JpaRepository<ProfessorPreferences, Long> {
    ProfessorPreferences findByUserId(Long userId);
}