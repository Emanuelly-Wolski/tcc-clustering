package com.clustering.clustering.repository;

import com.clustering.clustering.model.TeacherPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherPreferencesRepository extends JpaRepository<TeacherPreferences, Long> {
    TeacherPreferences findByUserId(Long userId);
}
