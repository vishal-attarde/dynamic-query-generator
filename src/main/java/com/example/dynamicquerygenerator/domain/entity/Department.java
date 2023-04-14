package com.example.dynamicquerygenerator.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "department")
public class Department {
	@Id
	@GeneratedValue(strategy  = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	@Column(name = "name")
	private String name;
	@Column(name = "cost_centre")
	private String costCenter;

	@OneToMany(mappedBy = "department")
	private List<Employee> employees;

	public Department(String name, String costCenter) {
		this.name = name;
		this.costCenter =costCenter;
	}

	public Department(){

	}
}
