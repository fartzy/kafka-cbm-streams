package com.acme.cbmkafka.consumer.config;

import java.util.List;

public class FilterProcessorConfig {

	private String name;
	private String filterType = "OUT";
	private String filterField;
	
	private List<String> filterValues;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public String getFilterField() {
		return filterField;
	}

	public void setFilterField(String filterField) {
		this.filterField = filterField;
	}

	public List<String> getFilterValues() {
		return filterValues;
	}

	public void setFilterValues(List<String> filterValues) {
		this.filterValues = filterValues;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FilterProcessorConfig [name=").append(name).append(", filterType=").append(filterType)
				.append(", filterField=").append(filterField).append(", filterValues=").append(filterValues)
				.append("]");
		return builder.toString();
	}
}