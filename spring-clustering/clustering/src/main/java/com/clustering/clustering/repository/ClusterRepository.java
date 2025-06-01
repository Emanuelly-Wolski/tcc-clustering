package com.clustering.clustering.repository;

import com.clustering.clustering.model.Cluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClusterRepository extends JpaRepository<Cluster, Long> {

    Optional<Cluster> findByUserId(Long userId);
}
