package com.clustering.clustering.repository;

import com.clustering.clustering.model.Cluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
Camada de repositório é utilizada dentro da classe ClusterService para realizar as operações no banco...
essa interface herda de JpaRepository e fornece todos os métodos básicos prontos para CRUD
*/

@Repository
public interface ClusterRepository extends JpaRepository<Cluster, Long> {

    Optional<Cluster> findByUserId(Long userId);
}
