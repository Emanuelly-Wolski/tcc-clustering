package com.clustering.clustering.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "preferencias_aluno")
public class StudentPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "turno", nullable = false)
    private String turno;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "linguagens_programacao", joinColumns = @JoinColumn(name = "preferencias_aluno_id"))
    @Column(name = "linguagem")
    private List<String> linguagemProgramacao;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "bancos_dados", joinColumns = @JoinColumn(name = "preferencias_aluno_id"))
    @Column(name = "banco")
    private List<String> bancoDeDados;

    @Column(name = "nivel_experiencia", nullable = false)
    private String nivelDeExperiencia;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "habilidades_pessoais", joinColumns = @JoinColumn(name = "preferencias_aluno_id"))
    @Column(name = "habilidade")
    private List<String> habilidadesPessoais;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "temas_interesse", joinColumns = @JoinColumn(name = "preferencias_aluno_id"))
    @Column(name = "tema")
    private List<String> temasDeInteresse;

    @Column(name = "disponibilidade", nullable = false)
    private String disponibilidade;

    @Column(name = "modalidade_trabalho", nullable = false)
    private String modalidadeTrabalho;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "framework_front", joinColumns = @JoinColumn(name = "preferencias_aluno_id"))
    @Column(name = "framework")
    private List<String> frameworkFront;

    @JsonProperty("user_id")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_email")
    private String userEmail;

    // Método para atualizar os campos 
    public void updateFrom(StudentPreferences updatedPreference) {
        if (updatedPreference.getTurno() != null) {
            this.turno = updatedPreference.getTurno();
        }
        if (updatedPreference.getlinguagemProgramacao() != null) {
            this.linguagemProgramacao = updatedPreference.getlinguagemProgramacao();
        }
        if (updatedPreference.getbancoDeDados() != null) {
            this.bancoDeDados = updatedPreference.getbancoDeDados();
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
        if (updatedPreference.getUserName() != null) {
            this.userName = updatedPreference.getUserName();
        }
        if (updatedPreference.getUserEmail() != null) {
            this.userEmail = updatedPreference.getUserEmail();
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

    public List<String> getlinguagemProgramacao() {
        return linguagemProgramacao;
    }

    public void setlinguagemProgramacao(List<String> linguagemProgramacao) {
        this.linguagemProgramacao = linguagemProgramacao;
    }

    public List<String> getbancoDeDados() {
        return bancoDeDados;
    }

    public void setbancoDeDados(List<String> bancoDeDados) {
        this.bancoDeDados = bancoDeDados;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}