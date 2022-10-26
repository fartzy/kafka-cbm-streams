package com.acme.cbmkafka.consumer.dto;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EDASequence implements Serializable {

	private String part1;
	private Long part2;
	
	public String getPart1() {
		return part1;
	}
	
	public void setPart1(String part1) {
		this.part1 = part1;
	}
	
	public Long getPart2() {
		return part2;
	}
	
	public void setPart2(Long part2) {
		this.part2 = part2;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EDASequence [part1=").append(part1).append(", part2=").append(part2).append("]");
		return builder.toString();
	}
}
