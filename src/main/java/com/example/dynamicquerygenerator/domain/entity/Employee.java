package com.example.dynamicquerygenerator.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "employee")
public class Employee {

	public Employee() {

	}

	public Employee(String firstName, String lastName, Double salary, String emailId, Department department) {

		this.firstName = firstName;
		this.lastName = lastName;
		this.salary = salary;
		this.emailId = emailId;
		this.department = department;
	}

	@Id
	@GeneratedValue(strategy  = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	@Column(name = "first_name")
	private String firstName;
	@Column(name = "last_name")
	private String lastName;
	@Column(name = "salary")
	private Double salary;
	@Column(name = "email_id")
	private String emailId;

	@ManyToOne
	@JoinColumn(name = "department_id")
	private Department department;
}
