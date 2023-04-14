package com.example.dynamicquerygenerator.repository;

import com.example.dynamicquerygenerator.domain.entity.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee,Integer> {
}
