package com.clustering.clustering.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cluster")
public class Cluster {

    @Id
    @Column(name = "user_id")
    private Long userId;  // Passa a ser a PK

    @Column(name = "cluster_id")
    private int clusterId;

    @Column(name = "user_role")
    private String userRole;

    // Getters e Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}