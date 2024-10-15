package com.clustering.clustering.controller;

import com.clustering.clustering.model.PreferenciasProfessor;
import com.clustering.clustering.service.PreferenciasProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preferencias-professor")
public class PreferenciasProfessorController {

    @Autowired
    private PreferenciasProfessorService service;

    @GetMapping
    public List<PreferenciasProfessor> listarTodos() {
        return service.listarTodos();
    }

    @PostMapping
    public PreferenciasProfessor salvar(@RequestBody PreferenciasProfessor preferenciasProfessor) {
        return service.salvar(preferenciasProfessor);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PreferenciasProfessor> buscarPorId(@PathVariable Long id) {
        PreferenciasProfessor professor = service.buscarPorId(id);
        if (professor != null) {
            return ResponseEntity.ok(professor);
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
