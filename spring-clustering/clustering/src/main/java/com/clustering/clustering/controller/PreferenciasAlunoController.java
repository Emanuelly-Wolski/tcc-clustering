package com.clustering.clustering.controller;

import com.clustering.clustering.model.PreferenciasAluno;
import com.clustering.clustering.service.PreferenciasAlunoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("preferencias-aluno")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class PreferenciasAlunoController {

    @Autowired
    private PreferenciasAlunoService service;

    @GetMapping
    public ResponseEntity<List<PreferenciasAluno>> getAll() {
        List<PreferenciasAluno> preferencesList = service.listarTodos();
        return ResponseEntity.ok(preferencesList);
    }

    @PostMapping
    public ResponseEntity<PreferenciasAluno> createPreference(@RequestBody PreferenciasAluno preferenciasAluno) {

        System.out.println("Criando preferência...");
        if (preferenciasAluno.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    
        System.out.println("Payload recebido com userId: " + preferenciasAluno);
    
        PreferenciasAluno createdPreference = service.salvar(preferenciasAluno);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPreference);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPreferenceById(@PathVariable Long id) {
        Optional<PreferenciasAluno> existingPreference = Optional.ofNullable(service.buscarPorId(id));
        if (existingPreference.isPresent()) {
            return ResponseEntity.ok(existingPreference.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Preferência não encontrada.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePreference(
            @PathVariable Long id,
            @RequestBody PreferenciasAluno updatedPreference) {

        Optional<PreferenciasAluno> existingPreferenceOptional = Optional.ofNullable(service.buscarPorId(id));
        if (existingPreferenceOptional.isPresent()) {
            PreferenciasAluno existingPreference = existingPreferenceOptional.get();

            existingPreference.updateFrom(updatedPreference); // Atualiza os campos
            PreferenciasAluno savedPreference = service.salvar(existingPreference);
            return ResponseEntity.ok(savedPreference);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePreference(@PathVariable Long id) {
        Optional<PreferenciasAluno> existingPreference = Optional.ofNullable(service.buscarPorId(id));
        if (existingPreference.isPresent()) {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
