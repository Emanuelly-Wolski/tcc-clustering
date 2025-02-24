package com.clustering.clustering.controller;

import com.clustering.clustering.model.PreferenciasProfessor;
import com.clustering.clustering.service.PreferenciasProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("preferencias-professor")
@CrossOrigin(origins = "*", allowedHeaders = "*", 
             methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class PreferenciasProfessorController {

    @Autowired
    private PreferenciasProfessorService service;

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<PreferenciasProfessor> list = service.listarTodos();
            return ResponseEntity.ok(list);
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "Erro ao obter as preferências."));
        }
    }

    @PostMapping
    public ResponseEntity<?> createPreference(@RequestBody PreferenciasProfessor pref) {
        try {
            PreferenciasProfessor created = service.salvar(pref);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "Erro ao criar a preferência."));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPreferenceById(@PathVariable Long id) {
        try {
            Optional<PreferenciasProfessor> existing = Optional.ofNullable(service.buscarPorId(id));
            if(existing.isPresent()) {
                return ResponseEntity.ok(existing.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body(Map.of("error", "Preferência não encontrada."));
            }
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "Erro ao obter a preferência."));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePreference(@PathVariable Long id, @RequestBody PreferenciasProfessor updatedPref) {
        try {
            PreferenciasProfessor existing = service.buscarPorId(id);
            if(existing != null) {
                existing.updateFrom(updatedPref);
                PreferenciasProfessor saved = service.salvar(existing);
                return ResponseEntity.ok(saved);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body(Map.of("error", "Preferência não encontrada."));
            }
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "Erro ao atualizar a preferência."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePreference(@PathVariable Long id) {
        try {
            PreferenciasProfessor existing = service.buscarPorId(id);
            if(existing != null) {
                service.deletar(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body(Map.of("error", "Preferência não encontrada."));
            }
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "Erro ao deletar a preferência."));
        }
    }
}

