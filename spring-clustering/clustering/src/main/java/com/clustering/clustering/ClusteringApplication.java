package com.clustering.clustering;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//inicializa o contexto Spring, carrega todos os beans, configurações, controladores, etc.
@SpringBootApplication
public class ClusteringApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClusteringApplication.class, args);
	}

}