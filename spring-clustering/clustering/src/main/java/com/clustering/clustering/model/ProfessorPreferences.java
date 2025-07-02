package com.clustering.clustering.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.List;

@Entity //entidade JPA (tabela no banco)
@Table(name = "professor_preferences")
public class ProfessorPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shift", nullable = false)
    private String shift;

    @Column(name = "available_for_advising", nullable = false)
    private String availableForAdvising;

    /*Os campos que são listas usam @ElementCollection, o que cria tabelas auxiliares no banco (ex: temas_interesse, linguagens_programacao) ligadas por uma chave estrangeira (professor_preferences_id)*/
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "programming_languages_professor", joinColumns = @JoinColumn(name = "professor_preferences_id"))
    @Column(name = "language")
    private List<String> programmingLanguages;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "taught_subjects_professor", joinColumns = @JoinColumn(name = "professor_preferences_id"))
    @Column(name = "subject")
    private List<String> taughtSubjects;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "personal_skills_professor", joinColumns = @JoinColumn(name = "professor_preferences_id"))
    @Column(name = "skill")
    private List<String> personalSkills;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "interest_topics_professor", joinColumns = @JoinColumn(name = "professor_preferences_id"))
    @Column(name = "topic")
    private List<String> interestTopics;

    @Column(name = "availability", nullable = false)
    private String availability;

    @Column(name = "work_modality", nullable = false)
    private String workModality;

    @JsonProperty("user_id")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_email")
    private String userEmail;

    // Getters e Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }

    public String getAvailableForAdvising() { return availableForAdvising; }
    public void setAvailableForAdvising(String availableForAdvising) { this.availableForAdvising = availableForAdvising; }

    public List<String> getProgrammingLanguages() { return programmingLanguages; }
    public void setProgrammingLanguages(List<String> programmingLanguages) { this.programmingLanguages = programmingLanguages; }

    public List<String> getTaughtSubjects() { return taughtSubjects; }
    public void setTaughtSubjects(List<String> taughtSubjects) { this.taughtSubjects = taughtSubjects; }

    public List<String> getPersonalSkills() { return personalSkills; }
    public void setPersonalSkills(List<String> personalSkills) { this.personalSkills = personalSkills; }

    public List<String> getInterestTopics() { return interestTopics; }
    public void setInterestTopics(List<String> interestTopics) { this.interestTopics = interestTopics; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    public String getWorkModality() { return workModality; }
    public void setWorkModality(String workModality) { this.workModality = workModality; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    // Método para atualizar os campos
    public void updateFrom(ProfessorPreferences updated) {
        if (updated.getShift() != null) this.shift = updated.getShift();
        if (updated.getAvailableForAdvising() != null) this.availableForAdvising = updated.getAvailableForAdvising();
        if (updated.getProgrammingLanguages() != null) this.programmingLanguages = updated.getProgrammingLanguages();
        if (updated.getTaughtSubjects() != null) this.taughtSubjects = updated.getTaughtSubjects();
        if (updated.getPersonalSkills() != null) this.personalSkills = updated.getPersonalSkills();
        if (updated.getInterestTopics() != null) this.interestTopics = updated.getInterestTopics();
        if (updated.getAvailability() != null) this.availability = updated.getAvailability();
        if (updated.getWorkModality() != null) this.workModality = updated.getWorkModality();
        if (updated.getUserId() != null) this.userId = updated.getUserId();
        if (updated.getUserName() != null) this.userName = updated.getUserName();
        if (updated.getUserEmail() != null) this.userEmail = updated.getUserEmail();
    }
}