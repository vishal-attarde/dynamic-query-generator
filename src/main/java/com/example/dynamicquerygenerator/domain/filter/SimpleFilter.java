package com.example.dynamicquerygenerator.domain.filter;

import jakarta.persistence.criteria.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SimpleFilter extends AbstractFilter {

    private Filter filter;

    @Override
    public Predicate toPredicate(Root root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, Map<String, Join<Object, Object>> attributeToJoin) {
        return getPredicateFromFilter(filter,root,criteriaBuilder,attributeToJoin);
    }
}
