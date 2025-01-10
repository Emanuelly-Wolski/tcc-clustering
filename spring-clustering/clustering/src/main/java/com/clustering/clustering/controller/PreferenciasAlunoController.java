package com.clustering.clustering.controller;

import com.clustering.clustering.model.PreferenciasAluno;
import com.clustering.clustering.service.PreferenciasAlunoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("preferencias-aluno")
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
    public ResponseEntity<PreferenciasAluno> buscarPorId(@PathVariable Long id) {
        PreferenciasAluno aluno = service.buscarPorId(id);
        if (aluno != null) {
            return ResponseEntity.ok(aluno);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (service.buscarPorId(id) != null) {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}