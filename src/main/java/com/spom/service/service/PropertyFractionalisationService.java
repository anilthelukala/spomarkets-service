package com.spom.service.service;

import com.spom.service.dto.PropertyFractionalisationDto;
import reactor.core.publisher.Mono;

public interface PropertyFractionalisationService {

    PropertyFractionalisationDto savePropertyFractionalisation(
            PropertyFractionalisationDto propertyFractionalisationDto) throws Exception;

    PropertyFractionalisationDto findByProjecId(String projectId) throws Exception;

    Mono<String> deletePropertyFractionalisation(String id) throws Exception;

	PropertyFractionalisationDto findByPropertyId(String propertyId);

}
