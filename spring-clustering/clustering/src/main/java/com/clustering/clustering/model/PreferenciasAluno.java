package com.clustering.clustering.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "preferencias_aluno")
public class PreferenciasAluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "turno", nullable = false)
    private String turno;

    @ElementCollection
    @CollectionTable(name = "linguagens_programacao", joinColumns = @JoinColumn(name = "preferencias_aluno_id"))
    @Column(name = "linguagem")
    private List<String> linguagensProgramacao;

    @ElementCollection
    @CollectionTable(name = "bancos_dados", joinColumns = @JoinColumn(name = "preferencias_aluno_id"))
    @Column(name = "banco")
    private List<String> bancosDeDados;

    @Column(name = "nivel_experiencia", nullable = false)
    private String nivelDeExperiencia;

    @ElementCollection
    @CollectionTable(name = "habilidades_pessoais", joinColumns = @JoinColumn(name = "preferencias_aluno_id"))
    @Column(name = "habilidade")
    private List<String> habilidadesPessoais;

    @ElementCollection
    @CollectionTable(name = "temas_interesse", joinColumns = @JoinColumn(name = "preferencias_aluno_id"))
    @Column(name = "tema")
    private List<String> temasDeInteresse;

    @Column(name = "disponibilidade", nullable = false)
    private String disponibilidade;

    @Column(name = "modalidade_trabalho", nullable = false)
    private String modalidadeTrabalho;

    @ElementCollection
    @CollectionTable(name = "framework_front", joinColumns = @JoinColumn(name = "preferencias_aluno_id"))
    @Column(name = "framework")
    private List<String> frameworkFront;

    @Column(name = "user_id") // Agora opcional
    private Long userId;

    // Método para atualizar os campos de uma preferência
    public void updateFrom(PreferenciasAluno updatedPreference) {
        if (updatedPreference.getTurno() != null) {
            this.turno = updatedPreference.getTurno();
        }
        if (updatedPreference.getLinguagensProgramacao() != null) {
            this.linguagensProgramacao = updatedPreference.getLinguagensProgramacao();
        }
        if (updatedPreference.getBancosDeDados() != null) {
            this.bancosDeDados = updatedPreference.getBancosDeDados();
        }
        if (updatedPreference.getNivelDeExperiencia() != null) {
            this.nivelDeExperiencia = updatedPreference.getNivelDeExperiencia();
        }
        if (updatedPreference.getHabilidadesPessoais() != null) {
            this.habilidadesPessoais = updatedPreference.getHabilidadesPessoais();
        }
        if (updatedPreference.getTemasDeInteresse() != null) {
            this.temasDeInteresse = updatedPreference.getTemasDeInteresse();
        }
        if (updatedPreference.getFrameworkFront() != null) {
            this.frameworkFront = updatedPreference.getFrameworkFront();
        }
        if (updatedPreference.getDisponibilidade() != null) {
            this.disponibilidade = updatedPreference.getDisponibilidade();
        }
        if (updatedPreference.getModalidadeTrabalho() != null) {
            this.modalidadeTrabalho = updatedPreference.getModalidadeTrabalho();
        }
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public List<String> getLinguagensProgramacao() {
        return linguagensProgramacao;
    }

    public void setLinguagensProgramacao(List<String> linguagensProgramacao) {
        this.linguagensProgramacao = linguagensProgramacao;
    }

    public List<String> getBancosDeDados() {
        return bancosDeDados;
    }

    public void setBancosDeDados(List<String> bancosDeDados) {
        this.bancosDeDados = bancosDeDados;
    }

    public String getNivelDeExperiencia() {
        return nivelDeExperiencia;
    }

    public void setNivelDeExperiencia(String nivelDeExperiencia) {
        this.nivelDeExperiencia = nivelDeExperiencia;
    }

    public List<String> getHabilidadesPessoais() {
        return habilidadesPessoais;
    }

    public void setHabilidadesPessoais(List<String> habilidadesPessoais) {
        this.habilidadesPessoais = habilidadesPessoais;
    }

    public List<String> getTemasDeInteresse() {
        return temasDeInteresse;
    }

    public void setTemasDeInteresse(List<String> temasDeInteresse) {
        this.temasDeInteresse = temasDeInteresse;
    }

    public String getDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(String disponibilidade) {
        this.disponibilidade = disponibilidade;
    }

    public String getModalidadeTrabalho() {
        return modalidadeTrabalho;
    }

    public void setModalidadeTrabalho(String modalidadeTrabalho) {
        this.modalidadeTrabalho = modalidadeTrabalho;
    }

    public List<String> getFrameworkFront() {
        return frameworkFront;
    }

    public void setFrameworkFront(List<String> frameworkFront) {
        this.frameworkFront = frameworkFront;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}