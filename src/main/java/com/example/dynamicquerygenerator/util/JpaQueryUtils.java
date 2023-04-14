package com.example.dynamicquerygenerator.util;

import com.example.dynamicquerygenerator.domain.entity.Employee;
import com.example.dynamicquerygenerator.domain.filter.SearchQuery;
import com.example.dynamicquerygenerator.domain.filter.SortOrder;
import com.example.dynamicquerygenerator.domain.filter.SortOrderMetadata;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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


	public PageRequest getPageRequest(SearchQuery searchQuery) {

		List<SortOrderMetadata> sortOrderMetadataList = searchQuery.getSortOrderMetadataObjects();
		List<Sort.Order> orders = new ArrayList<>();
		if (!CollectionUtils.isEmpty(sortOrderMetadataList)) {
			orders.addAll(sortOrderMetadataList.stream().map(sortOrderMetadata -> {
				if (sortOrderMetadata.getOrder().equals(SortOrder.ASC)) {
					return Sort.Order.asc(sortOrderMetadata.getField());
				}
				else {
					return Sort.Order.desc(sortOrderMetadata.getField());
				}
			}).filter(Objects::nonNull).collect(Collectors.toList()));
		}

		Sort sort = Sort.by(orders);
		return PageRequest.of(searchQuery.getPageNumber(), searchQuery.getPageSize(), sort);
	}


	public Page<Tuple> getPagedData(Specification specification,SearchQuery searchQuery) {
		PageRequest request = getPageRequest(searchQuery);
		TypedQuery<Tuple> tupleTypedQuery = getTupleQuery(specification, Employee.class, Sort.by(Sort.Direction.DESC,"salary"));
		Page<Tuple> tuplePage =  isUnpaged(request) ? new PageImpl<Tuple>(tupleTypedQuery.getResultList())
				: getPage(tupleTypedQuery, Employee.class, request, specification);
		return tuplePage;
	}
}
