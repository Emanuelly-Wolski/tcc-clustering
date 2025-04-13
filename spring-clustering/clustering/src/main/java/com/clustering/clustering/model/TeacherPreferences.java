package com.clustering.clustering.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
@Table(name = "preferencias_professor")
public class TeacherPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "turno", nullable = false)
    private String turno;

    @Column(name = "disponivel_orientacao", nullable = false)
    private String disponivelOrientacao;

    @Column(name = "linguagens_programacao", nullable = true)
    private String linguagensProgramacao; // Armazenado como string separada por vírgulas

    @Column(name = "disciplinas_lecionadas", nullable = true)
    private String disciplinasLecionadas;

    @Column(name = "habilidades_pessoais", nullable = true)
    private String habilidadesPessoais;

    @Column(name = "temas_interesse", nullable = true)
    private String temasInteresse;

    @Column(name = "disponibilidade", nullable = false)
    private String disponibilidade;

    @Column(name = "modalidade_trabalho", nullable = false)
    private String modalidadeTrabalho;

    // NOVOS CAMPOS para identificar o professor
    @JsonProperty("user_id")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_email")
    private String userEmail;

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

    public String getTemasInteresse() {
        return temasInteresse;
    }

    public void setTemasInteresse(String temasInteresse) {
        this.temasInteresse = temasInteresse;
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

    // Método para atualizar os campos
    public void updateFrom(TeacherPreferences updated) {
        if (updated.getTurno() != null) {
            this.turno = updated.getTurno();
        }
        if (updated.getDisponivelOrientacao() != null) {
            this.disponivelOrientacao = updated.getDisponivelOrientacao();
        }
        if (updated.getLinguagensProgramacao() != null) {
            this.linguagensProgramacao = updated.getLinguagensProgramacao();
        }
        if (updated.getDisciplinasLecionadas() != null) {
            this.disciplinasLecionadas = updated.getDisciplinasLecionadas();
        }
        if (updated.getHabilidadesPessoais() != null) {
            this.habilidadesPessoais = updated.getHabilidadesPessoais();
        }
        if (updated.getTemasInteresse() != null) {
            this.temasInteresse = updated.getTemasInteresse();
        }
        if (updated.getDisponibilidade() != null) {
            this.disponibilidade = updated.getDisponibilidade();
        }
        if (updated.getModalidadeTrabalho() != null) {
            this.modalidadeTrabalho = updated.getModalidadeTrabalho();
        }
        if (updated.getUserId() != null) {
            this.userId = updated.getUserId();
        }
        if (updated.getUserName() != null) {
            this.userName = updated.getUserName();
        }
        if (updated.getUserEmail() != null) {
            this.userEmail = updated.getUserEmail();
        }
    }
}
