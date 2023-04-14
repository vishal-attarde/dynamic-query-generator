package com.example.dynamicquerygenerator.domain.filter;

import com.example.dynamicquerygenerator.domain.filter.FilterOperator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Filter {
	//Name of the variable from Entity class on which filter has to be applied
	String field;
	//Filter operator
    FilterOperator operator;
	//Filter value
    Object value;
	//Join identifier
    String entityName;
}
