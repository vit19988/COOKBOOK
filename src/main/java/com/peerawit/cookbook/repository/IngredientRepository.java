package com.peerawit.cookbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.peerawit.cookbook.entity.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long>{

}
