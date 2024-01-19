package com.spom.service.service;

import com.spom.service.dto.PropertyFractionalisationDto;
import com.spom.service.dto.UserInfo;
import com.spom.service.model.PropertyFractionalisationEntity;
import com.spom.service.repository.ProjectRepository;
import com.spom.service.repository.PropertyFractionalisationRepsitory;
import com.spom.service.repository.PropertyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.persistence.EntityNotFoundException;
import java.util.Date;

@Service
public class PropertyFractionalisationServiceImpl implements PropertyFractionalisationService {

    private final Logger log = LoggerFactory.getLogger(PropertyFractionalisationServiceImpl.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyFractionalisationRepsitory propertyFractionalisationRepsitory;

    @Override
    public PropertyFractionalisationDto savePropertyFractionalisation(
            PropertyFractionalisationDto propertyFractionalisationDto) {

    	log.trace("Start PropertyFractionalisationService.. SavePropertyFractionalisation RequestBody :: {}",propertyFractionalisationDto);
    	
        Mono<PropertyFractionalisationEntity> savedPropertyFractionalisationEntity = null;

        PropertyFractionalisationEntity propertyFractionalisationEntity = new PropertyFractionalisationEntity();

        BeanUtils.copyProperties(propertyFractionalisationDto, propertyFractionalisationEntity);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();


        if (null != propertyFractionalisationEntity.getId()) {
            savedPropertyFractionalisationEntity = this.projectRepository
                    .findById(propertyFractionalisationEntity.getProjectId()).flatMap(existingProject -> {

                        return this.propertyRepository.findById(propertyFractionalisationEntity.getPropertyId())
                                .flatMap(existingProperty -> {

                                    return this.propertyFractionalisationRepsitory
                                            .findById(propertyFractionalisationEntity.getId())
                                            .flatMap(existingFractionalisation -> {
                                                // Update the existing Fractionalisation with the new values

                                                existingFractionalisation.setTotalCostOfProperty(
                                                        propertyFractionalisationEntity.getTotalCostOfProperty());
                                                existingFractionalisation
                                                        .setTotalNoOfPropertyUnits(propertyFractionalisationEntity.getTotalNoOfPropertyUnits());
                                                existingFractionalisation.setCompanyHoldingUnits(propertyFractionalisationEntity.getCompanyHoldingUnits());
                                                existingFractionalisation.setTemporaryUnitsBlocked(propertyFractionalisationEntity.getTemporaryUnitsBlocked());
                                                existingFractionalisation.setGearedAmountOfProperty(propertyFractionalisationEntity.getGearedAmountOfProperty());
                                                existingFractionalisation.setTotalAvailableUnitsForTrade(propertyFractionalisationEntity.getTotalAvailableUnitsForTrade());
                                                existingFractionalisation.setLandPurchaseCost(propertyFractionalisationEntity.getLandPurchaseCost());
                                                existingFractionalisation.setLegalCost(propertyFractionalisationEntity.getLegalCost());
                                                existingFractionalisation.setRegistrationCost(propertyFractionalisationEntity.getRegistrationCost());
                                                existingFractionalisation.setSoldUnits(propertyFractionalisationEntity.getSoldUnits());
                                                existingFractionalisation.setPricePerUnit(propertyFractionalisationEntity.getPricePerUnit());
                                                existingFractionalisation
                                                        .setBlockSize(propertyFractionalisationEntity.getBlockSize());
                                                existingFractionalisation
                                                        .setProjectId(propertyFractionalisationEntity.getProjectId());
                                                existingFractionalisation
                                                        .setPropertyId(propertyFractionalisationEntity.getPropertyId());
                                                existingFractionalisation.setModifiedBy(userInfo.getEmail());
                                                existingFractionalisation.setModifiedDate(new Date());
                                                existingFractionalisation.setPropertyValutaionDate(propertyFractionalisationEntity.getPropertyValutaionDate());
                                                
                                                
                                                // Save the updated Fractionalisation

                                                return this.propertyFractionalisationRepsitory
                                                        .save(existingFractionalisation);
                                            })
                                            .doOnError(error -> log.error("Error in updating Fractionalisation", error))
                                            .onErrorResume(error -> {
                                                return Mono.error(new Exception(error));
                                            });

                                }).switchIfEmpty(Mono.error(new EntityNotFoundException("Property not found with ID: "
                                        + propertyFractionalisationEntity.getPropertyId())));
                    }).switchIfEmpty(Mono.error(new EntityNotFoundException(
                            "Project not found with ID: " + propertyFractionalisationEntity.getProjectId())));
        } else {

            propertyFractionalisationEntity.setCreatedBy(userInfo.getEmail());
            propertyFractionalisationEntity.setCreatedDate(new Date());
            propertyFractionalisationEntity.setSoldUnits(0l);
            propertyFractionalisationEntity.setTemporaryUnitsBlocked(0l);
            propertyFractionalisationEntity.setPropertyValutaionDate(new Date());
            savedPropertyFractionalisationEntity = this.projectRepository
                    .findById(propertyFractionalisationEntity.getProjectId()).flatMap(existingProject -> {

                        return this.propertyRepository.findById(propertyFractionalisationEntity.getPropertyId())
                                .flatMap(existingProperty -> {
                                    return this.propertyFractionalisationRepsitory
                                            .save(propertyFractionalisationEntity).doOnError(error -> log
                                                    .error("Error in saving propertyFractionalisation", error))
                                            .onErrorResume(error -> Mono.error(new Exception(error)));
                                }).switchIfEmpty(Mono.error(new EntityNotFoundException("Property not found with ID: "
                                        + propertyFractionalisationEntity.getPropertyId())));
                    }).switchIfEmpty(Mono.error(new EntityNotFoundException(
                            "Project not found with ID: " + propertyFractionalisationEntity.getProjectId())));

        }

        PropertyFractionalisationDto savedpropertyFractionalisationDto = new PropertyFractionalisationDto();

        BeanUtils.copyProperties(savedPropertyFractionalisationEntity.block(), savedpropertyFractionalisationDto);
        
        log.trace("End PropertyFractionalisationService.. SavePropertyFractionalisation ResponseBody :: {}",savedpropertyFractionalisationDto);
        return savedpropertyFractionalisationDto;
    }

    @Override
    public PropertyFractionalisationDto findByProjecId(String projectId) {

        log.trace("Start PropertyFractionalisationService.. findByProjecId ProjectId :: {}",projectId);
        Mono<PropertyFractionalisationEntity> propertyFractionalisation = this.propertyFractionalisationRepsitory
                .findByProjectId(projectId)
                .doOnError(error -> log.error("Error in getPropertyFractionalisation ", error)).onErrorResume(error -> {
                    return Mono.error(new RuntimeException(error));
                });

        PropertyFractionalisationDto propertyFractionalisationDto = new PropertyFractionalisationDto();

        return propertyFractionalisation.flatMap(property -> {
	        BeanUtils.copyProperties(property, propertyFractionalisationDto);

	        return projectRepository.findById(propertyFractionalisationDto.getProjectId())
                    .switchIfEmpty(Mono.error(new EntityNotFoundException("Project not found with ID: " + propertyFractionalisationDto.getProjectId())))
                    .flatMap(projectEntity -> {
                        propertyFractionalisationDto.setPlatformCharges(projectEntity.getPlatformCharges());
                        propertyFractionalisationDto.setTotalAvailableUnitsForTrade
            	        (propertyFractionalisationDto.getTotalAvailableUnitsForTrade()-propertyFractionalisationDto.getSoldUnits()
            	        		-propertyFractionalisationDto.getTemporaryUnitsBlocked());
                        return Mono.just(propertyFractionalisationDto);
                    });
	    }).block();
    }

    @Override
    public Mono<String> deletePropertyFractionalisation(String id) {

    	log.trace("Start PropertyFractionalisationService.. deletePropertyFractionalisation Id :: {}",id);
        return this.propertyFractionalisationRepsitory.deleteById(id).thenReturn("deleted successfully")
                .doOnError(error -> log.error("Error in Deleting propertyFractionalisation ", error))
                .onErrorResume(error -> {
                    return Mono.error(new Exception(error));
                });
    }

	@Override
	public PropertyFractionalisationDto findByPropertyId(String propertyId) {
		log.trace("Start PropertyFractionalisationService.. findByPropertyId propertyId :: {}",propertyId);
        Mono<PropertyFractionalisationEntity> propertyFractionalisation = this.propertyFractionalisationRepsitory
                .findByPropertyId(propertyId)
                .doOnError(error -> log.error("Error in getPropertyFractionalisation ", error)).onErrorResume(error -> {
                    return Mono.error(new RuntimeException(error));
                });

        PropertyFractionalisationDto propertyFractionalisationDto = new PropertyFractionalisationDto();

        return propertyFractionalisation.flatMap(property -> {
	        BeanUtils.copyProperties(property, propertyFractionalisationDto);
	        
	        return projectRepository.findById(propertyFractionalisationDto.getProjectId())
                    .switchIfEmpty(Mono.error(new EntityNotFoundException("Project not found with ID: " + propertyFractionalisationDto.getProjectId())))
                    .flatMap(projectEntity -> {
                        propertyFractionalisationDto.setPlatformCharges(projectEntity.getPlatformCharges());
                        propertyFractionalisationDto.setTotalAvailableUnitsForTrade
            	        (propertyFractionalisationDto.getTotalAvailableUnitsForTrade()-propertyFractionalisationDto.getSoldUnits()
            	        		-propertyFractionalisationDto.getTemporaryUnitsBlocked());
                        return Mono.just(propertyFractionalisationDto);
                    });
	    }).block();
	}

}
