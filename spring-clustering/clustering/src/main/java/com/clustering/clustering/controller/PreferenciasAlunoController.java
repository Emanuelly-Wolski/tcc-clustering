package com.clustering.clustering.controller;

import com.clustering.clustering.dto.UserDTO;
import com.clustering.clustering.model.PreferenciasAluno;
import com.clustering.clustering.service.PreferenciasAlunoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("preferencias-aluno")
@CrossOrigin(origins = "*", allowedHeaders = "*", 
             methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class PreferenciasAlunoController {

    @Autowired
    private PreferenciasAlunoService service;

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private HttpServletRequest httpServletRequest;
    
    // URL base do microserviço de login 
    private final String userServiceUrl = "http://localhost:3000/api/auth/users/";

    /**
     * Endpoint para listar todas as preferências de alunos.
     */
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<PreferenciasAluno> preferencesList = service.listarTodos();
            return ResponseEntity.ok(preferencesList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao obter as preferências."));
        }
    }

    /**
     * Endpoint para criar uma nova preferência.
     * Busca os dados do usuário (nome e email) usando o token recebido no header
     */
    @PostMapping
    public ResponseEntity<?> createPreference(@RequestBody PreferenciasAluno preferenciasAluno) {
        try {
            System.out.println("Criando preferência com dados: " + preferenciasAluno);
            
            if (preferenciasAluno.getUserId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "O campo userId é obrigatório."));
            }
            
            // Obtém o token de autorização do header da requisição
            String token = httpServletRequest.getHeader("Authorization");
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token de autorização não fornecido."));
            }
            
            // Consulta o microserviço de login para obter os dados do usuário, usando o token recebido
            UserDTO user = getUserDTO(preferenciasAluno.getUserId(), token);
            
            // Armazena os dados do usuário na entidade
            preferenciasAluno.setUserName(user.getName());
            preferenciasAluno.setUserEmail(user.getEmail());
            
            PreferenciasAluno createdPreference = service.salvar(preferenciasAluno);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPreference);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao processar a solicitação."));
        }
    }

    /**
     * Endpoint para obter uma preferência por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPreferenceById(@PathVariable Long id) {
        try {
            Optional<PreferenciasAluno> existingPreference = Optional.ofNullable(service.buscarPorId(id));
            if (existingPreference.isPresent()) {
                return ResponseEntity.ok(existingPreference.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Preferência não encontrada."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao processar a solicitação."));
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
                existingPreference.updateFrom(updatedPreference);
                
                if (updatedPreference.getUserId() != null) {
                    String token = httpServletRequest.getHeader("Authorization");
                    if (token == null || token.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("error", "Token de autorização não fornecido."));
                    }
                    UserDTO user = getUserDTO(updatedPreference.getUserId(), token);
                    existingPreference.setUserName(user.getName());
                    existingPreference.setUserEmail(user.getEmail());
                    existingPreference.setUserId(updatedPreference.getUserId());
                }
                
                PreferenciasAluno savedPreference = service.salvar(existingPreference);
                return ResponseEntity.ok(savedPreference);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Preferência não encontrada."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao processar a solicitação."));
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
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Preferência não encontrada."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao processar a solicitação."));
        }
    }
    
    /**
     * Método auxiliar para buscar os dados do usuário no microserviço de login,
     * enviando o token de autorização recebido na requisição.
     */
    private UserDTO getUserDTO(Long userId, String token) {
        String url = userServiceUrl + userId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<UserDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, UserDTO.class);
        
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Não foi possível recuperar os dados do usuário com id: " + userId);
        }
    }
}