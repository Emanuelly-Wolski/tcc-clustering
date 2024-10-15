package com.clustering.clustering.service;

import com.clustering.clustering.model.PreferenciasProfessor;
import com.clustering.clustering.repository.PreferenciasProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PreferenciasProfessorService {

    @Autowired
    private PreferenciasProfessorRepository repository;

    public List<PreferenciasProfessor> listarTodos() {
        return repository.findAll();
    }

    public PreferenciasProfessor salvar(PreferenciasProfessor preferenciasProfessor) {
        return repository.save(preferenciasProfessor);
    }

    public PreferenciasProfessor buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
