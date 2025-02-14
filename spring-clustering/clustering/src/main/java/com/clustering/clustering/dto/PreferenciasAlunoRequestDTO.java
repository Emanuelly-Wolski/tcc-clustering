package com.clustering.clustering.dto;

import java.util.List;

public record PreferenciasAlunoRequestDTO(
    String turno,
    List<String> linguagemProgramacao,
    List<String> bancoDeDados,
    String nivelDeExperiencia,
    List<String> habilidadesPessoais,
    List<String> temasDeInteresse,
    String disponibilidade,
    String modalidadeTrabalho,
    List<String> frameworkFront,
    Long userId
) {}