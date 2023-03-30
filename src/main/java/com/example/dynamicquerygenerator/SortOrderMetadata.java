package com.example.dynamicquerygenerator;

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
