package com.clustering.clustering.controller;

import com.clustering.clustering.dto.UserDTO;
import com.clustering.clustering.model.Cluster;
import com.clustering.clustering.model.PreferenciasAluno;
import com.clustering.clustering.service.ClusterService;
import com.clustering.clustering.service.PreferenciasAlunoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clustering")
@CrossOrigin(origins = "*")
public class ClusteringController {

    @Autowired
    private PreferenciasAlunoService preferenciasAlunoService;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ClusterService clusterService;
    
    @Autowired
    private HttpServletRequest httpServletRequest;
    
    // URL do serviço Python (FastAPI)
    private final String clusteringServiceUrl = "http://localhost:8001/clustering";
    
    // URL do microserviço de login para buscar os detalhes do usuário
    private final String userServiceUrl = "http://localhost:3000/api/auth/users/";

    /**
     * Endpoint para obter sugestões para um usuário específico.
     * Também envia o token no header para o serviço Python.
     */
    @GetMapping("/sugeridos/{userId}")
    public ResponseEntity<?> getclustering(@PathVariable Long userId) {
        List<PreferenciasAluno> allPrefs = preferenciasAlunoService.listarTodos();
        List<Map<String, Object>> students = allPrefs.stream().map(pref -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", pref.getUserId());
            map.put("nomeCompleto", pref.getUserName());
            map.put("email", pref.getUserEmail());
            map.put("turno", pref.getTurno());
            map.put("disponibilidade", pref.getDisponibilidade());
            map.put("temasDeInteresse", pref.getTemasDeInteresse());
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("students", students);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Pega o token exatamente como chega do front
        String token = httpServletRequest.getHeader("Authorization");
        if (token != null && !token.isEmpty()) {
            headers.set("Authorization", token);
        }

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        // Faz a requisição ao serviço Python (endpoint /clustering)
        ResponseEntity<Map> response = restTemplate.postForEntity(clusteringServiceUrl, requestEntity, Map.class);

        return ResponseEntity.ok(response.getBody());
    }
    
    /**
     * Endpoint para clusterizar todos os usuários e salvar os resultados na tabela "cluster".
     * - Atualiza (ou cria) apenas 1 registro por userId.
     * - Ignora usuários com role ADMIN.
     */
    @GetMapping("/atualizar")
    public ResponseEntity<?> atualizarClusters() {
        // 1. Buscar todas as preferências (somente quem tem preferências será clusterizado)
        List<PreferenciasAluno> allPrefs = preferenciasAlunoService.listarTodos();
        
        // 2. Monta os dados para enviar ao serviço Python
        List<Map<String, Object>> students = allPrefs.stream().map(pref -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", pref.getUserId());
            map.put("nomeCompleto", pref.getUserName());
            map.put("email", pref.getUserEmail());
            map.put("turno", pref.getTurno());
            map.put("disponibilidade", pref.getDisponibilidade());
            map.put("temasDeInteresse", pref.getTemasDeInteresse());
            return map;
        }).collect(Collectors.toList());
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", 0);
        payload.put("students", students);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String token = httpServletRequest.getHeader("Authorization");
        if (token != null && !token.isEmpty()) {
            headers.set("Authorization", token);
        }

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);
        
        // 3. Chama o endpoint do serviço Python ("/all")
        ResponseEntity<Map> response = restTemplate.postForEntity(clusteringServiceUrl + "/all", requestEntity, Map.class);
        
        // 4. Obtém o mapeamento retornado (lista de objetos com "id" e "cluster")
        List<Map<String, Object>> clustersData = (List<Map<String, Object>>) response.getBody().get("clusters");
        
        // 5. Para cada registro, criar ou atualizar Cluster, ignorando admins
        List<Cluster> clustersToSave = new ArrayList<>();

        for (Map<String, Object> map : clustersData) {
            Long uid = ((Number) map.get("id")).longValue();
            int clusterNumber = ((Number) map.get("cluster")).intValue();

            // Busca a role do usuário no microserviço de login
            String role = "desconhecido";
            HttpHeaders authHeaders = new HttpHeaders();
            if (token != null && !token.isEmpty()) {
                authHeaders.set("Authorization", token);
            }
            HttpEntity<String> authEntity = new HttpEntity<>(authHeaders);

            try {
                ResponseEntity<UserDTO> userResponse = restTemplate.exchange(
                    userServiceUrl + uid,
                    HttpMethod.GET,
                    authEntity,
                    UserDTO.class
                );
                if (userResponse.getStatusCode().is2xxSuccessful() && userResponse.getBody() != null) {
                    String roleString = userResponse.getBody().getRole();
                    if (roleString != null) {
                        switch (roleString.toUpperCase()) {
                            case "ALUNO":
                                role = "aluno";
                                break;
                            case "PROFESSOR":
                                role = "professor";
                                break;
                            case "ADMIN":
                                role = "admin";
                                break;
                            default:
                                role = "desconhecido";
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                // role permanece "desconhecido"
            }

            // Ignora se for admin
            if ("admin".equalsIgnoreCase(role)) {
                continue; 
            }

            // Verifica se já existe registro para esse userId
            Optional<Cluster> existingOpt = clusterService.findByUserId(uid);
            Cluster c;
            if (existingOpt.isPresent()) {
                // Atualiza o registro existente
                c = existingOpt.get();
            } else {
                // Cria um novo registro
                c = new Cluster();
                c.setUserId(uid);
            }

            c.setClusterId(clusterNumber);
            c.setUserRole(role);

            clustersToSave.add(c);
        }
        
        // 6. Salvar (criar ou atualizar) todos os registros na tabela "cluster"
        clusterService.saveAll(clustersToSave);
        return ResponseEntity.ok("Clusters atualizados com sucesso");
    }
}