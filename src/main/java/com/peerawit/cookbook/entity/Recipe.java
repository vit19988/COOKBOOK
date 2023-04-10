package com.peerawit.cookbook.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.rest.core.annotation.RestResource;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@Table(name="recipes")
@Entity
@Data
public class Recipe {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	private String description;
	private String imagePath;
	
	@OneToMany(mappedBy = "recipe" ,cascade = CascadeType.ALL)
	@RestResource(exported = false)
	@JsonManagedReference
	private List<Ingredient> ingredients;

}
