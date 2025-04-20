// ProfessorPreferencesService.java
package com.clustering.clustering.service;

import com.clustering.clustering.model.ProfessorPreferences;
import com.clustering.clustering.repository.ProfessorPreferencesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProfessorPreferencesService {

    @Autowired
    private ProfessorPreferencesRepository repository;

    public List<ProfessorPreferences> findAll() {
        return repository.findAll();
    }

    public ProfessorPreferences save(ProfessorPreferences preferences) {
        return repository.save(preferences);
    }

    public ProfessorPreferences findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public void deleteByUserId(Long userId) {
        ProfessorPreferences pref = repository.findByUserId(userId);
        if (pref != null) {
            repository.delete(pref);
        }
    }

    public void updateUserName(Long userId, String newName) {
        ProfessorPreferences pref = repository.findByUserId(userId);
        if (pref != null) {
            pref.setUserName(newName);
            repository.save(pref);
        }
    }

    public void updateUserEmail(Long userId, String newEmail) {
        ProfessorPreferences pref = repository.findByUserId(userId);
        if (pref != null) {
            pref.setUserEmail(newEmail);
            repository.save(pref);
        }
    }
}