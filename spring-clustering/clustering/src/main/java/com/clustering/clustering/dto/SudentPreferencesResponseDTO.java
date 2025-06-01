package com.clustering.clustering.dto;

import java.util.List;
import com.clustering.clustering.model.StudentPreferences;

public record SudentPreferencesResponseDTO(
    Long id,
    String turno,
    List<String> linguagemProgramacao,
    List<String> bancoDeDados,
    String nivelDeExperiencia,
    List<String> habilidadesPessoais,
    List<String> temasDeInteresse,
    String disponibilidade,
    String modalidadeTrabalho,
    List<String> frameworkFront,
    Long userId,
    String nomeUsuario,
    String emailUsuario
) {
    /**
     * Construtor que mapeia StudentPreferences e adiciona
     * os dados do usuário (nome e email) obtidos do microserviço de login
     */
    public SudentPreferencesResponseDTO(StudentPreferences pref, String nomeUsuario, String emailUsuario) {
        this(
            pref.getId(),
            pref.getTurno(),
            pref.getlinguagemProgramacao(),
            pref.getbancoDeDados(),
            pref.getNivelDeExperiencia(),
            pref.getHabilidadesPessoais(),
            pref.getTemasDeInteresse(),
            pref.getDisponibilidade(),
            pref.getModalidadeTrabalho(),
            pref.getFrameworkFront(),
            pref.getUserId(),
            nomeUsuario,
            emailUsuario
        );
    }
}