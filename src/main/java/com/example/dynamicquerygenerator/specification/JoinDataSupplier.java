package com.example.dynamicquerygenerator.specification;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

import java.util.LinkedHashMap;
import java.util.Map;

public interface JoinDataSupplier<T> {

	default Map<String, Join<Object, Object>> getJoinData(Root<T> root, CriteriaQuery<?> query) {
		return new LinkedHashMap<>();
	}
}
