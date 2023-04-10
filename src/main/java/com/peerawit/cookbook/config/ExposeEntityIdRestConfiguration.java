package com.peerawit.cookbook.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class ExposeEntityIdRestConfiguration implements RepositoryRestConfigurer {

	@Autowired
	private EntityManager entityManager;

	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config,
			CorsRegistry cors) {

		entityManager.getMetamodel().getEntities().stream().map(entity -> entity.getJavaType())
				.forEach(entityClass -> config.exposeIdsFor(entityClass));

	}

}
