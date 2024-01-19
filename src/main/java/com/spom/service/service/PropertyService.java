package com.spom.service.service;


import com.spom.service.dto.PropertyDto;
import com.spom.service.dto.PropertyFilterDto;

import reactor.core.publisher.Mono;

import java.util.List;

public interface PropertyService {

    PropertyDto saveProperty(PropertyDto propertyDto) throws Exception;

    PropertyDto findByPropertyId(String propertyId) throws Exception;

    Mono<String> deleteProperty(String id) throws Exception;

    List<PropertyDto> findByFilters(PropertyFilterDto propertyFilterDto);

    List<PropertyDto> findProperty();
}
