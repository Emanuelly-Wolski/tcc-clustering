// package com.clustering.clustering.service;

// import com.clustering.clustering.model.TeacherPreferences;
// import com.clustering.clustering.repository.TeacherPreferencesRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import java.util.List;

// @Service
// public class TeacherPreferencesService {

//     @Autowired
//     private TeacherPreferencesRepository repository;

//     public List<TeacherPreferences> listarTodos() {
//         return repository.findAll();
//     }

//     public TeacherPreferences salvar(TeacherPreferences preferenciasProfessor) {
//         return repository.save(preferenciasProfessor);
//     }

//     public TeacherPreferences buscarPorId(Long id) {
//         return repository.findById(id).orElse(null);
//     }

//     public void deletar(Long id) {
//         repository.deleteById(id);
//     }

//     public void deleteByUserId(Long userId) {
//         TeacherPreferences pref = repository.findByUserId(userId);
//         if (pref != null) {
//             repository.delete(pref);
//         }
//     }
// }

package com.clustering.clustering.service;

import com.clustering.clustering.model.TeacherPreferences;
import com.clustering.clustering.repository.TeacherPreferencesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TeacherPreferencesService {

    @Autowired
    private TeacherPreferencesRepository repository;

    public List<TeacherPreferences> listarTodos() {
        return repository.findAll();
    }

    public TeacherPreferences salvar(TeacherPreferences preferenciasProfessor) {
        return repository.save(preferenciasProfessor);
    }

    public TeacherPreferences buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public void deleteByUserId(Long userId) {
        TeacherPreferences pref = repository.findByUserId(userId);
        if (pref != null) {
            repository.delete(pref);
        }
    }

    public void updateUserName(Long userId, String newName) {
        TeacherPreferences pref = repository.findByUserId(userId);
        if (pref != null) {
            pref.setUserName(newName);
            repository.save(pref);
            System.out.println("Nome do professor atualizado nas preferências: " + newName);
        }
    }

    public void updateUserEmail(Long userId, String newEmail) {
        TeacherPreferences pref = repository.findByUserId(userId);
        if (pref != null) {
            pref.setUserEmail(newEmail);
            repository.save(pref);
            System.out.println("Email do professor atualizado nas preferências: " + newEmail);
        }
    }
}

