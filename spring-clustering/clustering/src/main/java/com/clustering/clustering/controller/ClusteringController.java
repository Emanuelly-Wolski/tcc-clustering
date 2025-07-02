package com.clustering.clustering.controller;

import com.clustering.clustering.dto.UserDTO;
import com.clustering.clustering.model.Cluster;
import com.clustering.clustering.model.StudentPreferences;
import com.clustering.clustering.model.ProfessorPreferences;
import com.clustering.clustering.service.ClusterService;
import com.clustering.clustering.service.StudentPreferencesService;
import com.clustering.clustering.service.ProfessorPreferencesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/clustering")
@CrossOrigin(origins = "*")
public class ClusteringController {

    // Injeta serviço para acessar preferências de alunos
    @Autowired
    private StudentPreferencesService studentPreferencesService;

    @Autowired
    private ProfessorPreferencesService professorPreferencesService;

    // O RestTemplate é injetado aqui como bean (não precisa instanciar os objetos manualmente), configurado previamente na classe AppConfig.java
    // Ele permite enviar requisições HTTP para outros microsserviços...
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    // URL do serviço Python que realiza a clusterização, comunicação feita diretamente por meio do restTemplate
    private final String clusteringServiceUrl = "http://localhost:8001/clustering";
    // URL do serviço de autenticação (para buscar a role do usuário)
    private final String userServiceUrl = "http://localhost:3000/api/auth/users/";

    // Endpoint que retorna sugestões de perfis compatíveis com um usuário
    @GetMapping("/suggestions/{userId}")
    public ResponseEntity<?> getSuggestions(@PathVariable Long userId,
                                            @RequestParam(name = "targetRole", defaultValue = "aluno") String targetRole) {
        List<Map<String, Object>> profiles;

        // Se o objetivo for buscar orientador, usa dados de professores + adiciona o aluno logado
        if (targetRole.equalsIgnoreCase("professor")) {
            List<ProfessorPreferences> professorPrefs = professorPreferencesService.findAll();
            List<Map<String, Object>> professorCandidates = professorPrefs.stream().map(pref -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", pref.getUserId());
                map.put("nomeCompleto", pref.getUserName());
                map.put("email", pref.getUserEmail());
                map.put("turno", pref.getShift());
                map.put("disponibilidade", pref.getAvailability());
                map.put("temasDeInteresse", pref.getInterestTopics());
                return map;
            }).collect(Collectors.toList());

            // Busca as preferências do aluno logado
            Optional<StudentPreferences> userOpt = studentPreferencesService.listarTodos().stream()
                    .filter(p -> p.getUserId().equals(userId)).findFirst();
            if (userOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");

            StudentPreferences aluno = userOpt.get();
            Map<String, Object> userRecord = new HashMap<>();
            userRecord.put("id", aluno.getUserId());
            userRecord.put("nomeCompleto", aluno.getUserName());
            userRecord.put("email", aluno.getUserEmail());
            userRecord.put("turno", aluno.getTurno());
            userRecord.put("disponibilidade", aluno.getDisponibilidade());
            userRecord.put("temasDeInteresse", aluno.getTemasDeInteresse());

            profiles = new ArrayList<>(professorCandidates);
            profiles.add(userRecord);

        // Se objetivo for buscar alunos, pega todos os alunos (incluindo o logado)
        } else {
            profiles = studentPreferencesService.listarTodos().stream().map(pref -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", pref.getUserId());
                map.put("nomeCompleto", pref.getUserName());
                map.put("email", pref.getUserEmail());
                map.put("turno", pref.getTurno());
                map.put("disponibilidade", pref.getDisponibilidade());
                map.put("temasDeInteresse", pref.getTemasDeInteresse());
                return map;
            }).collect(Collectors.toList());
        }

        // Monta o payload para enviar ao Python
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("students", profiles);

        // Prepara headers HTTP com o token, se existir
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = httpServletRequest.getHeader("Authorization");
        if (token != null && !token.isEmpty()) headers.set("Authorization", token);

        // Aqui é criado um objeto que agrupa o que será enviado na requisição para o serviço de clusterização
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        // Envia requisição POST ao serviço Python (/clustering), espera como resposta um JSON, que será convertido para um Map Java automaticamente
        ResponseEntity<Map> response = restTemplate.postForEntity(clusteringServiceUrl, request, Map.class);
        Map responseBody = response.getBody(); //Extrai apenas o corpo da resposta, que é onde estão os dados retornados pelo python.

