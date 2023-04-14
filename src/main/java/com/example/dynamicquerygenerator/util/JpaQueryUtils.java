package com.example.dynamicquerygenerator.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

@Component
public class JpaQueryUtils {

	@PersistenceContext
	public EntityManager entityManager;


	private <S, U> Root<U> applySpecificationToCriteria(@Nullable Specification<U> spec, Class<U> domainClass,
			CriteriaQuery<S> query) {

		Assert.notNull(domainClass, "Domain class must not be null!");
		Assert.notNull(query, "CriteriaQuery must not be null!");

		Root<U> root = query.from(domainClass);

		if (spec == null) {
			return root;
		}

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		Predicate predicate = spec.toPredicate(root, query, builder);

		if (predicate != null) {
			query.where(predicate);
		}

		return root;
	}

	public <S> TypedQuery<Tuple> getTupleQuery(@Nullable Specification<S> spec, Class<S> domainClass, Sort sort) {
		CriteriaQuery<Tuple> tupleQuery =  this.entityManager.getCriteriaBuilder().createTupleQuery();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		Root<S> root = applySpecificationToCriteria(spec, domainClass, tupleQuery);
		if (sort.isSorted()) {
			tupleQuery.orderBy(org.springframework.data.jpa.repository.query.QueryUtils.toOrders(sort, root, builder));
		}
		return entityManager.createQuery(tupleQuery);
	}


	public boolean isUnpaged(Pageable pageable) {
		return pageable.isUnpaged();
	}

	public <S> Page<Tuple> getPage(TypedQuery<Tuple> query, final Class<S> domainClass, Pageable pageable, Specification<S> spec) {

		if (pageable.isPaged()) {
			query.setFirstResult((int) pageable.getOffset());
			query.setMaxResults(pageable.getPageSize());
		}
		return PageableExecutionUtils.getPage(query.getResultList(), pageable,() -> executeCountQuery(getCountQuery(spec, domainClass)));
	}

	public long executeCountQuery(TypedQuery<Long> countQuery) {
		List<Long> totals = countQuery.getResultList();
		long total = 0L;
		for (Long element : totals) {
			total += element == null ? 0 : element;
		}
		return total;
	}

	protected <S> TypedQuery<Long> getCountQuery(Specification<S> spec, Class<S> domainClass) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<S> root = query.from(domainClass);
		Predicate predicate = spec.toPredicate(root, query, builder);
		if (predicate != null) {
			query.where(predicate);
		}
		if (query.isDistinct()) {
			query.select(builder.countDistinct(root));
		} else {
			query.select(builder.count(root));
		}
		query.orderBy(Collections.<Order> emptyList());
		return entityManager.createQuery(query);
	}
}
