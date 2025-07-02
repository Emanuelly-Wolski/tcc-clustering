package com.clustering.clustering.dto;

import java.util.List;

// ----------------- NÃO ESTÁ SENDO UTILIZADO --------------------------------------------------------
//representa os dados enviados pelo front quando o aluno preenche e envia o formulário de preferências

public record SudentPreferencesRequestDTO(
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