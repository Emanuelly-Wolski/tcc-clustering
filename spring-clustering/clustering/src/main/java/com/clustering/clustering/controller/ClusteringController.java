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
     * Após receber as sugestões do serviço Python, para cada perfil
     * é consultada a tabela cluster para obter a role armazenada. Em seguida,
     * são filtradas as sugestões, retornando apenas perfis com role "aluno".
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
            // Não definimos a role aqui; ela será obtida da tabela cluster
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("students", students);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Pega o token exatamente como chega do front
        String token = httpServletRequest.getHeader("Authorization");
        if (token != null && !token.isEmpty()){
            headers.set("Authorization", token);
        }
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        // Faz a requisição ao serviço Python (endpoint /clustering)
        ResponseEntity<Map> response = restTemplate.postForEntity(clusteringServiceUrl, requestEntity, Map.class);
        Map responseBody = response.getBody();

        // Enriquecer cada sugestão com a role presente na tabela cluster
        List<Map<String, Object>> sugestoes = (List<Map<String, Object>>) responseBody.get("sugestoes");
        for (Map<String, Object> sugestao : sugestoes) {
            Long uid = ((Number) sugestao.get("id")).longValue();
            Optional<Cluster> clusterOpt = clusterService.findByUserId(uid);
            if (clusterOpt.isPresent()){
                sugestao.put("userRole", clusterOpt.get().getUserRole());
            }
        }
        // Filtra as sugestões para retornar apenas perfis com role igual a "aluno"
        List<Map<String, Object>> filteredSugestoes = sugestoes.stream()
                .filter(s -> s.containsKey("userRole") && 
                             s.get("userRole") != null &&
                             "aluno".equalsIgnoreCase(s.get("userRole").toString()))
                .collect(Collectors.toList());
        responseBody.put("sugestoes", filteredSugestoes);
        return ResponseEntity.ok(responseBody);
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
            // Não definimos a role aqui; ela será obtida via microserviço de login
            return map;
        }).collect(Collectors.toList());
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", 0);
        payload.put("students", students);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = httpServletRequest.getHeader("Authorization");
        if (token != null && !token.isEmpty()){
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
            String role = null; // Não forçamos valor padrão
            HttpHeaders authHeaders = new HttpHeaders();
            if (token != null && !token.isEmpty()){
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
                    role = userResponse.getBody().getRole();
                }
            } catch (Exception e) {
                // role permanece null se a consulta falhar
            }
            // Ignora se for admin
            if (role != null && "ADMIN".equalsIgnoreCase(role)) {
                continue;
            }
            Optional<Cluster> existingOpt = clusterService.findByUserId(uid);
            Cluster c;
            if (existingOpt.isPresent()){
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
        // 6. Salva (cria ou atualiza) os registros na tabela "cluster"
        clusterService.saveAll(clustersToSave);
        return ResponseEntity.ok("Clusters atualizados com sucesso");
    }
}