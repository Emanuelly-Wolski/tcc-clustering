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

    /**
     * Endpoint para listar todas as preferências de alunos.
     */
    @GetMapping
    public ResponseEntity<List<PreferenciasAluno>> getAll() {
        List<PreferenciasAluno> preferencesList = service.listarTodos();
        return ResponseEntity.ok(preferencesList);
    }

    /**
     * Endpoint para criar uma nova preferência.
     */
    @PostMapping
    public ResponseEntity<?> createPreference(@RequestBody PreferenciasAluno preferenciasAluno) {
        try {
            // Log do payload recebido
            System.out.println("Criando preferência com dados: " + preferenciasAluno);

            // Verificação de campos obrigatórios
            if (preferenciasAluno.getUserId() == null) {
                System.out.println("Erro: userId está nulo.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O campo userId é obrigatório.");
            }

            PreferenciasAluno createdPreference = service.salvar(preferenciasAluno);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPreference);

        } catch (Exception e) {
            System.err.println("Erro ao criar preferência: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar a solicitação.");
        }
    }

    /**
     * Endpoint para obter uma preferência por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPreferenceById(@PathVariable Long id) {
        Optional<PreferenciasAluno> existingPreference = Optional.ofNullable(service.buscarPorId(id));
        if (existingPreference.isPresent()) {
            return ResponseEntity.ok(existingPreference.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Preferência não encontrada.");
        }
    }

    /**
     * Endpoint para atualizar uma preferência existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePreference(
            @PathVariable Long id,
            @RequestBody PreferenciasAluno updatedPreference) {
        try {
            Optional<PreferenciasAluno> existingPreferenceOptional = Optional.ofNullable(service.buscarPorId(id));
            if (existingPreferenceOptional.isPresent()) {
                PreferenciasAluno existingPreference = existingPreferenceOptional.get();

                // Atualiza os campos
                existingPreference.updateFrom(updatedPreference);
                PreferenciasAluno savedPreference = service.salvar(existingPreference);
                return ResponseEntity.ok(savedPreference);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Preferência não encontrada.");
            }
        } catch (Exception e) {
            // Tratamento genérico de erro
            System.err.println("Erro ao atualizar preferência: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar a solicitação.");
        }
    }

    /**
     * Endpoint para deletar uma preferência por ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePreference(@PathVariable Long id) {
        try {
            Optional<PreferenciasAluno> existingPreference = Optional.ofNullable(service.buscarPorId(id));
            if (existingPreference.isPresent()) {
                service.deletar(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Preferência não encontrada.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao deletar preferência: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar a solicitação.");
        }
    }
}