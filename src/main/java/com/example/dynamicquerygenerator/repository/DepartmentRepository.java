package com.example.dynamicquerygenerator.repository;

import com.example.dynamicquerygenerator.domain.entity.Department;
import org.springframework.data.repository.CrudRepository;

public interface DepartmentRepository extends CrudRepository<Department,Integer> {
}
