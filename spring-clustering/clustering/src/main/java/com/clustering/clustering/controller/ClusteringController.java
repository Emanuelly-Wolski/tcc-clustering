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

    private final String clusteringServiceUrl = "http://localhost:8001/clustering";
    private final String userServiceUrl = "http://localhost:3000/api/auth/users/";

    @GetMapping("/suggestions/{userId}")
    public ResponseEntity<?> getSuggestions(@PathVariable Long userId,
                                            @RequestParam(name = "targetRole", defaultValue = "aluno") String targetRole) {
        List<Map<String, Object>> profiles;
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

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("students", profiles);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = httpServletRequest.getHeader("Authorization");
        if (token != null && !token.isEmpty()) headers.set("Authorization", token);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(clusteringServiceUrl, request, Map.class);
        Map responseBody = response.getBody();

        List<Map<String, Object>> sugestoes = (List<Map<String, Object>>) responseBody.get("sugestoes");
        for (Map<String, Object> s : sugestoes) {
            Long uid = ((Number) s.get("id")).longValue();
            s.put("userRole", clusterService.findByUserId(uid).map(Cluster::getUserRole).orElse(targetRole));
        }

        List<Map<String, Object>> filtered = sugestoes.stream()
                .filter(s -> targetRole.equalsIgnoreCase(String.valueOf(s.get("userRole"))))
                .collect(Collectors.toList());

        responseBody.put("sugestoes", filtered);
        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/update")
    public ResponseEntity<?> updateClusters(@RequestParam(name = "targetRole", defaultValue = "aluno") String targetRole) {
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

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", 0); // No update específico, apenas todos para clusterizar
        payload.put("students", profiles);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = httpServletRequest.getHeader("Authorization");
        if (token != null && !token.isEmpty()) headers.set("Authorization", token);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(clusteringServiceUrl + "/all", request, Map.class);
        List<Map<String, Object>> clustersData = (List<Map<String, Object>>) response.getBody().get("clusters");

        List<Cluster> clustersToSave = new ArrayList<>();
        for (Map<String, Object> map : clustersData) {
            Long uid = ((Number) map.get("id")).longValue();
            int clusterNumber = ((Number) map.get("cluster")).intValue();
            String role = null;
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

            if ("ADMIN".equalsIgnoreCase(role)) continue;

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