package com.clustering.clustering.controller;

import com.clustering.clustering.model.PreferenciasAluno;
import com.clustering.clustering.service.PreferenciasAlunoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preferencias-aluno")
public class PreferenciasAlunoController {

    @Autowired
    private PreferenciasAlunoService service;

    @GetMapping
    public List<PreferenciasAluno> listarTodos() {
        return service.listarTodos();
    }

    @PostMapping
    public PreferenciasAluno salvar(@RequestBody PreferenciasAluno preferenciasAluno) {
        return service.salvar(preferenciasAluno);
    }

    @GetMapping("/{id}")
    public PreferenciasAluno buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}

