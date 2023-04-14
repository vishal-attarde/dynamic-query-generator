package com.example.dynamicquerygenerator.domain.filter;

import com.example.dynamicquerygenerator.domain.filter.Filter;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.Map;


@Slf4j
public abstract class AbstractFilter {

    public abstract Predicate toPredicate(Root root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, Map<String, Join<Object, Object>> attributeToJoin);

    public Predicate getPredicate(Filter filter, CriteriaBuilder criteriaBuilder, Path expression) {

        Predicate predicate = null;
        switch (filter.getOperator()) {
            case EQUAL_TO:
                predicate = criteriaBuilder.equal(expression, filter.getValue());
                break;
            case LIKE:
                predicate = criteriaBuilder.like(expression, "%" + filter.getValue() + "%");
                break;
            case IN:
                predicate = criteriaBuilder.in(expression).value(filter.getValue());
                break;
            case GT:
                predicate = criteriaBuilder.greaterThan(expression, (Comparable) filter.getValue());
                break;
            case LT:
                predicate = criteriaBuilder.lessThan(expression, (Comparable) filter.getValue());
                break;
            case GTE:
                predicate = criteriaBuilder.greaterThanOrEqualTo(expression, (Comparable) filter.getValue());
                break;
            case LTE:
                predicate = criteriaBuilder.lessThanOrEqualTo(expression, (Comparable) filter.getValue());
                break;
            case NOT_EQUAL:
                predicate = criteriaBuilder.notEqual(expression, filter.getValue());
                break;
            case IS_NULL:
                predicate = criteriaBuilder.isNull(expression);
                break;
            case NOT_NULL:
                predicate = criteriaBuilder.isNotNull(expression);
                break;
            default:
                log.error("Invalid Operator");
                throw new IllegalArgumentException(filter.getOperator() + " is not valid operator");
        }
        return predicate;
    }

    public Predicate getPredicateFromFilter(Filter filter, Root root, CriteriaBuilder criteriaBuilder, Map<String, Join<Object, Object>> attributeToJoin) {
        Assert.notNull(filter,"Filter must not be null");
        if (attributeToJoin != null && attributeToJoin.get(filter.getEntityName()) != null) {
            return  getPredicate(filter, criteriaBuilder, attributeToJoin.get(filter.getEntityName()).get(filter.getField()));
        } else {
            return getPredicate(filter, criteriaBuilder, root.get(filter.getField()));
        }
    }
}
