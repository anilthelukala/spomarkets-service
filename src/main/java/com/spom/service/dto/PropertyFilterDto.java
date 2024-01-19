package com.spom.service.dto;

import lombok.Data;

@Data
public class PropertyFilterDto {
	private String location;
	private Long postalCode;
	private String historicalSuburbGrowthRate;
}
