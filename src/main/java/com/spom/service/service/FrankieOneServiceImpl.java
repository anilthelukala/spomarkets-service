package com.spom.service.service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.spom.service.dto.UserInfo;
import com.spom.service.frankieone.FrankieOnePermissions;
import com.spom.service.frankieone.FrankieOneSettings;
import com.spom.service.frankieone.Permission;

import reactor.core.publisher.Mono;


@Component
public class FrankieOneServiceImpl implements FrankieOneService{

    private final Logger log = LoggerFactory.getLogger(FrankieOneService.class);
    @Autowired
    private SpoWebClient spoWebClient;

    @Autowired
    FrankieOneSettings frankieOneSettings;


    public Mono<String> createFrankieOneSession() throws Exception {
        FrankieOnePermissions permission = new FrankieOnePermissions();
        Permission perm = new Permission();
        perm.setPreset("smart-ui");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        perm.setReference(userInfo.getUsername());
        permission.setPermissions(perm);
        return spoWebClient.post(frankieOneSettings.getSessionUrl(), permission, generateFrankieOneHeaders())
					.doOnNext(s -> log.info("FrankieOneSession AuthToken Response :::: {}", s))
					.onErrorResume(ex -> {
						if (ex instanceof WebClientResponseException) {
							log.error("Raw Error Response :::: {}", ((WebClientResponseException) ex).getResponseBodyAsString());
						}
                        return Mono.just(String.format("DemoToken"));
						//return Mono.just(String.format("Spo Error %s", ex.getMessage()));
					})
					.map(responseString -> responseString);
    }


    public Map<String, String> generateFrankieOneHeaders(){
        Map<String, String> headers = new HashMap<>();
        String authToken = frankieOneSettings.getCustomerId() + ":" + frankieOneSettings.getApiKey();
        String base64EncodedCredentials = Base64.getEncoder().encodeToString(authToken.getBytes());
        headers.put("Authorization","machine " + base64EncodedCredentials);
        headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }
    
}
