// ProfessorPreferencesRepository.java
package com.clustering.clustering.repository;

import com.clustering.clustering.model.ProfessorPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
Camada de repositório é utilizada dentro da classe ProfessorPreferencesService para realizar as operações no banco...
essa interface herda de JpaRepository e fornece todos os métodos básicos prontos para CRUD
*/

@Repository
public interface ProfessorPreferencesRepository extends JpaRepository<ProfessorPreferences, Long> {
    ProfessorPreferences findByUserId(Long userId);
}