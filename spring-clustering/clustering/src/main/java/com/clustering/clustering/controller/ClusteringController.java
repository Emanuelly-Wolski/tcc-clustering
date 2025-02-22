// ClusteringController.java
package com.clustering.clustering.controller;

import com.clustering.clustering.model.PreferenciasAluno;
import com.clustering.clustering.service.PreferenciasAlunoService;
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

    // URL do serviço Python (FastAPI) agora aponta para '/clustering'
    private final String clusteringServiceUrl = "http://localhost:8001/clustering";

    @GetMapping("/sugeridos/{userId}")
    public ResponseEntity<?> getclustering(@PathVariable Long userId) {
        // 1. Busca todas as preferências dos alunos do banco
        List<PreferenciasAluno> allPrefs = preferenciasAlunoService.listarTodos();

        // 2. Mapeia os dados para o formato esperado pelo serviço Python
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

        // 3. Monta o payload para enviar ao serviço Python
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("students", students);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        // 4. Chama o endpoint do serviço Python e obtém a resposta
        ResponseEntity<Map> response = restTemplate.postForEntity(clusteringServiceUrl, requestEntity, Map.class);
        
        return ResponseEntity.ok(response.getBody());
    }
}
