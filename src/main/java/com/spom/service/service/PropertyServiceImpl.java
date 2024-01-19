package com.spom.service.service;

import com.spom.service.dto.PropertyDto;
import com.spom.service.dto.PropertyFilterDto;
import com.spom.service.dto.PropertyFractionalisationDto;
import com.spom.service.dto.UserInfo;
import com.spom.service.model.ProjectEntity;
import com.spom.service.model.PropertyEntity;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final Logger log = LoggerFactory.getLogger(PropertyServiceImpl.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyFractionalisationRepsitory propertyFractionalisationRepsitory;

    @Override
    public PropertyDto saveProperty(PropertyDto propertyDto) {
    	log.trace("Start PropertyService.. saveProperty RequestBody :: {}", propertyDto);
        Mono<PropertyEntity> propertyEntity = null;
        PropertyEntity property = new PropertyEntity();
        BeanUtils.copyProperties(propertyDto, property);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        if (null != property.getId()) {
            propertyEntity = this.projectRepository.findById(property.getProjectId()).flatMap(existingProject -> {

                return this.propertyRepository.findById(property.getId()).flatMap(existingProperty -> {
                    // Update the existing property with the new values
                	existingProperty.setPropertyCode(property.getPropertyCode());
                	existingProperty.setPropertyName(property.getPropertyName());
                    existingProperty.setDescription(property.getDescription());
                    existingProperty.setAddressLine1(property.getAddressLine1());
                    existingProperty.setAddressLine2(property.getAddressLine2());
                    existingProperty.setPostalCode(property.getPostalCode());
                    existingProperty.setImage(property.getImage());
                    existingProperty.setVideoLink(property.getVideoLink());
                    existingProperty.setProjectId(property.getProjectId());
                    existingProperty.setPropertyActiveFlag(property.getPropertyActiveFlag());
                    existingProperty.setPropertyType(property.getPropertyType());
                    existingProperty.setBaseRate(property.getBaseRate());
                    existingProperty.setLatestRate(property.getLatestRate());
                    existingProperty.setGearedPercentage(property.getGearedPercentage());
                    existingProperty.setCapitalGrowthRate(property.getCapitalGrowthRate());
                    existingProperty.setHistoricalSuburbGrowthRate(property.getHistoricalSuburbGrowthRate());
                    existingProperty.setLatestBlockValuation(property.getLatestBlockValuation());
                    existingProperty.setSettlementDate(property.getSettlementDate());
                    existingProperty.setBathType(property.getBathType());
                    existingProperty.setBedType(property.getBedType());
                    existingProperty.setParkingType(property.getParkingType());
                    existingProperty.setSoldStatus(property.getSoldStatus());
                    existingProperty.setModifiedBy(userInfo.getEmail());
                    existingProperty.setModifiedDate(new Date());

                    // Save the updated property
                    return propertyRepository.save(existingProperty)
                            .doOnError(error -> log.error("Error in updating property", error))
                            .onErrorResume(error -> Mono.error(new Exception(error)));
                }).switchIfEmpty(
                        Mono.error(new EntityNotFoundException("Property not found with ID: " + property.getId())));

            }).switchIfEmpty(
                    Mono.error(new EntityNotFoundException("Project not found with ID: " + property.getProjectId())));

        } else {

            property.setCreatedBy(userInfo.getEmail());
            property.setCreatedDate(new Date());
            property.setTargetCompletionDate(new Date());
            propertyEntity = this.projectRepository.findById(property.getProjectId()).flatMap(existingProject -> {

                return this.propertyRepository.save(property)
                        .doOnError(error -> log.error("Error in saving property", error))
                        .onErrorResume(error -> Mono.error(new Exception(error)));

            }).switchIfEmpty(
                    Mono.error(new EntityNotFoundException("Project not found with ID: " + property.getProjectId())));

        }

        PropertyDto savePropertyDto = new PropertyDto();
        BeanUtils.copyProperties(propertyEntity.block(), savePropertyDto);
        
        log.trace("End PropertyService.. saveProperty ResponseBody :: {}", savePropertyDto);
        
        return savePropertyDto;
    }

    @Override
    public PropertyDto findByPropertyId(String propertyId) {

        log.trace("Start PropertyService.. findByPropertyId :: {}", propertyId);

        Mono<PropertyEntity> getProperty = this.propertyRepository.findById(propertyId).flatMap(property->{
        	return this.propertyFractionalisationRepsitory.findByPropertyId(property.getId())
        			.switchIfEmpty(Mono.just(new PropertyFractionalisationEntity()))
                    .map(propertyFraction -> {
                    	if(null!=propertyFraction.getId())
                        property.setPropertyFractionalisationEntity(propertyFraction);
                        return property;
                    });
        })
                .doOnError(error -> log.error("Error in getProperty ", error)).onErrorResume(error -> {
                    return Mono.error(new Exception(error));
                });

        PropertyDto propertyDto = new PropertyDto();
        PropertyFractionalisationDto propertyFractionalisationDto=new PropertyFractionalisationDto();
       
        
        PropertyDto getproperty= getProperty.flatMap(property -> {
            BeanUtils.copyProperties(property.getPropertyFractionalisationEntity(), propertyFractionalisationDto);
            BeanUtils.copyProperties(property, propertyDto);

            
            return projectRepository.findById(propertyFractionalisationDto.getProjectId())
                    .switchIfEmpty(Mono.error(new EntityNotFoundException("Project not found with ID: " + propertyFractionalisationDto.getProjectId())))
                    .flatMap(projectEntity -> {
                        propertyFractionalisationDto.setPlatformCharges(projectEntity.getPlatformCharges());
                        propertyDto.setPropertyFractionalisation(propertyFractionalisationDto);
                        return Mono.just(propertyDto);
                    });
        }).block();
        
        return getproperty;
    }

    @Override
    public Mono<String> deleteProperty(String id) {
    	
    	log.trace("Start PropertyService.. deleteProperty Id :: {}", id);
    	
        // Delete child records first
        Mono<Void> deleteChildren = this.propertyFractionalisationRepsitory.deleteByPropertyId(id).then();

        return deleteChildren.then(this.propertyRepository.deleteById(id)).thenReturn("Property deleted successfully")
                .doOnError(error -> log.error("Error in Deleting Property ", error)).onErrorResume(error -> {
                    return Mono.error(new Exception(error));
                });
    }

    @Override
    public List<PropertyDto> findByFilters(PropertyFilterDto propertyFilterDto) {
    	
    	log.trace("Start PropertyService.. findByFilters PropertyFilterBody :: {}", propertyFilterDto);
    	
        Flux<PropertyEntity> propertys = null;
        if (null != propertyFilterDto.getLocation() && propertyFilterDto.getLocation().equalsIgnoreCase("All") && null == propertyFilterDto.getPostalCode() ) {
            propertys = this.propertyRepository.findAll().flatMapSequential(property -> {
                return this.propertyFractionalisationRepsitory.findByPropertyId(property.getId())
                        .switchIfEmpty(Mono.just(new PropertyFractionalisationEntity()))
                        .map(propertyFraction -> {
                        	if(null!=propertyFraction.getId())
                            property.setPropertyFractionalisationEntity(propertyFraction);
                            return property;
                        });
            });
        } else if (null != propertyFilterDto.getLocation() && !propertyFilterDto.getLocation().equalsIgnoreCase("All") && null == propertyFilterDto.getPostalCode()
                ) {
            propertys = this.propertyRepository.findByProjectId(propertyFilterDto.getLocation()).flatMapSequential(property -> {
                return this.propertyFractionalisationRepsitory.findByPropertyId(property.getId())
                        .switchIfEmpty(Mono.just(new PropertyFractionalisationEntity()))
                        .map(propertyFraction -> {
                        	if(null!=propertyFraction.getId())
                            property.setPropertyFractionalisationEntity(propertyFraction);
                            return property;
                        });
            });
        } else if (null != propertyFilterDto.getLocation() && propertyFilterDto.getLocation().equalsIgnoreCase("All") && null != propertyFilterDto.getPostalCode()) {
            propertys = this.propertyRepository.findByPostalCode(propertyFilterDto.getPostalCode()).flatMapSequential(property -> {
                return this.propertyFractionalisationRepsitory.findByPropertyId(property.getId())
                        .switchIfEmpty(Mono.just(new PropertyFractionalisationEntity()))
                        .map(propertyFraction -> {
                        	if(null!=propertyFraction.getId())
                            property.setPropertyFractionalisationEntity(propertyFraction);
                            return property;
                        });
            });
        } else if (null != propertyFilterDto.getLocation() && !propertyFilterDto.getLocation().equalsIgnoreCase("All") && null != propertyFilterDto.getPostalCode()
                ) {
            propertys = this.propertyRepository.findByProjectIdAndPostalCode(propertyFilterDto.getLocation(), propertyFilterDto.getPostalCode()).flatMapSequential(property -> {
                return this.propertyFractionalisationRepsitory.findByPropertyId(property.getId())
                        .switchIfEmpty(Mono.just(new PropertyFractionalisationEntity()))
                        .map(propertyFraction -> {
                        	if(null!=propertyFraction.getId())
                            property.setPropertyFractionalisationEntity(propertyFraction);
                            return property;
                        });
            });
        }

        List<PropertyEntity> getPropertys = new ArrayList<PropertyEntity>();
        getPropertys = propertys.collectList().block();

        List<PropertyDto> getPropertyDtos = new ArrayList<PropertyDto>();
        PropertyDto propertyDto = null;
        for (PropertyEntity property : getPropertys) {
            propertyDto = new PropertyDto();

            BeanUtils.copyProperties(property, propertyDto);
            getPropertyDtos.add(propertyDto);
        }
        
        log.trace("End PropertyService.. findByFilters Response :: {}", getPropertyDtos);
        return getPropertyDtos;
    }

    @Override
    public List<PropertyDto> findProperty() {

    	log.trace("Start PropertyService.. findAllProperty :: {}");
    	
        Flux<PropertyEntity> propertys = this.propertyRepository.findAll().flatMapSequential(property -> {
            return this.propertyFractionalisationRepsitory.findByPropertyId(property.getId())
                    .switchIfEmpty(Mono.just(new PropertyFractionalisationEntity()))
                    .map(propertyFraction -> {
                    	if(null!=propertyFraction.getId())
                        property.setPropertyFractionalisationEntity(propertyFraction);
                        return property;
                    });
        });
        

        List<PropertyEntity> getPropertys = new ArrayList<PropertyEntity>();
        getPropertys = propertys.collectList().block();

        List<PropertyDto> getPropertyDtos = new ArrayList<PropertyDto>();
        PropertyDto propertyDto = null;
        for (PropertyEntity property : getPropertys) {
            propertyDto = new PropertyDto();
            PropertyFractionalisationDto propertyFractionalisationDto=new PropertyFractionalisationDto();
            BeanUtils.copyProperties(property.getPropertyFractionalisationEntity(), propertyFractionalisationDto);
            ProjectEntity projectEntity=this.projectRepository.findById(propertyFractionalisationDto.getProjectId()).switchIfEmpty(Mono.error(new EntityNotFoundException(
                    "Project not found with ID: " + propertyFractionalisationDto.getProjectId()))).block();
            propertyFractionalisationDto.setPlatformCharges(projectEntity.getPlatformCharges());
            BeanUtils.copyProperties(property, propertyDto);
            propertyDto.setPropertyFractionalisation(propertyFractionalisationDto);
            getPropertyDtos.add(propertyDto);
        }

        log.trace("End PropertyService.. findAllProperty Response :: {}",getPropertyDtos);
        
        return getPropertyDtos;
    }

}
