package com.clustering.clustering.controller;

import com.clustering.clustering.dto.UserDTO;
import com.clustering.clustering.model.Cluster;
import com.clustering.clustering.model.StudentPreferences;
import com.clustering.clustering.model.TeacherPreferences;
import com.clustering.clustering.service.ClusterService;
import com.clustering.clustering.service.StudentPreferencesService;
import com.clustering.clustering.service.TeacherPreferencesService;
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
    private StudentPreferencesService preferenciasAlunoService;

    @Autowired
    private TeacherPreferencesService preferenciasProfessorService;

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
    @GetMapping("/sugeridos/{userId}")
    public ResponseEntity<?> getclustering(
            @PathVariable Long userId,
            @RequestParam(name="targetRole", defaultValue="aluno") String targetRole) {

        List<Map<String, Object>> profiles;
        if (targetRole.equalsIgnoreCase("professor")) {
            // Obtém os candidatos a professor da tabela de preferências de professor
            List<TeacherPreferences> allProfessorPrefs = preferenciasProfessorService.listarTodos();
            List<Map<String, Object>> professorCandidates = allProfessorPrefs.stream().map(pref -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", pref.getUserId());
                map.put("nomeCompleto", pref.getUserName());
                map.put("email", pref.getUserEmail());
                map.put("turno", pref.getTurno());
                map.put("disponibilidade", pref.getDisponibilidade());
                if (pref.getTemasInteresse() != null && !pref.getTemasInteresse().isEmpty()) {
                    List<String> temasList = Arrays.stream(pref.getTemasInteresse().split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList());
                    map.put("temasDeInteresse", temasList);
                } else {
                    map.put("temasDeInteresse", new ArrayList<>());
                }
                return map;
            }).collect(Collectors.toList());

            // Obtém o registro do usuário logado (aluno) da tabela de preferências de aluno
            List<StudentPreferences> allAlunoPrefs = preferenciasAlunoService.listarTodos();
            Optional<StudentPreferences> userAlunoOpt = allAlunoPrefs.stream()
                    .filter(pref -> pref.getUserId() == userId)
                    .findFirst();
            if (!userAlunoOpt.isPresent()) {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
            }
            StudentPreferences userAluno = userAlunoOpt.get();
            Map<String, Object> userRecord = new HashMap<>();
            userRecord.put("id", userAluno.getUserId());
            userRecord.put("nomeCompleto", userAluno.getUserName());
            userRecord.put("email", userAluno.getUserEmail());
            userRecord.put("turno", userAluno.getTurno());
            userRecord.put("disponibilidade", userAluno.getDisponibilidade());
            userRecord.put("temasDeInteresse", userAluno.getTemasDeInteresse());

            // Junta os candidatos de professores com o registro do usuário logado
            profiles = new ArrayList<>(professorCandidates);
            profiles.add(userRecord);
        } else {
            // Caso targetRole seja "aluno", usa as preferências de aluno
            List<StudentPreferences> allPrefs = preferenciasAlunoService.listarTodos();
            profiles = allPrefs.stream().map(pref -> {
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
        List<Map<String, Object>> sugestoes = (List<Map<String, Object>>) responseBody.get("sugestoes");
        for (Map<String, Object> sugestao : sugestoes) {
            Long uid = ((Number) sugestao.get("id")).longValue();
            Optional<Cluster> clusterOpt = clusterService.findByUserId(uid);
            if (clusterOpt.isPresent()){
                sugestao.put("userRole", clusterOpt.get().getUserRole());
            } else {
                sugestao.put("userRole", targetRole);
            }
        }
        // Filtra para retornar somente os perfis com role igual a targetRole
        List<Map<String, Object>> filteredSugestoes = sugestoes.stream()
                .filter(s -> s.containsKey("userRole") &&
                        s.get("userRole") != null &&
                        targetRole.equalsIgnoreCase(s.get("userRole").toString()))
                .collect(Collectors.toList());
        responseBody.put("sugestoes", filteredSugestoes);
        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/atualizar")
    public ResponseEntity<?> atualizarClusters(@RequestParam(name="targetRole", defaultValue="aluno") String targetRole) {
        List<Map<String, Object>> profiles;
        if (targetRole.equalsIgnoreCase("professor")) {
            List<TeacherPreferences> allPrefs = preferenciasProfessorService.listarTodos();
            profiles = allPrefs.stream().map(pref -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", pref.getUserId());
                map.put("nomeCompleto", pref.getUserName());
                map.put("email", pref.getUserEmail());
                map.put("turno", pref.getTurno());
                map.put("disponibilidade", pref.getDisponibilidade());
                if (pref.getTemasInteresse() != null && !pref.getTemasInteresse().isEmpty()) {
                    List<String> temasList = Arrays.stream(pref.getTemasInteresse().split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList());
                    map.put("temasDeInteresse", temasList);
                } else {
                    map.put("temasDeInteresse", new ArrayList<>());
                }
                return map;
            }).collect(Collectors.toList());
        } else {
            List<StudentPreferences> allPrefs = preferenciasAlunoService.listarTodos();
            profiles = allPrefs.stream().map(pref -> {
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