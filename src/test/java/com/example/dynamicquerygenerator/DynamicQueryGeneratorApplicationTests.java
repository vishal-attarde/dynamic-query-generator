package com.example.dynamicquerygenerator;

import com.example.dynamicquerygenerator.domain.entity.Employee;
import com.example.dynamicquerygenerator.domain.filter.*;
import com.example.dynamicquerygenerator.specification.GenericSpecification;
import com.example.dynamicquerygenerator.specification.JoinDataSupplier;
import com.example.dynamicquerygenerator.util.JpaQueryUtils;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class DynamicQueryGeneratorApplicationTests {

	@Autowired
	JpaQueryUtils queryUtils;

	@Test
	void test_query_generation() {

		SimpleFilter salaryFilter = new SimpleFilter(new Filter("salary",FilterOperator.GTE,90,"Employee"));
		SimpleFilter departmentNameFilter = new SimpleFilter(new Filter("costCenter",FilterOperator.EQUAL_TO,"CostCenter-1","Department"));
		AndFilter firstAndFilter = new AndFilter(Arrays.asList(salaryFilter,departmentNameFilter));

		SimpleFilter salaryFilter1 = new SimpleFilter(new Filter("salary",FilterOperator.GTE,80,"Employee"));
		SimpleFilter departmentNameFilter1 = new SimpleFilter(new Filter("costCenter",FilterOperator.EQUAL_TO,"CostCenter-2","Department"));
		AndFilter secondAndFilter = new AndFilter(Arrays.asList(salaryFilter1,departmentNameFilter1));
		OrFilter orFilter = new OrFilter(Arrays.asList(firstAndFilter,secondAndFilter));

		SortOrderMetadata sortOrderMetadata = new SortOrderMetadata();
		sortOrderMetadata.setField("salary");
		sortOrderMetadata.setOrder(SortOrder.DESC);
		SearchQuery searchQuery =  new SearchQuery();
		searchQuery.setFilter(orFilter);
		searchQuery.setPageSize(10);
		searchQuery.setPageNumber(0);
		searchQuery.setSortOrderMetadataObjects(Arrays.asList(sortOrderMetadata));

		Specification<Employee> specification = getEmployeeDepartmentJoinSpecification(orFilter);


		PageRequest request = PageRequest.of(0,12, Sort.Direction.DESC,"salary");
		TypedQuery<Tuple> tupleTypedQuery = queryUtils.getTupleQuery(specification, Employee.class, Sort.by(Sort.Direction.DESC,"salary"));
		Page<Tuple> tuplePage =  isUnpaged(request) ? new PageImpl<Tuple>(tupleTypedQuery.getResultList())
				: queryUtils.getPage(tupleTypedQuery, Employee.class, request, specification);

		List<Tuple> serviceOrderDetailsTupleList = tuplePage.getContent();
	}

	public Specification<Employee> getEmployeeDepartmentJoinSpecification(AbstractFilter filter) {

		GenericSpecification<Employee> serviceOrderSpecification = new GenericSpecification<Employee>();
		serviceOrderSpecification.setFilter(filter);
		serviceOrderSpecification.setJoinDataSupplier(new JoinDataSupplier<Employee>() {
			@Override
			public Map<String, Join<Object, Object>> getJoinData(Root<Employee> root, CriteriaQuery<?> query) {

				Map<String, Join<Object, Object>> attributeToJoinMap = new LinkedHashMap<>();
				Join<Object, Object> joinDepartment = root.join("department", JoinType.INNER);
				attributeToJoinMap.put("Department", joinDepartment);
				query.multiselect(root,joinDepartment);
				//query.multiselect(root.get("id"), root.get("firstName"), joinDepartment.get("name"));
				return attributeToJoinMap;
			}
		});
		return serviceOrderSpecification;
	}

	public boolean isUnpaged(Pageable pageable) {

		return pageable.isUnpaged();
	}
}
