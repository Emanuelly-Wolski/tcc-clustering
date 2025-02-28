package com.clustering.clustering.service;

import com.clustering.clustering.model.Cluster;
import com.clustering.clustering.repository.ClusterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClusterService {

    @Autowired
    private ClusterRepository clusterRepository;

    public Optional<Cluster> findByUserId(Long userId) {
        return clusterRepository.findByUserId(userId);
    }

    public void saveAll(List<Cluster> clusters) {
        clusterRepository.saveAll(clusters);
    }

    public void save(Cluster cluster) {
        clusterRepository.save(cluster);
    }
}