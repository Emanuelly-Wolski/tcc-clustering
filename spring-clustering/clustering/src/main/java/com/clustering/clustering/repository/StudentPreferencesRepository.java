package com.clustering.clustering.repository;

import com.clustering.clustering.model.StudentPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
repositório usado dentro da classe StudentPreferencesService para realizar as operações no banco...
essa interface herda de JpaRepository e fornece todos os métodos básicos prontos para CRUD
*/

@Repository
public interface StudentPreferencesRepository extends JpaRepository<StudentPreferences, Long> {
    StudentPreferences findByUserId(Long userId);
}