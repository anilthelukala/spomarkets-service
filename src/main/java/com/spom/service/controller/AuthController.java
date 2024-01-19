package com.spom.service.controller;

import com.spom.service.common.CommonMessageUtil;
import com.spom.service.common.CommonResponse;
import com.spom.service.dto.RefreshTokenDto;
import com.spom.service.dto.User;
import com.spom.service.dto.UserCartDto;
import com.spom.service.dto.UserInfo;
import com.spom.service.model.LoginRequest;
import com.spom.service.model.UserEntity;
import com.spom.service.repository.UserMstRepository;
import com.spom.service.service.TokenService;
import com.spom.service.service.UserCartService;
import com.spom.service.service.UserService;

import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
    private UserCartService userCartService;
	
    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserMstRepository userMstRepository;
    
    public AuthController(TokenService tokenService, AuthenticationManager authenticationManager) {
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/basic")
    public String token(Authentication authentication) {
        LOG.trace("Start AuthController.. token RequestBody :: {}", authentication);
       
        String token = tokenService.generateToken(authentication);
       
        LOG.trace("End AuthController.. token Response :: {}", token);
        return token;
    }

    @PostMapping("/authorization")
    public ResponseEntity<CommonResponse> tokenByBody(@RequestBody LoginRequest loginRequest) {
        LOG.trace("Start AuthController.. tokenByBody RequestBody :: {}", loginRequest);
        
        User user=userService.findByEmail(loginRequest.username());
CommonResponse response = new CommonResponse();
		Date currentDate = new Date();
		if(null!=user&&null!=user.getLockoutEndTime()&&currentDate.compareTo(user.getLockoutEndTime()) < 0) {
			response.addErrorMsg(CommonMessageUtil.getExceptionMessage("Account is Temporarily locked. Please connect with SPO Markets Team!"));    
        	return new ResponseEntity<>(response, HttpStatus.OK);
			
		}
        
       
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        
        
        if(null==user) {
        	response.addErrorMsg(CommonMessageUtil.getExceptionMessage("Invalid UsenName or Password"));    
        	return new ResponseEntity<>(response, HttpStatus.OK); 	
        }
        else if(null!=user&&!passwordEncoder.matches(loginRequest.password(),user.getPassword() )) {
        	response.addErrorMsg(CommonMessageUtil.getExceptionMessage("Invalid UsenName or Password"));    
        	return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        
        try {
            String token = tokenService.generateToken(authentication);
            LOG.trace("Generated Token: {} for user {}", token, authentication);
            response.addData("token", token);
            response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("Login Successfully"));

         // Generate a GUID (UUID) for the logged-in user
            String userSessionId = UUID.randomUUID().toString();
            
        } catch (Exception e) {
            response.addErrorMsg(CommonMessageUtil.getExceptionMessage(e.getMessage()));

        }

        LOG.trace("End AuthController.. token Response :: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/refresh")
    public ResponseEntity<CommonResponse> refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {
         LOG.trace("Start AuthController.. refreshToken RequestBody :: {}", refreshTokenDto);
        CommonResponse response = new CommonResponse();
        try {
            String token = tokenService.refreshToken(refreshTokenDto.getToken());
            
            response.addData("token", token);
            response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("Refresh Token Successfully"));

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserInfo userInfo = (UserInfo) authentication.getPrincipal();

            List<UserCartDto> userCart = userCartService.findByUserName(userInfo.getUsername());
            for(UserCartDto cart:userCart) {
            if(null!=userCart) {
            	cart.setUserToken(token);
            	userCartService.saveUserCart(cart);
            }
            }
        } catch (Exception e) {
            LOG.error("Error in AuthController refreshToken Error Massage::{}", e.getMessage());
            response.addErrorMsg(CommonMessageUtil.getExceptionMessage(e.getMessage()));

        }

         LOG.trace("End AuthController.. refreshToken Response :: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
