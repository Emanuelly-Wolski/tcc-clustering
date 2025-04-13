package com.clustering.clustering.repository;

import com.clustering.clustering.model.StudentPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentPreferencesRepository extends JpaRepository<StudentPreferences, Long> {
    StudentPreferences findByUserId(Long userId);
}
