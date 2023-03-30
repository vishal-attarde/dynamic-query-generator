package com.example.dynamicquerygenerator;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

@Data
@Slf4j
public class GenericSpecification<T> implements Specification<T> {

	private AbstractFilter filter;
	private JoinDataSupplier<T> joinDataSupplier;

	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

		if (joinDataSupplier != null && filter !=null) {
			return filter.toPredicate(root, query, criteriaBuilder, joinDataSupplier.getJoinData(root,query));
		}
		return criteriaBuilder.conjunction();
	}
}
