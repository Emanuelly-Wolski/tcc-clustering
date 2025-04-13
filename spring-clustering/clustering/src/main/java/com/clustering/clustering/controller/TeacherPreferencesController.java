package com.clustering.clustering.controller;

import com.clustering.clustering.dto.UserDTO;
import com.clustering.clustering.model.TeacherPreferences;
import com.clustering.clustering.service.TeacherPreferencesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("preferencias-professor")
@CrossOrigin(origins = "*", allowedHeaders = "*", 
             methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class TeacherPreferencesController {

    @Autowired
    private TeacherPreferencesService service;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private HttpServletRequest httpServletRequest;

    // URL base do microserviço de login
    private final String userServiceUrl = "http://localhost:3000/api/auth/users/";

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<TeacherPreferences> list = service.listarTodos();
            return ResponseEntity.ok(list);
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "Erro ao obter as preferências."));
        }
    }

    @PostMapping
    public ResponseEntity<?> createPreference(@RequestBody TeacherPreferences pref) {
        try {
            if (pref.getUserId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "O campo userId é obrigatório."));
            }
            
            // Obtém o token de autorização do header da requisição
            String token = httpServletRequest.getHeader("Authorization");
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token de autorização não fornecido."));
            }
            
            // Consulta o microserviço de login para obter os dados do usuário
            UserDTO user = getUserDTO(pref.getUserId(), token);
            
            // Armazena os dados do usuário na entidade
            pref.setUserName(user.getName());
            pref.setUserEmail(user.getEmail());
            
            TeacherPreferences created = service.salvar(pref);
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
            Optional<TeacherPreferences> existing = Optional.ofNullable(service.buscarPorId(id));
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
    public ResponseEntity<?> updatePreference(@PathVariable Long id, @RequestBody TeacherPreferences updatedPref) {
        try {
            TeacherPreferences existing = service.buscarPorId(id);
            if(existing != null) {
                existing.updateFrom(updatedPref);

                // Se o userId estiver sendo atualizado, puxa novamente os dados do usuário
                if (updatedPref.getUserId() != null) {
                    String token = httpServletRequest.getHeader("Authorization");
                    if (token == null || token.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("error", "Token de autorização não fornecido."));
                    }
                    UserDTO user = getUserDTO(updatedPref.getUserId(), token);
                    existing.setUserName(user.getName());
                    existing.setUserEmail(user.getEmail());
                }
                TeacherPreferences saved = service.salvar(existing);
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
            TeacherPreferences existing = service.buscarPorId(id);
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
    
    // Método para obter os dados do usuário a partir do microserviço de login

    private UserDTO getUserDTO(Long userId, String token) {
        String url = userServiceUrl + userId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<UserDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, UserDTO.class);
        if(response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Não foi possível recuperar os dados do usuário com id: " + userId);
        }
    }
}

