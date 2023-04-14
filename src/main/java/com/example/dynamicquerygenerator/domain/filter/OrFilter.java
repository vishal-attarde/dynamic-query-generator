package com.example.dynamicquerygenerator.domain.filter;

import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrFilter extends AbstractFilter {

    private List<AbstractFilter> filters;

    @Override
    public Predicate toPredicate(Root root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, Map<String, Join<Object, Object>> attributeToJoi) {
       return criteriaBuilder.or(filters.stream().map(filter -> filter.toPredicate(root,query,criteriaBuilder,attributeToJoi)).collect(Collectors.toList()).toArray(Predicate[]::new));
    }
}
