package com.clustering.clustering.model;

import jakarta.persistence.*;

@Entity
@Table(name = "preferencias_aluno")
public class PreferenciasAluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "turno", nullable = false)
    private String turno;

    @Column(name = "linguagens_programacao", nullable = true)
    private String linguagensProgramacao;  // Armazenado como string separada por vírgulas

    @Column(name = "bancos_dados", nullable = true)
    private String bancosDeDados;  

    @Column(name = "nivel_experiencia", nullable = false)
    private String nivelDeExperiencia;

    @Column(name = "habilidades_pessoais", nullable = true)
    private String habilidadesPessoais;  

    @Column(name = "temas_interesse", nullable = true)
    private String temasDeInteresse;  

    @Column(name = "framework_front", nullable = true)
    private String frameworkFront;  

    @Column(name = "disponibilidade", nullable = false)
    private String disponibilidade;

    @Column(name = "modalidade_trabalho", nullable = false)
    private String modalidadeTrabalho;

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

    public String getLinguagensProgramacao() {
        return linguagensProgramacao;
    }

    public void setLinguagensProgramacao(String linguagensProgramacao) {
        this.linguagensProgramacao = linguagensProgramacao;
    }

    public String getBancosDeDados() {
        return bancosDeDados;
    }

    public void setBancosDeDados(String bancosDeDados) {
        this.bancosDeDados = bancosDeDados;
    }

    public String getNivelDeExperiencia() {
        return nivelDeExperiencia;
    }

    public void setNivelDeExperiencia(String nivelDeExperiencia) {
        this.nivelDeExperiencia = nivelDeExperiencia;
    }

    public String getHabilidadesPessoais() {
        return habilidadesPessoais;
    }

    public void setHabilidadesPessoais(String habilidadesPessoais) {
        this.habilidadesPessoais = habilidadesPessoais;
    }

    public String getTemasDeInteresse() {
        return temasDeInteresse;
    }

    public void setTemasDeInteresse(String temasDeInteresse) {
        this.temasDeInteresse = temasDeInteresse;
    }

    public String getFrameworkFront() {
        return frameworkFront;
    }

    public void setFrameworkFront(String frameworkFront) {
        this.frameworkFront = frameworkFront;
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
}
