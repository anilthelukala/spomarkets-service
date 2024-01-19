package com.spom.service.helper;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import com.spom.service.model.UserCartEntity;
import com.spom.service.repository.UserCartRepository;
import com.spom.service.service.UserCartService;

import reactor.core.publisher.Mono;

@Service
public class UserCartHelper {
	private final Logger log = LoggerFactory.getLogger(UserCartHelper.class);
	private final UserCartService userCartService;
	private final JwtDecoder decoder;
	private final long fixedRate; 

	@Autowired
	private UserCartRepository userCartRepository;
	
	public UserCartHelper(UserCartService userCartService, JwtDecoder decoder,
			@Value("${userCartScheduler.fixed-rate}") long fixedRate) {
		this.userCartService = userCartService;
		this.decoder = decoder;
		this.fixedRate = fixedRate;
	}

	@Scheduled(fixedRateString = "${userCartScheduler.fixed-rate}") 
	public void checkTokenExpiration() {
		log.info("Start UserCartHelper :: checkTokenExpiration Scheduler Timing::");
		try {
			List<UserCartEntity> userCarts = userCartService.getAllActiveCart();
			for (UserCartEntity cart : userCarts) {

//                Jwt jwt = decoder.decode(cart.getUserToken()); // When the token is expired then get invalid signature Exception 
//                if (jwt.getExpiresAt().isBefore(Instant.now())) {
//                    cart.setIsActive(false);
//                    Mono<UserCartEntity> updated =userCartRepository.save(cart);
//                }
				
				Date currentDate = new Date();
				Instant modifiedInstant = null;
				
				
				// Convert Date to Instant
				Instant currentInstant = currentDate.toInstant();
				if(null!=cart.getModifiedDate()) {
				 modifiedInstant = cart.getModifiedDate().toInstant();
				}
				else {
					modifiedInstant = cart.getCartCreatedDate().toInstant();
				}

				// Calculate the difference in minutes
				long minutesDifference = Duration.between(modifiedInstant, currentInstant).toMinutes();

				if (minutesDifference > 30) {
					

					cart.setIsActive(false);
					log.info("Start If Block :: checkTokenExpiration cart :: {} ",cart);
					Mono<UserCartEntity> updated =userCartRepository.save(cart);
					log.info("End If Block :: checkTokenExpiration updatedCart :: {} ",updated.block());
				}
			}
		} 
		catch (BadJwtException e) {
            // Handle the case where decoding fails or the token is invalid
            log.error("Failed to decode JWT : {}", e.getMessage());
        }
		catch (Exception e) {
			// Handle or log the exception appropriately
			log.error("Error in UserCart Update : {}",e.getMessage());
		}
	}
}
