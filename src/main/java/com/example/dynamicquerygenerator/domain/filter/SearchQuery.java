package com.example.dynamicquerygenerator.domain.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchQuery {

	private AbstractFilter filter;
	private int pageNumber;
	private int pageSize;
	private List<SortOrderMetadata> sortOrderMetadataObjects;
}
