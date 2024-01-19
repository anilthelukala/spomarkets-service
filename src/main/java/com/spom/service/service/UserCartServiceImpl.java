package com.spom.service.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.spom.service.dto.PropertyFractionalisationDto;
import com.spom.service.dto.UserCartDto;
import com.spom.service.dto.UserInfo;
import com.spom.service.model.ProjectEntity;
import com.spom.service.model.PropertyEntity;
import com.spom.service.model.PropertyFractionalisationEntity;
import com.spom.service.model.UserCartEntity;
import com.spom.service.repository.ProjectRepository;
import com.spom.service.repository.PropertyFractionalisationRepsitory;
import com.spom.service.repository.PropertyRepository;
import com.spom.service.repository.UserCartRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserCartServiceImpl implements UserCartService {
	private final Logger log = LoggerFactory.getLogger(UserCartServiceImpl.class);
	@Autowired
	private UserCartRepository userCartRepository;

	@Autowired
	private PropertyRepository propertyRepository;

	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
    private PropertyFractionalisationRepsitory propertyFractionalisationRepsitory;
	@Override
	public UserCartDto saveUserCart(UserCartDto userCartDto) {

		log.trace("Start UserCartServiceImpl.. saveUserCart RequestBody:: {}", userCartDto);
		UserCartEntity userCart = new UserCartEntity();

		BeanUtils.copyProperties(userCartDto, userCart);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserInfo userInfo = (UserInfo) authentication.getPrincipal();

		Mono<UserCartEntity> userCartEntity = null;
		if (null != userCart.getId()) {
			userCartEntity = this.propertyRepository.findById(userCart.getPropertyId()).flatMap(existingProperty -> {
				return this.userCartRepository.findById(userCart.getId()).flatMap(existingUserCart -> {
					existingUserCart.setPropertyId(userCart.getPropertyId());
					existingUserCart.setNoOfSelectedUnits(userCart.getNoOfSelectedUnits());
					existingUserCart.setIsActive(userCart.getIsActive());
					existingUserCart.setUserName(userInfo.getEmail());
					existingUserCart.setModifiedBy(userInfo.getEmail());
					existingUserCart.setModifiedDate(new Date());
					existingUserCart.setUserToken(userCart.getUserToken());
					return this.userCartRepository.save(existingUserCart);
				}).doOnError(error -> log.error("Error in updating UserCart", error)).onErrorResume(error -> {
					return Mono.error(new Exception(error));
				});
			}).switchIfEmpty(
					Mono.error(new EntityNotFoundException("Property not found with ID: " + userCart.getPropertyId())));
		} else {
			userCart.setCartCreatedDate(new Date());
			userCart.setCreatedBy(userInfo.getEmail());
			userCart.setIsActive(true);
			userCart.setUserName(userInfo.getEmail());
			userCartEntity = this.propertyRepository.findById(userCart.getPropertyId()).flatMap(existingProperty -> {
				return this.userCartRepository.save(userCart)
						.doOnError(error -> log.error("Error in saving userCart", error)).onErrorResume(error -> {
							return Mono.error(new Exception(error));
						});
			}).switchIfEmpty(
					Mono.error(new EntityNotFoundException("Property not found with ID: " + userCart.getPropertyId())));
		}

		UserCartDto savedUserCart = new UserCartDto();
		BeanUtils.copyProperties(userCartEntity.block(), savedUserCart);
		log.trace("End UserCartServiceImpl.. saveUserCart ResponseBody:: {}", savedUserCart);
		return savedUserCart;
	}

	@Override
	public List<UserCartDto> findByUserName(String userName) {
		
		log.trace("End UserCartServiceImpl.. findByUserName UserName={}", userName);
		Flux<UserCartEntity> userCartEntity = this.userCartRepository.findByUserNameAndIsActive(userName,true)
				.doOnError(error -> log.error("Error in getUserCart ", error)).onErrorResume(error -> {
					return Mono.error(new Exception(error));
				});

		
//		return userCartEntity.flatMap(user -> {
//	        BeanUtils.copyProperties(user, userCartDto);
//	        return Mono.just(userCartDto);
//	    }).block();
		
		   List<UserCartEntity> getUserCartEntity = new ArrayList<UserCartEntity>();
		   getUserCartEntity = userCartEntity.collectList().block();

	        List<UserCartDto> getUserCartDtos = new ArrayList<UserCartDto>();
	        UserCartDto getuserCartDto = null;
	        for (UserCartEntity userCart : getUserCartEntity) {
	        	getuserCartDto = new UserCartDto();

	        	PropertyFractionalisationEntity	propertyFractionalisation= this.propertyFractionalisationRepsitory.findByPropertyId(userCart.getPropertyId()).block();
	        	PropertyEntity	property= this.propertyRepository.findById(userCart.getPropertyId()).block();
	        	
	            BeanUtils.copyProperties(userCart, getuserCartDto);
	            
	        	PropertyFractionalisationDto propertyFractionalisationDto = new PropertyFractionalisationDto();
	            BeanUtils.copyProperties(propertyFractionalisation, propertyFractionalisationDto);
	            
	            getuserCartDto.setPropertyFractionalisation(propertyFractionalisationDto);
	            getuserCartDto.setProperty(property);
	            getuserCartDto.setUserToken(null);
	            getUserCartDtos.add(getuserCartDto);
	        }

	        log.trace("End UserCartServiceImpl.. findByUserName Response :: {}",getUserCartDtos);
		
		return getUserCartDtos;
	}

	@Override
	public List<UserCartEntity> getAllActiveCart(){
		
		Flux<UserCartEntity> userCartEntity = this.userCartRepository.findByIsActive(true)
				.doOnError(error -> log.error("Error in getUserCart ", error)).onErrorResume(error -> {
					return Mono.error(new Exception(error));
				});
		
		return userCartEntity.collectList().block();
	}

	@Override
	public Mono<String> removeCart(String id) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserInfo userInfo = (UserInfo) authentication.getPrincipal();

			
			return this.userCartRepository.findById(id).flatMap(existingUserCart -> {
					
					existingUserCart.setIsActive(false);
					existingUserCart.setModifiedBy(userInfo.getEmail());
					existingUserCart.setModifiedDate(new Date());
					return this.userCartRepository.save(existingUserCart);
				}).thenReturn("Remove Cart successfully").doOnError(error -> log.error("Error in updating UserCart", error)).onErrorResume(error -> {
					return Mono.error(new Exception(error));
				});
			
		
			

}

	@Override
	public UserCartDto findById(String cartId) {
		log.trace("End UserCartServiceImpl.. findById cartId={}", cartId);
		Mono<UserCartEntity> userCartEntity = this.userCartRepository.findById(cartId)
				.doOnError(error -> log.error("Error in getUserCart ", error)).onErrorResume(error -> {
					return Mono.error(new Exception(error));
				}).switchIfEmpty(
						Mono.error(new EntityNotFoundException("Cart not found with ID: " +cartId)));

		UserCartDto	userCartDto=new UserCartDto();
		
		UserCartEntity user=userCartEntity.block();
	        BeanUtils.copyProperties(user, userCartDto);
	        
	        PropertyFractionalisationEntity	propertyFractionalisation= this.propertyFractionalisationRepsitory.findByPropertyId(user.getPropertyId())
	        		.switchIfEmpty(Mono.error(new EntityNotFoundException("Property not found with ID: "
                            + user.getPropertyId()))).block();
        	PropertyEntity	property= this.propertyRepository.findById(user.getPropertyId()).block();
        	
        	
        ProjectEntity project=	this.projectRepository.findById(propertyFractionalisation.getProjectId())
            .switchIfEmpty(Mono.error(new EntityNotFoundException("Project not found with ID: " + propertyFractionalisation.getProjectId()))).block();
        	
        	PropertyFractionalisationDto propertyFractionalisationDto = new PropertyFractionalisationDto();
            BeanUtils.copyProperties(propertyFractionalisation, propertyFractionalisationDto);
            propertyFractionalisationDto.setTotalAvailableUnitsForTrade(propertyFractionalisationDto.getTotalAvailableUnitsForTrade()-propertyFractionalisationDto.getSoldUnits()
	        		-propertyFractionalisationDto.getTemporaryUnitsBlocked());
            propertyFractionalisationDto.setPlatformCharges(project.getPlatformCharges());
            userCartDto.setPropertyFractionalisation(propertyFractionalisationDto);
        	userCartDto.setProperty(property);
        	userCartDto.setUserToken(null);
        return 	userCartDto;
	}
}
