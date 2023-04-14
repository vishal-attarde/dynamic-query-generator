# Dynamic query generator
The dynamic query generator helps in generating dynamic queries on joined table with the help of Criteria API and Specification support.

### Features
   1: Compatible with Spring data jpa 3.0.4

   2: Dynamic query generation on joined tables.

   3: Query generation for any nested level of AND/OR filter using filtering model.

   4: Sorting and Pagination support

   5: Support for all types of operators (EQUAL_TO,LIKE,IN,GT,GTE,LT,LTE,NOT_EQUAL,IS_NULL,NOT_NULL)

### Example
Let us consider the following simple database schema:

1: Employee entity with basic details and Many-to-One association with Department entity.
2: Department entity with basic details and One-to-Many associations with Employee entity.

Let’s consider a hypothetical example where we need to retrieve employee data whose salary is greater than 90k from cost center “CostCenter-1” or employee data whose salary is greater than 80k from cost center “CostCenter-2”. This query requires a join between Employee and Department tables. 


Create filters and sorting data required for above query:

```java

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
		
```


Create a specification from the filter and join data:
```java
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

```

Using specification to get the paginated response

```java
Specification<Employee> specification = getEmployeeDepartmentJoinSpecification(searchQuery.getFilter());

PageRequest request = queryUtils.getPageRequest(searchQuery);
TypedQuery<Tuple> tupleTypedQuery = queryUtils.getTupleQuery(specification, Employee.class, Sort.by(Sort.Direction.DESC,"salary"));
Page<Tuple> tuplePage =  isUnpaged(request) ? new PageImpl<Tuple>(tupleTypedQuery.getResultList())
				: queryUtils.getPage(tupleTypedQuery, Employee.class, request, specification);

List<Tuple> serviceOrderDetailsTupleList = tuplePage.getContent();
	}
```
you can refer to unit test `DynamicQueryGeneratorApplicationTests.test_query_generation` for the implementation details.




