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
    @CollectionTable(name = "aluno_linguagens_programacao", joinColumns = @JoinColumn(name = "aluno_id"))
    @Column(name = "linguagem")
    private List<String> linguagensProgramacao;

    @ElementCollection
    @CollectionTable(name = "aluno_bancos_dados", joinColumns = @JoinColumn(name = "aluno_id"))
    @Column(name = "banco")
    private List<String> bancosDeDados;

    @Column(name = "nivel_experiencia", nullable = false)
    private String nivelDeExperiencia;

    @ElementCollection
    @CollectionTable(name = "aluno_habilidades_pessoais", joinColumns = @JoinColumn(name = "aluno_id"))
    @Column(name = "habilidade")
    private List<String> habilidadesPessoais;

    @ElementCollection
    @CollectionTable(name = "aluno_temas_interesse", joinColumns = @JoinColumn(name = "aluno_id"))
    @Column(name = "tema")
    private List<String> temasDeInteresse;

    @Column(name = "disponibilidade", nullable = false)
    private String disponibilidade;

    @Column(name = "modalidade_trabalho", nullable = false)
    private String modalidadeTrabalho;

    // Getters e Setters
}
