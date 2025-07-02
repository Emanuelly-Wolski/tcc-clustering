// package com.clustering.clustering.service;

// import com.clustering.clustering.model.StudentPreferences;
// import com.clustering.clustering.repository.StudentPreferencesRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.util.List;

// @Service
// public class StudentPreferencesService {

//     @Autowired
//     private StudentPreferencesRepository repository;

//     public List<StudentPreferences> listarTodos() {
//         return repository.findAll();
//     }

//     public StudentPreferences salvar(StudentPreferences preferenciasAluno) {
//         return repository.save(preferenciasAluno);
//     }

//     public StudentPreferences buscarPorId(Long id) {
//         return repository.findById(id).orElse(null);
//     }

//     /*
//      Exclui uma preferência a partir do ID da própria preferência (chave primária da tabela).
//      Usado em endpoints REST como DELETE /preferencias-aluno/{id}.
//      */
//     public void deletar(Long id) {
//         repository.deleteById(id);
//     }

//     /*
//      Exclui a preferência vinculada a um determinado usuário.
//      Usado internamente ao remover o usuário completamente do sistema (ex: via UserSyncController).
//      */
//     public void deleteByUserId(Long userId) {
//         StudentPreferences pref = repository.findByUserId(userId);
//         if (pref != null) {
//             repository.delete(pref);
//         }
//     }
// }

package com.clustering.clustering.service;

import com.clustering.clustering.model.StudentPreferences;
import com.clustering.clustering.repository.StudentPreferencesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
Essa classe é responsável por manipular dados da entidade de preferências dos alunos, serve como uma camada intermediária entre o controlador (ClusteringController) e o repositório (StudentPreferencesrRepository)
*/

@Service
public class StudentPreferencesService {

    //Injeta o repositório que acessa o banco de dados da tabela
    @Autowired
    private StudentPreferencesRepository repository;

    public List<StudentPreferences> listarTodos() {
        return repository.findAll();
    }

    public StudentPreferences salvar(StudentPreferences preferenciasAluno) {
        return repository.save(preferenciasAluno);
    }

    public StudentPreferences buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public void deleteByUserId(Long userId) {
        StudentPreferences pref = repository.findByUserId(userId);
        if (pref != null) {
            repository.delete(pref);
        }
    }

    public void updateUserName(Long userId, String newName) {
        StudentPreferences pref = repository.findByUserId(userId);
        if (pref != null) {
            pref.setUserName(newName);
            repository.save(pref);
            System.out.println("Nome do aluno atualizado nas preferências: " + newName);
        }
    }

    public void updateUserEmail(Long userId, String newEmail) {
        StudentPreferences pref = repository.findByUserId(userId);
        if (pref != null) {
            pref.setUserEmail(newEmail);
            repository.save(pref);
            System.out.println("Email do aluno atualizado nas preferências: " + newEmail);
        }
    }
}