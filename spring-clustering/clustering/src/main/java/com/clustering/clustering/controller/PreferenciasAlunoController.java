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
// @RequestMapping("/api/cluster/preferencias-aluno")
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
        try {
            // Converter listas (arrays) em strings separadas por vírgulas
            preferenciasAluno = convertArraysToString(preferenciasAluno);

            // Salvar a preferência
            PreferenciasAluno createdPreference = service.salvar(preferenciasAluno);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPreference);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PreferenciasAluno> getPreferenceById(@PathVariable Long id) {
        Optional<PreferenciasAluno> existingPreference = Optional.ofNullable(service.buscarPorId(id));
        return existingPreference.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PreferenciasAluno> updatePreference(@PathVariable Long id, @RequestBody PreferenciasAluno updatedPreference) {
        Optional<PreferenciasAluno> existingPreferenceOptional = Optional.ofNullable(service.buscarPorId(id));

        if (existingPreferenceOptional.isPresent()) {
            PreferenciasAluno existingPreference = existingPreferenceOptional.get();

            // Atualizar os campos e salvar
            existingPreference.updateFrom(convertArraysToString(updatedPreference));
            PreferenciasAluno savedPreference = service.salvar(existingPreference);

            return ResponseEntity.ok(savedPreference);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePreference(@PathVariable Long id) {
        Optional<PreferenciasAluno> existingPreference = Optional.ofNullable(service.buscarPorId(id));

        if (existingPreference.isPresent()) {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Método auxiliar para converter listas em strings separadas por vírgulas
    private PreferenciasAluno convertArraysToString(PreferenciasAluno preferenciasAluno) {
        if (preferenciasAluno.getLinguagensProgramacao() != null) {
            preferenciasAluno.setLinguagensProgramacao(String.join(",", preferenciasAluno.getLinguagensProgramacao()));
        }
        if (preferenciasAluno.getBancosDeDados() != null) {
            preferenciasAluno.setBancosDeDados(String.join(",", preferenciasAluno.getBancosDeDados()));
        }
        if (preferenciasAluno.getHabilidadesPessoais() != null) {
            preferenciasAluno.setHabilidadesPessoais(String.join(",", preferenciasAluno.getHabilidadesPessoais()));
        }
        if (preferenciasAluno.getTemasDeInteresse() != null) {
            preferenciasAluno.setTemasDeInteresse(String.join(",", preferenciasAluno.getTemasDeInteresse()));
        }
        if (preferenciasAluno.getFrameworkFront() != null) {
            preferenciasAluno.setFrameworkFront(String.join(",", preferenciasAluno.getFrameworkFront()));
        }
        return preferenciasAluno;
    }
}
