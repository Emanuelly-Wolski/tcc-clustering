package com.clustering.clustering.service;

import com.clustering.clustering.model.PreferenciasAluno;
import com.clustering.clustering.repository.PreferenciasAlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PreferenciasAlunoService {

    @Autowired
    private PreferenciasAlunoRepository repository;

    public List<PreferenciasAluno> listarTodos() {
        return repository.findAll();
    }

    public PreferenciasAluno salvar(PreferenciasAluno preferenciasAluno) {
        return repository.save(preferenciasAluno);
    }

    public PreferenciasAluno buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}

