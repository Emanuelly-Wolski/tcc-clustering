// ClusteringController.java
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

@RestController
@RequestMapping("/clustering")
@CrossOrigin(origins = "*")
public class ClusteringController {

    @Autowired
    private StudentPreferencesService studentPreferencesService;

    @Autowired
    private ProfessorPreferencesService professorPreferencesService;

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
     * Se targetRole for "professor", os candidatos são obtidos da tabela de preferências de professor.
     * Como o aluno é quem realiza a busca, seu registro (da tabela de preferências de aluno) é adicionado à lista,
     * permitindo que o algoritmo de clusterização identifique seu cluster.
     */
    @GetMapping("/suggestions/{userId}")
    public ResponseEntity<?> getSuggestions(
            @PathVariable Long userId,
            @RequestParam(name="targetRole", defaultValue="aluno") String targetRole) {

        List<Map<String, Object>> profiles;
        if (targetRole.equalsIgnoreCase("professor")) {
            // Obtém os candidatos a professor da tabela de preferências de professor
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

            // Obtém o registro do usuário logado (aluno) da tabela de preferências de aluno
            Optional<StudentPreferences> userStudentOpt = studentPreferencesService.listarTodos().stream()
                    .filter(pref -> pref.getUserId().equals(userId))
                    .findFirst();
            if (!userStudentOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            StudentPreferences student = userStudentOpt.get();
            Map<String, Object> userRecord = new HashMap<>();
            userRecord.put("id", student.getUserId());
            userRecord.put("nomeCompleto", student.getUserName());
            userRecord.put("email", student.getUserEmail());
            userRecord.put("turno", student.getTurno());
            userRecord.put("disponibilidade", student.getDisponibilidade());
            userRecord.put("temasDeInteresse", student.getTemasDeInteresse());

            // Junta os candidatos de professores com o registro do usuário logado
            profiles = new ArrayList<>(professorCandidates);
            profiles.add(userRecord);
        } else {
            // Caso targetRole seja "aluno", usa as preferências de aluno
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

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("students", profiles);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = httpServletRequest.getHeader("Authorization");
        if (token != null && !token.isEmpty()){
            headers.set("Authorization", token);
        }
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        // Chama o serviço Python para realizar a clusterização
        ResponseEntity<Map> response = restTemplate.postForEntity(clusteringServiceUrl, requestEntity, Map.class);
        Map responseBody = response.getBody();

        // Enriquecer as sugestões com a role
        List<Map<String, Object>> suggestions = (List<Map<String, Object>>) responseBody.get("sugestoes");
        for (Map<String, Object> suggestion : suggestions) {
            Long uid = ((Number) suggestion.get("id")).longValue();
            Optional<Cluster> clusterOpt = clusterService.findByUserId(uid);
            if (clusterOpt.isPresent()){
                suggestion.put("userRole", clusterOpt.get().getUserRole());
            } else {
                suggestion.put("userRole", targetRole);
            }
        }
        // Filtra para retornar somente os perfis com role igual a targetRole
        List<Map<String, Object>> filtered = suggestions.stream()
                .filter(s -> s.containsKey("userRole") && targetRole.equalsIgnoreCase(s.get("userRole").toString()))
                .collect(Collectors.toList());
        responseBody.put("sugestoes", filtered);
        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/update")
    public ResponseEntity<?> updateClusters(@RequestParam(name="targetRole", defaultValue="student") String targetRole) {
        List<Map<String, Object>> profiles;
        if (targetRole.equalsIgnoreCase("professor")) {
            List<ProfessorPreferences> professorPrefs = professorPreferencesService.findAll();
            profiles = professorPrefs.stream().map(pref -> {
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
            List<StudentPreferences> studentPrefs = studentPreferencesService.listarTodos();
            profiles = studentPrefs.stream().map(pref -> {
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

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", 0);
        payload.put("students", profiles);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = httpServletRequest.getHeader("Authorization");
        if (token != null && !token.isEmpty()){
            headers.set("Authorization", token);
        }
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(clusteringServiceUrl + "/all", requestEntity, Map.class);
        List<Map<String, Object>> clustersData = (List<Map<String, Object>>) response.getBody().get("clusters");

        List<Cluster> clustersToSave = new ArrayList<>();
        for (Map<String, Object> map : clustersData) {
            Long uid = ((Number) map.get("id")).longValue();
            int clusterNumber = ((Number) map.get("cluster")).intValue();

            String role = null;
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
                // Se a consulta falhar, role permanece nulo
            }
            if (role != null && "ADMIN".equalsIgnoreCase(role)) {
                continue;
            }
            Optional<Cluster> existingOpt = clusterService.findByUserId(uid);
            Cluster c;
            if (existingOpt.isPresent()){
                c = existingOpt.get();
            } else {
                c = new Cluster();
                c.setUserId(uid);
            }
            c.setClusterId(clusterNumber);
            c.setUserRole(role);
            clustersToSave.add(c);
        }
        clusterService.saveAll(clustersToSave);
        return ResponseEntity.ok("Clusters atualizados com sucesso");
    }
}