        // Recupera as sugestões (json,lista de objetos) da resposta do serviço Python e converte para uma lista de perfis sugeridos
        List<Map<String, Object>> sugestoes = (List<Map<String, Object>>) responseBody.get("sugestoes");

        // Para cada sugestão, busca a role do usuário (aluno ou professor)
        for (Map<String, Object> s : sugestoes) {
            Long uid = ((Number) s.get("id")).longValue();
            s.put("userRole", clusterService.findByUserId(uid).map(Cluster::getUserRole).orElse(targetRole));
        }

        // Filtra para garantir que só devolve perfis do papel correto
        List<Map<String, Object>> filtered = sugestoes.stream()
                .filter(s -> targetRole.equalsIgnoreCase(String.valueOf(s.get("userRole"))))
                .collect(Collectors.toList());

        // Atualiza o corpo da resposta com a lista filtrada de sugestões e retorna como resposta final ao front
        responseBody.put("sugestoes", filtered);
        return ResponseEntity.ok(responseBody);
    }

    // Atualiza os clusters de todos os perfis
    @GetMapping("/update")
    public ResponseEntity<?> updateClusters(@RequestParam(name = "targetRole", defaultValue = "aluno") String targetRole) {

        // Constrói lista de perfis conforme o papel
        List<Map<String, Object>> profiles;
        if (targetRole.equalsIgnoreCase("professor")) {
            profiles = professorPreferencesService.findAll().stream().map(pref -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", pref.getUserId());
                map.put("nomeCompleto", pref.getUserName());
                map.put("email", pref.getUserEmail());
                map.put("turno", pref.getShift());
                map.put("disponibilidade", pref.getAvailability());
                map.put("temasDeInteresse", pref.getInterestTopics());
                return map;
            }).collect(Collectors.toList());
        } else {
            profiles = studentPreferencesService.listarTodos().stream().map(pref -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", pref.getUserId());
                map.put("nomeCompleto", pref.getUserName());
                map.put("email", pref.getUserEmail());
                map.put("turno", pref.getTurno());
                map.put("disponibilidade", pref.getDisponibilidade());
                map.put("temasDeInteresse", pref.getTemasDeInteresse());
                return map;
            }).collect(Collectors.toList());
        }

        // Monta payload com todos os perfis (userId = 0 pois não queremos sugestões)
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", 0); 
        payload.put("students", profiles);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = httpServletRequest.getHeader("Authorization");
        if (token != null && !token.isEmpty()) headers.set("Authorization", token);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(clusteringServiceUrl + "/all", request, Map.class);
        List<Map<String, Object>> clustersData = (List<Map<String, Object>>) response.getBody().get("clusters");

        // Extrai a lista de clusters retornados
        List<Cluster> clustersToSave = new ArrayList<>();
        for (Map<String, Object> map : clustersData) {
            Long uid = ((Number) map.get("id")).longValue();
            int clusterNumber = ((Number) map.get("cluster")).intValue();
            String role = null;

            // Busca a role do usuário no serviço de autenticação
            try {
                HttpHeaders authHeaders = new HttpHeaders();
                if (token != null && !token.isEmpty()) authHeaders.set("Authorization", token);
                HttpEntity<String> authEntity = new HttpEntity<>(authHeaders);
                ResponseEntity<UserDTO> userResponse = restTemplate.exchange(
                        userServiceUrl + uid, HttpMethod.GET, authEntity, UserDTO.class);
                if (userResponse.getStatusCode().is2xxSuccessful()) {
                    role = userResponse.getBody().getRole();
                }
            } catch (Exception ignored) {}

            // Ignora administradores
            if ("ADMIN".equalsIgnoreCase(role)) continue;

            // Atualiza ou cria o registro de cluster no banco
            Cluster c = clusterService.findByUserId(uid).orElseGet(Cluster::new);
            c.setUserId(uid);
            c.setClusterId(clusterNumber);
            c.setUserRole(role);
            clustersToSave.add(c);
        }

        clusterService.saveAll(clustersToSave);
        return ResponseEntity.ok("Clusters atualizados com sucesso");
    }
}