package com.peerawit.cookbook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peerawit.cookbook.entity.Role;


public interface RoleRepository extends JpaRepository<Role, Long>{
	
	Optional<Role> findByName(String name);

}
