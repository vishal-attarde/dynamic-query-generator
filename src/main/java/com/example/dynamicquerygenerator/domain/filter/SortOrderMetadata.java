package com.example.dynamicquerygenerator.domain.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SortOrderMetadata {
	String field;
	SortOrder order;
}
