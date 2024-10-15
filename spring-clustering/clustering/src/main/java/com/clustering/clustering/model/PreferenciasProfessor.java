package com.clustering.clustering.model;

import jakarta.persistence.*;

@Entity
@Table(name = "preferencias_professor")
public class PreferenciasProfessor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "turno", nullable = false)
    private String turno;

    @Column(name = "disponivel_orientacao", nullable = false)
    private String disponivelOrientacao;

    @Column(name = "linguagens_programacao", nullable = true)
    private String linguagensProgramacao;  // Armazenado como string separada por vírgulas

    @Column(name = "disciplinas_lecionadas", nullable = true)
    private String disciplinasLecionadas;  

    @Column(name = "habilidades_pessoais", nullable = true)
    private String habilidadesPessoais; 

    @Column(name = "temas_interesse", nullable = true)
    private String temasDeInteresse;

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

    public String getDisponivelOrientacao() {
        return disponivelOrientacao;
    }

    public void setDisponivelOrientacao(String disponivelOrientacao) {
        this.disponivelOrientacao = disponivelOrientacao;
    }

    public String getLinguagensProgramacao() {
        return linguagensProgramacao;
    }

    public void setLinguagensProgramacao(String linguagensProgramacao) {
        this.linguagensProgramacao = linguagensProgramacao;
    }

    public String getDisciplinasLecionadas() {
        return disciplinasLecionadas;
    }

    public void setDisciplinasLecionadas(String disciplinasLecionadas) {
        this.disciplinasLecionadas = disciplinasLecionadas;
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
